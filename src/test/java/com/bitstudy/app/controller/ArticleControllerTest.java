package com.bitstudy.app.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 하기 전에 알아둘 것 : 이 테스트 코드를 작성하고 돌리면 결과적으로는 404 에러난다.
 *  이유는 아직 ArticleController 에 작성된 내용이 없고, dao 같은 것들도 없기 때문이다.
 *
 *  우선 작성하고 실제 코드(ArticleController)와 연결되는지 확인.
 *
 *  슬라이스 테스트 방식으로 테스트 할거임
 * */

/** autoConfiguration 을 가져올 필요가 없기 때문에 슬라이스 테스트 사용 가능 */
//@WebMvcTest // 이렇게만 쓰면 모든 컨트롤러를 다 읽어들인다. 아래처럼 필요한 클래스만 bean으로 읽어오기
@WebMvcTest(ArticleController.class)
@DisplayName("view 컨트롤러 - 게시글")
class ArticleControllerTest {

	private final MockMvc mvc;


	public ArticleControllerTest(@Autowired MockMvc mvc) {this.mvc = mvc;}

	/**테스트는 엑셀 api 순서대로 만들거다

	 * 엑셀 api 에 정의해놓은 view 부분의 url.
	 	/articles					GET 게시판 페이지
	 	/articles/{article-id}		GET 게시판 페이지
	 	/articles/search			GET 게시판 검색 전용 페이지
	 	/articles/search-hashtag	GET 게시판 해시태그 검색 전용 페이지
	 */

//	1) 게시판 리스트 페이지
	@Test
	@DisplayName("[view][GET] 게시판 리스트 페이지 - 정상호출")
	public void articleAll() throws Exception {
		mvc.perform(get("/articles"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("articles/index"))
				.andExpect(model().attributeExists("articles"));

		/** 설명 */
		/**
		 ### content().contentTypeCompatibleWith(MediaType.TEXT_HTML) ###
		 	뷰를 html로 코드를 짜고 있을것이다. /articles 로 받아온 데이터의 미디어 타입이 html 타입으로 되어있는지 확인
		  	contentType 의 경우 exact match 라서 미디어타입이 딱 text/html 로 나오는 것만 허용하기 때문에
		  	contentTypeCompatibleWith 을 이용해서 호환되는 타입까지 맞다고 해주는 것.

		 ### view().name("articles/index") ###
		 	가져온 뷰 파일명이 index 인지 확인.

		 ### model().attributeExists("articles") ###
		 	이 뷰에서는 게시글 들이 떠야 하는데, 이는 서버에서 데이터들을 가져왔다는 말이다. 그러면 모델 어트리뷰트로 데이터를 밀어넣어줬다는 말인데 그게 있는지 없는지 확인.
		 	articles는 개발자가 임의로 걸어주는 키값, 맵에 articles라는 키가 있는지 검색해라 라는 뜻.

		 * */
	}


	//	2) 게시글 상세 페이지
	@Test
	@DisplayName("[view][GET] 게시글 상세페이지 - 정상호출")
	public void articlesOne() throws Exception {
		mvc.perform(get("/articles/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("articles/detail"))
				.andExpect(model().attributeExists("article"))
				.andExpect(model().attributeExists("articleComments")); // 상세 페이지에는 댓글들도 같이 오니까 확인.
	}

	//	3) 게시판 검색 전용 페이지
	@Test
	@DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상호출")
	public void articlesSearch() throws Exception {
		mvc.perform(get("/articles/search"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("articles/search"));
	}

	//	4) 해시태그  검색 전용 페이지
	@Test
	@DisplayName("[view][GET] 게시글 해시태그 검색 전용 페이지 - 정상호출")
	public void articlesSearchHashtag() throws Exception {
		mvc.perform(get("/articles/search-hashtag"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("articles/search-hashtag"));
	}
}