package com.example.entity.vo.request;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswerRequestVO {
    private List<SubmitQuestionnaireAnswerVO> voList;
    private int accountId;
    private int questionnaireId;
}
