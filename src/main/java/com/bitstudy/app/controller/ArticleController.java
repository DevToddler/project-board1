package com.bitstudy.app.controller;


import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.dto.response.ArticleWithCommentsResponse;
import com.bitstudy.app.service.ArticleService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.TypeCache;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
@RequiredArgsConstructor // 필수 필드에 대한 생성자 자동 생성. 초기화 되지 않은 final 필드 또는 @NonNull이 붙은 필드에 대해 생성자 생성해주는 롬복 애너테이션
@Controller
@RequestMapping("/articles") // 모든 경로들은 /articles 로 시작하니까 클래스 레벨에 1차로 @RequestMapping("/articles") 걸어 둠.
public class ArticleController {

	private final ArticleService articleService;
	/** @RequiredArgsConstructor 로 만들어진 생성자를 읽어서 정보의 전달을 할 수 있게 한다. */

	/** 게시판 목록 페이지*/
	@GetMapping
	public String articles(
			@RequestParam(required = false) SearchType searchType,
			@RequestParam(required = false) String searchValue,
			@PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
			/** 설명 :
			 	@RequestParam : 검색어를 받아야 한다. @RequestParam 을 이용해서 getParameter 를 불러올거고, 반드시 있지 않아도 된다.(=> (required = false))
				@PageableDefault : 페이징 기본 설정 (한 페이지에 10개, 작성일 기준, 내림차순)
			 */
			ModelMap map
			/** Model 은 인터페이스, ModelMap 은 클래스(구현체) */) {


		// map.addAttribute("articles", List.of()); // 키 : articles, 값: 그냥 list
		map.addAttribute("articles", articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from));
		// 진짜로 정보를 넣어줘야 하니까 ArticleService.java 에 만들어놓은 searchArticles() 메서드에 값을 넣어주면 된다.
		// 그런데 searchArticles()의 반환 타입은 Dto 인데 Dto는 모든 엔티티의 데이터를 다 다루고 있어서 그걸 한번 더 가공해서 필요한 것들만 가지고 있는 ArticleResponse를 사용한다.

		return "articles/index";
	}

	/** 게시글 상세 페이지*/
	@GetMapping("/{articleId}")
	public String article(@PathVariable Long articleId, ModelMap map) {
		ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
		map.addAttribute("article", article);
		map.addAttribute("articleComments", article.articleCommentsResponse());
		// article.articleCommentsResponse() 해설: 현재 article에 ArticleCommentsResponse 의 정보가 담겨있으니까 article 안에 있는 articleComments를 꺼내면 된다.
		return "articles/detail";
	}
	
	
}
