package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor // 필수 필드에 대한 생성자를 자동으로 만들어주는 롬복 애너테이션
@Transactional // 이 클래스 동작하다가 하나라도 잘못되면 다시 롤백 시켜라 라는 뜻.
public class ArticleService {

	private final ArticleRepository articleRepository; // 아티클 관련 서비스이기 때문에 ArticleRepository 필요

	// 검색용
	@Transactional(readOnly = true) // 트랜잭션을 읽기 전용모드로 설정. 실수로 커밋해도 flush가 되지 않아서 엔티티가 등록, 수정, 삭제가 되지 않는다.
	public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable){
//		return
		/** Page : 전체 데이터 조회 (데이터를 다 가지고 있다)
		   		getTotalElements() : 개수 뽑기
		   		getTotalPages() : 별도의 size 를 줘서 총 페이지 개수 구할 때
		   		getNumber()	: 가져온 페이지의 번호

		  * Pageable : 페이징 기능
		 		JPA에서 DB 쿼리 날릴 때 limit 를 줘서 데이터를 가져온다. */

		/** ArticleServiceTest 의 withoutKeywordReturnArticlesAll() 부분 관련 실제 구현
		 *  When 부분에서 searchArticles 메서드를 이용해서 값들을 전달한다.
		 *  사실 검색 관련된 부분이기 때문에 검색어인 searchKeyword 를 이용해서 코드를 작성할거다.*/

		/* 검색어 없는 경우 */
		if(searchKeyword == null || searchKeyword.isBlank()) {
			return articleRepository.findAll(pageable).map(ArticleDto::from);
			// Page 클래스 맨 아래 map()이 있다. 이건 Page 안의 내용물을 형변환해서 다시 페이지로 새로 만들어 준다.(map()이 article 을 articleDto 로 바꿔서 리턴해준다.)
		}

		/* 검색어 있는 경우 */
		/** searchType 은 enum 으로 되어있다.
		 *  enum 을 주제로 각각의 쿼리를 만들거다.
		 *  e.g., TITLE 검색 시 제목 검색 쿼리, ID 검색 시 아이디 검색 쿼리 반환 */

		return switch (searchType) {
			case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
			case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
			case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
			case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
			case HASHTAG -> articleRepository.findByHashtagContaining(searchKeyword, pageable).map(ArticleDto::from);
		};
	}

	//	게시글 하나 호출
	@Transactional(readOnly = true)
	public ArticleWithCommentsDto getArticle(Long articleId) {
		return articleRepository.findById(articleId).map(ArticleWithCommentsDto::from).orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
	}

	// 게시글 생성
	@Transactional(readOnly = true)
	public void saveArticle(ArticleDto dto) {
		/** select 구문 때 처럼 DB에서 가져올 정보가 없기 때문에 그냥 void 라서 return 이 없다. */
		articleRepository.save(dto.toEntity());
	}

	// 게시글 수정
	@Transactional(readOnly = true)
	public void updateArticle(ArticleDto dto){
		try {
			Article article = articleRepository.getReferenceById(dto.id());

			if (dto.title() != null) {
				article.setTitle(dto.title());
			}

			if (dto.content() != null) {
				article.setContent(dto.content());
			}

			article.setHashtag(dto.hashtag());


		} catch (EntityNotFoundException e) {
			log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다. dto: " + dto);
		}
	}

	// 게시글 삭제
	@Transactional(readOnly = true)
	public void deleteArticle(long articleId) {
		articleRepository.deleteById(articleId);
	}

	// 게시글 개수 구하기 - 마지막 글일때는 '다음' 버튼 비활성화 시켜야 하기 때문.
	public long getArticleCount(){
		return articleRepository.count();
	}
}
