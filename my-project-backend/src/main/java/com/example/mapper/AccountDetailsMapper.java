package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.AccountDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {
}
