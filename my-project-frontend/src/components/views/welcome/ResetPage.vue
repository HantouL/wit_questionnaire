<script setup>

import {computed, reactive, ref} from "vue";
import {ChatDotSquare, Lock, Message} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const active = ref(0)
const formRef = ref()
const form = reactive({

  email:'',
  code:'',
  password:'',
  password_repeat: ''
})
const coldTime=ref(0)

const validateUsername=(rule,value,callback)=>{
  if(value===''){
    callback(new Error('请输入用户名'))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符,只能是中/英文'))
  }else{
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules={
  username: [
    {validator: validateUsername, trigger: ['blur','change']}
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
  ]
}

function askCode() {
  if (isEmailValid.value) {
    coldTime.value = 60;
    get(`/api/auth/ask-code?email=${form.email}&type=reset`, () => {
      ElMessage.success(`验证码已发送到邮箱${form.email}，请注意查收`);
      const timer = setInterval(() => {
        if (coldTime.value > 0) {
          coldTime.value--;
        } else {
          clearInterval(timer); // 清除定时器
        }
      }, 1000);
    }, (message) => {
      ElMessage.error(message);
      coldTime.value = 0;
    });
  } else {
    ElMessage.warning("请输入正确的电子邮件地址");
  }
}

const isEmailValid = computed(()=>/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(form.email))

function confirmReset(){
  formRef.value.validate((valid)=>{
    if(valid){
      post('/api/auth/reset-confirm',{
        email: form.email,
        code: form.code
      },()=>active.value++)
    }
  })
}

function doReset(){
  formRef.value.validate((valid)=>{
    if(valid){
      post('/api/auth/reset-password',{...form},()=> {
        ElMessage.success('密码重置成功,请重新登陆')
        router.push('/')
      })
    }
  })
}

</script>

<template>
  <div style="text-align: center">
    <div style="margin-top: 30px">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件"/>
        <el-step title="重新设定密码"/>
      </el-steps>
    </div>

    <div v-if="active===0" style="margin-top: 50px">
      <div style="font-size: 25px;font-weight: bold">重置密码</div>
      <div style="font-size: 14px;color: gray">请输入电子邮箱地址</div>

      <div style="margin: 0 20px">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item>
            <el-input v-model="form.email" type="email" placeholder="电子邮箱地址">
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="17">
                <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                  <template #prefix>
                    <el-icon><ChatDotSquare /></el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="5">
                <el-button @click="askCode" :disabled="coldTime>0||!isEmailValid" type="success">
                  {{coldTime>0?`请稍后${coldTime}秒`:`获取验证码`}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 60px;">
        <el-button @click="confirmReset" style="width: 270px" type="warning" plain>下一步</el-button>
      </div>
    </div>
    <div v-if="active===1" style="margin-top: 50px">
      <div>
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: gray">请输入您的新密码</div>
      </div>

      <div style="margin: 0 20px">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="password">
            <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="确认密码">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 60px;">
        <el-button @click="doReset" style="width: 270px" type="danger" plain>立即重置密码</el-button>
      </div>
    </div>

  </div>

</template>

<style scoped>

</style>