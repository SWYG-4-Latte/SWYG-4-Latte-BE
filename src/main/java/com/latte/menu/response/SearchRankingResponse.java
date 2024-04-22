package com.latte.menu.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class SearchRankingResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int rank;
    private String word;
}
