package com.example.entity.vo.response;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireStatisticsVO {
    String questionnaireName; //表名
    String questionnaireDesc; //表简介
    List<QuestionnaireDetailsStatisticsVO> questionnaireDetailsStatisticsVOList; //表的答案数据列表
}
