package com.latte.menu.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {
    COFFEE("커피", "https://postfiles.pstatic.net/MjAyNDA3MjZfMTcy/MDAxNzIxOTg3MjYwOTYx.EjG4i3fNZc-VOTcuQUM4V-huZ0x8GpkbpK9OF_xdQ4cg.tBUv9Qpj4KId1j-fysR11AkezLgn8QZ9ACl4xaR-ifsg.PNG/image_coffee.png?type=w773"),
    NONCOFFEE("논커피 라떼", "https://postfiles.pstatic.net/MjAyNDA3MjZfMjE3/MDAxNzIxOTg3MjYwOTU3.LAzG_ajD1ordQ3M_NJpoKmqYIc8JhW5wwKEhkVCYFY4g.ELIP3yCh8efzhl16_l3vTLleYyjY6GIxonU_FH74ANQg.PNG/image_noncoffeelatte.png?type=w773"),
    TEA("티/에이드/주스", "https://postfiles.pstatic.net/MjAyNDA3MjZfMTQg/MDAxNzIxOTg3MjYwOTU4.qs8nwYkZ5zfnHpqpaWpyzs5eX_j8Sv2Y2R5IemioFTsg.QP1Qa8dg6CPp_U3QPPxVkqFn7saR3cB0mlpk2vd0T-Yg.PNG/image_juice.png?type=w773"),
    SMOOTHIE("스무디", "https://postfiles.pstatic.net/MjAyNDA3MjZfMjI2/MDAxNzIxOTg3MjYwOTUw.q52aidM5oWflXgqrniTm1x0WlN3n7TTBY6XXfp9zPSkg.UQ0xz2P4YGaAK4-4SuXWYqJg3BaXA0U_eydQsYWMKU8g.PNG/image_smoothie.png?type=w773");

    String value;
    String imageUrl;
}
