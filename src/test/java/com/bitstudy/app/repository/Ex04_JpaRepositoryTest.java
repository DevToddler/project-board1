//package com.bitstudy.app.repository;
//
//import com.bitstudy.app.config.JpaConfig;
//import com.bitstudy.app.domain.Article;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**슬라이드 테스트
// * 지난번 TDD 때 각 메서드들을 서로 알아보지 못하게 만들었었다. 이처럼 메서드들 각각 테스트한 결과를 서로 못보게 잘라서 만드는 것.
// * */
//@DataJpaTest // 슬라이트 테스트
//@Import(JpaConfig.class)
///**원래라면 JPA에서 모든 정보를 컨트롤해야하는데 JpaConfig의 경우는 읽어오지 못한다. 이유는 이는 우리가 별도로 만든 파일이기 때문.
// * 그래서 따로 import해줘야한다.(해주지 않으면 config 안에 명시해둔 JpaAuditing 기능이 동작하지 않는다.)
// * */
//class Ex04_JpaRepositoryTest {
//
//	private final Ex04_ArticleRepository_기본테스트용 articleRepository;
//	private final Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository;
//
//	/**원래는 둘 다 @Autowired 가 붙어야 하는데, JUnit5 버전과 Spring Boot 최신 버전을 이용하면 Test에서 생성자 주입패턴을 사용할 수 있다.*/
//
//	/**생성자 만들기 - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는 거라서 위와 상관없이 @Autowired를 붙여야 한다.*/
//	Ex04_JpaRepositoryTest(@Autowired Ex04_ArticleRepository_기본테스트용 articleRepository, @Autowired Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository) {
//		this.articleRepository = articleRepository;
//		this.articleCommentRepository = articleCommentRepository;
//	}
//
//
//	/**트랜잭션 시 사용하는 메서드
//	 * 사용법 : repository명.메소드()
//	 * 1) findAll() - 모든 컬럼을 조회할 때 사용. 페이징 가능(pageable).
//	 * 				  당연히 select 작업을 하지만, 잠깐 사이에 해당 테이블에 어떤 변화가 있었는지 알 수 없기 때문에 select 전에
//	 * 				  먼저 최신 데이터를 잡기 위해서 update를 한다.
//	 * 				  동작순서 : update -> select
//	 * 2) findById() - 한 건에 대한 데이터 조회 시 사용
//	 * 				   primary key로 레코드 한 건 조회.
//	 * 				   () 안에 글 번호를 넣어줘야 한다.
//	 * 3) save() - 레코드 저장할 때 사용(insert, update)
//	 * 4) count() - 레코드 개수 뽑을 때 사용
//	 * 5) delete() - 레코드 삭제
//	 * */
//
//	/** 테스트 용 데이터 가져오기
//	 * 1) mockaroo 사이트 접속
//	 * */
//	// select 테스트
//	@DisplayName("select 테스트")
//	@Test
//	void selectTest(){
//		/** 셀렉팅을 할 거니까 articleRepository를 기준으로 테스트 할 것이다.*/
//		List<Article> articles = articleRepository.findAll();
//
//		/** assertJ를 이용해서 테스트 */
//		assertThat(articles).isNotNull().hasSize(100); // articles 가 NotNull이고 사이즈가 100개 면 통과
//	}
//
//	// insert 테스트
//	@DisplayName("insert 테스트")
//	@Test
//	void insertTest(){
//		/**기존의 article 개수를 센 다음, insert 하고 , 기존거보다 현재꺼가 1 차이 나면 테스트 성공
//		 * */
//
//		// 기존 카운트 구하기
//		long prevCnt = articleRepository.count();
//
//		// insert 하기
//		Article article = Article.of("제목", "내용", "#태그");
//		articleRepository.save(article);
//
//		// 기존꺼랑 현재꺼 개수 차이 구하기
//		assertThat(articleRepository.count()).isEqualTo(prevCnt + 1);
//		/**주의!! : 이대로 테스트하면 createdAt 못찾는다고 에러 난다.
//		 * 이유는 JpaConfig파일에 auditing을 쓰겠다고 세팅을 해뒀는데, 해당 엔티티(Article.java)에서 auditing을 쓴다고 명시해놓지 않아서이다.
//		 * 엔티티에 가서 클래스 레벨로 @EntityListeners(AuditingEntityListener.class)를 달아준다.
//		 * */
//	}
//
//	// update 테스트
//	@DisplayName("update 테스트")
//	@Test
//	void updateTest(){
//		/**기존의 데이터 하나 있어야 되고, 그걸 수정했을 때를 관찰할거임.
//		 *
//		 * 1) 기존의 컨텍스트로부터 엔티티 객체를 하나 가져온다.(DB에서 뽑아온다)
//		 * 2) 업데이트로 해시태그를 바꾸기
//		 * */
//
//		/*
//		articleRepository =>
//		findById(1L) =>
//		orElseThrow() => 없으면 throw 시켜서 일단 테스트가 끝나게 하자
//		* */
//		Article article = articleRepository.findById(1L).orElseThrow();
//		String updateHashtag = "#abcd";
//		article.setHashtag(updateHashtag);
////		Article savedArticle = articleRepository.saveAndFlush(article);
//		Article savedArticle = articleRepository.save(article);
//		assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
//	}
//
//	// delete 테스트
//	@DisplayName("delete 테스트")
//	@Test
//	void deleteTest(){
//		Article article = articleRepository.findById(1L).orElseThrow();
//
//		long prevArticleCnt = articleRepository.count();
//		long prevArticleCommentCnt = articleCommentRepository.count();
//		int CommentSizeOnArticle = article.getArticleComments().size();
//
//		articleRepository.delete(article);
//
//		assertThat(articleRepository.count()).isEqualTo(prevArticleCnt - 1);
//		assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommentCnt - CommentSizeOnArticle);
//	}
//}