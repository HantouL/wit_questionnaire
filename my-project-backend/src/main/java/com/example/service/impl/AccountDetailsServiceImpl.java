package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.Account;
import com.example.entity.dao.AccountDetails;
import com.example.entity.vo.request.AccountDetailsSaveVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService{

    @Resource
    AccountService accountService;


    //根据用户id获取用户详细信息
    @Override
    public AccountDetails getAccountDetailsById(int id) {
        return this.query().eq("account_id",id).one();
    }

    @Override
    public Boolean saveAccountDetail(int id, AccountDetailsSaveVO vo) {
        Account account = accountService.findAccountByNameOrEmail(vo.getUsername());
        if(account==null||account.getId()==id){
            accountService.update()
                    .eq("id",id)
                    .set("username",vo.getUsername())
                    .update();
            AccountDetails details = new AccountDetails();
            details.setAccountId(id);
            details.setGender(vo.getGender());
            details.setPhone(vo.getPhone());
            details.setQq(vo.getQq());
            details.setWechat(vo.getWechat());
            details.setDesc(vo.getDesc());
            this.saveOrUpdate(details);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteAccountDetail(int id) {
        return this.removeById(id);
    }
}
