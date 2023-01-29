package com.bitstudy.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/** 할일: URL 에 localhost:8080/ 만 쳐도 articles 페이지가 알아서 잡히게 하는거 */

/* 컨트롤러로 등록하면 원래 MainController 랑 맵핑 겹쳐서 에러뜸. 그래서 주석처리함 */
//@Controller
public class Ex20_2_MainController_페이지_자동_이동 {

    @GetMapping("/")
    public String root() {
//        return "redirect:/articles";
        return "forward:/articles";

    }
}

