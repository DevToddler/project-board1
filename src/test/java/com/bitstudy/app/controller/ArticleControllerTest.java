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

/**
 * 지금 상태로 테스트 돌리면 콘솔창에 401이 뜬다. (401 : 파일은 찾았는데 인증을 못 받아서 못들어간다 라는 뜻
 * 이유는 기본 웹 시큐리티를 불러와서 그런것 이다.
 * config > SecurityConfig 를 읽어오게 하면 된다.
 */

//@WebMvcTest // 이렇게만 쓰면 모든 컨트롤러를 다 읽어들인다. 아래처럼 필요한 클래스만 bean으로 읽어오기
@WebMvcTest(ArticleController.class)
@DisplayName("view 컨트롤러 - 게시글")
@Import(SecurityConfig.class) // 시큐리티 config 임포트 해주기
class ArticleControllerTest {

	private final MockMvc mvc;

	public ArticleControllerTest(@Autowired MockMvc mvc) {this.mvc = mvc;}


	//	1) 게시판 리스트 페이지
	@Test
	@DisplayName("[view][GET] 게시판 리스트 페이지 - 정상호출")
	public void articleAll() throws Exception {
		mvc.perform(get("/articles")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andExpect(view().name("articles/index")).andExpect(model().attributeExists("articles"));
	}

	//	2) 게시글 상세 페이지
	@Test
	@DisplayName("[view][GET] 게시글 상세페이지 - 정상호출")
	public void articlesOne() throws Exception {
		mvc.perform(get("/articles/1")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andExpect(view().name("articles/detail")).andExpect(model().attributeExists("article")).andExpect(model().attributeExists("articleComments")); // 상세 페이지에는 댓글들도 같이 오니까 확인.
	}

	//	3) 게시판 검색 전용 페이지
	@Test
	@DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상호출")
	public void articlesSearch() throws Exception {
		mvc.perform(get("/articles/search")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andExpect(view().name("articles/search"));
	}

	//	4) 해시태그  검색 전용 페이지
	@Test
	@DisplayName("[view][GET] 게시글 해시태그 검색 전용 페이지 - 정상호출")
	public void articlesSearchHashtag() throws Exception {
		mvc.perform(get("/articles/search-hashtag")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andExpect(view().name("articles/search-hashtag"));
	}
}