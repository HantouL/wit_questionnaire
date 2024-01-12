package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.SubmitAnswerRequestVO;
import com.example.entity.vo.request.SubmitQuestionnaireAnswerVO;
import com.example.entity.vo.response.MyAnswerDetailsVO;
import com.example.entity.vo.response.QuestionnaireListVO;
import com.example.service.AnswerDetailsService;
import com.example.service.AnswerService;
import com.example.service.QuestionnaireService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    @Resource
    AnswerService answerService;

    @Resource
    AnswerDetailsService answerDetailsService;

    @Resource
    QuestionnaireService questionnaireService;

    @PostMapping("/submitAnswer")
    public RestBean<String> submitQuestionnaireAnswer(@RequestBody @Valid SubmitAnswerRequestVO requestVO){ //RMBR 这里的SubmitAnswerRequestVO成员变量的名称需要严格与请求传来的名称对应
        int accountId = requestVO.getAccountId();
        int questionnaireId = requestVO.getQuestionnaireId();
        List<SubmitQuestionnaireAnswerVO> voList = requestVO.getVoList();
        Date date = new Date();
        //添加到answer表
        Integer answerId = answerService.saveAnswer(accountId, questionnaireId, date);
        //添加到answer_details表
        return answerDetailsService.saveAnswerDetails(voList,answerId)?RestBean.success():RestBean.failure(406,"提交失败,请勿重复提交");
    }

    @GetMapping("/getMyAnswerList")
    public RestBean<List<QuestionnaireListVO>> getMyAnswerList(@RequestAttribute("id") int accountId){// TODO 检查,将所有需要显式传入id的地方换成从token中读取  注意!!! 有些地方需要判断是否 确实需要从token中提取,因为token中只能看到自己的,而有时候管理员需要且有查询别人的权限
        List<Integer> qIdList = answerService.getMyAnswerQuestionnaireIdList(accountId);
        List<QuestionnaireListVO> qList = questionnaireService.getQuestionnaireListByIdList(qIdList);

        return RestBean.success(qList);
    }

    @GetMapping("/getMyAnswerDetails/{questionnaireId}")
    public RestBean<List<MyAnswerDetailsVO>> getMyAnswerDetails(@PathVariable int questionnaireId,
                                                                @RequestAttribute("id") int accountId){
        int answerId = answerService.getAnswerIdByAccountIdAndQuestionnaireId(accountId,questionnaireId);
        List<MyAnswerDetailsVO> answerDetailList = answerDetailsService.getMyAnswerDetailsVOListByAnswerId(answerId);
        return RestBean.success(answerDetailList);
    }


}
