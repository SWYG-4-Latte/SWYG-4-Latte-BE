package com.latte.menu.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrandType {
    STARBUCKS("스타벅스", "https://upload.wikimedia.org/wikipedia/en/thumb/d/d3/Starbucks_Corporation_Logo_2011.svg/1200px-Starbucks_Corporation_Logo_2011.svg.png"),
    COMPOSE("컴포즈", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvli9CjxHPgJxDR1IbUhk35awK3TOLEt5e1qTc-3ijCQ&s"),
    TWOSOME("투썸플레이스", "https://www.twosome.co.kr/resources/images/content/bi_img_logo_.svg"),
    PAIK("빽다방", "https://firebasestorage.googleapis.com/v0/b/latte-e8266.appspot.com/o/327294194_929628688477291_6295134427025366399_n.png?alt=media"),
    EDIYA("이디야", "https://ediyastore.com/web/upload/goodymall/kr/main/logo01.png");

    String value;
    String imageUrl;
}
