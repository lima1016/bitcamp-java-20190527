// generalization 기법으로 생성한 수퍼 클래스를 추상 클래스로 선언하기
package ch17.d;

public class Test01 {

  public static void main(String[] args) {
    int[] values = {23, 7, 12, 15, 9, 2, 22, 8, 11, 25, 13, 5};
    
    // => Sorter 클래스를 추상클래스로 선언하면 다음과 같이 직접 사용할 수 없다.
//    display(new Sorter(), values); // 컴파일 오류
    // => Sorter 클래스처럼 서브 클래스에게 공통 필드나 기능을 상속해주는 용도로 만든 클래스인 경우
    // 직접 사용하지 못하게 abstract 클래스로 선언하면
    // 다른 개발자가 잘못 사용할 일을 막을 수 있다.
    
  }

  // 컴파일러는 어떤 인스턴스에 대해서 메소드가 호출할수 있냐없냐 검사하고 
  // 레퍼런스가 어느 타입인지 보고 그클래스에 있는지 보고 super클래스들을 확인함!
  // 있으묜 okay 없으면 nope!
  // 실행시 => JVM
  static void display(Sorter sorter, int[] values) {
    
    // 정렬 객체의 클래스가 뭔지 상관없다.
    // 그 클래스를 사용할 때는 공통 분모가 되는 
    // 수퍼 클래스의 메서드를 호출한다.
    sorter.sort(values);
    // sort()가 존재하는 메소드인지 확인
    // 실행을 할때 호출되는 메소드가 다르다.
    // 컴파일과 실행은 엄연히 다름.
    
    
    // 정렬된 값을 출력한다.
    for (int  value : values) {
      System.out.print(value + ",");
    }
    System.out.println();
  }
}






