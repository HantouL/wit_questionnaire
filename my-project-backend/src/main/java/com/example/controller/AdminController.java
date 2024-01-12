package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dao.Account;
import com.example.entity.dao.Questionnaire;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import com.example.service.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController{
    @Resource
    AccountService accountService;

    @Resource
    AccountDetailsService accountDetailsService;

    @Resource
    QuestionnaireService questionnaireService;

    @Resource
    QuestionnaireDetailsService questionnaireDetailsService;

    @Resource
    AnswerService answerService;

    @Resource
    AnswerDetailsService answerDetailsService;


    //获取所有问卷的列表
    @GetMapping("/questionnaireList")
    public RestBean<List<QuestionnaireListVO>> questionnaireList(){
        List<Questionnaire> questionnaireList = questionnaireService.getQuestionnaireList();
        List<QuestionnaireListVO> list = convertToQuestionnaireListDTO(questionnaireList);
        return RestBean.success(list);
    }

    @GetMapping("/questionnaireList/{id}")
    public RestBean<List<QuestionnaireListVO>> getQuestionnaireListById(@PathVariable("id") int id){
        List<Questionnaire> questionnaireList = questionnaireService.getQuestionnaireListById(id);
        List<QuestionnaireListVO> list = convertToQuestionnaireListDTO(questionnaireList);
        return RestBean.success(list);
    }

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute("id") int id){
        Account account = accountService.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }

    @GetMapping("/account-list")
    public RestBean<List<AccountListVO>> accountList(){
        List<Account> accountList = accountService.getAccountList();
        List<AccountListVO> list = convertToAccountListDTO(accountList);
        return RestBean.success(list);
    }

    @PostMapping("/delete-account-by-id/{id}")
    public RestBean<String> deleteAccountById(@PathVariable("id") int id){
            accountService.deleteAccount(id);
            accountDetailsService.deleteAccountDetail(id);
        return RestBean.success("删除成功!");
    }

    //新增问卷
    @PostMapping("/createQuestionnaire")
    public RestBean<String> createQuestionnaire(@RequestAttribute("id") int creatorId,
                                                @RequestBody @Valid CreateQuestionnaireVO vo){
        //先在问卷表中创建一个新问卷，获取该新问卷的id
        Integer questionnaireId = questionnaireService.createQuestionnaire(vo.getQuestionnaireName(),new Date(),vo.getQuestionnaireDesc(),creatorId,vo.getStatus());
        //拿着新id，遍历所有的问卷中的题目，将该题目加入数据库，并和问卷的id进行绑定；遍历完成后，问卷也创建成功了
        questionnaireDetailsService.createQuestionnaireDetails(questionnaireId,vo.getQuestionList());
        return RestBean.success("新建问卷成功!");
    }

    @PostMapping("/changeQuestionnaireStatus")
    public RestBean<Boolean> changeQuestionnaireStatus(@RequestBody @Valid ChangeQuestionnaireStatusVO vo){
        return questionnaireService.changeStatusById(vo.getQuestionnaireId(),vo.getStatus()) ? RestBean.success():RestBean.failure(406,"状态修改失败");
    }

    @GetMapping("/getAnswerListById/{id}")
    public RestBean<List<QuestionnaireListVO>> getAnswerListById(@PathVariable("id") int userId){
        List<Integer> qIdList = answerService.getMyAnswerQuestionnaireIdList(userId);
        List<QuestionnaireListVO> qList = questionnaireService.getQuestionnaireListByIdList(qIdList);

        return RestBean.success(qList);
    }

    @PostMapping("/getAnswerDetailsById")
    public RestBean<List<MyAnswerDetailsVO>> getMyAnswerDetails(@RequestBody @Valid GetUserAnswerDetailsListVO vo){
        int answerId = answerService.getAnswerIdByAccountIdAndQuestionnaireId(vo.getUserId(),vo.getQuestionnaireId());
        List<MyAnswerDetailsVO> answerDetailList = answerDetailsService.getMyAnswerDetailsVOListByAnswerId(answerId);
        return RestBean.success(answerDetailList);
    }

    @PostMapping("/addAccount")
    public RestBean<String> addAccount(@RequestBody @Valid AddAccountVO vo,
                                       @RequestAttribute("role") String role){
        String result = null;
        if(role.equals("admin")){
            if(vo.getRole().equals("user"))
                result = accountService.addAccount(vo);
            else result = "普通管理员无权新建管理员!";
        }else if(role.equals("superAdmin")){
            if(vo.getRole().equals("user")||vo.getRole().equals("admin"))
                result = accountService.addAccount(vo);
            else result = "无法新增超级管理员或用户类型输入异常!";
        }
        return result == null ? RestBean.success() : RestBean.failure(400,result);
    }

    @PostMapping("/editAccount")
    public RestBean<String> editAccount(@RequestBody @Valid EditAccountVO vo,
                                        @RequestAttribute("role") String role){
        String result = null;
        if(role.equals("admin")){
            result = accountService.EditAccount(vo);
        }else if(role.equals("superAdmin")){
            result = accountService.saEditAccount(vo);
        }

        return result == null ? RestBean.success() : RestBean.failure(400,result);
    }

    @GetMapping("/getQuestionnaireStatistics/{qid}")
    public RestBean<QuestionnaireStatisticsVO> getQuestionnaireStatistics(@PathVariable("qid") int qid){
        QuestionnaireStatisticsVO questionnaireStatisticsVO = new QuestionnaireStatisticsVO();
        Questionnaire questionnaire = questionnaireService.getQuestionnaireById(qid);
        questionnaireStatisticsVO.setQuestionnaireDesc(questionnaire.getDesc());
        questionnaireStatisticsVO.setQuestionnaireName(questionnaire.getName());
        List<QuestionnaireDetailsStatisticsVO> questionnaireDetailsStatisticsVOList =  questionnaireDetailsService.getQuestionnaireDetailsStatistic(qid);
        questionnaireStatisticsVO.setQuestionnaireDetailsStatisticsVOList(questionnaireDetailsStatisticsVOList);
        return RestBean.success(questionnaireStatisticsVO);
    }

    public List<AccountListVO> convertToAccountListDTO(List<Account> accountList) {
        return accountList.stream()
                .map(account -> new AccountListVO(
                        account.getId(),
                        account.getUsername(),
                        account.getEmail(),
                        account.getRole(),
                        account.getRegisterTime()
                ))
                .collect(Collectors.toList());
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

}
