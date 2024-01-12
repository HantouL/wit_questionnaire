package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.QuestionnaireDetails;
import com.example.entity.vo.request.CreateQuestionnaireDetailsVO;
import com.example.entity.vo.response.QuestionnaireDetailsStatisticsVO;

import java.util.List;

public interface QuestionnaireDetailsService extends IService<QuestionnaireDetails> {
    List<QuestionnaireDetails> getQuestionnaireDetaisList(int qid);

    void createQuestionnaireDetails(Integer questionnaireId, List<CreateQuestionnaireDetailsVO> questionVoList);

    List<QuestionnaireDetailsStatisticsVO> getQuestionnaireDetailsStatistic(int qid);
}
