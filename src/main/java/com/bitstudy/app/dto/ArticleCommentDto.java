package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleCommentDto(
		Long id,
		Long articleId,
		UserAccountDto userAccountDto,
		String content,
		LocalDateTime createdDate,
		String createdBy,
		LocalDateTime modifiedDate,
		String modifiedBy
) {

	public static ArticleCommentDto of(Long id, Long articleId, UserAccountDto userAccountDto, String content, LocalDateTime createdDate, String createdBy, LocalDateTime modifiedDate, String modifiedBy) {
		return new ArticleCommentDto(id, articleId, userAccountDto, content, createdDate, createdBy, modifiedDate, modifiedBy);
	}

	public static ArticleCommentDto from(ArticleComment entity) {
		return new ArticleCommentDto(
				entity.getId(),
				entity.getArticle().getId(),
				UserAccountDto.from(entity.getUserAccount()),
				entity.getContent(),
				entity.getCreatedDate(),
				entity.getCreatedBy(),
				entity.getModifiedDate(),
				entity.getModifiedBy()
		);
	}

	public ArticleComment toEntity(Article entity) {
		return ArticleComment.of(
				entity,
				userAccountDto.toEntity(),
				content
		);
	}

}
