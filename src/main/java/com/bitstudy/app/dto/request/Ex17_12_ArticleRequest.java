package com.bitstudy.app.dto.request;

import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.UserAccountDto;

public record Ex17_12_ArticleRequest(
        String title,
        String content,
        String hashtag
) {
    public static Ex17_12_ArticleRequest of(String title, String content, String hashtag) {
        return new Ex17_12_ArticleRequest(title, content,hashtag);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto){
        return ArticleDto.of(
                userAccountDto,
                title,
                content,
                hashtag
        );
    }
}
