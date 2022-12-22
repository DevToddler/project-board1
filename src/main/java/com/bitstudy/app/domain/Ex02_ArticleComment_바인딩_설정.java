package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

//@Entity
@Getter
@ToString
@Table(indexes = {
//		@Index(columnList = "article"),
		@Index(columnList = "createdDate"),
		@Index(columnList = "createdBy")
})
public class Ex02_ArticleComment_바인딩_설정 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@ManyToOne(optional = false)
//	@JoinColumn(name = "Article_id")
	private Article article;
	/**연관관계 매핑(JPA 스타일)
	 *
	 * 연관관계 없이 만들면 private Long articleId; 와 같이 관계형 데이터베이스 스타일로 하면 된다.
	 * 하지만 이번엔 Article과 ArticleComment가 관계를 맺고 있는 걸 객체지향적으로 표현하려고 이렇게 쓸 것이다.
	 *
	 * @ManyToOne	=> 단방향이라는 뜻의 애너테이션.
	 * (optional = false)	=> 필수로 필요한 값이라는 의미.
	 *
	 * '댓글은 여러개 : 게시글 1개' 이기 때문에 단방향 방식 사용.
	*/
	@Setter
	@Column(nullable = false, length = 500)
	private String content; // 본문

	//메타 데이터
	@CreatedDate
	@Column(nullable = false)
	private LocalDateTime createdDate; // 생성일시

	@CreatedBy
	@Column(nullable = false, length = 100)
	private String createdBy; // 생성자

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime modifiedDate; // 수정일시

	@LastModifiedBy
	@Column(nullable = false, length = 100)
	private String modifiedBy; // 수정자

}
