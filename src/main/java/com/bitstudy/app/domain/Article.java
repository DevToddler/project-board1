package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.cglib.core.Local;

import javax.persistence.Entity;
import java.time.LocalDateTime;
/* Lombok 사용하기
 *
 * 1) 롬복을 이용해서 클래스를 엔티티로 변경
 * 2) getter/setter, toString 등의 롬복 어노테이션 사용
 * 3) 동등성, 동일성 비교할 수 있는 코드 넣어볼거임
 *
 * */

@Getter
@Setter
@ToString
@Entity
public class Article {

	private Long id;
	private String title; // 제목
	private String content; // 본문
	private String hashtag; // 해시태그
	
	//메타 데이터
	private LocalDateTime createdAt; // 생성일시
	private String createBy; // 생성자
	private LocalDateTime modifiedAt; // 수정일시
	private String modifiedBy; // 수정자
	
}
