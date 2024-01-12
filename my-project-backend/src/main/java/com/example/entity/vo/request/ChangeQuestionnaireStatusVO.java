package com.example.entity.vo.request;

import lombok.Data;

@Data
public class ChangeQuestionnaireStatusVO {
    Integer questionnaireId;
    int status;
}
