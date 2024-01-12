package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.Questionnaire;
import com.example.entity.vo.response.QuestionnaireListVO;
import com.example.mapper.QuestionnaireMapper;
import com.example.service.QuestionnaireService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionnaireServiceImpl extends ServiceImpl<QuestionnaireMapper, Questionnaire> implements QuestionnaireService {

    @Resource
    QuestionnaireMapper questionnaireMapper;

    @Override
    public List<Questionnaire> getQuestionnaireList() {
        return this.baseMapper.selectList(null);
    }

    /*获取所有当前状态为启用的问卷列表*/
    @Override
    public List<Questionnaire> getActiveQuestionnaireList() {
        return this.query().eq("status",1).list();
    }

    @Override
    public List<Questionnaire> getQuestionnaireListById(int id) {
        return this.query().eq("creator_id",id).list();
    }

    @Override
    public List<QuestionnaireListVO> getQuestionnaireListByIdList(List<Integer> qIdList) {
        List<QuestionnaireListVO> voList = new ArrayList<>();
        qIdList.forEach(q->{
            Questionnaire questionnaire = this.getById(q);
            QuestionnaireListVO vo = new QuestionnaireListVO(questionnaire.getId(),
                                                            questionnaire.getName(),
                                                            questionnaire.getCreateTime(),
                                                            questionnaire.getDesc(),
                                                            questionnaire.getStatus());
            voList.add(vo);
        });
        return voList;
    }

    @Override
    public Integer createQuestionnaire(String questionnaireName, Date createTime, String questionnaireDesc, int creatorId, int status) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setName(questionnaireName);
        questionnaire.setCreatorId(creatorId);
        questionnaire.setStatus(status);
        questionnaire.setCreateTime(createTime);
        questionnaire.setDesc(questionnaireDesc);
        this.save(questionnaire);
        return questionnaire.getId();
    }

    @Override
    public boolean changeStatusById(Integer questionnaireId, int status) {
        return questionnaireMapper.changeStatusById(questionnaireId,status) > 0 ? true:false;
    }

    @Override
    public Questionnaire getQuestionnaireById(int id) {
        return this.getById(id);
    }
}
