package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

/** 할일: Long id  빼고 UserId 를 @Id 로 변경 */

@Getter
@ToString
@Table(indexes = {
/*삭제*/ //@Index(columnList = "userId", unique = true),
        @Index(columnList = "email" , unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class UserAccount extends AuditingFields {

/*삭제*/
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

/*삭제*///@Setter @Column(nullable = false, length = 50) private String userId;

/*추가*/ @Id @Column(length = 50) private String userId;

    @Setter @Column(nullable = false) private String userPassword;
    @Setter @Column(length = 100) private String email;
    @Setter @Column(length = 100) private String nickname;
    @Setter private String memo;

    ///////////////////////////////////////////////////////////


    protected UserAccount() {}

    private UserAccount(String userId, String userPassword, String email, String nickname, String memo) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static UserAccount of(String userId, String userPassword, String email, String nickname, String memo){
        return new UserAccount(userId, userPassword, email, nickname, memo);
    }

/*추가 - userId를 비교할때 that.id 가 제대로 동작하지 않아서 userId 비교가 되지 않는다.
*       그래서 아래와 같이 getUserId() 를 써서 getter 로 값을 가져와서 비교하도록 변경했다. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getUserId() != null && this.getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }

/*삭제*/
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        UserAccount that = (UserAccount) o;
//        return userId.equals(that.userId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId);
//    }
}

