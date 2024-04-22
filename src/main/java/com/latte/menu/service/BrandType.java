package com.latte.menu.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrandType {
    STARBUCKS("스타벅스", "https://upload.wikimedia.org/wikipedia/en/thumb/d/d3/Starbucks_Corporation_Logo_2011.svg/1200px-Starbucks_Corporation_Logo_2011.svg.png"),
    COMPOSE("컴포즈", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvli9CjxHPgJxDR1IbUhk35awK3TOLEt5e1qTc-3ijCQ&s"),
    TWOSOME("투썸", "https://www.twosome.co.kr/resources/images/content/bi_img_logo_.svg"),
    PAIK("빽다방", "https://scontent-gmp1-1.xx.fbcdn.net/v/t39.30808-1/327294194_929628688477291_6295134427025366399_n.png?stp=dst-png_p200x200&_nc_cat=108&ccb=1-7&_nc_sid=5f2048&_nc_ohc=FQWfDu-IezcAb4WWJNr&_nc_oc=Adig29tvYscarzzP3LQS2iC9Vtl77Ou03VRUeSynceYUrYtFs9PdjBPU19-GaymrjtU&_nc_ht=scontent-gmp1-1.xx&oh=00_AfDZ8tNL14th8svkRKFOobFJaPKbFQjEAbeJGms-LP2GOg&oe=662C2B81"),
    EDIYA(" 이디야", "https://ediyastore.com/web/upload/goodymall/kr/main/logo01.png");

    String value;
    String imageUrl;
}
