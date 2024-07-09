package com.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//負責處理 分頁內容
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPage<T>{
    private long pageNum;
    private List<T> Product;


}
