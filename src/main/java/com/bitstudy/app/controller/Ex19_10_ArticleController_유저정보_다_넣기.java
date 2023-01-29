package com.bitstudy.app.controller;


import com.bitstudy.app.domain.type.FormStatus;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.request.ArticleRequest;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.dto.response.ArticleWithCommentsResponse;
import com.bitstudy.app.serucity.BoardPrincipal;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 할일: 1) 맨 아래 deleteArticle() 메서드에 articleService.deleteArticle(articleId, boardPrincipal.getUsername());
 *      2) 'TODO: 인증 정보 넣어줘야 한다' 라고 했던 부분들에 사용자정보 받아오기.
 *          등록, 수정, 삭제 관련한텐 다 있음.( postNewArticle(), updateArticle(), deleteArticle() )
 * */


@RequiredArgsConstructor
@Controller
@RequestMapping("/articles19")
public class Ex19_10_ArticleController_유저정보_다_넣기 {
    /** @RequiredArgsConstructor 로 만들어진 생성자(여기선 articlaService)를 사용할거다.
    쉽게 말하면 @RequiredArgsConstructor 로 만들어진 생성자를 얘가 읽어서 정보의 전달을 할 수 있게 해준다.
    (articleService 에 생성자를 통해 정보들이 다 들어가게 된다.) */
    private final ArticleService articleService;

    private final PaginationService paginationService;


    /** 게시판 리스트 보여주기 */
    @GetMapping
    public String articles(
            /**검색어를 받아야 한다. @RequestParam 를 이용해서 getParameter 를 불러올거고, 얘네들을 반드시 있지 않아도 된다. 없으면 게시글 전체 조회하면 되니까 required = false 해서 null 들어올 수 있게 걸어놓고, 검색어를 입력하면 검색기능으로 이어지게 만들어볼거다.*/
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            // size = 10 : 게시판 한 페이지당 10개씩 보여주겠다는 뜻
            // sort = 뭘 기준으로 하겠다.
            // direction = 내림차순, 오름차순 선택
            ModelMap map
    ) {

        /** 어짜피 이 아래 map 이나 List 에서 Page 정보가 똑같이 필요하기 때문에 그냥 원래 map 안에 있던 Page 정보를 밖으로 빼서 변수에 담아놓은것뿐임.   */
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
    public String postNewArticle(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest
    ) {
        // TODO: 인증 정보를 넣어줘야 한다.
/*삭제*/
//        articleService.saveArticle(articleRequest.toDto(
//            UserAccountDto.of(
//                "bitstudy",
//                "asdf",
//                "bitstudy@email.com",
//                "bitstudy",
//                "memo", null, null, null, null
//            )
//        ));

        /*추가*/ articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

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
/* 새로 추가 - 게시글 수정할때도 사용자 인증을 받아야 한다.
            유저 정보를 UserAccountDto.of를 통해서 임의로 만들어진 데이터를 사용했다.
            그러나 이제 사용정보를 받아올수 있으니 boardPrincipal을 이용해서 정의할수 있다  */
    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId,
                                ArticleRequest articleRequest,
/*새로추가*/                     @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ){
        /*삭제*/ //articleService.updateArticle(articleId, articleRequest.toDto(UserAccountDto.of(
        //        "bitstudy", "asdf", "bitstudy@email.com", "bitstudy", "memo", null, null, null, null
        //)));
        //return "redirect:/articles/" + articleId;

/*추가*/ articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));
        /* boardPrincipal.toDto() 가 userAccountDto 를 반환해준다.
         *   반환된 유저dto 를 articleRequest로 아티클dto로 또 바꿔서  updateArticle 로 보낸다.
         * */
        return "redirect:/articles";
    }

    /* 게시글 삭제하기 */
    /* 원래 delete 는 REST 방식에서는 delete 라고 했었지만 form 태그 쓸때는 get 과 Post만 허용되기 떄문에 어쩔수 없음. */
    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,

/* 추가 - 인증정보 가져온다.*/
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        // TODO: 인증 정보를 넣어줘야 한다.
/*삭제*///articleService.deleteArticle(articleId, userId);
/*추가*/articleService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }


    /* 여기까지 하고 ArticleControllerTest 가서 전체 테스트 돌리면 다 통과함*/
}
























