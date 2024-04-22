package com.latte;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {



    /**
     * 홈
     * @return
     */
    @GetMapping("")
    public String home() {
        return "Latte Server";
    }

}
