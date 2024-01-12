package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class QuestionnaireListVO {
    Integer id;
    String name;
    Date createTime;
    @TableField("`desc`")
    String desc;
    int status;
}
