package com.bitstudy.app.dto.response;

import com.bitstudy.app.dto.ArticleCommentDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 댓글 요청하는 컨트롤러에 응답으로 내보내는 전용 DTO 임*/

/* 이대로 돌리면 에러가 났을거임.
 * 이유는 이 파일에서 유저아이디에 관련된 데이터를 nickname 하나만 내보내주고 있다.
 * 저 밑에 if (nickname == null || nickname.isBlank()) 에서 닉네임이 있는경우에만 보내주고, 없으면 userId 를 보내준다.
 * 그런데 아까 detail.th.xml 에서 현재 로그인 한 유저가 글쓴이인지 확인하는 부분이 있다. 게시글도 마찬가지다.
 * 그 페이지 가보면 *{userId} == ${#authentication.name} 이런 파트 있는데
 * 한마디로 좋던 싫던 userId 라는 값도 만들어서 보내줘야 한다는 말이다.
 * 그래서 userId 필드를 추가한다.
 *   */

public record Ex20_6_ArticleCommentResponse_사용자인증정보_추가(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,

        /*새로 추가 - 이거 바꾸면 아래 of 메서드에서도 바꿔야함*/
        String userId
) implements Serializable {

    public static Ex20_6_ArticleCommentResponse_사용자인증정보_추가 of(Long id, String content, LocalDateTime createdAt, String email, String nickname, /**/String userId/**/) {
/*추가*/  return new Ex20_6_ArticleCommentResponse_사용자인증정보_추가(id, content, createdAt, email, nickname, /**/userId/**/);
    }

    public static Ex20_6_ArticleCommentResponse_사용자인증정보_추가 from(ArticleCommentDto dto) { /* ArticleCommentDto 을 받아서*/
        String nickname = dto.userAccountDto().nickname(); /* nickname 을 받아오는데*/
        if (nickname == null || nickname.isBlank()) { /* 만약에 nickname 이 없으면 */
            nickname = dto.userAccountDto().userId(); /* userId 를 대신 불러와서 nickname 변수에 넣어라. */
            // 어찌됐든 nickname 을 채워넣겠다는 코드임
        }

        return new Ex20_6_ArticleCommentResponse_사용자인증정보_추가(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
/*추가*/         /**/dto.userAccountDto().userId()/**/
        );
    }


}
