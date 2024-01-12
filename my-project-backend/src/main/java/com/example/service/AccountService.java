package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.Account;
import com.example.entity.vo.request.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    Account findAccountById(int id);
    /**
     * 发送邮箱验证码
     * @param type 发送验证码的类型(注册/修改密码....)
     * @param email 邮箱地址
     * @param ip 请求发送的ip地址(防止同一ip短时频繁请求)
     * @return
     */
    String registerEmailVerifyCode(String type,String email,String ip);

    /**
     * 通过邮箱注册
     * @param emailRegisterVO
     * @return
     */
    String registerEmailAccount(EmailRegisterVO emailRegisterVO);

    String resetConfirm(ConfirmResetVO confirmResetVO);
    String resetEmailAccountPassword(EmailResetVO emailResetVO);
    String modifyEmail(int id, ModifyEmailVO vo);
    List<Account> getAccountList();
    Boolean deleteAccount(int id);

    String saEditAccount(EditAccountVO vo);
    String EditAccount(EditAccountVO vo);

    String addAccount(AddAccountVO vo);
}
