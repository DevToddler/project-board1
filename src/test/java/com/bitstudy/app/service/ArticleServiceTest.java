package com.bitstudy.app.service;

import com.bitstudy.app.repository.ArticleRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

//import static org.junit.jupiter.api.Assertions.*;
import org.assertj.core.api.Assertions; // 임포트 임의로 작성해주고 static 삭제했음

/**
 * 서비스 비즈니스 로직은 슬라이스 테스트 기능 사용 안하고 만들어 볼거다.
 * 스프링부트 어플리케이션 컨셀그스가 뜨는데 걸리는 시간을 없애려고 한다.
 * 디펜던시가 추가돼야 하는 부분에는 Mocking 을 하는 방식으로 한다.
 * 그래서 많이 사용하는 라이브러리가 mokito 라는게 있다.(스프링 테스트 패키지에 내장되어 있음.)
 * @ExtendWith(MockitoExtension.class) 쓰면 된다.
 */
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
	/** Mock 을 주입하는 거에다가 @InjectMocks 을 달아줘야한다. 그 외의 것들에는 @Mock 달아준다.
	 * */
	@InjectMocks
	private ArticleService sut; // sut - system under test. 테스트 짤 때 사용하는 이름 중 하나.

	@Mock
	private ArticleRepository articleRepository; // 의존하는 걸 가져와야 함.(테스트 중간에 mocking 할 때 필요)




}