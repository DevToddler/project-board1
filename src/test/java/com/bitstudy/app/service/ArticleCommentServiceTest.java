package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.ArticleCommentRepository;
import com.bitstudy.app.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * 할일 : 댓글의 CRUD 관련된 테스트만 만들기
 */

@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
	/** @InjectMocks, @Mock  설명
	 	Mock 달린 걸 InjectMocks에 넣어서 테스트 환경을 가짜로(db가 너무 무거울 경우, 데이터 바뀔 위험 등등) 만드는 개념
	 	아래 실제 Service 처럼 테스트 환경을 만들어 주는 개념.
			 @RequiredArgsConstructor
			 public class ArticleCommentService {
			 	private final ArticleRepository articleRepository;
			 	private final ArticleCommentRepository articleCommentRepository;
			 }
	 */
	@InjectMocks
	private ArticleCommentService sut;

	@Mock
	private ArticleRepository articleRepository;
	@Mock
	private ArticleCommentRepository articleCommentRepository;

	// 댓글 리스트 조회
	@DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트 모두 반환")
	@Test
	void givenSearchById_thenReturnCommentsAll(){
		// Given
		Long articleId = 1L;
		ArticleComment expected = createArticleComment("content");
		given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));

		// When
		List<ArticleCommentDto> actual = sut.searchArticleComment(articleId);

		// Then
		assertThat(actual).hasSize(1).first().hasFieldOrPropertyWithValue("content", expected.getContent());

		then(articleCommentRepository).should().findByArticle_Id(articleId);
	}

	// 댓글 저장
	@DisplayName("댓글 정보를 입력하면, 댓글 저장")
	@Test
	void givenCommentInfo_thenSaveComment(){
		// Given
		ArticleCommentDto dto = createArticleCommentDto("content");

		given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
		given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

		// When
		sut.saveArticleComment(dto);

		// Then
		then(articleRepository).should().getReferenceById(dto.articleId());

		then(articleCommentRepository).should().save(any(ArticleComment.class));
	}

	// 댓글 수정
	@DisplayName("댓글 정보를 입력하면, 댓글 수정")
	@Test
	void givenCommentInfo_thenUpdateComment(){
		// Given
		String oldContent = "content";
		String updateContent = "댓글";
		ArticleComment articleComment = createArticleComment(oldContent);
		ArticleCommentDto dto = createArticleCommentDto(updateContent);

		given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

		// When
		sut.updateArticleComment(dto);

		// Then
		assertThat(articleComment.getContent()).isNotEqualTo(oldContent).isEqualTo(updateContent);
		then(articleCommentRepository).should().getReferenceById(dto.id());
	}

	// 댓글 삭제
	@DisplayName("댓글 id, 댓글 수정")
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


	/**********************************************************************/
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

	private UserAccountDto createUserAccountDto(){
		return UserAccountDto.of(
				1L,
				"bitstudy",
				"asdf",
				"bitstudy@email.com",
				"nickname",
				"memo",
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

	private UserAccount createUserAccount(){
		return UserAccount.of(
				"bitstudy",
				"password",
				"bitstudy@email.com",
				"bitstudy",
				null
		);
	}

	private Article createArticle(){
		return Article.of(
				createUserAccount(),
				"title",
				"content",
				"hashtag"
		);
	}
}