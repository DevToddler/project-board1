package com.bitstudy.app.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafConfig {

	/** @Bean 영역에 thymeleaf3Properties, @ConfigurationProperties 에 빨간 밑줄 생긴다.
	 *  이를 처리하기 위해서 메인파일(AppApplication.java)로 가서
	 *  @ConfigurationPropertiesScan 넣기 애너테이션 넣기.
	 * */

	@Bean
	public SpringResourceTemplateResolver thymeleafTemplateResolver(
			SpringResourceTemplateResolver defaultTemplateResolver,
			Thymeleaf3Properties thymeleaf3Properties

			/** thymeleafTemplateResolver 라는 빈을 등록하는데 리턴타입은 SpringResourceTemplateResolver 이다.
			 * 그런데 이게 타임리프 auto-configuration 을 불렀을 때 자동으로 잡힌다. 그런데 decoupledLogic 을 세팅하는건 이미 만들어져 있다.
			 * (외부 프로퍼티라서 인식 못할 뿐). 그래서 인식할 수 있게 application.yaml 파일 가서 thymeleaf를 별도로 등록해주어야한다.*/
	) {

		defaultTemplateResolver.setUseDecoupledLogic(thymeleaf3Properties.isDecoupledLogic());
		return defaultTemplateResolver;
	}


	@RequiredArgsConstructor
	@Getter
	@ConstructorBinding
	@ConfigurationProperties("spring.thymeleaf3")
	public static class Thymeleaf3Properties {
		private final boolean decoupledLogic;
	}
}
