package com.bitstudy.app.repository;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface Ex19_8_ArticleRepository_인증 extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>
        ,QuerydslBinderCustomizer<QArticle> {


    /** 제목으로 검색할때 */
    Page<Article> findByTitleContaining(String title, Pageable pageable);

    /** 내용으로 검색할때 */
    Page<Article> findByContentContaining(String title, Pageable pageable);

    /** 유저아이디로 검색할때 */
    Page<Article> findByUserAccount_UserIdContaining(String title, Pageable pageable);

    /** 닉네임으로 검색할때 */
    Page<Article> findByUserAccount_NicknameContaining(String title, Pageable pageable);

    /** 해시태그로 검색할때 */
    Page<Article> findByhashtagContaining(String title, Pageable pageable);

/*추가*/ void deleteByIdAndUserAccount_UserId(Long articleId, String userid);

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {

        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.createdAt, root.createdBy, root.hashtag);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // like '%${문자열}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // 이건 날짜니까 DateTimeExpression 으로 하고, eq는 equals의 의미. 날짜필드는 정확한 검색만 되도록 설정. 근데 이렇게 하면 시분초가 다 0으로 인식됨. 이부분은 별도로 시간 처리할때 건드릴거임.
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);

    }

}
