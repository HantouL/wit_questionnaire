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
@TableName("db_questionnaire_details")
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireDetails implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer questionnaireId;
    Integer type;
    @TableField("`desc`")
    String desc;
    String options;
}
