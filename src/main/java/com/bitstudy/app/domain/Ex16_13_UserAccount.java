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
//@Entity
public class Ex16_13_UserAccount extends AuditingFields {

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


    protected Ex16_13_UserAccount() {}

    private Ex16_13_UserAccount(String userId, String userPassword, String email, String nickname, String memo) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static Ex16_13_UserAccount of(String userId, String userPassword, String email, String nickname, String memo){
        return new Ex16_13_UserAccount(userId, userPassword, email, nickname, memo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ex16_13_UserAccount that = (Ex16_13_UserAccount) o;
/*삭제*/  //return id.equals(that.id);
/*추가*/  return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
/*삭제*/  //return Objects.hash(id);
/*추가*/  return Objects.hash(userId);
    }

}



















