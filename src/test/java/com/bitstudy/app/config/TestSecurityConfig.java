package com.bitstudy.app.config;


import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserAccountRepository userAccountRepository;

    /* 할일: 실제 DB에 유저 정보가 들어있지만 테스트를 돌릴때는 불러지지 않는다. 그래서 테스트용 가짜 유저정보를 만든다. */
    /* 인증과 관련된 테스트용 계정 생성 메소드를 만들건데 이건 테스트 메소드 실행되기 전에 별도로 JUnit5 에서 이 메서드를 불러오게 해줘야 한다.*/
    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "bitstudyTest", /* 이거 이름 기억해놓기. ArticleCommentTest 에서 테스트 할때 사용함. */
                "pw",
                "bitstudy-test@email.com",
                "bitstudy-test",
                "test memo"
        )));
    }

}
