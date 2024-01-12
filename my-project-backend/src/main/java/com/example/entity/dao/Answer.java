package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("db_answer")
@AllArgsConstructor
@NoArgsConstructor
public class Answer implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    int accountId;
    int questionnaireId;
    Date submitTime;
}
