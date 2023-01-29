package com.bitstudy.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/** 할일: URL 에 localhost:8080/ 만 쳐도 articles 페이지가 알아서 잡히게 하는거 */


@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
//        return "redirect:/articles";
        return "forward:/articles";

    }
}

