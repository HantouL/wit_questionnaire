<script setup>
import {useStore} from "@/store";
import {computed, ref} from "vue";
import Card from "@/components/components/Card.vue";
import {Avatar} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import { useRoute } from 'vue-router';
import router from "@/router";

const store = useStore();
const registerTime = computed(()=>new Date(store.user.registerTime).toLocaleString());

const route = useRoute();
const userId = route.params.userId;
const userName = route.params.userName;
console.log(userId);

const questionnaireList = ref([]);

get(`api/admin/getAnswerListById/${userId}`, data => {
  questionnaireList.value = data;
});

const dateFormatter = (dateString) => {
  return new Date(dateString).toLocaleDateString("zh-CN");
};

const isViewingDetails = ref(false); // 是否查看问卷详情
const currentQuestionnaireDetails = ref([]); // 当前问卷的详情内容
const currentQuestionnaire = ref({});

const myAnswerList = ref([]); //我的答案表

const viewDetails = async (item) => {
  await get(`api/questionnaire/questionnaireDetails/${item.id}`,data=>{
    currentQuestionnaireDetails.value = data;
    currentQuestionnaire.value = item;
  });

  // 设置当前问卷详情内容
  currentQuestionnaireDetails.value = item;

  post(`api/admin/getAnswerDetailsById`,
      {userId: userId,
        questionnaireId: item.id
      }
      ,(data)=>{
        myAnswerList.value = data;
      });

  // 打开问卷详情弹窗
  isViewingDetails.value = true;
};

//RMBR 查询我的答案需要传入表的id,用户id从token里解析; 先查询答案表id,再从答案详细表里拉取详细答案, 只需要返回答案和题号,然后解析,在选择器上展示
const getAnswer = (questionId) => {
  const answer = myAnswerList.value.find(item => item.questionnaireDetailsId === questionId);
  //console.log(answer);
  if (answer) {
    if (answer.myanswer === null) {
      console.log('空');
      return [''];
    }else if (answer.myanswer !== null) {
      if (answer.myanswer.includes('&@@&')){
        console.log(answer.myanswer.split('&@@&'));
        return answer.myanswer.split('&@@&');
      }else {
        return [answer.myanswer]
      }
    }
  }
  return [];
};

function endNew(){
  isViewingDetails.value = false;
}

function backToUserManagement(){
  router.push(`/index/user-management`);
}

</script>


<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :title="$route.params.userName + ` 填写的问卷`" :icon="Avatar" :desc="`在这里查看 ` + $route.params.userName + ` 填写的问卷调查`" v-if="!isViewingDetails">
        <el-button type="primary" @click="backToUserManagement" style="margin-left: 15px;">返回用户管理页面</el-button>
        <div style="display: flex; flex-wrap: wrap">
          <div v-for="(item, index) in questionnaireList" :key="index" class="questionnaire-wrapper">
            <QuestionnaireItem
                :key="item.id"
                :title="item.name"
                :desc="item.desc"
                :createTime="dateFormatter(item.createTime)"
            >
              <el-button type="primary" @click="viewDetails(item)" plain>查看详情</el-button>
            </QuestionnaireItem>
          </div>
        </div>
      </card>
      <card :title="currentQuestionnaire.name" :icon="Avatar" :desc="currentQuestionnaire.desc" v-if="isViewingDetails">
        <div v-for="(question, index) in currentQuestionnaireDetails" :key="index">
          <div>{{ question.desc }}</div>
          <template v-if="question.type === 0"> <!-- 单选题 -->
            <el-radio-group class="questionnaire-details-item" :v-model="question.answer" :model-value="getAnswer(question.id)[0]" disabled>
              <el-radio :label="option" v-for="(option, optionIndex) in question.options.split('&$$&')" :key="optionIndex">{{ option }}</el-radio>
            </el-radio-group>
          </template>
          <template v-else-if="question.type === 1"> <!-- 多选题 -->
            <el-checkbox-group class="questionnaire-details-item" :model-value="getAnswer(question.id)" :v-model="question.answer" disabled>
              <el-checkbox :label="option" v-for="(option, optionIndex) in question.options.split('&$$&')" :key="optionIndex">{{ option }}</el-checkbox>
            </el-checkbox-group>
          </template>
          <template v-else-if="question.type === 2"> <!-- 文本题 -->
            <el-input placeholder="请输入文本" :v-model="question.answer" :model-value="getAnswer(question.id)" disabled></el-input>
          </template>
        </div>
        <div style="display: flex;justify-content: center;margin-top: 30px">
          <el-button type="primary" @click="endNew" class="questionnaire-details-item" plain>返回</el-button>
        </div>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar size="70" :src="store.avatarUrl"/>
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

.questionnaire-wrapper {
  width: 25%; /* 每个小模块占据20%的宽度 */
  padding: 10px;
  box-sizing: border-box;
}

.questionnaire-details-item{
  margin: 5px 5px 5px 5px;
}

</style>
