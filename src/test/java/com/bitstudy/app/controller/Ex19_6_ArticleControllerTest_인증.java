package com.bitstudy.app.controller;

import com.bitstudy.app.config.TestSecurityConfig;
import com.bitstudy.app.domain.type.FormStatus;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.dto.request.ArticleRequest;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
import com.bitstudy.app.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/* 이거 삭제 - 기존에 사용하고 있던 Import 는  여기에는 사용자관련 정보가 들어가지 않은 상태이며,
            이는 곧 인증 기능관련 여부를 테스트 할수 없는 상태*/
//@Import(SecurityConfig.class)
/* 새로 추가 - 여기는 테스트 파일이기 떄문에 TestSecurityConfig.class 를 사용해서 아까 만들었던 @BeforeTestMethod 꺼를 가져와서 사용한다. */
@Import({TestSecurityConfig.class, FormDataEncoder.class})
/* 여기까지만 바꾸고 전체 테스트 돌려보면, 게시판리스트 부분은 통과 하는데 인증이 필요한 글쓰기, 수정, 삭제 관련된건 다 에러난다.
    사용자 인증이 안되서 그런건데 인증이 안된 경우에는 로그인 페이지로 이동되도록 할거다.

    맨 아래 articlesOne() 메서드부터 새로 추가된 부분임
* */


@WebMvcTest(ArticleController.class)
@DisplayName("view 컨트롤러 - 게시글")
class Ex19_6_ArticleControllerTest_인증 {

    private final MockMvc mvc;
/*추가*/private final FormDataEncoder formDataEncoder;

    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;

    public Ex19_6_ArticleControllerTest_인증(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }



    /**1) 게시판 (리스트) 페이지*/
    @Test
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상호출")
    public void articlesAll() throws Exception {
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());

        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));

        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
    public void givenSearchKeyword_thenReturnArticles() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";

        given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));
        /** 페이징이 어떻게 나오던 일단 상관없다. 단지 검색을 했을때 페이징 기능을 호출하긴 하는가만 보면 됨 */

        // When & Then
        mvc.perform(
                get("/articles")
                        .queryParam("searchType", searchType.name())
                        .queryParam("searchValue", searchValue)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));

        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());

    }


    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception {
        // Given
        /** sort 관련 */
        String sortName = "title";
        String direction = "desc";

        /** paging 관련 */
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5); /** 이번에는 검증 대상이 페이징 기능이니까 따로 페이지 리스트를 따로 빼서 만들었음 */

        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());

        /** 이번엔 anyInt 대신 실제로 값을 보낼거임 */
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(get("/articles")
                        .queryParam("page", String.valueOf(pageNumber))
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }



/*---------------------------------------------------------------------------------------------*/
/*-------------         여기부터 보면 됨       --------------------------------------------------*/
/*---------------------------------------------------------------------------------------------*/

/* 여기 수정 */
    @WithMockUser /* 인증 정보를 넣어주는 애너테이션. 그냥 '인증이 통과됐다~ 근데 뉴규신지??(아이디같은건 모르는 채로 인증통과임.)' 정도의 느낌인 녀석임.
                @WithMockUser 은 우리가 만든 SecurityConfig 에 있는 userDetailService 를 이용하지 않기 때문에 실제 사용자정보를 이용할 수는 없다.
                지금 당장은 상관 없지만 글쓰기 같은 현재 유저 정보를 가져와서 사용해야 할때는 문제가 있다.  */
    @Test
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상호출, 인증된 사용자")
    public void articlesOne() throws Exception {
        // Given
        Long articleId = 1L;
        long totalCount = 1L;

        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(totalCount);

        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail")) // 이건 해당 뷰 파일명이 detail 인지 확인
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments")) // 상세페이지에는 댓글들도 여러개 있을수도 있으니까 모델 어트리뷰트에 articleComments 라는 키값으로 된게 있냐 라고 물어보는거
                .andExpect(model().attribute("totalCount", totalCount));

        then(articleService).should().getArticleWithComments(articleId);
        then(articleService).should().getArticleCount();

    }


/*추가*/ @WithMockUser
    @DisplayName("[view][GET] 새 게시글 작성 페이지") // 이건 그냥 페이지 보여주기만 하는거
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }


/* 새로 추가 - 여기 중요!!!
 *           위에 articlesOne() 에서 언급한거임.
 *           진짜 누구인지에 대한 유저 정보가 필요한 곳이다.
 *           그래서 여기 저기서 유저 정보를 떙겨와야 하는데, 간단하게 할 수 있다.
 *           @WithUserDetails 를 쓰면 된다.
 *
 *           @WithUserDetails 는 실제 구현한 "userDetailsService" 서비스를 이용할 수 있다.
 *           대신 실제로 정보가 DB에 있어야 한다.
 *               value: 유저이름. (이건 TestSecurityConfig.java 에서 userId의 값 가져오면 됨.)
 *               userDetailsServiceBeanName: 어디서 유저 정보 가져올건지. (우리는 SecurityConfig 에 userDetailsService() 만들었음)
 *               setupBefore: 언제 셋업이 되야 하는지
 *
 *           이거 넣고 테스트 돌리면 통과함!!
 *   */
    @WithUserDetails(value="bitstudyTest",userDetailsServiceBeanName = "userDetailsService",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        // Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        // When & Then
        mvc.perform(post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                        /**  csrf(Cross Site Request Forgery)은 시큐리티에서 제공하는 보안 관련 메서드이다.
                         해커가 희생자의 권한을 도용해서 희생자 의지와는 무관하게 공젹자가 의도한 행위를 특정 웹사이트에 요청하게 하는 공격
                         ex) 희생자 권한을 도용(뺏어서) 페이스북에 광고성 글을 계속 올리는거
                         * */
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }


    /* 새로 추가 - 인증 안된 상태로 게시글 보러 가면, 로그인 페이지로 이동 */
    /* 수정 */@DisplayName("[view][GET] 게시글 수정 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
        /* shouldHaveNoInteractions(): 이건 아무 인터랙션이 없었다. (아무것도 동작 안했다는 뜻)*/
    }

    /* 새로 추가 */@WithMockUser // 페이지를 보여만 주는 상태라서 유저데이터 가져올 필요 없다.
    /* 수정 */@DisplayName("[view][GET] 게시글 수정 페이지 - 정상 호출, 인증된 사용자")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(articleService).should().getArticle(articleId);
    }

/* 추가 */@WithUserDetails(value="bitstudyTest",userDetailsServiceBeanName = "userDetailsService",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesNewArticle() throws Exception {
        // Given
        long articleId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).updateArticle(eq(articleId), any(ArticleDto.class));

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }


    /* 추가 */@WithUserDetails(value="bitstudyTest",userDetailsServiceBeanName = "userDetailsService",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        /* 새로생성 */String userId = "bitstudyTest";
        /* 수정 */   willDoNothing().given(articleService).deleteArticle(articleId, userId);
/* 수정하는 이유: 아무나 이 메소드(deleteArticle) 에 게시글 아이디만 날리면 다 지우게 할거임?
                그래서 삭제할때는 글번호랑, 유저아이디 같이 가져와서 DB에 둘 다 매칭 될때만 삭제하게 해야함. */

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        /* 수정 */ then(articleService).should().deleteArticle(articleId, userId);
    }

    /*  ArticleController.java
     *   ArticleService.java
     *   ArticleRepository.java
     *   ArticleServiceTest.java
     *   ArticleController.java    다 수정 하고 전체 테스트 돌리면 다 통과할거임 */


    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                createUserAccountDto(),
                "title",
                "content",
                "#java"
        );
    }


    /** 아티클 코멘트 만드는 메서드 */
    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }

    /* 유져 어카운트 DTO 만드는 메서드 */
    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "bitstudy",
                "pw",
                "bitstudy@email.com",
                "bitstudy",
                "memo",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }

}















