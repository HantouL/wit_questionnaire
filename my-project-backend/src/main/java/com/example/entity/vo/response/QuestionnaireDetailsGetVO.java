package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionnaireDetailsGetVO {
    int id;
    int type;
    @TableField("`desc`")
    String desc;
    String options;
}
