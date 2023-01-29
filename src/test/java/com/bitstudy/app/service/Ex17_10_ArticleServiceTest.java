package com.bitstudy.app.service;


import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.ArticleRepository;
import com.bitstudy.app.repository.UserAccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/** 할일: ("게시글의 ID를 입력하면, 게시글을 삭제한다") 부분에 userId 관련 추가  * */

@ExtendWith(MockitoExtension.class)
class Ex17_10_ArticleServiceTest {
    /** Mock을 주입하는 거에다가 @InjectMocks 을 달아줘야 한다. 그 외의 것들 한테는 @Mock 달아준다. */
    @InjectMocks
    private Ex17_9_ArticleService sut; // sut - system under test. 테스트 짤때 사용하는 이름중 하나. 이건 테스트 대상이다 라는 뜻

    @Mock
    private ArticleRepository articleRepository; // 의존하는걸 가져와야 함. (테스트 중간에 mocking 할때 필요)

/*추가 - 게시글에서는 사용자 정보도 같이 다룰거임. */
    @Mock private UserAccountRepository userAccountRepository;
    
    /** 테스트 할 기능들 정리
     * 1. 검색
     * 2. 각 게시글 선택하면 해당 상세 페이지로 이동
     * 3. 페이지네이션  */

    /* 1. 게시판 페이지로 이동 */
    @DisplayName("검색어 없이 게시글 검색하면, 게시글 리스트를 반환 한다.")
    @Test
    void withoutKeywordReturnArticlesAll() {
        // Given - 페이지 기능을 넣기
        Pageable pageable = Pageable.ofSize(20); // 한페이지에 몇개 가져올건지 결정
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        /** Pageable - org.springframework.data.domain
         *  given - org.mockito.BDDMockito */

        // When - 입력 없는지(null) 실제 테스트 돌리는 부분
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }


    @DisplayName("검색어 이용해서 게시글 검색하면, 게시글 리스트를 반환 한다.")
    @Test
    void withKeywordReturnArticlesAll() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    /* 2. 게시글 페이지로 이동 */
    @DisplayName("게시글 ID로 조회하면, 게시글을(하나) 반환한다.")
    @Test
    void selectedArticleReturnArticleOne() {
        // Given
        Article article = createArticle();
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
/*삭제*/ //ArticleWithCommentsDto dto = sut.getArticle(articleId);
/*추가*/ ArticleDto dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

/*추가*/
    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = Assertions.catchThrowable(() -> sut.getArticle(articleId));
                    // catchThrowable() : () 안에 예외가 발생시킬 코드를 넣는다.

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }


    /*추가*/
    @DisplayName("게시글 ID로 조회하면, 댓글 달긴 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }


/* -------------------------------------------------------------------------------- */
/* 여기서부터 변화 있음. - 모든 게시글 생성, 수정, 삭제 에서는 사용자 정보를 같이 가지고 다닐거임. */
/* -------------------------------------------------------------------------------- */
    /* 3. 게시글 생성  */
    @DisplayName("게시글 정보 입력하면, 게시글(하나) 생성한다")
    @Test
    void givenGetArticleInfoWhenCreateArticleOne() {
        // Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());
/*추가*/ given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount()); // UserAccountRepository.java 가서 <UserAccount, Long> 을 <UserAccount, String> 으로 바꿈


        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
/*추가*/ then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());// 게시글정보 입력시 게시글 생성 여부 테스트 // UserAccountRepository.java 가서 <UserAccount, Long> 을 <UserAccount, String> 으로 바꿈

    }
    
    /* 4. 게시글 수정 */
    @DisplayName("게시글 수정 정보 입력하면, 게시글(하나) 수정한다")
    @Test
    void givenModifiedArticleInfoWhenUpdateArticleOne() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("title", "content", "#java");

        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        // dto.id() 는 getId() 이다.
        // dto가 record 이기 때문에 별도로 getter 를 만들 필요가 없다.
        // 대신 이걸 불러다 쓸때에는 일반필드처럼 가져다 쓰면 된다.

/*추가*/ //given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());
/*해석: userAccountRepository에서 getReferenceById를 통해 사용자의 Id값을 가져(참조)오고,
        userAccountDto의 toEntity로 데이터를 전송할 것이다.*/

        // When
/*삭제*/ //sut.updateArticle(dto);
/*추가*/ sut.updateArticle(dto.id(), dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title",dto.title())
                .hasFieldOrPropertyWithValue("content",dto.content())
                .hasFieldOrPropertyWithValue("hashtag",dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
/*추가*/ then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("title", "content", "#java");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
/*삭제*/ //sut.updateArticle(dto);
/*추가*/ sut.updateArticle(dto.id(), dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }


    /* 5. 게시글 삭제*/
    @DisplayName("게시글 ID 입력하면, 게시글(하나) 삭제한다")
    @Test
    void givenArticleIdWhenDeleteArticleOne() {
        // Given
        Long articleId = 1L;
/*추가*/ String userId = "bitstudy";

/*삭제*///willDoNothing().given(articleRepository).deleteById(articleId);
/*변경*/ willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId, userId);

        // When
/*삭제*/ sut.deleteArticle(articleId);
///*변경*/ sut.deleteArticle(articleId, userId);

        // Then
/*삭제*/then(articleRepository).should().deleteById(articleId);
/*변경*/// then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId, userId);
    }


    @DisplayName("게시글 수 조회하면, 게시글 수 반환")
    @Test
    void givenNoting_thenReturnArticleCount() {
        // Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();

    }
    

///////////////////////////////////////////////////////////////
    private UserAccount createUserAccount() {
        return UserAccount.of(
                "bitstudy",
                "password",
                "bitstudy@email.com",
                "bitstudy",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
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
        /*삭제*/ //1L,
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
















