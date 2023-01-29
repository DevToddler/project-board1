package com.bitstudy.app.domain.type;

import lombok.Getter;

/* 서치타입이 한글로 나오게 부가정보 추가*/
public enum SearchType {
//    제목, 본문, id, 글쓴이, 해시태그
//      TITLE, CONTENT, ID, NICKNAME, HASHTAG

      TITLE("제목"),
      CONTENT("본문"),
      ID("유저아이디"),
      NICKNAME("닉네임"),
      HASHTAG("해시태그");

      @Getter
      private final String description;


      SearchType(String description) {
            this.description = description;
      }
}
