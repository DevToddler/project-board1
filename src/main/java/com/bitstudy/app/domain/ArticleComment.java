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
import java.util.Objects;

/** 할일: @JoinColumn 이용해서 FK 키 연결 및 생성자 변경. */

@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
@Getter
@ToString(callSuper = true) // 모든 필드의 toString 생성
// 상위(UserAccount)에 있는 toString 까지 출력할 수 있도록 callSuper 넣음
public class ArticleComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // 게시글 고유 아이디

    @Setter
    @ManyToOne(optional = false) // 필수 값이라는 뜻
    private Article article;

/* 삭제 */
/*    @Setter
    @ManyToOne(optional = false) // 필수 값이라는 뜻
    private UserAccount userAccount;*/


/* 새로 삽입 */
    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; // 유저 정보 (ID)

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

/**    //메타데이터
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일자

    @CreatedBy
    @Column(nullable = false,length = 100)
    private String createdBy; // 생성자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일자

    @LastModifiedBy
    @Column(nullable = false,length = 100)
    private String modifiedBy; // 수정자*/

    protected ArticleComment() {}

    private ArticleComment(Article article, /* 새로 추가 */UserAccount userAccount, String content) {
        this.article = article;
/* 새로 추가 */this.userAccount = userAccount;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return new ArticleComment(article, /* 새로 추가 */userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}












