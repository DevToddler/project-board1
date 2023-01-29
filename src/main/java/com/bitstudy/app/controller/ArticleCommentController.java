package com.bitstudy.app.controller;


import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.dto.request.ArticleCommentRequest;
import com.bitstudy.app.dto.request.ArticleRequest;
import com.bitstudy.app.serucity.BoardPrincipal;
import com.bitstudy.app.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/** 할일:  댓글 쓰기, 삭제 시 유저정보 추가함.  */

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    /** 댓글 쓰기*/
    @PostMapping ("/new")
    public String postNewArticleComment(
/*추가*/     @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleCommentRequest articleCommentRequest
    ) {

/*삭제*//** articleCommentService.saveArticleComment(articleCommentRequest.toDto(
         UserAccountDto.of(
         "bitstudy", "pw", "bitstudy@mail.com", null, null
         )
         ));*/

        /*추가*/ articleCommentService.saveArticleComment(articleCommentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    /** 댓글 삭제 */
    @PostMapping ("/{commentId}/delete")
    public String deleteArticleComment(
            @PathVariable Long commentId,
            Long articleId,
            /*추가*/ @AuthenticationPrincipal BoardPrincipal boardPrincipal

    ) {
/*삭제*/ //articleCommentService.deleteArticleComment(commentId);
/*추가*/ articleCommentService.deleteArticleComment(commentId, boardPrincipal.getUsername());

        return "redirect:/articles/" + articleId;
    }


}