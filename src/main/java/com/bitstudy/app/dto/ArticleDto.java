package com.bitstudy.app.dto;


import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

/** record 란
 *  자바 16버전에서 새로 나온 것.
 *  Dto와 비슷. Dto를 구현하려면 getter, setter, equals, hashcode, toString 같은 데이터 처리를 수행하기 위해 오버라이드 된 메서드를 반복해서 작성해야 한다.
 *  이런것들을 보일러 플레이트 코드(여러곳에서 재사용 되는 반복적으로 비슷한 형태를 가진 코드)라고 한다.
 *  롬복을 이용하긴 하지만 근본적인 한계는 해결 못한다.
 *  그래서 특정 데이터와 관련있는 필드들만 묶어놓은 자료구조로 record 라는 것이 생겼다.
 *
 *  *주의 : record는 entity로 쓸 수 없다. Dto로만 가능
 *  		이유는 쿼리 결과를 매핑할 때 객체를 인스턴스화 할 수 있도록 매개변수가 없는 생성자가 필요하지만,
 *  		record에서는 매개변수가 없는 생성자(기본생성자)를 제공하지 않는다. 또한 setter도 사용할 수 없다.(그래서 모든 필드의 값을 입력한 후에 생성할 수 있다.)
 *
 * */
public record ArticleDto(
		Long id,
		UserAccountDto userAccountDto,
		String title,
		String content,
		String hashtag,
		LocalDateTime createdDate,
		String createdBy,
		LocalDateTime modifiedDate,
		String modifiedBy) {

	public static ArticleDto of(Long id,
								UserAccountDto userAccountDto,
								String title,
								String content,
								String hashtag,
								LocalDateTime createdDate,
								String createdBy,
								LocalDateTime modifiedDate,
								String modifiedBy){
		return new ArticleDto(id, userAccountDto, title, content, hashtag, createdDate, createdBy, modifiedDate, modifiedBy);
	}

	/** entity를 매개변수로 입력하면 ArticleDto로 변환해주는 메서드.
	 *
	 * entity를 받아서 new 로 ArticleDto 인스턴스 생성
	 *
	 * */
	public static ArticleDto from(Article entity){
		return new ArticleDto(
				entity.getId(),
				UserAccountDto.from(entity.getUserAccount()),
				entity.getTitle(),
				entity.getContent(),
				entity.getHashtag(),
				entity.getCreatedDate(),
				entity.getCreatedBy(),
				entity.getModifiedDate(),
				entity.getModifiedBy()
		);
	}

	public Article toEntity(){
		return Article.of(
				userAccountDto.toEntity(),
				title,
				content,
				hashtag
		);
	}

}
