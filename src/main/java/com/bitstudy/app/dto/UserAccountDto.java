package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDto(
        // Long id, // 이제 실제로 돌릴때 id 는 우리가 안만들거라서 필요 없다.
        String userId,
        String userPassword,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

/*삭제*/
    /*public static UserAccountDto of(Long id, String userId, String userPassword, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto( id, userId, userPassword, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }*/

/* 원래꺼에서 Long id 부분 뺀거*/
    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto( userId, userPassword, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }
    /*새로 추가 - createdAt, createdBy, modifiedAt, modifiedBy 이 정보들은 데이터베이스에 영속화를 시킬때 DB에 의해서 자동으로 들어가는 값이다. 우리는 그걸 위해 JPA Auditing 을 사용해서 자동으로 값을 만들고 있다. 그래서 영속화된 정보를 불러올때는 문제가 없는데, save 를 위해서 DTO 를 만들고 repository에 저장을 할때에는 이 값들이 없다. 그래서 별도로 null 값을 가지는 메서드를 하나 더 만들었다. */
    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo) {
        return new UserAccountDto(userId, userPassword, email, nickname, memo, null, null, null, null);
    }

    /////////////////////////////////////////////////////////////////////////////

    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
/*삭제*/         //entity.getId(),
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    /* 위에거랑 반대. dto 를 주면 엔티티를 생성하는 메서드 */
    public UserAccount toEntity() {
        return UserAccount.of(
                userId,
                userPassword,
                email,
                nickname,
                memo
        );
    }




}








