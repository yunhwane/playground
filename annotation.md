## 어노테이션 설명 
```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
```
## 불필요한 접근 제한자 노출 방지 
- 기본 생성자를 public으로 노출하지 않고 protected로 설정함으로써, 클래스의 인스턴스를 외부에서 직접 생성하는 것을 방지
- 엄격한 캡슐화를 제공
- JavaBean 규약을 준수하면서 객체 초기화를 더 유연하게 할 수 있음.

## builder 사용시 생성자 위에 사용 
- @Builder 는 class Target 일 경우에 없을 때, 모든 멤버 변수 파라미터로 받는 기본 생성자를 생성, 있으면 따로 생성자를 생성하지 않기 때문에 일치하는 생성자가 없음 따라서 compile 에러 발생

## 해결 코드 
```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member{
    private Long id;
    private String name;
    private Integer age;

    @Builder
    public Member(String name, Integer age){
                this.name = name;
                this.age = age;
    }
}
```

