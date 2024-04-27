package com.latte.menu.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MenuSearchRankingResponse implements Serializable {
    private int rank;
    private String word;
}
