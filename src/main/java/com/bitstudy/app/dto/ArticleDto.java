package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;



public record ArticleDto( /* 우선 엔티티가 가지고 있는 모든 정보를 dto도 가지고 있게 해서 나중에 응답할때 어떤걸 보내줄지 선택해서 가공하게 할거임 */
        Long id,
        UserAccountDto userAccountDto, /** 회원정보는 꼭 가지고 있어서 억지로 땡겨와서 넣음 */
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
        ) {

/*추가*/
    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, String hashtag) {
        return new ArticleDto(null, userAccountDto, title, content, hashtag, null, null, null, null);
    }
    
    
    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    /* entity를 매개변수로 입력하면 ArticleDto로 변환해주는 메서드.
    *
    * entity를 받아서 new 한 다음에 인스턴스에다가 entity. 이라고 해가면서 맵핑시켜서 return 하고 있는거
    * 맵퍼라고 부름.  */
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    /* 위에거랑 반대. dto 를 주면 엔티티를 생성하는 메서드 */
    // DTO 정보로 부터 엔티티를 하나 만들어서 세이브 하는 코드임
/*추가*/
    /** 기존코드는 toEntity() 를 실행하면, 여기서 또 UserAccountDto 에 있는 toEntity() 를 부르면서 타고 들어가게 했었는데, 지금은 toEntity 에 다이렉트로 매개변수 UserAccount를 받아서 별도로 다른데로 타고 들어갈 필요가 없도록 만들었다.   */
    public Article toEntity(UserAccount userAccount) {
        return Article.of(
                userAccount,
                title,
                content,
                hashtag
        );
    }

/*삭제*/
//    public Article toEntity() {
//        return Article.of(
//                userAccountDto.toEntity(),
//                title,
//                content,
//                hashtag
//        );
//    }
}










