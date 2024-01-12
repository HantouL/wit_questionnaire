<script setup>
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import Card from "@/components/components/Card.vue";
import {Avatar, Delete, DataAnalysis, Edit, Select, Lock, Message, User} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import { format } from 'date-fns';
import {ElMessage} from "element-plus";
import router from "@/router";

const store = useStore();
const registerTime = computed(()=>new Date(store.user.registerTime).toLocaleString());

const dormitory = ref([]);

get('api/admin/account-list', data => {
  dormitory.value = data;
});

const search = ref('');

const tables = computed(() => {
  if (search.value) {
    return dormitory.value.filter(data =>
        Object.keys(data).some(key =>
            String(data[key]).toLowerCase().includes(search.value.toLowerCase())
        )
    );
  } else {
    return dormitory.value;
  }
});

const dateFormatter = (row, column, cellValue) => {
  return format(new Date(cellValue), 'yyyy/MM/dd HH:mm:ss');
};

function deleteAccount(id,email,role){
  if(role=="admin"||role=="superAdmin"){
    ElMessage.warning("无法删除管理员")
    return;
  }
  if(store.user.email===email){
    ElMessage.warning("无法删除自己!")
    return;
  }else{
    post(`api/admin/delete-account-by-id/${id}`,null,()=>{
      ElMessage.success("删除成功");
      get('api/admin/account-list', data => {
        dormitory.value = data;
      });
    },()=>{
      ElMessage.warning("删除失败")
    })
  }
}

function getUserAnswerView(userId,userName){
  router.push(`/index/user-answer/${userId}/${userName}`);
}

// 下面是编辑用户信息的Drawer

const editDrawer = ref({
  show: false,
  baseForm: reactive({})
});

const validateUsername=(rule,value,callback)=>{
  if(value===''){
    callback(new Error('请输入用户名'))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符,只能是中/英文'))
  }else{
    callback()
  }
}

const validateEmail = (rule,value,callback) =>{
  if(value===''){
    callback(new Error('请输入电子邮箱'))
  }else if(!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(value)){
    callback(new Error("请输入合法的电子邮箱地址"))
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
  ]
}

function openEditDrawer(rowData) {

  //判断操作权限
  if((rowData.role == "admin" || rowData.role == "superAdmin") && store.user.role == "admin"){
    ElMessage.warning("管理员无权编辑管理员信息")
    return;
  }

  //初始化baseForm
  editDrawer.value.baseForm = reactive({
    id: "",
    username: "",
    email: "",
    role: ""
  })

  editDrawer.value.baseForm.username = rowData.username;
  editDrawer.value.baseForm.email = rowData.email;
  editDrawer.value.baseForm.role = rowData.role;
  editDrawer.value.baseForm.id = rowData.id;

  console.log(editDrawer.baseForm);
  editDrawer.value.show = true;
}

function closeEditDrawer() {
  editDrawer.value.show = false;
  editDrawer.baseForm = null;
}

function submitEditing(){
  const isEmailValid = computed(()=>/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(editDrawer.value.baseForm.email));
  if(isEmailValid.value){
    if(store.user.role=="admin"){
      post('/api/admin/editAccount',editDrawer.value.baseForm,()=>{
        ElMessage.success("用户信息修改成功!");
        get('api/admin/account-list', data => {
          dormitory.value = data;
        })
        closeEditDrawer();
      },()=>{
        ElMessage.success("用户信息修改失败!");
      })
    }else if(store.user.role=="superAdmin"){
      const isRoleValid = computed(() => {
        const role = editDrawer.value.baseForm.role;
        return role === "user" || role === "admin";
      });
      if(isRoleValid.value){
        post('/api/admin/editAccount',editDrawer.value.baseForm,()=>{
          ElMessage.success("用户信息修改成功!");
          get('api/admin/account-list', data => {
            dormitory.value = data;
          })
          closeEditDrawer();
        },()=>{
          ElMessage.success("用户信息修改失败!");
        })
      }
    }
  }else{
    ElMessage.error("请输入正确的电子邮箱地址!");
  }
}

// 下面是新增用户的Drawer

const addDrawer = ref({
  show: false,
  baseForm: reactive({
    username: "",
    email: "",
    role: "user",
    password: "",
    passwordRepeat: ""
  })
});

const newAccountRef = ref();

function openAddDrawer() {
  addDrawer.value.show = true;
}

function closeAddDrawer() {
  addDrawer.value.show = false;
  addDrawer.baseForm = null;
}

function submitAddAccount(){
  newAccountRef.value.validate((valid) => {
    if(valid) {
      post('/api/admin/addAccount', {
        username: addDrawer.value.baseForm.username,
        password: addDrawer.value.baseForm.password,
        email: addDrawer.value.baseForm.email,
        role: addDrawer.value.baseForm.role
      }, () => {
        ElMessage.success('新增用户成功!')
        get('api/admin/account-list', data => {
          dormitory.value = data;
        })
        closeAddDrawer();
      })
    } else {
      ElMessage.warning('请完整填写注册表单内容')
    }
  })
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== addDrawer.value.baseForm.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const addRule={
  username: [
    {validator: validateUsername, trigger: ['blur','change']}
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  passwordRepeat: [
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ],
  email: [
    {validator: validateEmail, trigger: ['blur', 'change']}
  ],
  role: [
    { required: true, message: '请选择用户类型', trigger: 'blur' },
  ]
}

</script>


<template>
  <div style="display: flex">
    <div class="settings-left">
      <card title="用户管理" :icon="Avatar" desc="在这里管理用户">
        <div class="dormitory">
          <div class="searchWord">
            <el-row type="flex" justify="start">
              <el-col :span="1.9">
                <el-tag type="warning" size="large">模糊查询</el-tag>
              </el-col>
              <el-col :span="5" style="margin-right: 10px;">
                <el-input placeholder="请输入搜索内容" v-model="search"></el-input>
              </el-col>
              <el-col :span="17" style="text-align: right;">
                <el-button style="width: 150px" type="success" @click="openAddDrawer">新增用户</el-button>
              </el-col>
            </el-row>
          </div>
          <div class="dormitoryData" style="margin-top: 10px">
            <el-table
                ref="dormitoryTable"
                :data="tables"
                stripe
                style="width: 100%">
              <el-table-column label="用户id" prop="id">
              </el-table-column>
              <el-table-column label="用户名" prop="username">
              </el-table-column>
              <el-table-column label="email" prop="email">
              </el-table-column>
              <el-table-column label="用户类型" prop="role">
              </el-table-column>
              <el-table-column label="注册时间" prop="registerTime" :formatter="dateFormatter">
              </el-table-column>
              <!-- 编辑按钮列 -->
              <el-table-column label="操作">
                <template v-slot="scope">
                    <el-popconfirm :title="`确定要删除`+scope.row.username+`吗?`" @confirm="()=>deleteAccount(scope.row.id,scope.row.email,scope.row.role)">
                      <template #reference>
                      <el-button type="danger" :icon="Delete" />
                      </template>
                    </el-popconfirm>
                  <el-tooltip placement="top">
                    <template #content> 查看他填写的问卷 </template>
                    <el-button type="primary" @click="getUserAnswerView(scope.row.id,scope.row.username)" :icon="DataAnalysis" />
                  </el-tooltip>
                  <el-tooltip placement="top">
                    <template #content> 编辑他的信息 </template>
                    <el-button type="success" @click="openEditDrawer(scope.row)" :icon="Edit" />
                  </el-tooltip>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <el-drawer v-model="editDrawer.show" title="编辑用户信息" :before-close="closeEditDrawer">
          <div v-if="editDrawer.baseForm">
            <el-form :model="editDrawer.baseForm" :rules="rules" label-position="top" style="margin: 0 10px 10px 10px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="editDrawer.baseForm.username" maxlength="10" placeholder="用户名">
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="电子邮箱地址" prop="email">
                <el-input v-model="editDrawer.baseForm.email" placeholder="邮箱">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="用户类型" prop="role" v-if="store.user.role=='superAdmin'">
                <el-select
                    v-model="editDrawer.baseForm.role"
                    clearable
                >
                  <el-option label="普通用户" value="user" />
                  <el-option label="管理员" value="admin" />
                </el-select>
              </el-form-item>
              <div>
                <el-button @click="submitEditing" :icon="Select" type="success" style="width: 35%" plain>修改用户信息</el-button>
              </div>
            </el-form>
          </div>
        </el-drawer>

        <el-drawer v-model="addDrawer.show" title="新增用户" :before-close="closeAddDrawer">
          <el-form :model="addDrawer.baseForm" :rules="addRule" ref="newAccountRef" label-position="top" style="margin: 0 10px 10px 10px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="addDrawer.baseForm.username" maxlength="10" placeholder="用户名">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="电子邮箱地址" prop="email">
              <el-input v-model="addDrawer.baseForm.email" placeholder="邮箱">
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="addDrawer.baseForm.password" maxlength="20" type="password" placeholder="密码">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="重复密码" prop="passwordRepeat">
              <el-input v-model="addDrawer.baseForm.passwordRepeat" maxlength="20" type="password" placeholder="密码">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-text type="info">普通管理员只能添加普通用户</el-text>
            <el-form-item label="用户类型" prop="role" v-if="store.user.role=='superAdmin'">
              <el-select
                  v-model="addDrawer.baseForm.role"
                  clearable
              >
                <el-option label="普通用户" value="user" />
                <el-option label="管理员" value="admin" />
              </el-select>
            </el-form-item>
            <div>
              <el-button @click="submitAddAccount" :icon="Select" type="success" style="width: 35%" plain>修改用户信息</el-button>
            </div>
          </el-form>
        </el-drawer>

      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar size="default" :src="store.avatarUrl"/>
            <div style="font-weight: bold">您好，{{store.user.username}}</div>
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <div style="font-size: 14px;color: gray;padding: 10px">
            <div>账号注册时间: {{registerTime}}</div>
            <div style="font-size: 14px;color: gray; ">欢迎您访问问卷调查系统</div>
          </div>
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