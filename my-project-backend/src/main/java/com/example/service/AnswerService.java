package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.Answer;

import java.util.Date;
import java.util.List;

public interface AnswerService extends IService<Answer> {

    Integer saveAnswer(int accountId, int questionnaireId, Date submitTime);

    List<Integer> getMyAnswerQuestionnaireIdList(Integer accountId);

    Integer getAnswerIdByAccountIdAndQuestionnaireId(int accountId, int questionnaireId);
}
