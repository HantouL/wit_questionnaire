package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.Questionnaire;
import com.example.entity.vo.response.QuestionnaireListVO;

import java.util.Date;
import java.util.List;

public interface QuestionnaireService extends IService<Questionnaire> {
    List<Questionnaire> getQuestionnaireList();
    List<Questionnaire> getActiveQuestionnaireList();

    List<Questionnaire> getQuestionnaireListById(int id);

    List<QuestionnaireListVO> getQuestionnaireListByIdList(List<Integer> qIdList);

    Integer createQuestionnaire(String questionnaireName,Date createTime,String questionnaireDesc,int creatorId,int status);

    boolean changeStatusById(Integer questionnaireId,int status);

    Questionnaire getQuestionnaireById(int id);
}
