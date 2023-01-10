package com.bitstudy.app.service;

import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.repository.ArticleCommentRepository;
import com.bitstudy.app.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Ex11_3_ArticleCommentService {
	private final ArticleRepository articleRepository;
	private final ArticleCommentRepository articleCommentRepository;

	@Transactional(readOnly = true)
	public List<ArticleCommentDto> searchArticleComment(long articleId) {
		return articleCommentRepository.findByArticle_Id(articleId).stream().map(ArticleCommentDto::from).toList();
	}

	public void saveArticleComment(ArticleCommentDto dto){
		try {
			articleCommentRepository.save(dto.toEntity(articleRepository.getReferenceById(dto.articleId())));

		} catch (EntityNotFoundException e) {
			log.warn("댓글 저장 실패");
		}
	}

	public void updateArticleComment(ArticleCommentDto dto) {
		try {
			ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
			if(dto.content() != null){
				articleComment.setContent(dto.content());
			}
		} catch (EntityNotFoundException e) {
			log.warn("댓글 수정 실패");
		}
	}

	public void deleteArticleComment(Long articleCommentId) {
		articleCommentRepository.deleteById(articleCommentId);
	}
}
