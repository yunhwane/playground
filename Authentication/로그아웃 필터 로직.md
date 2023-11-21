## JWT RefreshToken 
- 슬라이딩 세션이라고도 불리우는데, 엑세스 토큰 만료기간을 짧게 가지며, RefreshToken을 활용하여 재발급하는 인증/ 인가 방식이다.이때 JWT의 리프레쉬토큰은 Stateful토큰이라는점.

# 연습하면서 만들어본 과정 프로세스 

1. #### 로그인 요청 및 인증

- 사용자가 로그인 시도 시, 클라이언트는 사용자 ID와 비밀번호를 서버로 전송합니다.
- 서버는 전달받은 정보를 기반으로 사용자를 인증하고, 유효한 경우 JWT( JSON Web Token)을 생성합니다.

2. #### JWT 토큰 발급

- 서버에서 사용자 인증이 성공하면, 서버는 유저를 위한 JWT 토큰을 생성합니다.
- 이 토큰은 클라이언트에게 안전하게 전송됩니다.

3. #### 요청 시 토큰 전송

- 클라이언트가 서버로부터 받은 JWT 토큰을 가지고 특정 기능을 수행하고자 할 때, 요청에 토큰을 첨부하여 서버로 전송합니다.

4. #### 토큰 관리 및 보안

- 서버는 Refresh Token을 Redis의 key 값으로, Access Token을 Redis의 value 값으로 저장합니다.
- 이로써 클라이언트에서 보내온 Access Token이 유효한지 확인합니다. 동시에, 클라이언트가 로그아웃하여 블랙리스트에 추가된 토큰인지 확인합니다.

5. #### 토큰 검증 및 권한 부여

- 서버는 클라이언트로부터 받은 Access Token이 Redis에 저장된 Refresh Token과 매핑되어 있는지 확인합니다.
- 또한, 해당 토큰이 블랙리스트에 있는지, 그리고 토큰이 유효한지 여부를 검증합니다.
- 이러한 인증 절차가 완료되면, 클라이언트의 요청에 대한 결과를 제공합니다.

6. #### 인증 완료 및 결과 제공
- 모든 인증 과정이 완료되면, 서버는 클라이언트에게 요청된 작업의 결과를 제공합니다. 이 과정에서 사용자의 권한과 인증 정보가 고려됩니다.

## Redis 설정하기
```java
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(host,port);
    }


    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        // 아래 두 라인을 작성하지 않으면, key값이 \xac\xed\x00\x05t\x00\x03sol 이렇게 조회된다.
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

}
```
- 필자는 템플릿을 받아 사용했다. JpaRepositoy 사용도 가능하다.

### 로그아웃 시 redis에 처리할 service

```java
@Service
public class BlacklistTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_KEY = "jwt:blacklist";

    public BlacklistTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addInvalidToken(String token) {
        redisTemplate.opsForSet().add(BLACKLIST_KEY, token);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.opsForSet().isMember(BLACKLIST_KEY, token);
    }
    
}
```
- Redis set으로 key값은 jwt:blacklist 

```redis
SMEMBERS key
```

### 로그아웃 처리와 블랙리스트 처리 로직을 Custom filter에 구현

```java
 public CustomLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, JwtService jwtService, BlacklistTokenService blacklistTokenService) {
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.jwtService = jwtService;
        this.blacklistTokenService = blacklistTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresLogout(request,response)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // 로그아웃 로직을 추가하고 로그아웃 처리

            if (auth != null) {
                String token = jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).orElse(null);
                // 로그아웃 처리
                if(token != null){
                    // 블랙리스트 처리
                    blacklistTokenService.addInvalidToken(token);
                }
                logoutSuccessHandler.onLogoutSuccess((HttpServletRequest) request, (HttpServletResponse) response, auth);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
```
### 로그아웃 성공 핸들러 구현
```java
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Logout Success");
        response.getWriter().flush();
    }
}
```
### Security Config Bean 등록
```java
       ....
           
           .and()
                .addFilterBefore(customLogoutFilter(),LogoutFilter.class)
                .addFilterBefore(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);
                
                ...
                
    @Bean
    public CustomLogoutFilter customLogoutFilter(){
        CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(logoutSuccessHandler(),jwtService,blacklistTokenService);
        return customLogoutFilter;
    }
    
    @Bean
    public CustomLogoutSuccessHandler logoutSuccessHandler(){
        return new CustomLogoutSuccessHandler();
    }
```
1. customLogoutFilter를 LogoutFilter 이전에 추가
2. jsonUsernamePasswordLoginFilter 도 custom한 login filter이며, LogoutFilter 이전에 추가
3. jwt인증 필터는 jsonUsernamePasswordLoginFilter 이전에 추가


## resfresh token 로그아웃 시 redis에 만료 후 조회하며 인증을 막는 로직을 짜면 된다. 
![다운로드](https://github.com/yunhwane/playground/assets/147581818/a5a7afc1-30da-4927-b6a8-8e3fbed10a74)



