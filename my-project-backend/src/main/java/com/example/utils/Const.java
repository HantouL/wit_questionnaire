package com.example.utils;

import org.springframework.stereotype.Component;

@Component
public class Const {
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";

    public static final String VERIFY_EMAIL_DATA = "verify:email:data";

    public static final int ORDER_CORS = -102;
    public static final int ORDER_LIMIT = -101;

    public static final String FLOW_LIMIT_COUNTER = "flow:counter";
    public static final String FLOW_LIMIT_BLOCK = "flow:block";

}
