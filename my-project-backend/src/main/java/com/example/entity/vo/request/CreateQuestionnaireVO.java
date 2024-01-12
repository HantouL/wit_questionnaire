package com.example.entity.vo.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionnaireVO {
    String questionnaireName;
    String questionnaireDesc;
    Integer creatorId;
    Integer status;
    List<CreateQuestionnaireDetailsVO> questionList;
}
