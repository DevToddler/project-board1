package com.bitstudy.app.controller;

import com.bitstudy.app.config.TestSecurityConfig;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.dto.request.ArticleCommentRequest;
import com.bitstudy.app.service.ArticleCommentService;
import com.bitstudy.app.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/* 새로 추가 - 여기는 테스트 파일이기 떄문에 TestSecurityConfig.class 를 사용해서 아까 만들었던 @BeforeTestMethod 꺼를 가져와서 사용한다. */
@Import({TestSecurityConfig.class, FormDataEncoder.class})
//@Import({SecurityConfig.class, FormDataEncoder.class})

@DisplayName("view 컨트롤러 - 게시글")
@WebMvcTest(Ex19_15_ArticleCommentControllerTest_댓글_사용자인증.class)
class Ex19_15_ArticleCommentControllerTest_댓글_사용자인증 {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private ArticleCommentService articleCommentService;


    Ex19_15_ArticleCommentControllerTest_댓글_사용자인증(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }


    @Test
    @DisplayName("[view][POST] 댓글 등록")
    void givenNewArticleCommentInfo_thenSaveArticleComment() throws Exception {
       // Given
        long articleId = 1L;
        ArticleCommentRequest request = ArticleCommentRequest.of(
                articleId,
                "test content"
        );
       willDoNothing().given(articleCommentService).saveArticleComment(any(ArticleCommentDto.class));


        // When
        mvc.perform(post("/comments/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED) /** Rest 템플릿으로 Post 전송시에 Form_UrlEncoded 를 사용해야함. */
                                .content(formDataEncoder.encode(request))
                                .with(csrf()) // import -> SecurityMockMvcRequestPostProcessors
                        /** csrf 검증도 같이 한다는 뜻*/

                )
                .andExpect(status().is3xxRedirection()) /** 그러면 작업이 끝나고 리다이렉션이 일어날거임 */
                .andExpect(view().name("redirect:/articles/" + articleId)) /** 이건 해당 뷰 파일명이 '/articles/아이디' 인지 확인. 정확히는 해당 컨트롤러에 return "뷰이름" 부분 말하는거임. */
                .andExpect(redirectedUrl("/articles/" + articleId)); /** 실제 리다이렉션 걸어주는걸 예상한다 라는 뜻 */
        then(articleCommentService).should().saveArticleComment(any(ArticleCommentDto.class));

        /** andExpect 는 컨트롤러 레이어에서 제대로 돌아갔는지를 확인함.
         then 부분은 테스트시 서비스 레이어에서 제대로 돌아갔는지 확인함.
          그래서 각자가 서로꺼를 못알아보게 함.*/
    }


    /*댓글 삭제할때는 WithUserDetails 쓴다. */
    @WithUserDetails(value="bitstudyTest",userDetailsServiceBeanName = "userDetailsService",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 댓글 삭제 - 정상 호출")
    @Test
    void givenArticleCommentId_thenDeletesArticleComment() throws Exception {
        // Given
        long articleId = 1L;
        long articleCommentId = 1L;
        /*추가*/ String userId = "bitstudyTest";
        /*삭제*/ //willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId);
        /*추가*/ willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId, userId);

        // When & Then
        mvc.perform(
                        post("/comments/" + articleCommentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        /*삭제*///then(articleCommentService).should().deleteArticleComment(articleCommentId);
        /*추가*/ then(articleCommentService).should().deleteArticleComment(articleCommentId, userId);
    }
}