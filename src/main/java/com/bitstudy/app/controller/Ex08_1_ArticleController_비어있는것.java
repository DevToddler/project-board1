package com.bitstudy.app.controller;


import org.springframework.web.bind.annotation.RequestMapping;

/** 뷰 엔드포인트 관련 컨트롤러
 *
 * 엑셀 api 에 정의해놓은 view 부분의 url.
 	/articles					GET 게시판 페이지
  	/articles/{article-id}		GET 게시판 페이지
  	/articles/search			GET 게시판 검색 전용 페이지
  	/articles/search-hashtag	GET 게시판 해시태그 검색 전용 페이지
 *
 * Thymeleaf : 뷰 파일은 HTML 로 작업할건데, 타임리프를 설치 함으로서 스프링은 이제 HTML 파일을 마크업으로 보지 않고, 타임리프 템플릿 파일로 인식한다.
 * 그래서 이 HTML 파일들은 아무데서나 작성할 수 없고, resources > templates 폴더 안에만 작성 가능하다.
 * 그 외의 css, img, js 들은 resources > static 폴더 안에 작성 가능.
 * */

@RequestMapping("/articles") // 모든 경로들은 /articles 로 시작하니까 클래스 레벨에 1차로 @RequestMapping("/articles") 걸어 둠.
public class Ex08_1_ArticleController_비어있는것 {
	/** BDD 하러가기 */
}
