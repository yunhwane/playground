## Transaction의 필요성

만약, DB 데이터 수정 시에 예외 발생이 된다면 DB의 데이터들은 수정이 되기 전에 다시 돌아가야되며, 돌아간 뒤에 다시 수정 작업이 필요할 것이다. 따라서, **여러 작업 시 문제가 생겼을 경우 이전 상태로 Rollback 하기 위해 사용되는 것이 Transaction이다.** 

> 더이상 쪼갤 수 없는 최소 작업 단위를 말한다.
> 

### commit

작업이 마무리 된 상태

### rollback

작업을 취소하고 이전의 상태로 되돌리기 위한 작업 

## Spring이 제공하는 Transaction 핵심 기술

- 동기화
- 추상화
- AOP

### 동기화

- 트랜잭션을 시작하기 위한 Connection 객체를 특별한 저장소에 보관하고, 필요할 때 꺼내 쓸 수 있도록 하는 기술
- 트랜잭션 동기화 저장소는 작업 쓰레드 마다 Connection 객체를 독립적으로 관리하기 때문에 멀티 쓰레드 환경에서도 충돌 여지가 없다.

```java
//동기화 진행하기 
TranscationSynchronizeManager.initSynchronization();
Connection c = DataSourceUtils.getConnection(dataSource);

//작업 진행

//동기화 종료시키기
DataSourceUtils.releaseConnection(c, dataSource);
TransactionSynchronizeManager.unbindResource(dataSource);
TransactionSynchronizeManager.clearSynchronization()
```

- JDBC 가 아닌 Hibernate 기술을 쓴다면, Connection 객체에 종속적인 트랜잭션 동기화 코드는 문제가 생기게 된다.

Hibernate 에서는 Connection 객체가 아닌 Session 객체를 사용하기 때문이다. 따라서 이러한 문제를 해결하기 위한 추상화 기술이 있다. 

### 추상화

- 트랜잭션 기술의 공통점을 담은 추상화 기술
- 종속적인 코드 이용하지 않고, 일관되게 트랜잭션 관리 가능

```java
public Object invoke(MethodInvoation invoation) throws Throwable {
	TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
	
	try {
		Object ret = invoation.proceed();
		this.transactionManager.commit(status);
		return ret;
	} catch (Exception e) {
		this.transactionManager.rollback(status);
		throw e;
	}
}
```

여기서 트랜잭션 관리 코드들이 비즈니스 로직과 함께 쓰인다.

개발자는 비즈니스 로직에 집중하고 싶고, 중복 코드를 없앨 수 있는 방법이 무엇일까 ? 

Spring AOP를 이용하여 트랜잭션 부분을 핵심 비즈니스 로직과 분리하는 방법이 있다. 

### AOP를 이용한 트랜잭션 분리

```java
public void addUsers(List<User> userList) {
	TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
	
	try {
		for (User user: userList) {
			if(isEmailNotDuplicated(user.getEmail())){
				userRepository.save(user);
			}
		}

		this.transactionManager.commit(status);
	} catch (Exception e) {
		this.transactionManager.rollback(status);
		throw e
	}
}
```

위의 코드를 보면, User를 등록하는 서비스 코드에서 어떻게 트랜잭션 코드를 핵심 비즈니스 로직과 분리 시킬 것인가라고 생각한다면, AOP를 이용하여 중심점과 횡단점을 분리시키는 코드를 적용하는 것이 올바를 것이다. 따라서 이를 적용한 @Transactional 어노테이션을 지원한다. 

```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public void addUsers(List<User> userList) {
        for (User user : userList) {
            if (isEmailNotDuplicated(user.getEmail())) {
                userRepository.save(user);
            }
        }
    }
}
```

## Spring 트랜잭션 세부 설정하기

- Spring의 DefaultTransactionDefinition이 구현하고 있는 TransactionDefinition 인터페이스는 트랜잭션 동작방식에 영향을 줄 수 있는 4가지 속성을 정의할 수 있다.

---

트랜잭션에서는 시작과 끝나는 위치를 정해야하고, 시작되고 끝날 범위를 트랜잭션 경계라고 한다. 

## 트랜잭션 전파

- 트랜잭션 전파란 트랜잭션의 경계에서 이미 진행중인 트랜잭션이 있거나 없을 때, 어떻게 동작할 것인가를 결정하는 방식을 의미한다.

### **A의 트랜잭션에 참여(PROPAGATION_REQUIRED)**

B의 코드는 새로운 트랜잭션을 만들지 않고 A에서 진행중이 트랜잭션에 참여할 수 있다. 이 경우 B의 작업이 마무리 되고 나서, 남은 A의 작업(2)을 처리할 때 예외가 발생하면 A와 B의 작업이 모두 취소된다. 왜냐하면 A와 B의 트랜잭션이 하나로 묶여있기 때문이다.

### 독립적인 트랜잭션 생성(PROPAGATION_REQUIRES_NEW)

반대로 B의 트랜잭션은 A의 트랜잭션과 무관하게 만들 수 있다. 이 경우 B의 트랜잭션 경계를 빠져나오는 순간 B의 트랜잭션은 독자적으로 커밋 또는 롤백되고, 이것은 A에 어떠한 영향도 주지 않는다. 즉, 이후 A가 (2)번 작업을 하면서 예외가 발생해 롤백되어도 B의 작업에는 영향을 주지 못한다.

### 트랜잭션 없이 동작(PROPAGATION_NOT_SUPPORTED)

B의 작업에 대해 트랜잭션을 걸지 않을 수 있다. 만약 B의 작업이 단순 데이터 조회라면 굳이 트랜잭션이 필요 없을 것이다.

## 격리 수준

- 모든 데이터베이스 트랜잭션은 격리수준을 가지고 있어야한다.  서버에서 여러개 트랜잭션을 동시 진행이 가능한데 트랜잭션을 독립적으로 만들고 순차 진행하면 안전하겠지만 성능이 떨어질 수 있다. 따라서 적절히 격리 수준을 정하여 트랜잭션을 조준하여 문제가 발생하지 않도록 제어해야한다.

## 제한 시간

- 트랜잭션 수행 시간을 제한할 수 있다.
- PROPAGATION_REQUIRED나 PROPAGATION_REQUIRES_NEW의 경우에 사용해야만 의미가 있다.

## 읽기 전용

- 읽기 전용 설정하면 트랜잭션 내에서 데이터를 조작하는 시도를 막을 수 있고, 액세스 기술에 따라 성능이 향상될 수 있다.
