package com.bitstudy.app.repository;

import com.bitstudy.app.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**TDD를 위해서 임시로 만들어놓은 저장소(이걸로 DB에 접근) */

/** 할일 - 클래스 위에 @RepositoryRestResource 넣어서 해당 클래스를 spring rest data 사용할 준비 시켜놓기
 *
 * */
@RepositoryRestResource
public interface Ex07_1_ArticleRepository extends JpaRepository<Article, Long> {

}
