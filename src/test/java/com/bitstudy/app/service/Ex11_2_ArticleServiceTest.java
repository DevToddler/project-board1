package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * 서비스 비즈니스 로직은 슬라이스 테스트 기능 사용 안하고 만들어 볼거다.
 * 스프링부트 어플리케이션 컨셀그스가 뜨는데 걸리는 시간을 없애려고 한다.
 * 디펜던시가 추가돼야 하는 부분에는 Mocking 을 하는 방식으로 한다.
 * 그래서 많이 사용하는 라이브러리가 mokito 라는게 있다.(스프링 테스트 패키지에 내장되어 있음.)
 * @ExtendWith(MockitoExtension.class) 쓰면 된다.
 */
@ExtendWith(MockitoExtension.class)
class Ex11_2_ArticleServiceTest {
	/** Mock 을 주입하는 거에다가 @InjectMocks 을 달아줘야한다. 그 외의 것들에는 @Mock 달아준다.
	 * */
	@InjectMocks
	private ArticleService sut; // sut - system under test. 테스트 짤 때 사용하는 이름 중 하나.

	@Mock
	private ArticleRepository articleRepository; // 의존하는 걸 가져와야 함.(테스트 중간에 mocking 할 때 필요)

	/**테스트할 기능들 정리
	 *  1) 검색
	 *  2) 각 게시글 선택하면 해당 상세페이지로 이동
	 *  3) 페이지네이션  */

	// 1) 검색
	@Test
	@DisplayName("검색어 없이 게시글 검색하면, 게시글 리스트를 반환")
	void withoutKeywordReturnArticlesAll(){
		// Given - 페이지 기능을 넣기
		Pageable pageable = Pageable.ofSize(20); // 한페이지에 몇 개 가져올건지 결정
		given(articleRepository.findAll(pageable)).willReturn(Page.empty());
		/** Pageable- org.springframework.data.domain
		 *  given - org.mockito.BDDMockito*/

		// When - 검색어 입력 없는 경우
		Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

		// Then
		assertThat(articles).isEmpty();
		then(articleRepository).should().findAll(pageable);
	}

	@Test
	@DisplayName("검색어로 게시글 검색하면, 게시글 리스트를 반환")
	void withKeywordReturnArticlesAll(){
		// Given -
		SearchType searchType = SearchType.TITLE;
		String searchKeyword = "title";
		Pageable pageable = Pageable.ofSize(20);
		given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

		// When -
		Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

		// Then
		assertThat(articles).isEmpty();
		then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
	}

	// 게시글 하나 호출
	@Test
	@DisplayName("게시글 선택하면, 게시글(하나) 반환")
	void selectedArticleReturnArticleOne(){
		// Given -
		Article article = createArticle();
		Long articleId = 1L;
		given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

		// When -
		ArticleWithCommentsDto dto = sut.getArticle(articleId);

		// Then
		assertThat(dto)
				.hasFieldOrPropertyWithValue("title", article.getTitle())
				.hasFieldOrPropertyWithValue("content", article.getContent())
				.hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
		then(articleRepository).should().findById(articleId);
	}

	// 게시글 생성
	@Test
	@DisplayName("게시글 정보 입력하면, 게시글(하나) 생성")
	void givenGetArticleInfoWhenCreateArticleOne(){
		// Given -
		ArticleDto dto = createArticleDto();
		given(articleRepository.save(any(Article.class))).willReturn(createArticle());

		// When -
		sut.saveArticle(dto);

		// Then
		then(articleRepository).should().save(any(Article.class));
	}

	// 게시글 수정
	@Test
	@DisplayName("게시글 수정 정보 입력하면, 게시글 수정")
	void givenModifiedArticleInfoWhenUpdateArticleOne(){
		// Given -
		/** save는 insert,update를 돌리기 전에 select를 먼저 돌린다 */
		/** dto.id() 에서 id()는 record 에서의 getter 라고 보면 된다. */
		ArticleDto dto = createArticleDto("title", "content", "#java");
		Article article = createArticle();

		given(articleRepository.getReferenceById(dto.id())).willReturn(article);


		// When -
		sut.updateArticle(dto);

		// Then
		assertThat(article)
				.hasFieldOrPropertyWithValue("title", dto.title())
				.hasFieldOrPropertyWithValue("content", dto.content())
				.hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
		then(articleRepository).should().getReferenceById(dto.id());
	}

	// 게시글 삭제
	@Test
	@DisplayName("게시글 삭제")
	void givenArticleIdWhenDeleteArticleOne(){
		// Given -
		Long articleId = 1L;
		willDoNothing().given(articleRepository).deleteById(articleId);

		// When -
		sut.deleteArticle(articleId);

		// Then
		then(articleRepository).should().deleteById(articleId);

	}


/******************************************************************************/
	private UserAccount createUserAccount(){
		return UserAccount.of("bitstudy", "password", "bitstudy@email.com", "bitstudy", null);
	}

	private Article createArticle(){
		return Article.of(createUserAccount(), "title", "content", "#java");
	}
	private ArticleDto createArticleDto() {
		return createArticleDto("title", "content", "#java");
	}
	private ArticleDto createArticleDto(String title, String content, String hashtag) {
		return ArticleDto.of(
				1L,
				createUserAccountDto(),
				title,
				content,
				hashtag,
				LocalDateTime.now(),
				"bitstudy",
				LocalDateTime.now(),
				"bitstudy"
		);
	}
	private UserAccountDto createUserAccountDto() {
		return UserAccountDto.of(
				1L,
				"bitstudy",
				"password",
				"bitstudy@email.com",
				"bitstudy",
				"memomemo",
				LocalDateTime.now(),
				"bitstudy",
				LocalDateTime.now(),
				"bitstudy"
		);
	}



}