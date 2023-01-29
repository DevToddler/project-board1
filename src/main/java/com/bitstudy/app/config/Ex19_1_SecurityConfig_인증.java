package com.bitstudy.app.config;

import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.UserAccountRepository;
import com.bitstudy.app.serucity.BoardPrincipal;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*이거 살아있으면 서비스 돌릴때 원본파일이랑 겹쳐서 에러남*/
//@Configuration
public class Ex19_1_SecurityConfig_인증 {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

/* 삭제 */
//        return http
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // HttpSecurity 의 authorizeHttpRequests 에서 모든 요청(anyRequest() 부분)이 인증을 허용(permitAll() 부분)하겠다
//                .formLogin() // 로그인 페이지를 만들고
//                .and().build(); // 빌드 해라
////////////////////////////////////////////////////////////////////////////////

/*추가*/
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(  /* mvcMatchers: 스프링에 패턴 매칭 기능이 들어간 메서드. 컨트롤러에서 맵핑할때 "/articles/** /form" 이런식으로 경로 설정할때도 있는데 그런 특정 경로를 지정해서 권한을 설정할 수도 있게 하는 메서드임. */
                                HttpMethod.GET, /* 특정 경로를 지정 하는 부분임. - GET 방식, 루트페이지, 게시판리스트 페이지 는  */
                                "/",
                                "/articles"
                        ).permitAll()  /* 권한을 허용하고 */
                        .anyRequest().authenticated() /* 그 외의 경로로 접근할때는 어떤 요청이든 authentication(인가) 과정을 거치도록 한다 */
                )
                .formLogin().and() // formLogin() : 로그인 페이지를 만들어준다.
                .logout()
                .logoutSuccessUrl("/") /* 로그아웃을 성공하면 "/" 이 경로로 이동해라 라는뜻*/
                .and()
                .build();

    }

    /* 사용자 정보 가져오는 부분 - 실제 인증 데이터를 가져오는 서비스 로직을 가져온다. */
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) { /* DB에 있는 user 정보를 주입 받아야 하니까 매개변수로 userAccountRepository 를 받는다.  */
        return username -> userAccountRepository
                .findById(username)/* username 으로 유저 한명 찾고*/
                .map(UserAccountDto::from)/* 맵핑해서 username 으로 찾은 결과를 dto 로 바꿈 */
                .map(BoardPrincipal::from) /* BoardPrincipal: 사용자 정보를 가져올 수 있는 record. userDetail 을 받아옴 */
                // Ex19_2_BoardPrincipal 하고 오기
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username)); /* 혹시라도 인증된 사용자를 못찾을때에 대한 대안으로 UsernameNotFoundException 사용함.*/
    }

    /*추가 - 지금 바로는 안씀. 일단 가지고 있기. 패스워드 인코더임.  */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();


/* *********************************************************************** */
/* *********************************************************************** */
/* *********************************************************************** */
/* 엄청중요함. 이거 안하면 security 다 해도 로그인 할때 에러남.*/
        // createDelegatingPasswordEncoder 이거 Ctrl + B 로 들어가보면 어떤 방식으로 할 수 있는지 다 나온다.
        // 나중에 data.sql 에 비번 부분에 '{암호화방식}암호' 형식으로 넣으면 된다.
        // ex) {noop}asdf
/* *********************************************************************** */
/* *********************************************************************** */
/* *********************************************************************** */
    }
}

/* 다 하면 브라우저에서 실행해보기.

 주의: 게시판 페이지 들어가는 주소는 괜찮음.
        다만 거기서 글을 눌렀을때 로그인 화면 나와야 성공하는거임.

 다 하면 Ex19_3_JpaConfig 가기
    */
