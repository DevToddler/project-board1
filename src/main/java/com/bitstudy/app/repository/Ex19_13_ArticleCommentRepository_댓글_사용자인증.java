package com.bitstudy.app.repository;

import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/** deleteByIdAndUserAccount_UserId  */

@RepositoryRestResource
public interface Ex19_13_ArticleCommentRepository_댓글_사용자인증 extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>
        , QuerydslBinderCustomizer<QArticleComment> {

    /** 게시글에 딸려있는 댓글 검색 */
    List<ArticleComment> findByArticle_Id(Long articleId);
    /** 중요!!
    * findByArticle_Id 해설: 아직은 자동완성 기능 안되지만 일단 findById 같은건데 _(언더바) 의 의미가 별도 있다. 무작정 붙이는거 아님.
    * 언더바는 타고 들어가야 할때 사용된다.
    
    * 게시글로 댓그을 검색해야 하는데, 이런 경우에 사용하는 방법이다.
    * ArticleComment 안에는 Article 이랑 UserAccount 가 있는데, 그 안에 있는 객체 이름인 article을 쓰고 _(언더바)로 내려가면 그 객체 안으로 들어간다.
    
    * */


/*추가*/void deleteByIdAndUserAccount_UserId(Long articleCommentId, String userId);


    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {

        bindings.excludeUnlistedProperties(true);

        bindings.including(root.content, root.createdAt, root.createdBy);

        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
