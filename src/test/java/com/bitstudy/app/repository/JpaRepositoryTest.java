package com.bitstudy.app.repository;

import com.bitstudy.app.config.JpaConfig;
import com.bitstudy.app.domain.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class JpaRepositoryTest {

	private final ArticleRepository articleRepository;
	private final ArticleCommentRepository articleCommentRepository;


	JpaRepositoryTest(@Autowired ArticleRepository articleRepository, @Autowired ArticleCommentRepository articleCommentRepository) {
		this.articleRepository = articleRepository;
		this.articleCommentRepository = articleCommentRepository;
	}

	//select Test
	@Test
	void selectTest(){
		List<Article> articleList = articleRepository.findAll();
		assertThat(articleList).isNotNull().hasSize(100);
	}

	//insert Test
	@Test
	void insertTest(){
		Article article = Article.of("제목", "본문", "#해시태그");
		long prevCnt = articleRepository.count();
		articleRepository.save(article);
		assertThat(articleRepository.count()).isEqualTo(prevCnt + 1);
	}

	//update Test
	@Test
	void updateTest(){
		Article article = articleRepository.findById(1L).orElseThrow();
		String newTitle = "새로운 제목";
		article.setTitle(newTitle);
		Article updatedArticle = articleRepository.save(article);
		assertThat(updatedArticle).hasFieldOrPropertyWithValue("title", newTitle);
	}

	//delete Test
	@Test
	void deleteTest(){
		Article article = articleRepository.findById(1L).orElseThrow();
		long prevArticleCnt = articleRepository.count();
		long prevArticleCommentCnt = articleCommentRepository.count();
		int commentSizeOnArticle1 = article.getArticleComments().size();

		articleRepository.delete(article);

		assertThat(articleRepository.count()).isEqualTo(prevArticleCnt - 1);
		assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommentCnt - commentSizeOnArticle1);
	}
}