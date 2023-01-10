package com.bitstudy.app.dto.response;


import com.bitstudy.app.dto.ArticleDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Dto 보다 조금 더 세밀한 정보를 가질 수있도록?*/
public record ArticleResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdDate,
        String email,
        String nickname
) implements Serializable {

    public static ArticleResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdDate, String email, String nickname) {
        return new ArticleResponse(id, title, content, hashtag, createdDate, email, nickname);
    }


    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdDate(),
                dto.userAccountDto().email(),
                nickname
        );
    }

}
