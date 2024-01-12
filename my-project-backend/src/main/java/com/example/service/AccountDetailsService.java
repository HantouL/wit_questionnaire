package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.AccountDetails;
import com.example.entity.vo.request.AccountDetailsSaveVO;

public interface AccountDetailsService extends IService<AccountDetails> {
    AccountDetails getAccountDetailsById(int id);

    Boolean saveAccountDetail(int id, AccountDetailsSaveVO vo);

    Boolean deleteAccountDetail(int id);
}
