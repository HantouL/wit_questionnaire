package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.QuestionnaireDetails;
import com.example.entity.vo.request.CreateQuestionnaireDetailsVO;
import com.example.entity.vo.response.QuestionnaireDetailsStatisticsVO;
import com.example.mapper.QuestionnaireDetailsMapper;
import com.example.service.AnswerDetailsService;
import com.example.service.QuestionnaireDetailsService;
import com.example.utils.WordCloudUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class QuestionnaireDetailsServiceImpl extends ServiceImpl<QuestionnaireDetailsMapper, QuestionnaireDetails> implements QuestionnaireDetailsService {

    @Resource
    AnswerDetailsService answerDetailsService;

    @Resource
    WordCloudUtils wordCloudUtils;

    /*通过问卷id拉取问卷详情列表*/
    @Override
    public List<QuestionnaireDetails> getQuestionnaireDetaisList(int qid) {
        return this.query().eq("questionnaire_id",qid).list();
    }

    @Override
    public void createQuestionnaireDetails(Integer questionnaireId, List<CreateQuestionnaireDetailsVO> questionVoList) {
        questionVoList.forEach(vo->{
            QuestionnaireDetails details = new QuestionnaireDetails();
            details.setQuestionnaireId(questionnaireId);
            details.setType(vo.getType());
            details.setDesc(vo.getDesc());
            details.setOptions(vo.getOptions());
            this.save(details);
        });
    }

    /**
     * 根据qid,先拉取该问卷所有问题详情的列表,然后遍历问题详情,对于每个问题详情:
     * 获取名字, 类型, 问题id, 问题选项选择统计Map 或 词云图片base64编码的地址
     * @param qid
     */
    @Override
    public List<QuestionnaireDetailsStatisticsVO> getQuestionnaireDetailsStatistic(int qid) {
        List<QuestionnaireDetails> questionnaireDetaisList = getQuestionnaireDetaisList(qid);
        List<QuestionnaireDetailsStatisticsVO> questionnaireDetailsStatisticsVOList = new ArrayList<QuestionnaireDetailsStatisticsVO>();
        for (QuestionnaireDetails questionnaireDetails : questionnaireDetaisList) {//遍历每一个问题
            QuestionnaireDetailsStatisticsVO questionnaireDetailsStatisticsVO = new QuestionnaireDetailsStatisticsVO();
            questionnaireDetailsStatisticsVO.setQuestionnaireDetailsDesc(questionnaireDetails.getDesc());//设置问题名
            questionnaireDetailsStatisticsVO.setQuestionnaireDetailsId(questionnaireDetails.getId());//问题id

            Integer questionnaireDetailsType = questionnaireDetails.getType();
            questionnaireDetailsStatisticsVO.setType(questionnaireDetailsType);//问题类型


            //单选或多选题
            if(questionnaireDetailsType==0|| questionnaireDetailsType==1){
                String[] options = questionnaireDetails.getOptions().split("\\&\\$\\$\\&");
                HashMap<String, Integer> map = new HashMap<>();
                //初始化map
                for (String option : options) {
                    map.put(option,0);
                }
                List<String> myanswerList = answerDetailsService.getMyanswerByQuestionnaireDetailsId(questionnaireDetails.getId());//查询问题详情
                if(myanswerList!=null)
                    for (String s : myanswerList) {
                        if((s!=null)&&s.contains("&@@&")){
                            String[] myOptions = s.split("\\&\\@\\@\\&");
                            for (String option : myOptions) {
                                int count = map.containsKey(option) ? map.get(option) : 0;
                                map.put(option,count+1);
                            }
                        }else if((s!=null)&&!s.equals("")){
                            int count = map.containsKey(s) ? map.get(s) : 0;
                            map.put(s,count+1);
                        }
                    }
                questionnaireDetailsStatisticsVO.setData(map);
                questionnaireDetailsStatisticsVOList.add(questionnaireDetailsStatisticsVO);
            }else if(questionnaireDetailsType==2){//文本题
                List<String> myanswerList = answerDetailsService.getMyanswerByQuestionnaireDetailsId(questionnaireDetails.getId());//查询问题详情

                boolean flag = false;
                for (String s : myanswerList) {
                    if(s!=null&&!s.equals("")){
                        flag = true;
                    }
                }
                if(flag){
                    String wordCloud = wordCloudUtils.getWordCloud(myanswerList);
                    questionnaireDetailsStatisticsVO.setBase64Img(wordCloud);
                    questionnaireDetailsStatisticsVOList.add(questionnaireDetailsStatisticsVO);
                }else{
                    questionnaireDetailsStatisticsVO.setBase64Img(null);
                    questionnaireDetailsStatisticsVOList.add(questionnaireDetailsStatisticsVO);
                }

            }

        }
        return questionnaireDetailsStatisticsVOList;
    }

}
