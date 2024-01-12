<script setup>
import {useStore} from "@/store";
import {computed, ref} from "vue";
import Card from "@/components/components/Card.vue";
import {Avatar} from "@element-plus/icons-vue";
import QuestionnaireItem from "@/components/components/QuestionnaireItem.vue";
import {get, post} from "@/net";
import {format} from "date-fns";
import { useRouter } from 'vue-router';
import {ElMessage} from "element-plus";

const router = useRouter();

const store = useStore();
const registerTime = computed(()=>new Date(store.user.registerTime).toLocaleString());

const questionnaireList = ref([]);

const isWriting = ref(false); //开始填写问卷?

const mySubmittedQuestionnaireList = ref([]);


get(`api/answer/getMyAnswerList`, data => {
  mySubmittedQuestionnaireList.value = data;
  console.log(mySubmittedQuestionnaireList.value);
});

//拉取当前已激活的问卷列表
get('api/questionnaire/activeQuestionnaireList', data => {
  const isWriting = false; //开始填写问卷?
  questionnaireList.value = data;
  console.log(questionnaireList);
});

const dateFormatter = (dateString) => {
  return new Date(dateString).toLocaleDateString("zh-CN");
};

const currentQuestionnaire = ref({});
const currentQuestionnaireDetails = ref({});

const findSubmitted = (id) => {
  return mySubmittedQuestionnaireList.value.find(item => item.id === id) !== undefined;
};

const startWrite = async (item) => {

  isWriting.value = true;
  currentQuestionnaire.value = item;
  console.log(currentQuestionnaire)

  // 获取问卷详情信息
  await get(`api/questionnaire/questionnaireDetails/${item.id}`,data=>{
    currentQuestionnaireDetails.value = data;
    console.log(data);
  });
}

const answers = ref([]);//表单结果

// TODO 对已经填写过的问卷进行提示操作,并且不让二次填写
const back = () => {
  get(`api/answer/getMyAnswerList`, data => {
    mySubmittedQuestionnaireList.value = data;
  });

  get('api/questionnaire/activeQuestionnaireList', data => {
    const isWriting = false; //开始填写问卷?
    questionnaireList.value = data;
  });
  isWriting.value = false;
  currentQuestionnaire.value = null;
  currentQuestionnaireDetails.value = null;
  answers.value = [];
}

// 提交问卷的函数
const submitQuestionnaire = () => {
  const accountId = store.user.id; // 获取当前登录用户的id

  // 判断是否至少填写了一个题目
  if (currentQuestionnaireDetails.value.some(question => question.answer)) {
    // 遍历每个题目的答案，封装成对象并添加到答案数组中
    currentQuestionnaireDetails.value.forEach((question) => {
      const { id: questionnaireId } = currentQuestionnaire.value;
      const { id: questionnaireDetailsId, answer } = question;

      // 将多选选项的答案拼接成字符串，使用&@@&分隔
      const myAnswer =
          question.type === 1 ? (answer || []).join('&@@&') : question.answer;

      // 创建一个包含题目答案信息的对象
      const answerObject = {
        questionnaire_details_id: questionnaireDetailsId,
        myanswer: myAnswer,
      };

      // 将答案对象添加到答案数组中
      answers.value.push(answerObject);
    });

    // 在控制台打印答案数组，用于测试
    // console.log(answers.value);
    const requestData = {
      voList: answers.value,
      accountId: accountId,
      questionnaireId: currentQuestionnaire.value.id
    };

    post(`/api/answer/submitAnswer`,requestData,()=>{
      ElMessage.success( "提交成功!");
    })


    //TODO 清空answer
    answers.value = [];
  } else {
    ElMessage.warning("请至少填写一个题目");
  }
};

</script>


<template>
  <div style="display: flex">
    <div class="settings-left">
      <card  title="问卷填写" :icon="Avatar" desc="在这里查看进行中的问卷调查" v-if="!isWriting">
        <div style="display: flex; flex-wrap: wrap">
<!--          从刚才拉取的列表中，动态渲染问卷组件-->
          <div v-for="(item, index) in questionnaireList" :key="index" class="questionnaire-wrapper">
            <QuestionnaireItem
                :key="item.id"
                :title="item.name"
                :desc="item.desc"
                :createTime="dateFormatter(item.createTime)"
            >
              <el-button type="primary" plain @click="startWrite(item)" v-if="!findSubmitted(item.id)">开始填写</el-button>
              <el-button type="info" plain v-if="findSubmitted(item.id)">已填写</el-button>
            </QuestionnaireItem>
          </div>
        </div>
      </card>
      <card :title="currentQuestionnaire.name" :icon="Avatar" :desc="currentQuestionnaire.desc" v-if="isWriting">
<!--        这里编写填写问卷的逻辑-->
        <template v-if="currentQuestionnaireDetails && currentQuestionnaireDetails.length > 0">
          <div v-for="(question, index ) in currentQuestionnaireDetails" :key="index">
            <div>{{ question.desc }}</div>
            <template v-if="question.type === 0">
              <el-radio-group v-model="question.answer" class="questionnaire-details-item">
                <el-radio :label="option" v-for="(option, optionIndex) in question.options.split('&$$&')" :key="optionIndex">{{ option }}</el-radio>
              </el-radio-group>
            </template>
            <template v-else-if="question.type === 1">
              <el-checkbox-group v-model="question.answer" class="questionnaire-details-item">
                <el-checkbox :label="option" v-for="(option, optionIndex) in question.options.split('&$$&')" :key="optionIndex">{{ option }}</el-checkbox>
              </el-checkbox-group>
            </template>
            <template v-else-if="question.type === 2" class="questionnaire-details-item">
              <el-input v-model="question.answer" placeholder="请输入文本"></el-input>
            </template>
          </div>
        </template>
        <template else>
          <div>暂无题目</div>
        </template>
        <el-button type="primary" @click="submitQuestionnaire" class="questionnaire-details-item">提交问卷</el-button>
        <el-button type="primary" @click="back" class="questionnaire-details-item">返回</el-button>
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
