package com.example.entity.vo.request;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsSaveVO {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    String username;
    @Min(0)
    @Max(1)
    int gender;
    @Pattern(regexp = "^1[3456789]\\d{9}$")
    @Length(max=11)
    String phone;
    @Length(max=13)
    String qq;
    @Length(max=20)
    String wechat;
    @Length(max=240)
    String desc;
}
