package com.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDetailsStatisticsVO {
    String questionnaireDetailsDesc; //问题详情
    int type; //问题类型
    int questionnaireDetailsId; // 表问题id
    HashMap<String, Integer> data;// 单选/多选用,<选项名,选项数量>
    String base64Img;// 文本题,返回生成词云的Base64编码的图片
}
