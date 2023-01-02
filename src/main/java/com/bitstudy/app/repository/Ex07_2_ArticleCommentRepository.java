package com.bitstudy.app.repository;

import com.bitstudy.app.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * HAL 확인해보기 => 서비스 실행하고 브라우저에서 localhost:8080/api
 * */
@RepositoryRestResource
public interface Ex07_2_ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

}
