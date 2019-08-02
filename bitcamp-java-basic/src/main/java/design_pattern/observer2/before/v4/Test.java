// v4(홍길동, 5월19일에 추가함): 텍스트 분석기에 기능 추가 - 한줄 주석 세기
package design_pattern.observer2.before.v4;

import java.io.FileReader;

public class Test {

  public static void main(String[] args) {
    // FillReader를 생성한놈이 닫는 것까지!
    try (FileReader in = new FileReader("build.gradle")){
      
      TextAnalyzer analyzer = new TextAnalyzer(in);
      analyzer.execute();
      
    } catch (Exception e) {
      System.out.println("실행 중 오류 발생!");
    }
    

  }

}