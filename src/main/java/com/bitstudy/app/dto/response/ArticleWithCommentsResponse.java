package com.bitstudy.app.dto.response;


import com.bitstudy.app.dto.ArticleWithCommentsDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/* 이대로 돌리면 에러가 났을거임.
 * 이유는 이 파일에서 유저아이디에 관련된 데이터를 nickname 하나만 내보내주고 있다.
 * 저 밑에 if (nickname == null || nickname.isBlank()) 에서 닉네임이 있는경우에만 보내주고, 없으면 userId 를 보내준다.
 * 그런데 아까 detail.th.xml 에서 현재 로그인 한 유저가 글쓴이인지 확인하는 부분이 있다. 게시글도 마찬가지다.
 * 그 페이지 가보면 *{userId} == ${#authentication.name} 이런 파트 있는데
 * 한마디로 좋던 싫던 userId 라는 값도 만들어서 보내줘야 한다는 말이다.
 * 그래서 userId 필드를 추가한다.
 *   */

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname,

        /*새로 추가 - 이거 바꾸면 아래 of 메서드에서도 바꿔야함*/
        String userId,

        Set<ArticleCommentResponse> articleCommentsResponse
) implements Serializable {

    public static ArticleWithCommentsResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname, /**/String userId/**/, Set<ArticleCommentResponse> articleCommentResponses) {
/*추가*/       return new ArticleWithCommentsResponse(id, title, content, hashtag, createdAt, email, nickname, /**/userId/**/,  articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
/*추가*/     /**/dto.userAccountDto().userId(),/**/
                dto.articleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

}
