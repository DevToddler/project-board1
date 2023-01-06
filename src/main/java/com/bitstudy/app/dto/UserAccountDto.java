package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDto(
		Long id,
		String userId,
		String userPw,
		String email,
		String nickname,
		String memo,
		LocalDateTime createdDate,
		String createdBy,
		LocalDateTime modifiedDate,
		String modifiedBy
) {

	public static UserAccountDto of(Long id, String userId, String userPw, String email, String nickname, String memo, LocalDateTime createdDate, String createdBy, LocalDateTime modifiedDate, String modifiedBy) {
		return new UserAccountDto( id, userId, userPw, email, nickname, memo, createdDate, createdBy, modifiedDate, modifiedBy);
	}

	/////////////////////////////////////////////////////////////////////////////

	public static UserAccountDto from(UserAccount entity) {
		return new UserAccountDto(
				entity.getId(),
				entity.getUserId(),
				entity.getUserPw(),
				entity.getEmail(),
				entity.getNickname(),
				entity.getMemo(),
				entity.getCreatedDate(),
				entity.getCreatedBy(),
				entity.getModifiedDate(),
				entity.getModifiedBy()
		);
	}

	/* 위에거랑 반대. dto 를 주면 엔티티를 생성하는 메서드 */
	public UserAccount toEntity() {
		return UserAccount.of(
				userId,
				userPw,
				email,
				nickname,
				memo
		);
	}




}








