<script setup>
import {computed, reactive, ref} from "vue";
import {ChatDotSquare, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const coldTime=ref(0)
const formRef = ref()

function askCode() {
  if (isEmailValid.value) {
    coldTime.value = 60;
    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
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

const rule={
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

const register = () => {
  formRef.value.validate((valid) => {
    if(valid) {
      post('/api/auth/register', {
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      }, () => {
        ElMessage.success('注册成功，欢迎加入我们')
        router.push("/")
      })
    } else {
      ElMessage.warning('请完整填写注册表单内容')
    }
  })
}

const isEmailValid = computed(()=>/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(form.email))

</script>

<template>
  <div style="text-align: center;margin: 0 20px">
      <div style="margin-top: 100px">
        <div style="font-size: 25px;font-weight: bold">欢迎注册</div>
        <div style="font-size: 14px;color: gray">欢迎注册我们的问卷调查平台,请在下方填写注册信息</div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rule" ref="formRef">
            <el-form-item prop="username">
              <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

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

            <el-form-item prop="email">
              <el-input v-model="form.email" maxlength="20" type="email" placeholder="电子邮箱地址">
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
        <div style="margin-top: 40px;">
          <el-button @click="register" style="width: 270px" type="warning" plain>注册</el-button>
        </div>
        <div style="margin-top: 20px">
          <span style="font-size: 14px;line-height: 15px;color: grey">已有账号? </span>
          <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
        </div>
      </div>
  </div>
</template>

<style scoped>

</style>