package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

public record Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현 of(Long id, Long articleId, UserAccountDto userAccountDto, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현(id, articleId, userAccountDto, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }



    /* 새로 추가
          엔티티에 만들어 넣기 전까지 못 넣는 정보들을 null 로 구성할 수 있는 팩토리 메소드를 만들었다.
          맨 앞에 id랑 뒤에 메타 데이터 부분은 이제 우리가 억지로 받아서 넣을 부분이 아니라서 null 들어가게 변경할거다.

         createdAt, createdBy, modifiedAt, modifiedBy 이 정보들은 데이터베이스에 영속화를 시킬때 DB에 의해서 자동으로 들어가는 값이다.
         우리는 그걸 위해 JPA Auditing 을 사용해서 자동으로 값을 만들고 있다.
         그래서 영속화된 정보를 불러올때는 문제가 없는데, save 를 위해서 DTO 를 만들고 repository에 저장을 할때에는 이 값들이 없다.
         그래서 별도로 null 값을 가지는 메서드를 하나 더 만들었다.

      * */
    public static Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현 of(Long articleId, UserAccountDto userAccountDto, String content) {
        return new Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현(null, articleId, userAccountDto, content,null,null,null,null);
    }


    public static Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현 from(ArticleComment entity) {
        return new Ex18_4_ArticleCommentDto_댓글쓰기삭제_구현(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article article, UserAccount userAccount) {
        return ArticleComment.of(
                article,
                userAccount,
                content
        );
    }

}
