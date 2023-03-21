package com.ywh.ywh_caffeine.vo;

import lombok.Data;


@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultVo<T> {

    private T data;

    private String message;

    private boolean success;

}
