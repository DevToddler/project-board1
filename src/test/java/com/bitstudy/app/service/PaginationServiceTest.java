package com.bitstudy.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*; // 직접 입력했음
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;

/** webEnvironment : 기본값 Mock. Mocking 한 웹 환경을 구성. None 을 넣어서 웹 환경을 넣지 않아서 부트 테스트를 가볍게 할 수 있다.
 	classes = PaginationService.class 를 이용해서 설정 클래스를 지정해서 더 가볍게 할 수 있다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class PaginationServiceTest {

	private final PaginationService sut;

	public PaginationServiceTest(@Autowired PaginationService sut) { this.sut = sut;}

	/** 파라미터 테스트
	 @ParameterizedTest 란 한번에 여러 argument 를 등록해놓고 한번에 여러번 돌릴 수 있는 기능을 가지고 있는 테스트 애너테이션.
	 (해당 기능을 제외하면 @Test 와 같다. 그래서 여기선 @Test 를 붙이지 않는다.)
	 @ParameterizedTest  사용 시 테스트를 위해서 들어가는 값이나 객체들이 필요한데 그걸 Source 라고 한다.

	 Source 종류 세가지
	 1) ValueSource - 같은 타입의 여러가지 단순한 값(literal value)들을 테스트 할 때 사용
	 					ex) @ValueSource(ints = {0, 101}
	 2) CsvSource - 콤마로 구분되는 값들 사용
	 					ex) @CsvSource({"10, true" , "100, false"})
	 3) MethodSource - 메서드에서 리턴되는 값들을 인자로 사용
	 					* @MethodSource => 웬만하면 이걸 쓰면 된다. 입력값이 별도의 메서드로 있어야 한다.
	 */
	@ParameterizedTest(name = "[{index}] 현재 페이지 : {0}, 총페이지:{1}  =>  {2}")
	/** name 을 이용해서 출력포멧을 설정할 수 있다.(@DisplayName 과 비슷한 것이다)
	 	{0}, {1}, {2} 의 뜻은 매개변수 순서
	 	=>  {0} : int currPageNumber
	 		{1} : int totalPages
	 		{2} : List<Integer> expected
	 */
	@MethodSource /** 이름을 따로 주지 않으면 이름이 같은 메서드 불러온다. 원래라면 @MethodSource("메서드 명")*/
	@DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리스트 만들기")
	void givenCurrPageNumberAndTotalPages_whenReturnBarList(int currPageNumber, int totalPages, List<Integer> expected){
		// Given

		// When
		List<Integer> actual = sut.getPaginationBarNumbers(currPageNumber, totalPages);

		// Then
		assertThat(actual).isEqualTo(expected);
	}

	static Stream<Arguments> givenCurrPageNumberAndTotalPages_whenReturnBarList(){
		return Stream.of(
				/** arguments 는 딥다이브 방식으로  org.junit.jupiter.params.provider.Arguments 임포트하기
				 	givenCurrPageNumberAndTotalPages_whenReturnBarList 메서드의 매개변수 순서에 맞춘 것
				 	한 페이지 당 몇개 씩 보여줄지는 ArticleController 의 article() 메서드에서 @PageableDefault(size = 10) 부분에 설정해 놓음
				 */
				arguments(0, 10, List.of(0, 1, 2, 3, 4)),
				arguments(1, 10, List.of(0, 1, 2, 3, 4)),
				arguments(2, 10, List.of(0, 1, 2, 3, 4)),
				arguments(3, 10, List.of(1, 2, 3, 4, 5)),
				arguments(4, 10, List.of(2, 3, 4, 5, 6)),
				arguments(5, 10, List.of(3, 4, 5, 6, 7)),
				arguments(6, 10, List.of(4, 5, 6, 7, 8)),
				arguments(7, 10, List.of(5, 6, 7, 8, 9)),
				arguments(8, 10, List.of(6, 7, 8, 9)),
				arguments(9, 10, List.of(7, 8, 9))
		);
	}

	@DisplayName("현재 설정되어 있는 페이지네이션 바의 길이 알아내기")
	@Test
	void givenNothing_whenCallMethod(){
		// Given
		// When
		int barLength = sut.currentBarLength();
		// Then
		assertThat(barLength).isEqualTo(5);
	}
}