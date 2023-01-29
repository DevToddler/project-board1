package com.bitstudy.app.controller;

import com.bitstudy.app.config.SecurityConfig;
import com.bitstudy.app.domain.type.FormStatus;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.dto.request.ArticleRequest;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
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


@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class) 
@DisplayName("view 컨트롤러 - 게시글")
class Ex17_1_ArticleControllerTest_글쓰기_수정_삭제 {

    private final MockMvc mvc;

    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;

    public Ex17_1_ArticleControllerTest_글쓰기_수정_삭제(@Autowired MockMvc mvc) {
    this.mvc = mvc;
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



    /**2) 게시글 (상세) 페이지*/
    @Test
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상호출")
    public void articlesOne() throws Exception {
        // Given
        /**  상세페이지니까 기본 게시글 번호 1번 으로 설정. */
        Long articleId = 1L;

        /** 상세페이지니까 기본 게시글 개수 1개 있다고 설정. */
        long totalCount = 1L; /* Long 은 null 사용 가능, long 은 불가.  ( int 와 Integer 와 같음) */

/*삭제*///given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
/*추가 - 게시글 상세 불러오는거라서 Article 이랑 Comments 둘 다 가져올거임*/
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        // dto 를 만들어야 해서 맨 아래에 createArticleWithCommentsDto() 메서드를 만듬.

        /**게시글 개수 구하기 */
        given(articleService.getArticleCount()).willReturn(totalCount);

        mvc.perform(get("/articles/"+articleId)) /** 테스트니까 그냥 1번 글 가져와라 할거임 */
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"))
               .andExpect(model().attributeExists("totalCount"));

/*삭제*/ //then(articleService).should().getArticle(articleId);
/*추가*/ then(articleService).should().getArticleWithComments(articleId);
        then(articleService).should().getArticleCount();
    }


///////////////////////////////////////
    /* 글쓰기 페이지 이동*/



    /* 게시글 등록(글쓰기) - 저장버튼 눌렀을때 */
    @Test
    @DisplayName("[view][GET] 게시글 등록 페이지 - 정상호출")
    void givenArticleInfo_thenSaveArticle() throws Exception {
        // Given
        ArticleRequest articleRequest = ArticleRequest.of("title","content","#java");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        // When
        mvc.perform(
                post("/articles/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        // Then
        then(articleService).should().saveArticle(any(ArticleDto.class));

    }
    
    /* 게시글 수정*/
    @DisplayName("[view][GET] 게시글 수정 페이지")
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

    /* 게시글 수정한거 DB에 업데이트 하기 */
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
                                //.content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                                /*  csrf(Cross Site Request Forgery)은 시큐리티에서 제공하는 보안 관련 메서드이다.
                                    해커가 희생자의 권한을 도용해서 희생자 의지와는 무관하게 공젹자가 의도한 행위를 특정 웹사이트에 요청하게 하는 공격
                                    ex) 희생자 권한을 도용(뺏어서) 페이스북에 광고성 글을 계속 올리는거
                                * */
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }


    /* 게시글 삭제하기 */
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "bitstudy";
/* 수정 */   //willDoNothing().given(articleService).deleteArticle(articleId, userId);
//        willDoNothing().given(articleService).deleteArticle(articleId);

        // When & Then
        mvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                                /*  csrf(Cross Site Request Forgery)은 시큐리티에서 제공하는 보안 관련 메서드이다.
                                    해커가 희생자의 권한을 도용해서 희생자 의지와는 무관하게 공젹자가 의도한 행위를 특정 웹사이트에 요청하게 하는 공격
                                    ex) 희생자 권한을 도용(뺏어서) 페이스북에 광고성 글을 계속 올리는거
                                * */
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
//        then(articleService).should().deleteArticle(articleId);
        /* 수정 */ //then(articleService).should().deleteArticle(articleId, userId);
    }


///////////////////////////////////////

/*추가*/private ArticleDto createArticleDto() {
        return ArticleDto.of(
                createUserAccountDto(),
                "title",
                "content",
                "#java"
        );
    }

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

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
        /*삭제*/ //1L,
                "bitstudy",
                "password",
                "bitstudy@email.com",
                "bitstudy",
                "memo memmo",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }

}

















