package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**할일 : Article.java 와 ArticleComment.java 의 중복 필드를 합치기
 * 1) Article에 있는 메타데이터(auditing에 관련된 필드들)들 가져오기
 * 2) 클래스 레벨에 @MappedSuperclass 달아주기
 * 3) auditing 과 관련된 것들 다 가져오기
 * 		e.g., Article에서 @EntityListeners(AuditingEntityListener.class)
 *
 * 파싱 : 일정한 문법을 토대로 나열된 data를 그 문법에 맞춰서 새롭게 구성하는 것.
 * */
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString
@MappedSuperclass
public class AuditingFields {
	//메타 데이터
	@CreatedDate
	@Column(nullable = false)
	private LocalDateTime createdDate; // 생성일시

	@CreatedBy
	@Column(nullable = false, length = 100)
	private String createdBy; // 생성자
	// 최초 생성자는 (현재 코드 상태로는) 인증받고 오지 않았기 때문에 따로 알아낼 수가 없다. 이 때, JpaConfig 파일을 사용한다.

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime modifiedDate; // 수정일시

	@LastModifiedBy
	@Column(nullable = false, length = 100)
	private String modifiedBy; // 수정자
}
