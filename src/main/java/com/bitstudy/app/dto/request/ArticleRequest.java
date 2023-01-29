package com.bitstudy.app.dto.request;

import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.dto.response.ArticleResponse;

import java.time.LocalDateTime;

public record ArticleRequest(
        String title,
        String content,
        String hashtag
) {
    public static ArticleRequest of(String title, String content, String hashtag) {
        return new ArticleRequest(title, content,hashtag);
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
