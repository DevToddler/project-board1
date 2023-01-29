package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.ArticleCommentRepository;
import com.bitstudy.app.repository.ArticleRepository;
import com.bitstudy.app.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

/* 할일: 댓글의 CRUD 관련된 테스트만 만들기*/
/*  service > ArticleCommentService.java 랑 같이 볼것 */

@ExtendWith(MockitoExtension.class)
class Ex18_5ArticleCommentServiceTest_댓글쓰기삭제_구현 {

    @InjectMocks private ArticleCommentService sut;

    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;

/*추가*/ @Mock private UserAccountRepository userAccountRepository;


    /* 댓글 리스트 조회 */
    @DisplayName("게시글 ID 로 조회하면, 해당하는 댓글 리스트 모두 반환")
    @Test
    void givenSearchById_thenReturnCommentsAll() {
        // Given
        ArticleComment expected = createArticleComment("content");
        /* 1L 번 게시글 기준으로 모든 댓글들 다 리턴해오기 */
        Long articleId = 1L;
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of((expected)));

        // When
        List<ArticleCommentDto> actual = sut.searchArticleComment(articleId);

        // Then
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(articleCommentRepository).should().findByArticle_Id(articleId);

    }

    /* 댓글 저장 */
    @DisplayName("댓글 내용를 입력하면, 댓글 저장")
    @Test
    void givenCommentInfo_thenSaveComment() {
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);
        
        /* userAccountRepository 한테 요 위에서 생성한 dto 에서 만들었던 userAccountDto 의 userId 를 참조하게 하려면 아무렇게나 유저어카운트 만들어서 넘겨주면 된다. (실제 유저의 내용이 뭔지는 중요하지 않기 떄문임.) */
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());

        // When
        sut.saveArticleComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));

/*추가*/ then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
    }

    /* 댓글 수정 */
    @DisplayName("댓글 내용를 입력하면, 댓글 수정")
    @Test
    void givenCommentInfo_thenUpdateComment() {
        // Given
        String oldContent = "content";
        String updateContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent); /** 새로운 테스트용 댓글 생성*/
        ArticleCommentDto dto = createArticleCommentDto(updateContent);
        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        // When
        sut.updateArticleComment(dto);

        // Then
        assertThat(articleComment.getContent()) /* 지금 바꿔치기 한 articleComment의 content가  */
                .isNotEqualTo(oldContent) /* oldContent 랑 다르고*/
                .isEqualTo(updateContent); /* updateContent 랑 같으면 테스트 통과 */

        then(articleCommentRepository).should().getReferenceById(dto.articleId());
    }

    /* 댓글 삭제 */
    @DisplayName("댓글 id 입력하면, 댓글 삭제")
    @Test
    void givenCommentId_thenDeleteComment() {
        // Given
        Long articleCommentId = 1L;
        willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);

        // When
        sut.deleteArticleComment(articleCommentId);

        // Then
        then(articleCommentRepository).should().deleteById(articleCommentId);
    }

    //////////////////////////////////////////////

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }
    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
//                1L,
                "bitstudy",
                "password",
                "bitstudy@email.com",
                "bitstudy",
                "memo메모",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                Article.of(
                    createUserAccount(),
                    "title",
                    "content",
                    "hashtag"
                ),
                createUserAccount(),
                content
        );
    }
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
}