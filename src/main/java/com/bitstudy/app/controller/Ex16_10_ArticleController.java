package com.bitstudy.app.controller;


import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.dto.response.ArticleWithCommentsResponse;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/* 할일: 제목, 본문, 이름 등으로 검색 되도록 한다. */

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles16")
public class Ex16_10_ArticleController {

    private final ArticleService articleService;

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
}
