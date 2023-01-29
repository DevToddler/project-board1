package com.bitstudy.app.dto.request;

import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.dto.UserAccountDto;

public record Ex18_3_ArticleCommentRequest_댓글쓰기삭제_구현(Long articleId, String content) {

    public static Ex18_3_ArticleCommentRequest_댓글쓰기삭제_구현 of(Long articleId, String content) {
        return new Ex18_3_ArticleCommentRequest_댓글쓰기삭제_구현(articleId, content);
    }


    /* 댓글 DTO 받을때 User의 정보는 댓글의 request 로부터는 받지 못한다. 나중에 인증을 통해서 별도로 받거나 해야한다.
      그래서 매개변수를 이용해서 외부에서 입력이 가능하게 만들었음. */
    public ArticleCommentDto toDto(UserAccountDto userAccountDto){
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                content
        );
    }
}
