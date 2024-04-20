package com.latte;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {



    /**
     * 홈
     * @return
     */
    @GetMapping("")
    public String home() {
        return "home";
    }

}
