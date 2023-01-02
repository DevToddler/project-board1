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

/** QueryDsl 의 QuerydslPredicateExecutor 와 QuerydslBinderCustomizer 를 이용해서 검색 기능을 만들어 볼거다.*/
@RepositoryRestResource
public interface Ex07_5_ArticleCommentRepository extends JpaRepository<ArticleComment, Long>, QuerydslPredicateExecutor<ArticleComment>, QuerydslBinderCustomizer<QArticleComment> {

	@Override
	default void customize(QuerydslBindings bindings, QArticleComment root){

		bindings.excludeUnlistedProperties(true);

		bindings.including(root.content, root.createdDate, root.createdBy);

		/** 3) 'and검색' 만 됐었는데 'or검색' 가능하게 바꾸기 */
		bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
		bindings.bind(root.createdDate).first(DateTimeExpression::eq); // DateTimeExpression 으로하고, eq는 equals를 의미. 날짜 필드는 정확한 검색만 되도록 설정. 그런데 이렇게 하면 시분초가 다 0으로 인식 됨. 나중에 시간 처리할 때 수정.
		bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
	}
}
