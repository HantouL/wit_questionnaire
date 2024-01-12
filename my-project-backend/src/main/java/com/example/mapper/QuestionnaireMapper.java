package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.Questionnaire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuestionnaireMapper extends BaseMapper<Questionnaire> {
    @Update("update db_questionnaire set status = #{status} where id = #{questionnaireId}")
    int changeStatusById(@Param("questionnaireId") Integer questionnaireId, @Param("status") int status);
}
