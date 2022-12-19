package com.bitstudy.app.domain;

import java.time.LocalDateTime;

public class ArticleComment {
	private Long id;
	private Article article; // 연관관계 매핑(JPA 스타일)
	private String content; // 본문

	//메타 데이터
	private LocalDateTime createdAt; // 생성일시
	private String createBy; // 생성자
	private LocalDateTime modifiedAt; // 수정일시
	private String modifiedBy; // 수정자

}
