package com.bitstudy.app.controller;


import com.bitstudy.app.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 로그인 페이지 기능 테스트
    로그인 페이지는 스프링 시큐리티와 부트가 만들어서 준거다. 한마디로 이미 테스트가 다 검증이 되서 우리가 별도로 테스트할건 없다. 그래서 최소한의 테스르만 할거다. (저 기능이 우리 서비스에 존재하는지만 확인하면 됨.)
    그래서 별도로 실제 AuthController 파일이 존재하지 않아도 된다.

    테스트 시나리오: get("/login") 로 페이지를 날렸을때 상태코드가 200 나오면 테스트 통과
 */

@Import(SecurityConfig.class) /* ArticleControllerTest 와 동일한 환경에서 테스트 하기 위해서 이거 넣음 */
@WebMvcTest
public class AuthControllerTest {

    private final MockMvc mvc;

    public AuthControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }


    @Test
    @DisplayName("[view][GET] 로그인 페이지 - 정상호출")
    public void loginPass() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }
}
