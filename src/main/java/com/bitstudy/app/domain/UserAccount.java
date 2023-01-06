package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

/** 회원 관리에 대한 부분. Auditing 까지 연결해서 사용해야 함. */

@Entity
@Getter
@ToString
@Table(indexes = {
		@Index(columnList = "userId"),
		@Index(columnList = "email"),
		@Index(columnList = "createdDate"),
		@Index(columnList = "createdBy")
})
public class UserAccount extends AuditingFields{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@Column(nullable = false, length = 50, unique = true)
	private String userId;

	@Setter
	@Column(nullable = false)
	private String userPw;

	@Setter
	@Column(length = 100)
	private String email;

	@Setter
	@Column(length = 100)
	private String nickname;

	@Setter
	private String memo;

	protected UserAccount(){}

	private UserAccount(String userId, String userPw, String email, String nickname, String memo) {
		this.userId = userId;
		this.userPw = userPw;
		this.email = email;
		this.nickname = nickname;
		this.memo = memo;
	}

	public static UserAccount of(String userId, String userPw, String email, String nickname, String memo){
		return new UserAccount(userId, userPw, email, nickname, memo);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserAccount that = (UserAccount) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
