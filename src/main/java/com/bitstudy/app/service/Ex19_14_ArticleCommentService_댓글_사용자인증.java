package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.repository.ArticleCommentRepository;
import com.bitstudy.app.repository.ArticleRepository;
import com.bitstudy.app.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/*  test > service > ArticleCommentServiceTest.java   랑 같이 볼것*/

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Ex19_14_ArticleCommentService_댓글_사용자인증 {
    private final UserAccountRepository userAccountRepository;

    private final ArticleRepository articleRepository;

/*추가 - 테스트 코드 중 ("댓글 내용를 입력하면, 댓글 저장") 에서 유저 정보를 이용하기 때문에 여기 유저 정보 관련꺼 불러온다.  */
    private final ArticleCommentRepository articleCommentRepository;



    /* 댓글 리스트 조회 */
    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComment(long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .toList();
    }

    /* 댓글 저장 */
    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            /* 댓글을 썼던 해당 게시글에 대한 정보 불러온거임.  */
            Article article = articleRepository.getReferenceById(dto.articleId());

            /* 내 댓글의 작성자 정보 가져온거임.*/
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            articleCommentRepository.save(dto.toEntity(article, userAccount));

        } catch(EntityNotFoundException e) {
            log.warn("댓글 저장 실패");
        }
    }

    /* 댓글 수정 */
    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());

            if(dto.content() != null) {
                articleComment.setContent(dto.content());
            }
             
        } catch(EntityNotFoundException e) {
            log.warn("댓글 수정 실패");
        }
    }

/*삭제*/
    /**public void deleteArticleComment(Long articleCommentId) {
     articleCommentRepository.deleteById(articleCommentId);
     }*/

/*추가 - userId 관련 추가*/
    public void deleteArticleComment(Long articleCommentId, String userId) {
        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }
}











