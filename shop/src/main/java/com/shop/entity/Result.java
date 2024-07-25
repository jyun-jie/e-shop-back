package com.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//負責回應controller request 是否成功
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <E> Result<E> success(E data){
        return new Result<>(0, "success", data);
    }

    public static Result success() {
        return new Result(0, "success", null);
    }

    public static Result error(String message) {
        return new Result(1, message, null);
    }

}
