<script setup>

import Card from "@/components/components/Card.vue";
import {Refresh,Select,Message, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {reactive,computed,ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useStore();

const registerTime = computed(()=>new Date(store.user.registerTime).toLocaleString());

const desc = ref()
const baseFormRef = ref()
const emailFormRef = ref()
const baseForm = reactive({
  username: '',
  gender: 0,
  phone: '',
  qq: '',
  wechat: '',
  desc: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const validateUsername=(rule,value,callback)=>{
  if(value===''){
    callback(new Error('请输入用户名'))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符,只能是中/英文'))
  }else{
    callback()
  }
}

const rules={
  username: [
    {validator: validateUsername, trigger: ['blur','change']}
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
  ]
}

function saveDetails(){
  baseFormRef.value.validate(isValid=>{
    if(isValid){
      loading.base = true
      post('api/account/save-details',baseForm,()=>{
        ElMessage.success('用户信息保存成功')
        store.user.username = baseForm.username
        desc.value=baseForm.desc
        loading.base = false
      },(message)=>{
        ElMessage.warning(message)
        loading.base = false
      })
    }
  })
}

const loading = reactive({
  form: true,
  base: false
})


// 从后端拉取当前登录的用户的详细信息
get('api/account/details',data=>{
  baseForm.username = store.user.username;
  baseForm.gender = data.gender;
  baseForm.phone = data.phone;
  baseForm.wechat = data.wechat;
  baseForm.qq = data.qq;
  baseForm.desc = desc.value = data.desc;
  emailForm.email = store.user.email;
  loading.form = false;
})

const coldTime=ref(0)
const isEmailValid = computed(()=>/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(emailForm.email))
function askCode() {
  if(isEmailValid.value){
    coldTime.value = 60;
    get(`/api/auth/ask-code?email=${store.user.email}&type=modify`, () => {
      ElMessage.success(`验证码已发送到当前绑定邮箱${store.user.email}，请注意查收`);
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
  }else{
    ElMessage.warning("请输入正确的电子邮件地址");
  }
}

function modifyEmail(){
  if(emailForm.code!== ''){
    if(isEmailValid.value) {
      post('/api/account/modify-email',emailForm,()=>{
        ElMessage.success("邮件修改成功")
        store.user.email = emailForm.email
        emailForm.code = ''
      },()=>{
        ElMessage("验证码错误")
      })
    }
  }else{
    ElMessage.warning("请输入验证码")
  }
}

function beforeAvatarUpload(rawFile){
  if(rawFile.type!==`image/jpeg`&&rawFile.type!==`image/png`){
    ElMessage.error(`头像图片只能是JPG/PNG格式`)
    return false;
  }else if(rawFile.size/1024>100){
    ElMessage.error(`头像图片大小不能超过100kb`)
    return false;
  }
  return true;
}

function uploadSuccess(response){
  ElMessage.success(`头像上传成功`)
  store.user.avatar = response.data
}

</script>


<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :icon="User" title="账号信息设置" desc="在这里编辑您的个人信息" v-loading="loading.form">
        <el-form :model="baseForm" :rules="rules" ref="baseFormRef" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username" maxlength="10"/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"/>
          </el-form-item>
          <el-form-item label="QQ号" prop="qq">
            <el-input v-model="baseForm.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="微信号" prop="wechat">
            <el-input v-model="baseForm.wechat" maxlength="20"/>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input type="textarea" :rows="5" v-model="baseForm.desc" maxlength="240"></el-input>
          </el-form-item>
          <div>
            <el-button @click="saveDetails" :loading="loading.base" :icon="Select" type="success" style="width: 35%" plain>保存用户信息</el-button>
          </div>
        </el-form>
      </card>
      <card title="电子邮件设置" desc="您可以在这里修改默认绑定的电子邮件地址" :icon="Message">
        <el-form :model="emailForm" ref="emailFormRef" :rules="rules" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="要修改成的电子邮件地址" prop="email">
            <el-input v-model="emailForm.email"></el-input>
          </el-form-item>
          <el-form-item label="验证码">
            <el-row style="width: 100%;" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码" v-model="emailForm.code"></el-input>
              </el-col>
              <el-col :span="6">
                <el-button @click="askCode" type="success" style="width: 100%" plain>获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button @click="modifyEmail" :icon="Refresh" type="success" style="width: 35%" plain>更新电子邮件</el-button>
          </div>
        </el-form>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar size="70" :src="store.avatarUrl"/>
            <div>
            <el-upload
                :action="axios.defaults.baseURL + `/api/file/avatar`"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :on-success="uploadSuccess"
                :headers="accessHeader()"
            >
              <el-button size="small" round>修改头像</el-button>
            </el-upload>
            </div>
            <div style="font-weight: bold">您好，{{store.user.username}}</div>
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <div style="font-size: 14px;color: gray;padding: 10px">
            {{desc || '这个用户很懒,没有个人简介'}}
          </div>
        </card>
        <card style="margin-top: 10px;font-size: 14px">
          <div>账号注册时间: {{registerTime}}</div>
          <div style="font-size: 14px;color: gray; ">欢迎您访问问卷调查系统</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>

.settings-left{
  flex: 1;
  margin: 20px;
}

.settings-right{
  width: 300px;
  margin: 20px 30px 20px 0;
}
</style>