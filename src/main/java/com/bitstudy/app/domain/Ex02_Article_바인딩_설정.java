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
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/* Lombok 사용하기
 *
 * 1) 롬복을 이용해서 클래스를 엔티티로 변경
 * 2) getter/setter, toString 등의 롬복 어노테이션 사용
 * 3) 동등성, 동일성 비교할 수 있는 코드 넣어볼거임
 *
 * */
//@Entity
@Getter
@ToString
@Table(indexes = {
		@Index(columnList = "title"),
		@Index(columnList = "hashtag"),
		@Index(columnList = "createdDate"),
		@Index(columnList = "createdBy"),
})
public class Ex02_Article_바인딩_설정 {

	@Id // 전체 필드 중에 이게 PK라고 알려주는 것.
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 필드를 auto_increment 로 설정해야할 경우. 기본키 전략
	private Long id;

	@Setter
	@Column(nullable = false)
	private String title; // 제목

	@Setter
	@Column(nullable = false, length = 10000)
	private String content; // 본문

	@Setter
	private String hashtag; // 해시태그

	/**
	 * 양방향 바인딩
	 */
	@OrderBy("id") // 양방향 바인딩을 할건데 정렬 기준을 id로 하겠다는 뜻
	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
	@ToString.Exclude
	/** (중요) 맨 위 @ToString이 있는데 lazy load 관련 이슈로
	 *  퍼포먼스, 메모리 저하를 일으킬 수 있어서 성능적으로 안좋은 영향을 줄 수 있다. 그래서 해당 필드를 가려주세요 라는 의미의 어노테이션.
	 */
	private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
	/** (중요)!!!!!순환 참조 이슈
	 * @ToString.Exclude 를 달아주지 않으면 순환참조 이슈가 생길 수 있다.
	 * ToString이 id, title, content, hashtag, 다 찍고 Set<ArticleComment> 부분을 찍으려고 하면서 private Article article; 을 보는 순간
	 * Article의 @ToString이 다시 반복적으로 동작하면서 메모리가 터질 수 있다.
	 *
	 * ArticleComment에 걸지 않고 Article에 걸어주는 이유는 댓글이 글을 참조하는 건 정상적인 경우이지만, 반대로 글이 댓글을 참조하는건 일반적인 경우는 아니기 때문.
	 * */

	/* JPA auditing : JPA 에서 자동으로 세팅하게 해줄 때 사용하는 기능. config 파일이 별도로 있어야 한다. (JpaConfig)
	* 아래와 같이 어노테이션 붙여주기만 하면 auditing이 작동한다.
    * @CreatedDate : 최초에 INSERT 할 때 자동으로 한번 넣어준다.
    * @CreatedBy : 최초에 INSERT 할 때 자동으로 한번 넣어준다.
    * @LastModifiedDate : 작성 당시의 시간을 실시간으로 넣어준다.
    * @LastModifiedBy : 작성 당시의 작성자를 실시간으로 넣어준다.
	* */

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



	/** Entity 만들 때는 무조건 기본 생성자가 필요하다.
	 *  public 또는 protected 만 가능한데, 기본 생성자 안쓰이게 하고 싶어서 protected로 둠.
	 * */
	protected Ex02_Article_바인딩_설정(){}

	/** 사용자가 입력하는 값만 받기. 나머지는 시스템이 알아서 하도록.
	 * */
	private Ex02_Article_바인딩_설정(String title, String content, String hashtag) {
		this.title = title;
		this.content = content;
		this.hashtag = hashtag;
	}

	public static Ex02_Article_바인딩_설정 of(String title, String content, String hashtag){
		return new Ex02_Article_바인딩_설정(title, content, hashtag);
	}
	/** 정적 팩토리 메서드 (factory method pattern 중에 하나)
	 * 객체 생성 역할을 하는 클래스 메서드 라는 뜻.
	 * !!!중요 : 무조건 static으로 놔야 한다.!!!
	 *
	 * 장점
	 * 1) static 이기 때문에 new를 이용해서 생성자를 만들지 않아도 된다.
	 * 2) return 을 가지고 있기 때문에 상속 시 값을 확인할 수 있다.
	 * 3) (중요) 객체 생성을 캡슐화 할 수 있다.
	 * */


	/** 만약에 Article 클래스를 이요해서 게시글들을 list 에 담아서 화면을 구성할건데, 그걸 하려면 Collection을 이용해야 한다.
	 *
	 * Collection : 객체의 모음(그룹). 자바가 제공하는 최상위 컬렉션(인터페이스).
	 * 				하이버네이트는 중복을 허용하고 순서를 보장하지 않는다고 가정
	 * 	 - Set : 중복 허용 안함. 순서도 보장하지 않음
	 * 	 - List : 중복 허용, 순서 있음
	 * 	 - Map : key, value 구조로 되어 있는 특수 컬렉션
	 *
	 *	list에 넣거나 또는 list에 있는  중복요소를 제거하거나 정렬할 때 비교를 할 수 있어야 하기 때문에
	 *	동일성, 동등성 비교를 할 수 있는 equals와 hashCode를 구현해야 한다.
	 *
	 * 	모든 데이터들을 비교해도 되지만, 다 불러와서 비교하면 느려질 수 있다.
	 * 	사실 id 만 같으면 두 엔티티가 같다는 뜻이니까 id만 가지고 비교하는 걸 구현하자.
	 *
	 * */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Ex02_Article_바인딩_설정 article = (Ex02_Article_바인딩_설정) o;
		return id.equals(article.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/** == 와 equals 차이
	 *
	 * == : 동일성 비교, 값이랑 주소값까지 비교.
	 * equals : 동등성 비교, 값 만 본다
	 * hashCode : 객체를 식별하는 Integer 값.
	 * 			  객체가 가지고 있는 데이터를 특정 알고리즘을 적용해서 계산된 정수값을 hashCode 라고 함.
	 * 			  사용 이유 - 객체를 비교할 때 드는 비용이 낮다.
	 * */
}
