package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.hibernate.validator.internal.util.logging.formatter.ClassObjectFormatter;

/**
 * RestBean
 * @param code 状态码
 * @param data 数据
 * @param message 消息
 * @param <T>
 */
public record RestBean<T>(int code,T data,String message) {
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200,data,"请求成功!");
    }

    public static <T> RestBean<T> success() {
        return success(null);
    }

    public static <T> RestBean<T> unauthorized(String message){
        return failure(401,message);
    }

    public static <T> RestBean<T> forbidden(String message){
        return failure(403,message);
    }

    public static <T> RestBean<T> failure(int code,String message){
        return new RestBean<>(code,null,message);
    }

    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

}
