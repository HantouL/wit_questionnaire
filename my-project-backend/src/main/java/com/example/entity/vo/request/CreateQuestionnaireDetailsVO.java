package com.example.entity.vo.request;

import lombok.Data;

@Data
public class CreateQuestionnaireDetailsVO {
    String desc;
    String options;
    int type;
}
