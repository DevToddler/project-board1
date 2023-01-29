package com.bitstudy.app.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 /** 통합테스트로 변경해서 테스트 할거임 */

@Disabled("Spring Data Rest 통합데이터는 현재 불필요하므로 제외시킴") /* 클래스 레벨에 붙여서 해당 테스트 클래스의 모든 메서드들을 체크하지 않게 한다. 
   이유는 테스트가 다 통과한걸 확인했고, 당장 개발하는데 계속 돌릴 필요 없기 때문*/
@SpringBootTest /* 이것만 있으면 MockMvc 를 알아볼 수가 없어서 @AutoConfigureMockMvc 도 같이 넣기 */
@AutoConfigureMockMvc
@DisplayName("Data REST - API 테스트")
@Transactional /* 테스트 돌리면 Hibernate 부분에 select 쿼리문이 나오면서 실제 DB 를 건드리는데, 테스트 끝난 이후에 DB를 롤백 시키는 용도 */

public class DataRestTest {
    private final MockMvc mvc; /* 1) MockMvc 생성 (빈 준비)*/

    public DataRestTest(@Autowired MockMvc mvc) { /* 2) MockMvc 에게 요청에 대한 정보를 입력(주입) */
        this.mvc = mvc;
    }

    
//    @Disabled("구현중")
    /*  [api] - 게시글 리스트 전체 조회 */
    @DisplayName("[api] - 게시글 리스트 전체 조회")
    @Test
    void articleAll() throws Exception {

            mvc.perform(get("/api/articles"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }
     /*  [api] - 게시글 단건 조회 */
     @DisplayName("[api] - 게시글 단건 조회")
     @Test
     void articleOne() throws Exception {

         mvc.perform(get("/api/articles/1"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
     }


     //////////////////////////////////////////
     @DisplayName("[api] - 댓글 리스트 전체 조회")
     @Test
     void articleCommentAll() throws Exception {

         mvc.perform(get("/api/articleComments"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
     }


     @DisplayName("[api] - 댓글 단건 조회")
     @Test
     void articleCommentOne() throws Exception {

         mvc.perform(get("/api/articleComments/1"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
     }


     @DisplayName("[api] - 게시글의 댓글 리스트 조회")
     @Test
     void articleCommentAllByArticle() throws Exception {

         mvc.perform(get("/api/articles/1/articleComments"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
     }
}










