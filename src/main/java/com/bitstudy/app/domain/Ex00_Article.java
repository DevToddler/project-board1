package com.bitstudy.app.domain;

import java.time.LocalDateTime;

public class Ex00_Article {
	private Long id;
	private String title; // 제목
	private String content; // 본문
	private String hashtag; // 해시태그
	
	//메타 데이터
	private LocalDateTime createdDate; // 생성일시
	private String createBy; // 생성자
	private LocalDateTime modifiedDate; // 수정일시
	private String modifiedBy; // 수정자
	
}
