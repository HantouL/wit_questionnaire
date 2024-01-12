package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.AnswerDetails;
import com.example.entity.vo.request.SubmitQuestionnaireAnswerVO;
import com.example.entity.vo.response.MyAnswerDetailsVO;

import java.util.List;

public interface AnswerDetailsService extends IService<AnswerDetails> {
    Boolean saveAnswerDetails(List<SubmitQuestionnaireAnswerVO> voList, int answerId);
    List<MyAnswerDetailsVO> getMyAnswerDetailsVOListByAnswerId(int answerId);
    List<String> getMyanswerByQuestionnaireDetailsId(int qdId);
}
