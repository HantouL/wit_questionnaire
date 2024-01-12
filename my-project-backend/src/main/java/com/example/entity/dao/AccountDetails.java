package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("db_account_details")
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer accountId;
    int gender;
    String phone;
    String qq;
    String wechat;
    @TableField("`desc`")
    String desc;
}
