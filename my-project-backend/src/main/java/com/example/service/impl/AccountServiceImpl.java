package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.Account;
import com.example.entity.vo.request.*;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils flowUtils;

    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if(account==null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User.withUsername(username)//由于这里传入的可能是邮箱或者用户名,这里直接返回原来传入的登录账号,不能返回account.getusername(),如果传入的是邮箱,这样就会造成后续验证的错误
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    @Override
    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username",text)
                .or()
                .eq("email",text)
                .one();//one返回一个满足查询条件的结果对象
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id",id).one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip){//防止同一ip多次刷判断
            if(!this.verifyLimit(ip))
                return "请求频繁,请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;//验证码生成
            Map<String,Object> data = Map.of("type",type,"email",email,"code",code);
            amqpTemplate.convertAndSend("mail",data);//将消息放入消息队列email
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA+email, String.valueOf(code), 5, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO emailRegisterVO) {
        String email = emailRegisterVO.getEmail();
        String code = getEmailVerifyCode(email);
        if(code==null) return "请先获取验证码";
        if(this.existAccountByUsername(emailRegisterVO.getUsername())) return "此用户名已被注册,请更换用户名";
        if(this.existAccountByEmail(email)) return "此邮箱已被注册,请更换邮箱注册";
        if(!code.equals(emailRegisterVO.getCode())) return "验证码错误,请重新输入";
        String password = passwordEncoder.encode(emailRegisterVO.getPassword());
        Account account = new Account(null,emailRegisterVO.getUsername(), password,email,"user",new Date(),null);
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
        if(this.save(account)){
            return null;
        }else{
            return "内部错误,请联系管理员";
        }

    }

    @Override
    public String resetConfirm(ConfirmResetVO confirmResetVO) {
        String email = confirmResetVO.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA+email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(confirmResetVO.getCode())) return "验证码错误,请重新输入";
        return null;
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO emailResetVO) {
        String email = emailResetVO.getEmail();
        String confirm = this.resetConfirm(new ConfirmResetVO(email,emailResetVO.getCode()));
        if(confirm!=null) return confirm;
        String password = passwordEncoder.encode(emailResetVO.getPassword());
        boolean update = this.update().eq("email",email).set("password",password).update();
        if(update){
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String code = getEmailVerifyCode(vo.getEmail());
        String email = vo.getEmail();
        if(code==null) return "请先获取验证码!";
        if(!code.equals(vo.getCode())) return "验证码错误,请重新输入";
        this.deleteEmailVerifyCode(email);
        Account account = findAccountByNameOrEmail(email);
        if(account !=null && account.getId() != id)
            return "该电子邮件地址已经被其他账号绑定";
        this.update()
                .set("email",email)
                .eq("id",id)
                .update();
        return null;
    }

    @Override
    public List<Account> getAccountList() {
        return this.baseMapper.selectList(null);
    }

    @Override
    public Boolean deleteAccount(int id) {
        return this.removeById(id);
    }

    @Override
    public String saEditAccount(EditAccountVO vo) {
        if(this.getById(vo.getId()).getRole().equals("superAdmin")) return "无法修改超级管理员的用户类型";
        if(vo.getRole().equals("superAdmin")) return "无法将用户类型修改为超级管理员";
        Account emailAccount = this.query().eq("email", vo.getEmail()).one();
        Account nameAccount = this.query().eq("username", vo.getUsername()).one();
        if(emailAccount != null && vo.getId()!=emailAccount.getId()) return "该电子邮箱已经被其他账号绑定";
        if(nameAccount != null && vo.getId()!=nameAccount.getId()) return "该用户名已存在";

        this.update()
                .set("email",vo.getEmail())
                .set("username",vo.getUsername())
                .set("role",vo.getRole())
                .eq("id",vo.getId())
                .update();
        return null;
    }

    @Override
    public String EditAccount(EditAccountVO vo) {
        Account emailAccount = this.query().eq("email", vo.getEmail()).one();
        Account nameAccount = this.query().eq("username", vo.getUsername()).one();
        if(this.getById(vo.getId()).getRole()!="user") return "权限不足,无法修改";
        if(emailAccount != null && vo.getId()!=emailAccount.getId()) return "该电子邮箱已经被其他账号绑定";
        if(nameAccount != null && vo.getId()!=nameAccount.getId()) return "该用户名已存在";

        this.update()
                .set("email",vo.getEmail())
                .set("username",vo.getUsername())
                .eq("id",vo.getId())
                .update();
        return null;
    }

    @Override
    public String addAccount(AddAccountVO vo) {
        if(this.existAccountByUsername(vo.getUsername())) return "此用户名已被注册,请更换用户名";
        if(this.existAccountByEmail(vo.getEmail())) return "此邮箱已被注册,请更换邮箱注册";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null, vo.getUsername(), password, vo.getEmail(), vo.getRole(), new Date(), null);
        if(this.save(account)){
            return null;
        }else{
            return "内部错误,请联系管理员";
        }

    }

    private boolean existAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }

    private boolean existAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username",username));
    }

    private boolean verifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_LIMIT+ip;
        return flowUtils.limitOnceCheck(key,60);
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }


}
