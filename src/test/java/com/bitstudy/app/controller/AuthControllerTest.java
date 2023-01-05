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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 로그인 페이지 기능 테스트
 	로그인 페이지는 스프링 시큐리티와 부트가 만들어서 준것이다. 이미 테스트가 다 검증이 돼서 우리가 별도로 테스트할 건 없다.
 	그래서 최소한의 테스트만 할 것이다. (해당 기능이 우리 서비스에 존재하는지만 확인하면 된다)
 	별도로 실제 AuthController 파일이 존재하지 않아도 된다.

 	get("/login") 로 상태코드가 200 나오면 테스트 통과

 */
@WebMvcTest
@Import(SecurityConfig.class)
public class AuthControllerTest {

	private final MockMvc mvc;

	public AuthControllerTest(@Autowired MockMvc mvc) {this.mvc = mvc;}

	@Test
	@DisplayName("[view][GET] 로그인 페이지 - 정상호출")
	public void loginPass() throws Exception {
		mvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
	}
}
