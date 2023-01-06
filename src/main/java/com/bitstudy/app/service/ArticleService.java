package com.bitstudy.app.service;

import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor // 필수 필드에 대한 생성자를 자동으로 만들어주는 롬복 애너테이션
@Transactional // 이 클래스 동작하다가 하나라도 잘못되면 다시 롤백 시켜라 라는 뜻.
public class ArticleService {

	private final ArticleRepository articleRepository; // 아티클 관련 서비스이기 때문에 ArticleRepository 필요

	// 검색용
	@Transactional(readOnly = true) // 트랜잭션을 읽기 전용모드로 설정. 실수로 커밋해도 flush가 되지 않아서 엔티티가 등록, 수정, 삭제가 되지 않는다.
	public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable){
		return Page.empty();
	}
}
