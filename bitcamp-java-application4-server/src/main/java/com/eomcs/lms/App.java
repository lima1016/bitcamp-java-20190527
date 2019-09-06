// v54_1: Apache의 HttpClient를 이용하여 HTTP 서버 만들기
package com.eomcs.lms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import com.eomcs.util.RequestMappingHandlerMapping;

public class App {

  // Log4j의 로그 출력 도구를 준비한다.
  private static final Logger logger = LogManager.getLogger(App.class);

  ApplicationContext appCtx;
  RequestMappingHandlerMapping handlerMapping;
  int state;

  public App() throws Exception {
    appCtx = new AnnotationConfigApplicationContext(AppConfig.class);

    // Spring IoC 컨테이너에 들어 있는(Spring IoC 컴테이너가 생성한) 객체 알아내기
    String[] beanNames = appCtx.getBeanDefinitionNames();
    logger.debug("[Spring IoC 컨테이너 객체들]----------------------");
    for (String beanName : beanNames) {
      logger.debug(
          String.format("%s(%s)", appCtx.getBean(beanName).getClass().getSimpleName(), beanName));
    }
    logger.debug("---------------------------------------------");

    handlerMapping = createRequestMappingHandlerMapping();
  }

  private RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
    RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();

    // 객체물에서 @Component 애노테이션이 붙은 객체 목록을 꺼낸다.
    Map<String, Object> components = appCtx.getBeansWithAnnotation(Component.class);
    logger.trace("[요청 핸들러] --------------------------------");

    // 객체 안에 선언된 메서드 중에서 @RequestMapping이 붙은 메서드를 찾아낸다.
    Collection<Object> objList = components.values();
    objList.forEach(obj -> {

      Method[] methods = null;

      // 프록시 객체가 나올일이있으니까 만든거임
      // Board lesson member 원본나옴
      // Photoboard는 transaction수행중
      // (스프링에서도 내부적으로 프록시를 만들어서 처리를함 close and realClose
      // 그래서 한번 씌워서 가짜를 만들어준거임 )
      if (AopUtils.isAopProxy(obj)) { // 원본이 아니라 프록시 객체라면
        try {
          // 프록시 객체의 클래스가 아니라 원본 객체의 클래스 정보를 가져온다.
          // 원본.class 값이 들어감
          Class<?> originClass = // 메소드는 주소주고 가져올수있음
              (Class<?>) obj.getClass().getMethod("getTargetClass").invoke(obj);
          methods = originClass.getMethods();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else { // 원본 객체일 경우,
        // 원본 객체의 클래스로부터 메서드 목록을 가져온다.
        methods = obj.getClass().getMethods();
      }
      for (Method m : methods) {
        RequestMapping anno = m.getAnnotation(RequestMapping.class);
        if (anno == null)
          continue;
        // @RequestMapping이 붙은 메서드를 찾으면 mapping 객체에 보관한다.
        // 어짜피 배열의 크기는 1 add 객체 메소드
        mapping.addRequestHandler(anno.value()[0], obj, m);

        logger.trace(String.format("%s ==> %s.%s()", anno.value()[0],
            obj.getClass().getSimpleName(), m.getName()));
      }
    });

    return mapping;
  }

  @SuppressWarnings("static-access")
  private void service() throws Exception {
    // 소켓정보를 담은 값을 리턴
    SocketConfig socketConfig =
        SocketConfig.custom().setSoTimeout(15000).setTcpNoDelay(true).build();

    final HttpServer server = ServerBootstrap.bootstrap()
        // Builder설정하는 과정
        .setListenerPort(8888).setServerInfo("Bitcamp/1.1").setSocketConfig(socketConfig)
        .setSslContext(null).setExceptionLogger(ex -> {
          if (ex instanceof SocketTimeoutException) {
            System.out.println("Connection timed out");
          } else if (ex instanceof ConnectionClosedException) {
            System.out.println(ex.getMessage());
          } else {
            ex.printStackTrace();
          }
        }).registerHandler("*", new CommandHttpRequestHandler(handlerMapping)).create();

    server.start();
    logger.info("서버 실행중");
    server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        server.shutdown(5, TimeUnit.SECONDS);
      }
    });
  }

  static class StdErrorExceptionLogger implements ExceptionLogger {

    @Override
    public void log(final Exception ex) {
      if (ex instanceof SocketTimeoutException) {
        System.err.println("Connection timed out");
      } else if (ex instanceof ConnectionClosedException) {
        System.err.println(ex.getMessage());
      } else {
        ex.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {

    try {
      App app = new App();
      app.service();

    } catch (Exception e) {
      System.out.println("시스템 실행 중 오류 발생!");

      StringWriter out2 = new StringWriter();
      e.printStackTrace(new PrintWriter(out2));
      logger.debug(out2.toString());
    }
  }
}
