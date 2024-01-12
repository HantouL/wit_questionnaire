package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.AnswerDetails;
import com.example.entity.vo.request.SubmitQuestionnaireAnswerVO;
import com.example.entity.vo.response.MyAnswerDetailsVO;
import com.example.mapper.AnswerDetailsMapper;
import com.example.service.AnswerDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerDetailsServiceImpl extends ServiceImpl<AnswerDetailsMapper, AnswerDetails> implements AnswerDetailsService {
    @Override
    public Boolean saveAnswerDetails(List<SubmitQuestionnaireAnswerVO> voList, int answerId) {
        if(answerId==-1) return false;
        voList.forEach(vo->{
            AnswerDetails answerDetails = new AnswerDetails();
            answerDetails.setAnswerId(answerId);
            answerDetails.setQuestionnaireDetailsId(vo.getQuestionnaire_details_id());
//            System.out.println(vo.getQuestionnaireDetailsId());
            answerDetails.setMyanswer(vo.getMyanswer());
            this.save(answerDetails);
        });
        return true;
    }

    @Override
    public List<MyAnswerDetailsVO> getMyAnswerDetailsVOListByAnswerId(int answerId) {
        List<AnswerDetails> answerList = this.query().eq("answer_id", answerId).list();
        List<MyAnswerDetailsVO> myAnswerDetailsList = new ArrayList<>();
        answerList.forEach(answer->{
            myAnswerDetailsList.add(new MyAnswerDetailsVO(answer.getQuestionnaireDetailsId(),answer.getMyanswer()));
        });
        return myAnswerDetailsList;
    }

    @Override
    public List<String> getMyanswerByQuestionnaireDetailsId(int qdId) {
        return this.query()
                .eq("questionnaire_details_id",qdId)
                .list()
                .stream()
                .map(AnswerDetails::getMyanswer)
                .collect(Collectors.toList());
    }
}
