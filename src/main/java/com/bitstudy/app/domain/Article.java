package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;




//@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@ToString(callSuper = true) // UserAccount 에 있는 toString 까지 출력할 수 있도록.
@Table(indexes = {
		@Index(columnList = "title"),
		@Index(columnList = "hashtag"),
		@Index(columnList = "createdDate"),
		@Index(columnList = "createdBy"),
})
public class Article extends AuditingFields{

	@Id // 전체 필드 중에 이게 PK라고 알려주는 것.
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 필드를 auto_increment 로 설정해야할 경우. 기본키 전략
	private Long id;

	@Setter
	@ManyToOne(optional = false)
	private UserAccount userAccount;

	@Setter
	@Column(nullable = false)
	private String title; // 제목

	@Setter
	@Column(nullable = false, length = 10000)
	private String content; // 본문

	@Setter
	private String hashtag; // 해시태그


	@OrderBy("createdDate desc") // 댓글리스트를 최근시간 순으로 정렬되도록 함.
	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
	@ToString.Exclude

	private final Set<ArticleComment> articleComments = new LinkedHashSet<>();



	/**************************************************************************/


	/** Entity 만들 때는 무조건 기본 생성자가 필요하다.
	 *  public 또는 protected 만 가능한데, 기본 생성자 안쓰이게 하고 싶어서 protected로 둠.
	 * */
	protected Article(){}

	/** 사용자가 입력하는 값만 받기. 나머지는 시스템이 알아서 하도록.
	 * */
	private Article(UserAccount userAccount, String title, String content, String hashtag) {
		this.userAccount = userAccount;
		this.title = title;
		this.content = content;
		this.hashtag = hashtag;
	}

	public static Article of(UserAccount userAccount, String title, String content, String hashtag){
		return new Article(userAccount, title, content, hashtag);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Article article = (Article) o;
		return id.equals(article.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
