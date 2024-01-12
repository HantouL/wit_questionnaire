package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dao.Account;
import com.example.entity.dao.AccountDetails;
import com.example.entity.vo.request.AccountDetailsSaveVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.entity.vo.response.AccountDetailsVO;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Resource
    AccountService accountService;

    @Resource
    AccountDetailsService accountDetailsService;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute("id") int id){
        Account account = accountService.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }

    //获取用户详细信息
    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute("id") int id){
        AccountDetails details = Optional
                .ofNullable(accountDetailsService.getAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute("id") int id,
                                      @RequestBody @Valid AccountDetailsSaveVO vo){
        boolean success = accountDetailsService.saveAccountDetail(id,vo);
        return success ? RestBean.success() : RestBean.failure(400,"此用户名已被其他用户名使用,请重新更换");
    }

    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute("id") int id,
                                      @RequestBody @Valid ModifyEmailVO vo){
        String result = accountService.modifyEmail(id, vo);
        return result == null ? RestBean.success() : RestBean.failure(400,result);
    }
}
