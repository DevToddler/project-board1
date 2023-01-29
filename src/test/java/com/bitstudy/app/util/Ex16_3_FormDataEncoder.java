package com.bitstudy.app.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/* form data는 api와는 다른 형태로 직렬화하여 사용해야 한다.
객체를 규약에 맞게 form data 포캣의 문자열로 바꿔주는 유틸리티를 테스트 전용으로 만들고, 테스트도 함께 작성한다.
* */
@TestComponent
public class Ex16_3_FormDataEncoder {

    private final ObjectMapper mapper;

    public Ex16_3_FormDataEncoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    public String encode(Object obj) {
        Map<String, String> fieldMap = mapper.convertValue(obj, new TypeReference<>() {});
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.setAll(fieldMap);

        return UriComponentsBuilder.newInstance()
                .queryParams(valueMap)
                .encode()
                .build()
                .getQuery();
    }

}
