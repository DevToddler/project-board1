package com.bitstudy.app.config;

import com.bitstudy.app.serucity.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/* 이거 살아있으면 Auditing 겹쳐서 에러남 */
//@Configuration
//@EnableJpaAuditing
public class Ex19_3_JpaConfig {

    // 사람이름 정보 넣어주려고 만드는 config 설정
    @Bean
    public AuditorAware<String> auditorAware() {
/*삭제*/ // return () -> Optional.of("bitstudy");
        // 이렇게 하면 앞으로 JPA Auditing 할 때 마다 사람이름은 이걸로 넣게 된다.
        // TODO : 나중에 스프링 시큐리티로 인증기능 붙일 때 수정 필요.
        
        
/*이 아래 다 추가*/
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                /* 해설:  ofNullable: 일반 객체뿐만 아니라 null값까지 입력으로 받을 수 있다는 뜻. 인증이 안됐을수도 있으니까
                 *       SecurityContextHolder: 모든 인증정보를 가지고 있음
                 *       getContext(): SecurityContextHolder 안에서 시큐리티 컨텍스트를 가져온다.(컨텍스트란 애플리케이션 환경에 대한 인터페이스 이자, 추상 클래스 이다. 즉, 애플리케이션의 현재 상태 를 뜻한다) */
                .map(SecurityContext::getAuthentication)
                /* SecurityContext 는 org.springframework.security.core.context 사용
                 * getAuthentication: 인증 정보를 불러온다. Authentication(인증), Authorization(권한) 이라는 뜻 */
                .filter(Authentication::isAuthenticated) /* isAuthenticated: filter를 이용해서 인증이 됐는지 확인 하는 뜻. 로그인 한 상태인지 보라는거임.  */
                .map(Authentication::getPrincipal) /* Authentication 안에 Principal 이라는 인증정보 가져오라는 뜻 */
                .map(BoardPrincipal.class::cast) /* 아까 만든 BoardPrincipal class 를 불러온다.  */
                .map(BoardPrincipal::getUsername); /* BoardPrincipal 에서 인증받은 유저 이름을 가져온다. */
    }

/* 다 했으면
    인증정보를 이용해서 만들어놓은 기능들을 사용할 수 있게 바꿔줘야 한다.

    test >  config 폴더 생성  > Ex19_4_TestSecurityConfig.java 만들기
*  */


}

