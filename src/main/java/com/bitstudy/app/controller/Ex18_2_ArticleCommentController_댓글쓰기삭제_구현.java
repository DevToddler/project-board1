package com.bitstudy.app.controller;


import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.dto.request.ArticleCommentRequest;
import com.bitstudy.app.service.ArticleCommentService;
import com.bitstudy.app.service.Ex18_6_ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** 할일: 아티클 불러올때 댓글을 불러오는 기능까지 다 동작하고 있는 상태임.
 * 
 * ArticleWithCommentsResponse.java 에서 
 * Set<ArticleCommentResponse> articleCommentsResponse 부분이 이미 댓글 끌어오고 있는 상태
 * 
 * 그래서 여기서는 댓글의 작성과 삭제만 다룰거임
 * */

@Controller
@RequestMapping("/comments18")
@RequiredArgsConstructor
public class Ex18_2_ArticleCommentController_댓글쓰기삭제_구현 {

    private final Ex18_6_ArticleCommentService articleCommentService;
    
    /* 댓글 쓰기*/
    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {

        /** 아래 saveArticleComment() 없으면 테스트 에러난다.
        새롭게 댓글을 넣을때는 댓글 입력하는 사람 정보도 필요하다.
        다음에 할 '인증기능 구현' 부분에서 스프링 시큐리티를 이용해서 로그인 로그아웃이나 인증 여부기능을 구현한다면 이게 필요 없는데 지금은 없으니깐 가짜로 만들어서 넣어줌.*/
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(
                UserAccountDto.of(
                        "bitstudy",
                        "asdf",
                        "bitstudy@email.com",
                        "bitstudy",
                        "memo", null, null, null, null
                )
            )
        );

        /* 댓글 쓰고 난 이후 원래 있던 게시글 페이지에 머물러 있어야 하기 때문에 현재 아티클 id 가 필요함.
        *  그런데 ArticleCommentRequest 에서 이미 articleId 를 받고 있기 때문에 별도로 받을필요 없음.
        *  */
        return "redirect:/articles/"+articleCommentRequest.articleId();
    }

    
    /* 댓글 삭제*/
    /** 원래 delete 는 REST 방식에서는 delete 라고 했었지만 form 태그 쓸때는 get 과 Post만 허용되기 떄문에 어쩔수 없음. */
    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId) {
        /* 댓글이 삭제 된 다음 원래 있던 페이지가 리다이렉트 되어야 하기 때문에 Long articleId 를 받아놔야 한다. */

        articleCommentService.deleteArticleComment(commentId);
        return "redirect:/articles/"+articleId;
    }

}
