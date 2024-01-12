package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("db_questionnaire")
@AllArgsConstructor
@NoArgsConstructor
public class Questionnaire implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer creatorId;
    String name;
    Date createTime;
    @TableField("`desc`")
    String desc;
    Integer status;
}
