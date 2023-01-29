package com.bitstudy.app.controller;


import com.bitstudy.app.domain.type.FormStatus;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.dto.request.ArticleRequest;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.dto.response.ArticleWithCommentsResponse;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.Ex17_9_ArticleService;
import com.bitstudy.app.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* 할일: 제목, 본문, 이름 등으로 검색 되도록 한다. */

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles17")
public class Ex17_2_ArticleController_글쓰기_수정_삭제 {

    private final Ex17_9_ArticleService articleService;

    private final PaginationService paginationService;


    /** 게시판 리스트 보여주기 */
    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {


        Page<ArticleResponse> articles = articleService.searchArticles(searchType,searchValue,pageable).map(ArticleResponse::from);

        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
/* 추가*/map.addAttribute("searchTypes", SearchType.values());
        /* values(): enum인 요소들을 배열로 넘겨준다 */

        return "articles/index";
    }

    /** 게시글 상세 페이지 */
    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
/*삭제 - 상세페이지 불러올때는 댓글도 같이 불러올거라서 getArticleWithComments()으로 변경함. */
        //ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
/*추가*/  ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));

        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());
        return "articles/detail";
    }

    ///////////////////////////////////////////////////
/*추가*/

    /* 글쓰기 페이지 이동*/
    // 주소창에 /form 이라고 친 경우
    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    /* 게시글 등록(글쓰기) - 저장버튼 눌렀을때 */
    @PostMapping("/form")
    public String postNewArticle(ArticleRequest articleRequest) {
        articleService.saveArticle(articleRequest.toDto(
            UserAccountDto.of(
                "bitstudy",
                "asdf",
                "bitstudy@email.com",
                "bitstudy",
                "memo", null, null, null, null
            )
        ));
        return "redirect:/articles";
    }


    /* 게시글 수정화면 띄우기만 하기 */
    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    /* 게시글 수정한거 DB에 업데이트 하기 */
    @PostMapping ("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.updateArticle(articleId, articleRequest.toDto(UserAccountDto.of(
                "bitstudy", "asdf", "bitstudy@email.com", "bitstudy", "memo", null, null, null, null
        )));

        return "redirect:/articles/" + articleId;
    }

    /* 게시글 삭제하기 */
    /* 원래 delete 는 REST 방식에서는 delete 라고 했었지만 form 태그 쓸때는 get 과 Post만 허용되기 떄문에 어쩔수 없음. */
    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId, String userId) {


        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.deleteArticle(articleId);

        return "redirect:/articles";
    }



}



























