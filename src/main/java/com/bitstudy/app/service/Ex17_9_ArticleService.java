package com.bitstudy.app.service;


import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.repository.ArticleRepository;
import com.bitstudy.app.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


/** 게시판 페이지 페이지네이션 */
@Slf4j
@Service
@RequiredArgsConstructor // 필수 필드에 대한 생성자를 자동으로 만들어주는 롬복 애너테이션
@Transactional // 이 클래스 동작하다가 하나라도 잘못되면 다시 롤백 시켜라 라는 뜻
public class Ex17_9_ArticleService {

    private final ArticleRepository articleRepository; // 아티클 관련 서비스 이기 때문에 ArticleRepository 필요

/*추가*/private final UserAccountRepository userAccountRepository; // 유저 정보 사용


    /* 1. 게시판 페이지로 이동 */
    // 검색용
    @Transactional(readOnly = true) // 트랜잭션을 읽기 전용 모드로 설정. 실수로 커밋해도 flush 가 되지 않아서 엔티티가 등록, 수정, 삭제 가 되지 않는다.
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
//        return Page.empty();
        /* Page: 전체 데이터 (건수)조회 (데이터를 다 가지고 있다)
                 getTotalElements(): 개수 뽑기
                 getTotalPages(): 별도의 size 를 줘서 총 페이지 개수
                 getNumber(): 가져온 페이지의 번호를 뽑아준다.

        *  Pageable: 페이징 기능
                     JPA에서 DB 쿼리 날릴때 limit 를 날려서 데이터 가져옴 */

        /** ArticleServiceTest 의 withoutKeywordReturnArticlesAll() 부분 관련 실제 구현
         When 부분에서 searchArticles 메서드를 이용해서 값들을 전달한다.
         사실 검색 관련된 부분이기 때문에 검색어인 searchKeyword 를 이용해서 코드를 짤거임.
         * */

        /* 검색어 없는 경우 */
        if(searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
            // Page 클래스 맨 아래 map() 이 있다. 이건 Page 안의 내용물을 형변환해서 다시 페이지로 새로 만들어준다. (map() 이 article을 articleDto 로 바꿔서 리턴해줌)
        }

        /* 검색어 있는 경우 */
        /** searchType 은 enum 으로 되어있음.
         * enum 을 주제로 각각의 쿼리를 만들거임.
         * ex) TITLE 검색시 제목 검색 쿼리, ID 검색시 아이디 검색 쿼리 반환 */

        return switch(searchType) {
            /** 원래 switch case 방식
               cast TITle:
                    코드
                    break;
                이런 식이었는데 이제 '->' 키워드를 이용해서 알아서 리턴까지 할 수 있게 발전됨.
             * */

            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByhashtagContaining(searchKeyword, pageable).map(ArticleDto::from);
        };

    }


    /* 2. 게시글 페이지로 이동 */
    /* 게시글 하나 호출 */

    /** 게시글 하나 호출 */
    /*추가 - 게시글만 다루는 메서드 */
    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

/* 추가 - 메서드 이름 변경함 getArticle  에서  getArticleWithComments  로  변경 
*         게시글 이랑 댓글 다 같이 다루는 메서드*/
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId) // articleId 로 게시글 하나 잡을거니까 findById
                .map(ArticleWithCommentsDto::from) // Dto 로 변환해줘야 해서 map() 사용
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }


/* -------------------------------------------------------------------------------- */
/* 여기서부터 변화 있음. - 모든 게시글 생성, 수정, 삭제 에서는 사용자 정보를 같이 가지고 다닐거임. */
/* -------------------------------------------------------------------------------- */

    /* 게시글 생성*/
    public void saveArticle(ArticleDto dto) {
        /** select 구문때 처럼 DB 에서 가져올 정보가 없기 때문에 그냥 void 라서 return 이 없다.  */

        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

/*삭제*/ //articleRepository.save(dto.toEntity());
        // toEntity 를 이용해서 매개변수로 받은 dto(단순히 데이터를 가지고 있기만 한. 누가 어떤 정보인지 모르는 상태) 정보로부터 엔티티를 하나 만들어서 세이브 하는 코드
/*추가*/ articleRepository.save(dto.toEntity(userAccount));
        /* ArticleDto.java 변경해야함. */
    }

    /* 게시글 수정*/
/*삭제*///public void updateArticle(ArticleDto dto) {
/*추가*/public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
/*추가*/     UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

System.out.println("여기: " + (dto));
/*추가 - 유저가 동일한지 검사함.
        article에서 영속성으로 게시글정보랑 같이 가져온 유저정보가 userAccount 에 담겨있는 유저정보와 같은지 동일한지 확인하는거*/
//            if (article.getUserAccount().equals(userAccount)) {
                /** 매개변수 dto 로 받은 title, content 는 not null 필드이기 때문에 if로 감싸서 null 이 아닌 경우에만 동작하도록 함. */
                if (dto.title() != null) {
                    article.setTitle(dto.title());
                }
                if (dto.content() != null) {
                    article.setContent(dto.content());
                }

                article.setHashtag(dto.hashtag()); /** null 가능 필드라서 if문으로 검사할 필요 없다.*/
//            }
        } catch (EntityNotFoundException e) {
            // 수정 정보를 넣었을때 게시글이 없어진 경우
            log.warn("게시글 업데이트 실패. 게시글을 찾을수 없습니다. dto:" + dto);
        }

    }
    
    /* 게시글 삭제*/
/*삭제*/public void deleteArticle(long articleId) {
/*생성*///public void deleteArticle(long articleId, String userId) {

        articleRepository.deleteById(articleId);
        /** 이거 바꾸면 ArticleRepository.java  랑  ArticleServiceTest.java 가서도 바꿔야함*/
    }
    
    /* 게시글 개수 구하기 - 마지막 글일때는 '다음' 버튼 비활성화 시켜야 하기 때문. */
    public long getArticleCount() {
        return articleRepository.count();
    }
}










