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

/** QueryDsl 의 QuerydslPredicateExecutor 와 QuerydslBinderCustomizer 를 이용해서 검색 기능을 만들어 볼거다.*/
@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long>, QuerydslPredicateExecutor<Article>, QuerydslBinderCustomizer<QArticle> {
	/* 조심할 것 : QuerydslBinderCustomizer 는 QArticle 을 사용하므로 build.gradle에서 queryDsl을 빌드 하고 와야한다. */

	/* QuerydslPredicateExecutor 는 Article 안에 있는 모든 필드에 대한 기본 검색기능을 추가해준다.
	*
	* 순서
	*  1) 바인딩
	*  2) 검색용 필드를 추가
	* */

	Page<Article> findByTitleContaining(String searchKeyword, Pageable pageable);
	Page<Article> findByContentContaining(String searchKeyword, Pageable pageable);
	Page<Article> findByUserAccount_UserIdContaining(String searchKeyword, Pageable pageable);
	Page<Article> findByUserAccount_NicknameContaining(String searchKeyword, Pageable pageable);
	Page<Article> findByHashtagContaining(String searchKeyword, Pageable pageable);




	@Override
	default void customize(QuerydslBindings bindings, QArticle root){
		/** 1) 바인딩
		 *  현재 QuerydslPredicateExecutor 때문에 Article에 있는 모든 필드에 대한 검색이 열려있는 상태이다.
		 *  근데 우리가 원하는건 선택적 필드(제목, 본문, id, 글쓴이, 해시태그)만 검색에 사용되도록 하고 싶다.
		 *  그래서 선택적으로 검색을 하게 하기 위해서 bindings.excludeUnlistedProperties 을 쓴다.
		 *  bindings.excludeUnlistedProperties 는 리스팅을 하지 않은 프로퍼티는 검색에 포함할지 말지 결정할 수 있는 메서드.
		 *  true 라면 검색에서 제외, false 는 모든 프로퍼티를 열어주는것(기본값)
		 * */
		bindings.excludeUnlistedProperties(true);

		/** 2) 검색용(원하는) 필드를 지정(추가) 하는 부분
		 *  including 을 이용해서 title, content, createdDate, createdBy, hashtag 검색 가능하게 만들것이다.
		 *  (id 는 인증기능 달아서 유저 정보를 알아올 수 있을 때 할것이다.)
		 *  includeing 사용법 : 'root.필드명'
		 * */
		bindings.including(root.title, root.content, root.createdDate, root.createdBy, root.hashtag);

		/** 3) 'and검색' 만 됐었는데 'or검색' 가능하게 바꾸기 */
		bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // like '%${문자열}%'
		bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
		bindings.bind(root.createdDate).first(DateTimeExpression::eq); // DateTimeExpression 으로하고, eq는 equals를 의미. 날짜 필드는 정확한 검색만 되도록 설정. 그런데 이렇게 하면 시분초가 다 0으로 인식 됨. 나중에 시간 처리할 때 수정.
		bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
		bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
	}

	/**빌드 (ctrl + f9) 이후에 HAL 가서 체크하기 */
}
