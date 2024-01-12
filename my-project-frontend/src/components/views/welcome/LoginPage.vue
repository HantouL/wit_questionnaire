<script setup>

import {reactive} from "vue";
import {User, Lock} from "@element-plus/icons-vue";
import {login} from "@/net";
import { ref } from 'vue';
import router from "@/router";

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rule = {
  username: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ]
}

const formRef = ref()

function userLogin() {
  formRef.value.validate((isValid) => {
    if(isValid) {
      login(form.username, form.password, form.remember, () => {router.push('/index')})
    }
  });
}

</script>


<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color: gray">在进入系统之前,请先登录</div>
    </div>

    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="25" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" maxlength="20" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

          <el-row>
            <el-col :span="12" style="text-align: left">
              <el-form-item prop="remember">
                <el-checkbox v-model="form.remember" label="记住我"></el-checkbox>
              </el-form-item>
            </el-col>
            <el-col :span="12" style="text-align: right">
              <el-link @click="router.push('/reset')">忘记密码?</el-link>
            </el-col>
          </el-row>
      </el-form>
    </div>
    <div style="margin-top: 20px">
      <el-button @click="userLogin" style="width: 270px" type="success" plain>立即登录</el-button>
    </div>
    <el-divider>
      <span style="font-size: 13px;color: gray">还没有账号?</span>
    </el-divider>
    <div>
      <el-button @click="router.push('/register')" type="warning" style="width: 270px" plain>立即注册</el-button>
    </div>
  </div>
</template>


<style scoped>


</style>