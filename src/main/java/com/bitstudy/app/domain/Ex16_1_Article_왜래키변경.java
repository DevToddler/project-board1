package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** 할일: @JoinColumn 이용해서 FK 키 연결 및 생성자 변경. */

@Table(indexes = {
        @Index(columnList = "title"),  // 검색속도 빠르게 해주는 작업
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
//@Entity // Lombok 을 이용해서 클래스를 엔티티로 변경 @Entity 가 붙은 클래스는 JPA 가 관리하게 된다.
@Getter // 모든 필드의 getter 들이 생성
@ToString(callSuper = true) // 모든 필드의 toString 생성
                            // 상위(UserAccount)에 있는 toString 까지 출력할 수 있도록 callSuper 넣음
public class Ex16_1_Article_왜래키변경 extends AuditingFields {

    @Id // 전체 필드중에서 PK 표시 해주는 것 @Id 가 없으면 @Entity 어노테이션을 사용 못함
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 필드가 auto_increment 인 경우 @GeneratedValue 를 써서 자동으로 값이 생성되게 해줘야 한다. (기본키 전략)
    private long id; // 게시글 고유 아이디


    @Setter
    @ManyToOne(optional = false)
/*추가*/@JoinColumn(name = "userId")
    private UserAccount userAccount; // 조인 컬럼은 외래 키를 매핑할 때 사용


    @Setter
    @Column(nullable = false)
    private String title; // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문

    @Setter
    private String hashtag; // 해시태그

//    @OrderBy("id")
    @OrderBy("createdAt desc") // 댓글리스트를 최근시간꺼로 정렬되도록 바꿈
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    protected Ex16_1_Article_왜래키변경() {}

/* 이제부터 게시글에 관련된 정보는 실제 유저의 정보를 같이 가져가야 하기 떄문에 생성자에서도 userAccount 를 쓴다.*/
    private Ex16_1_Article_왜래키변경(/*추가*/UserAccount userAccount, String title, String content, String hashtag) {
/*추가*/this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Ex16_1_Article_왜래키변경 of(/*추가*/UserAccount userAccount, String title, String content, String hashtag){
        return new Ex16_1_Article_왜래키변경(/*추가*/userAccount, title,content,hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ex16_1_Article_왜래키변경 article = (Ex16_1_Article_왜래키변경) o;
        return id == article.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
