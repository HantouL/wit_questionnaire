package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("db_answer_details")
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDetails implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    int answerId;
    int questionnaireDetailsId;
    String myanswer;
}
