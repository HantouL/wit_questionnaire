package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dao.Questionnaire;
import com.example.entity.dao.QuestionnaireDetails;
import com.example.entity.vo.response.QuestionnaireDetailsGetVO;
import com.example.entity.vo.response.QuestionnaireListVO;
import com.example.service.QuestionnaireDetailsService;
import com.example.service.QuestionnaireService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    @Resource
    QuestionnaireService questionnaireService;

    @Resource
    QuestionnaireDetailsService questionnaireDetailsService;

    @GetMapping("/activeQuestionnaireList")
    public RestBean<List<QuestionnaireListVO>> activeQuestionnaireList(){
        List<Questionnaire> questionnaireList = questionnaireService.getActiveQuestionnaireList();
        List<QuestionnaireListVO> list = convertToQuestionnaireListDTO(questionnaireList);
        return RestBean.success(list);
    }

    @GetMapping("/questionnaireDetails/{qid}")
    public RestBean<List<QuestionnaireDetailsGetVO>> questionnaireDetails(@PathVariable("qid") int id){
        List<QuestionnaireDetails> questionnaireDetaisList = questionnaireDetailsService.getQuestionnaireDetaisList(id);
        List<QuestionnaireDetailsGetVO> questionnaireDetailsGetVOList = convertToQuestionnaireDetailsGetVO(questionnaireDetaisList);
        return RestBean.success(questionnaireDetailsGetVOList);
    }

    @GetMapping("/getQuestionnaire/{qid}")
    public RestBean<Questionnaire>  getQuestionnaire(@PathVariable("qid")int qid){
        Questionnaire questionnaire = questionnaireService.getQuestionnaireById(qid);
        return questionnaire != null ? RestBean.success(questionnaire) : RestBean.failure(404,"没有找到对应问卷,请联系管理员");
    }

    private List<QuestionnaireListVO> convertToQuestionnaireListDTO(List<Questionnaire> questionnaireList) {
        return questionnaireList.stream()
                .map(questionnaire -> new QuestionnaireListVO(
                        questionnaire.getId(),
                        questionnaire.getName(),
                        questionnaire.getCreateTime(),
                        questionnaire.getDesc(),
                        questionnaire.getStatus()
                ))
                .collect(Collectors.toList());
    }

    private List<QuestionnaireDetailsGetVO> convertToQuestionnaireDetailsGetVO(List<QuestionnaireDetails> questionnaireDetaisList) {
        return questionnaireDetaisList.stream()
                .map(questionnaire -> new QuestionnaireDetailsGetVO(
                        questionnaire.getId(),
                        questionnaire.getType(),
                        questionnaire.getDesc(),
                        questionnaire.getOptions()
                ))
                .collect(Collectors.toList());
    }
}
