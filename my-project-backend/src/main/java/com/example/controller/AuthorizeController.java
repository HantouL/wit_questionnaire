package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset|modify)") String type,
                                        HttpServletRequest request){
        return this.messageHandle(()->this.accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Validated EmailRegisterVO emailRegisterVO){
        return this.messageHandle(()-> accountService.registerEmailAccount(emailRegisterVO));
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO confirmResetVO){
        return this.messageHandle(()-> accountService.resetConfirm(confirmResetVO));
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO emailResetVO){
        return this.messageHandle(()-> accountService.resetEmailAccountPassword(emailResetVO));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message==null){//正常发送
            return RestBean.success();
        }else{
            return RestBean.failure(400,message);
        }
    }
}
