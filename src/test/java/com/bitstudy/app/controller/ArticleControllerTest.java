package com.bitstudy.app.controller;

import com.bitstudy.app.config.SecurityConfig;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

	@MockBean
	private ArticleService articleService;
	@MockBean
	private PaginationService paginationService;
	/** @MockBean : 테스트 시 테스트에 필요한 객체를 bean으로 등록시켜서 기존 객체 대신 사용할 수 있게 만들어 준다.
	 *   ArticleController 에 있는 private final ArticleService articleService; 부분의 articleService 을 배제하기 위해서
	 *   @MockBean 을 사용한다. 이유는 MockMvc 가 입출력 관련된 것들만 보게 하기 위해서 서비스 로직을 끊어주기 위해서 사용. (속도) */

		public ArticleControllerTest(@Autowired MockMvc mvc) {this.mvc = mvc;}


	//	1) 게시판 리스트 페이지
	@Test
	@DisplayName("[view][GET] 게시판 리스트 페이지 - 정상호출")
	public void articleAll() throws Exception {
		/** searchKeyword 없을 때 검색어 없이 들어갈 거라서 */
		given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());

		/** 페이지네이션 파트 추가 */
		given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));

		mvc.perform(get("/articles"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("articles/index"))
				.andExpect(model().attributeExists("articles"))
				.andExpect(model().attributeExists("paginationBarNumbers"));


		then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
		then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
	}

	//	2) 게시글 상세 페이지
	@Test
	@DisplayName("[view][GET] 게시글 상세페이지 - 정상호출")
	public void articlesOne() throws Exception {

		Long articleId = 1L;

		long totalCount = 1L;

		given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

		given(articleService.getArticleCount()).willReturn(totalCount);

		mvc.perform(get("/articles/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("articles/detail"))
				.andExpect(model().attributeExists("article"))
				.andExpect(model().attributeExists("articleComments")) // 상세 페이지에는 댓글들도 같이 오니까 확인.
				.andExpect(model().attributeExists("totalCount"));


		then(articleService).should().getArticle(articleId);
		then(articleService).should().getArticleCount();
	}

//	//	3) 게시판 검색 전용 페이지
//	@Disabled("구현중")
//	@Test
//	@DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상호출")
//	public void articlesSearch() throws Exception {
//		mvc.perform(get("/articles/search")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andExpect(view().name("articles/search"));
//	}
//
//	//	4) 해시태그  검색 전용 페이지
//	@Disabled("구현중")
//	@Test
//	@DisplayName("[view][GET] 게시글 해시태그 검색 전용 페이지 - 정상호출")
//	public void articlesSearchHashtag() throws Exception {
//		mvc.perform(get("/articles/search-hashtag"))
//				.andExpect(status().isOk())
//				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
//				.andExpect(view().name("articles/search-hashtag"));
//	}




	/* *************************************************************************** */
	private ArticleWithCommentsDto createArticleWithCommentsDto(){
		return ArticleWithCommentsDto.of(
				1L,
				createUserAccountDto(),
				Set.of(),
				"title",
				"content",
				"#java",
				LocalDateTime.now(),
				"bitstudy",
				LocalDateTime.now(),
				"bitstudy"
		);
	}
	private UserAccountDto createUserAccountDto(){
		return UserAccountDto.of(
				1L,
				"bitstudy",
				"password",
				"bitstudy@email.com",
				"bitstudy",
				"memo",
				LocalDateTime.now(),
				"bitstudy",
				LocalDateTime.now(),
				"bitstudy"
		);
	}


}