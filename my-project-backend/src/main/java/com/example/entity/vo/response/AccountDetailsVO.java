package com.example.entity.vo.response;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AccountDetailsVO {
    int gender;
    String phone;
    String qq;
    String wechat;
    String desc;
}
