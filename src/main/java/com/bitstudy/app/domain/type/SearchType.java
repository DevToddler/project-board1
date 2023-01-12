package com.bitstudy.app.domain.type;

import lombok.Getter;

import javax.persistence.GeneratedValue;

public enum SearchType {
	// 제목, 본문, id, 글쓴이, 해시태그
	TITLE("제목"),
	CONTENT("본문"),
	ID("유저아이디"),
	NICKNAME("닉네임"),
	HASHTAG("해시태그");

	@Getter
	private final String description;


	SearchType(String description) {this.description = description;}
}
