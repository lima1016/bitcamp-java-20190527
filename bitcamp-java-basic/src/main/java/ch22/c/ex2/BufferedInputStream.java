package ch22.c.ex2;

import java.io.IOException;
import java.io.InputStream;

// InputStream에 기능을 덧붙이는 플러그인 역할을 수행하는 클래스이다.
// => 이런 클래스를 데코레이터(decorator)라 한다.
// => 데코레이터는 기능을 덧붙이는 대상 클래스와 같은 조상을 가져야 한다.
//    그리고 생성자에게 대상 객체 주소를 받아야 한다.
//    작업을 수행할 때 대상 객체를 사용한다.
//    그리고 자신만의 기능을 덧붙인다.
public class BufferedInputStream extends InputStream {
  
  InputStream in;
  byte[] buf = new byte[8192];
  int size = 0;
  int cursor = 0;
  int count = 0;
  
  public BufferedInputStream(InputStream in) {
    this.in = in;
  }
  
  public int read() throws IOException {
    if (cursor >= size) { // 버퍼에 보관된 데이터를 다 읽었으면, 
      count++;
      size = in.read(buf); // 다시 FileInputStream을 사용하여 1024 바이트를 읽어 온다.
      if (size == -1) // 물론 파일에 읽은 데이터가 없다면, 즉 다 읽었다면 -1을 리턴한다.
        return -1;
      cursor = 0; // FileInputStream을 사용하여 새로 1024 바이트를 읽어 버퍼에 저장했다면,
    }             // 다시 커서의 위치를 0으로 설정한다.
    
    // 배열에 들어 있는 바이트의 값이 양수일 경우 int로 리턴하면 그 값 그대로 리턴된다.
    // 예) 0x7f(01001111, 10진수로 127) 값을 리턴한다면 
    //     4바이트 int 값 0x0000007f 가 리턴 된다.
    // 문제는 첫 비트가 1로 시작하는 바이트 값을 int로 리턴하는 경우이다.
    // JVM은 첫 비트가 1일 경우 음수 간주하고 
    // byte 값을 int로 바꿀 때 앞의 3바이트를 모두 음수를 의미하는 1로 설정한다.
    // 예) 0x80(10000000, 10진수로 128) 값을 리턴한다면
    //     4바이트 int 값 0xffffff80 이 리턴 된다.
    // 즉 바이트 배열에 들어 있는 값의 첫 비트가 1로 시작할 경우 이런 오류가 발생하는 것이다.
    // 원래의 바이트 값을 온전히 4바이트로 바꿔 리턴하고 싶다면,
    // 앞의 3바이트를 무조건 0으로 만들면 된다.
    // 즉 다음과 같이 4바이트로 변환된 값을 리턴하기 전에
    // 비트 연산자 &를 사용하여 앞의 3바이트를 0으로 설정하라!
    // 예) 0x80 --> 0xffffff80 & 0x000000ff --> 0x00000080
    
    // 현재 커서가 가리키는 위치의 버퍼 항목을 리턴한다.
    // 단 앞의 3바이트는 0으로 설정한 다음에 리턴한다.
    return buf[cursor++] & 0x000000ff; 
  }
  
}





