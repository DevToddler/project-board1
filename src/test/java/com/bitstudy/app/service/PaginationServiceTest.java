package com.bitstudy.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;

/* webEnvironment: 기본값 Mock. Mocking한 웹 환경 구성. None 을 넣어서 웹 환경을 넣지 않아서 부트 테스트를 가볍게 할 수 있다.
   classes = PaginationService.class 를 이용해서 설정클래스를 지정해서 더 가볍게 할 수 있다.
* */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
//@SpringBootTest
class PaginationServiceTest {

    /* 그냥 @SpringBootTest 쓸거면 new 필요 */
    //private final PaginationService sut = new PaginationService();

    private final PaginationService sut;
    public PaginationServiceTest(@Autowired PaginationService sut) {
        this.sut = sut;
    }


        /** 파라미터 테스트
        * @ParameterizedTest 란 여러 argument 를 등록해놓고 한번에 여러번 돌릴 수 있는 기능을 가지고 있는 테스트 애너테이션. 그걸 제외하면 @Test 와 같음.(그래서 여기서 @Test 생략함)
          @ParameterizedTest 사용시 테스트를 위해서 들어가는 값이나 객체들을 필요한데 그걸 Source 라고 한다.

          Source 종류 세가지
          1) ValueSource - 같은 타입의 여러 가지 단순한 값(literral value)들을ㅇ 테스트 할때 사용
                            ex) @ValueSource(ints = {0, 101}
          2) CsvSource - comma(,) 로 구분되는 값을 사용
                            ex) @CsvSource({"10, true" , "100, false"})
          3) MethodSource - 메소들에서 리턴되는 값을 인자로 사용
                            *왠만하면 이거 쓰면 됨
                            입력값이 별도의 메서드로 있어야 한다.
         *  */
    @ParameterizedTest(name="[{index}] 현재페이지: {0}, 총페이지: {1} => {2}")
        /** 그냥 @ParameterizedTest 만 써도 되는데 출력시 로그에 지저분하게 나온다. name 을 이용해서 출력 포멧을 설정할 수 있다.(@DisplayName 같은거임)
         * {0} {1} {2} 의 의미는 givenCurrPageNumberAndTotalPages_whenReturnBarList 메서드의 매개변수 순서.
         * {0} => int currentPageNumber
         * {1} => int totalPages
         * {2} => List<Integer> expected 를 의미*/
    @MethodSource /* 이게 있어야 Arguments메서드 가져온다. @MethodSource("연결할 메서드 명") 써주면 되는데, 생략하면 해당 테스트 메서드와 같은 이름꺼 가져온다. */
    
    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리스트 만들기")
    void givenCurrPageNumberAndTotalPages_whenReturnBarList(int currentPageNumber, int totalPages, List<Integer> expected) {
        // Given

        // When
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalPages);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenCurrPageNumberAndTotalPages_whenReturnBarList() {
        return Stream.of(
                
        /** arguments 는 딥다이브 해서 org.junit.jupiter.params.provider.Arguments 를 import 하기
            
            arguments(0, 10, List.of(0,1,2,3,4)) 에서 매개변수 순서는 위에 givenCurrPageNumberAndTotalPages_whenReturnBarList 메서드의 매개변수 순서에 맞춘것
            현재 페이지가 1페이지(여기선 0)고, 전체 페이지수는 10 이라고 설정, 
            현재 이상태로 돌리면 나와야 하는(expect) 결과는 (0,1,2,3,4) 로 예상된다는 뜻.
            전체 아티클 수가 100개니까 10개씩 하면 총 10페이지이고, 한페이지당 몇개씩 보여주는거는 
            ArticleController의 articles() 메서드에서 @PageableDefault(size = 10) 부분에 설정 해놓음

            * 주의: 1페이지는 0으로 표시된다. (-1씩 생각해야한다)
         
         * */
                
                arguments(0, 10, List.of(0,1,2,3,4)),
                arguments(1, 10, List.of(0,1,2,3,4)),
                arguments(2, 10, List.of(0,1,2,3,4)),
                arguments(3, 10, List.of(1,2,3,4,5)),
                arguments(4, 10, List.of(2,3,4,5,6)),
                arguments(5, 10, List.of(3,4,5,6,7)),
                arguments(6, 10, List.of(4,5,6,7,8)),
                arguments(7, 10, List.of(5,6,7,8,9)),
                arguments(8, 10, List.of(6,7,8,9)),
                arguments(9, 10, List.of(7,8,9))
        );
    }

    @DisplayName("현재 설정되어 있는 페이지네이션 바의 길이 알아내기")
    @Test
    void givenNothing_whenCallMethod() {
        // Given

        // When
        int barLength = sut.currentBarLength();

        // Then
        assertThat(barLength).isEqualTo(5);
    }
}