package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.Answer;
import com.example.mapper.AnswerMapper;
import com.example.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {
    @Override
    public Integer saveAnswer(int accountId, int questionnaireId, Date submitTime) {
        Answer answer = new Answer();
        answer.setAccountId(accountId);
        answer.setQuestionnaireId(questionnaireId);
        answer.setSubmitTime(submitTime);
        if(this.query().eq("questionnaire_id",questionnaireId)
                .eq("account_id",accountId).exists())
            return -1;
        return this.save(answer) ? answer.getId():-1;//RMBR 这里的id是否是正确设置为自增后的ID?
    }

    @Override
    public List<Integer> getMyAnswerQuestionnaireIdList(Integer accountId) {
        QueryWrapper<Answer> queryWrapper= new QueryWrapper<>();
        queryWrapper.select("questionnaire_id").eq("account_id",accountId);
        List<Answer> answerList = this.baseMapper.selectList(queryWrapper);

        List<Integer> questionnaireIdList = new ArrayList<>();
        for (Answer a : answerList) {
            questionnaireIdList.add(a.getQuestionnaireId());
        }

        return questionnaireIdList;
    }

    @Override
    public Integer getAnswerIdByAccountIdAndQuestionnaireId(int accountId, int questionnaireId) {
        Answer a = this.query()
                .eq("account_id", accountId)
                .eq("questionnaire_id", questionnaireId)
                .one();
        return a.getId();
    }
}
