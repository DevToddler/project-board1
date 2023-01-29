package com.bitstudy.app.repository;


import com.bitstudy.app.config.JpaConfig;
import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;

//import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/* 할일: 그냥 바로 전체 테스트 돌려보면 insert() 메서드만 오류난다.
    오류 내용은 createdBy가 null 이라는건데,
    securityconfig의 UserDetailsService 에서 이제 userAccountRepository를 건드리게 되는데
    이 녀석이 제대로 빈으로 등록 되어있지 않거나, 인증 정보 혹은 사용자의 정보가 들어있지 않으면 테스트가 실패하게 되는것이다.
    따라서 서큐리티 전용 설정을 작성해서 추가해주는 것이 좋다.

  !!  맨 아래 TestJpaConfig 클래스 만들어서 테스트에서만 돌아갈 수 있는 전용 config 파일을 만들면 됨 !!
*  */

@DataJpaTest
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DisplayName("JPA 테스트")
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    /** 원래는 둘 다 @Autowired가 붙어야 하는데, JUnit5 버전과 최신 버전의 스프링 부트를 이용하면 Test에서 생성자 주입패턴을 사용할 수 있다.  */

    private final UserAccountRepository userAccountRepository;


    /** 생성자 만들기 - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는거라서 위에랑 상관 없이 @Autowired 를 붙여야 함 */
    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }


    /** select 테스트 */
    @DisplayName("select 테스트")
    @Test
    void selectTest() {
        /*** 셀렉팅을 할거니까 articleRepository 를 기준으로 테스트 할거임.
            maven방식: dao -> mapper 로 정보 보내고 DB 갔다 와서 C 까지 돌려보낼건데 dao에서 DTO를 list에 담아서 return
         * */

        List<Article> articles  =  articleRepository.findAll();

        /*** assertJ 를 이용해서 테스트 할거임
         * articles 가 NotNull 이고 사이즈가 ?? 개면 통과
         *
         * * */
        assertThat(articles).isNotNull().hasSize(100);

    }

    /** insert 테스트 */
    @DisplayName("insert 테스트")
    @Test
    void insertTest() {
        /*** 기존의 article 개수를 센 다음에, insert 하고, 기존거보다 현재꺼가 1 차이가 나면 insert 제대로 됐다는 뜻. */
 

        // 기존 카운트 구하기
        long prevCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("new_bitstudy", "asdf",null, null, null));
/*추가 설명 - UserAccount.java 에서 userId 를 UK 키로 바꿔서 여기서 테스트 할때도 중복 에러 안터지게 하려고 userId 부분에 new_bitstudy 로 아이디 변경함 */
        
        // insert 하기
        Article article =  Article.of(userAccount, "제목","내용","#해시태그"); // new Article
        articleRepository.save(article);

        // 기존꺼랑 현재꺼랑 개수 차이 구하기
        assertThat(articleRepository.count()).isEqualTo(prevCount + 1);


    }


    /** update 테스트 */
    @DisplayName("update 테스트")
    @Test
    void updateTest() {
        /*** 기존의 데이터 하나 있어야 되고, 그걸 수정했을때를 관찰할거임.
         *
         * 1) 기존의 영속성 컨텍스트로부터 하나 엔티티를 객체를 가져온다. (DB에서 한줄 뽑아온다)
         * 2) 업데이트로 해시태그를 바꾸기 */

        /** 순서1) 기존의 영속성 컨텍스트로부터 하나 엔티티를 객체를 가져온다. (DB에서 한줄 뽑아온다)
            articleRepository -> 기존의 영속성 컨텍스트로부터
            findById(1L) -> 하나 엔티티를 객체를 가져온다
            .orElseThrow() -> 없으면 throw 시켜서 일단 테스트가 끝나게 하자
        */
        Article article = articleRepository.findById(1L).orElseThrow();

        /** 순서2) 업데이트로 해시태그를 바꾸기
            엔티티에 있은 setter 를 이용해서 updatehashtag 에 있는 문자열로 업데이트 하기
        *   1. 변수 updateHashtag 에 바꿀 문자열 저장
            2. 엔티티(article)에 있는 setter를 이용해서 변수 updateHashtag 에 있는 문자열을 넣고
                (해시태그 바꿀꺼니까 setHashtag. 이름 어찌할지모르겠으면 실제 엔티티 파일 가서 setter 만들어보기. 그 이름 그대로 쓰면 됨)
            3. 데이터 베이스에 업데이트 하기
        * */
        String updateHashtag = "#abcd";
        article.setHashtag(updateHashtag);
        //articleRepository.save(article);
        Article savedArticle = articleRepository.saveAndFlush(article);
        /*** save 로 놓고 테스트를 돌리면 콘솔(Run)탭에 update 구문이 나오지 않고 select 구문만 나온다. 이유는 영속성 컨텍스트로부터 가져온 데이터를 그냥 save만 하고 아무것도 하지 않고 끝내버리면 어짜피 롤백 되니까 스프링부트는 다시 원래의 값으로 돌아가질거다. 그래서 그냥 했다 치고 update 를 하지 않는다.(코드의 유효성은 확인)
         그래서 save 를 하고 flush를 해줘야 한다.

         - flush 란 (push 같은거)
         1. 변경점 감지
         2. 수정된 Entity 를 sql 저장소에 등록
         3. sql 저장소에 있는 쿼리를 DB에 전송
         * */

        /** 순서3) 위에서 바꾼 savedArticle 에 업데이트 된 hashtag 필드에 updateHashtag 에 저장되어 있는 값("#abcd") 이 있는지 확인해봐라  */
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
//        assertThat(savedArticle.getHashtag()).isEqualTo(updateHashtag);
    }


    /** delete 테스트 */
    @DisplayName("delete 테스트")
    @Test
    void deleteTest() {
        /***  기존의 데이터들이 있다고 치고, 그중에 값을 하나 꺼내고, 지워야 한다.
         *
         * 1) 기존의 영속성 컨텍스트로부터 하나 엔티티를 객체를 가져온다. => findById
         * 2) 지우면 DB에서 하나 사라지기 때문에 count 를 구해놓고 => .count()
         * 3) delete 하고( -1) => .delete();
         * 4) 2번에서 구한 count와 지금 순간의 개수 비교해서 1 차이나면 테스트 통과 => .isEqualTo()
         * */

        /** 1) 기존의 영속성 컨텍스트로부터 하나 엔티티를 객체를 가져온다.
        * - 순서
            articleRepository -> 기존의 영속성 컨텍스트로부터
            findById(1L) -> 하나 엔티티를 객체를 가져온다
            .orElseThrow() -> 없으면 throw 시켜서 일단 테스트가 끝나게 하자
        * */
        Article article = articleRepository.findById(1L).orElseThrow();

        /** 2) 지우면 DB에서 하나 사라지기 때문에 count 를 구해놓고
        * 게시글(articleRepository) 뿐만 아니라, 연관된 댓글(articleCommentRepository) 까지 삭제할거라서 두개의 개수를 다 알아내기.
        *  */
        long prevArticleCount = articleRepository.count();
        long prevArticleCommentCount = articleCommentRepository.count(); // 데이터베이스에 있는 모든 댓글의 수
        int deletedCommentSize = article.getArticleComments().size(); // 해당 게시글에 딸려있는 댓글의 수
        // 나중에 "모든 댓글의 수 - 게시글에 딸려있는 댓글 수" 하면 몇개 지워졌는지 알 수 있다.

        /** 3) delete 하고 (전체 게시글 개수 -1 됨) */
        articleRepository.delete(article);

        /** 테스트 통과 조건 - 2번에서 구한거랑 여기서 구하는 개수가 1 차이나는 경우 */
        assertThat(articleRepository.count()).isEqualTo(prevArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommentCount - deletedCommentSize);



    }

/* 새로 생성 - auditing을 자동으로 넣는 부분을 테스트때만 시큐리티를 무시하게 만들기 */
    @EnableJpaAuditing
    @TestConfiguration // 테스트 할떄만 Configuration 로 동작.
    public static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("bitstudy");
            // 우리가 시큐리티 적용하기 전에 썼던대로 쓰는거임.
        }
    }

    /* 다 하면 맨 위에 가서
        @Import(JpaRepositoryTest.TestJpaConfig.class)넣고
        전체 테스트 돌리기
        그럼 다 통과함
    */
}



















