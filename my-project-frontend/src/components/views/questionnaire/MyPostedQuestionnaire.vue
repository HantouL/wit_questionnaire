<script setup>
import {useStore} from "@/store";
import {computed, ref} from "vue";
import Card from "@/components/components/Card.vue";
import {Avatar,DocumentAdd} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const store = useStore();
const registerTime = computed(()=>new Date(store.user.registerTime).toLocaleString());

const questionnaireList = ref([]);

//拉取所有的问卷表
get(`api/admin/questionnaireList`, data => {
  questionnaireList.value = data;
  console.log(questionnaireList);
});

const dateFormatter = (dateString) => {
  return new Date(dateString).toLocaleDateString("zh-CN");
};

const isEditingQuestionnaire = ref(false); // 是否编辑问卷详情
const isViewing = ref(false); // 是否查看问卷详情
const currentQuestionnaireDetails = ref([]); // 当前问卷的详情内容
const currentQuestionnaire = ref({});

//新增问卷
const addQuestionnaire = () => {
  isEditingQuestionnaire.value = true;
};

// 新增题目
const addQuestion = (type) => {
  const question = {
    type: type,
    desc: "",
    options: [``],
  };
  currentQuestionnaireDetails.value.push(question);
};

const submitQuestionnaire = () =>{
  console.log(currentQuestionnaireDetails);

  const postData = {
      questionnaireName: currentQuestionnaire.value.name,
      questionnaireDesc: currentQuestionnaire.value.desc,
      status: 1,
      questionList: currentQuestionnaireDetails.value.map((question) => {
        return {
          type: question.type,
          desc: question.desc,
          options: question.options.filter(option => option.trim() !== "").join("&$$&"), // 将非空选项数组使用&$$&连接
        };
      }),
  }

  if (!postData.questionnaireName || !postData.questionnaireDesc) {
    ElMessage.error("问卷名称和描述不能为空");
    return;
  }

  if (postData.questionList.length===0){
    ElMessage.warning("问卷至少需要包含一个问题!");
    return;
  }

  if (postData.questionList.length===0){
    ElMessage.warning("问卷至少需要包含一个问题!");
    return;
  }

  // 判断每个单选或多选题目是否都至少有一个非空选项
  let hasEmptyOption = false;

  for (let i = 0; i < currentQuestionnaireDetails.value.length; i++) {
    const question = currentQuestionnaireDetails.value[i];
    if (question.type === 0 || question.type === 1) { // 单选或多选题
      hasEmptyOption = question.options.every((option) => option.trim() === "");
      if (hasEmptyOption) {
        break;
      }
    }
  }

  if (hasEmptyOption) {
    ElMessage.warning("每个单选或多选题目至少需要有一个非空选项");
    return;
  }

  post(`/api/admin/createQuestionnaire`,postData,(data)=>{
    console.log(data);
    ElMessage.success( "提交成功!");
  })

  // 清空当前问卷的相关数据
  currentQuestionnaire.value = {};
  currentQuestionnaireDetails.value = [];

  // 重新获取问卷列表的数据
  get(`api/admin/questionnaireList`, (data) => {
    questionnaireList.value = data;
    console.log(questionnaireList);
  });

  isEditingQuestionnaire.value = false;
}

function changeStatus(qid,status){
  post(`/api/admin/changeQuestionnaireStatus`, {
    questionnaireId: qid,
    status: status
  },(data)=>{
    console.log(data);
    ElMessage.success( "状态修改成功!");
  })
}

const currentQuestionnaireDetailsView = ref([]); // 当前问卷的详情内容
const currentQuestionnaireView = ref({});

const viewQuestionnaire = async (item) => {
  isViewing.value = true;
  currentQuestionnaireView.value = item;

  // 获取问卷详情信息
  await get(`api/questionnaire/questionnaireDetails/${item.id}`,data=>{
    currentQuestionnaireDetailsView.value = data;
    console.log(data);
  });
}

function endView(){
  isViewing.value = false;
}

function endNew(){
  currentQuestionnaire.value = {};
  currentQuestionnaireDetails.value = [];
  isEditingQuestionnaire.value = false;
}

function addOption(question) {
  // 判断上一个选项是否为空
  const lastOption = question.options[question.options.length - 1];
  if (lastOption.trim() === "") {
    ElMessage.warning("上一个选项不能为空");
    return;
  }
  // 新增选项
  question.options.push("");
}

function viewQuestionnaireStatistic(questionnaireId){
  console.log(questionnaireId);
  router.push(`/index/questionnaire-statistics/${questionnaireId}`);
}

</script>


<template>
  <div style="display: flex">
    <div class="settings-left">
      <card  title="我发布的问卷" :icon="Avatar" desc="在这里查看和管理调查问卷" v-if="!isEditingQuestionnaire&&!isViewing">
        <el-button type="primary" @click="addQuestionnaire" style="margin-left: 15px;">新增问卷</el-button>
        <div style="display: flex; flex-wrap: wrap">
          <div v-for="(item, index) in questionnaireList" :key="questionnaireList.length" class="questionnaire-wrapper">
            <QuestionnaireItem
                :key="item.id"
                :title="item.name"
                :desc="item.desc"
                :createTime="dateFormatter(item.createTime)"
            >
              <el-button type="primary" @click="viewQuestionnaire(item)" size="small" plain>查看问卷</el-button>
              <el-button type="primary" @click="viewQuestionnaireStatistic(item.id)" size="small" plain>问卷数据汇总</el-button>
              <el-switch
                  v-model="item.status"
                  :model-value="item.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="changeStatus(item.id, item.status)"
                  class="switch-item"
                  style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              />
            </QuestionnaireItem>
          </div>
        </div>
      </card>

      <!-- 新增问卷模块的内容 -->
      <card  v-if="isEditingQuestionnaire">
        <div class="questionnaire-details-item">
          <el-input
              v-model="currentQuestionnaire.name"
              placeholder="请输入问卷名称"
          >
            <template v-slot:prepend>
              问卷名称：
            </template>
          </el-input>
          <el-input
              v-model="currentQuestionnaire.desc"
              placeholder="请输入问卷描述">
            <template v-slot:prepend>
              问卷描述：
            </template>
          </el-input>
        </div>
        <div v-for="(question, index) in currentQuestionnaireDetails" :key="index">
          <div class="questionnaire-details-item">
            <div>
              <el-row>
                问题{{index+1}}
                <div v-if="question.type==0">(单选题)</div>
                <div v-if="question.type==1">(多选题)</div>
                <div v-if="question.type==2">(文本题)</div>
                <el-input
                    v-model="question.desc"
                    placeholder="请输入问题描述"
                ></el-input>
              </el-row>
            </div>
            <div v-if="question.type === 0 || question.type === 1">
              <!-- 单选题和多选题的选项输入 -->
              <div v-for="(option, optionIndex) in question.options" :key="optionIndex">
                <el-input
                    v-model="question.options[optionIndex]"
                    placeholder="请输入选项"
                    style="width: 60%"
                ></el-input>
              </div>
              <el-button
                  type="primary"
                  @click="addOption(question)">
              新增选项
              </el-button>
            </div>
          </div>
        </div>
        <div style="display: flex;justify-content: center;margin-top: 30px">
          <el-button @click="submitQuestionnaire" type="success" plain style="width: 300px;">创建问卷</el-button>
          <el-button type="primary" @click="endNew" class="questionnaire-details-item" plain style="translate: 0 -8px">返回</el-button>
        </div>
      </card>
      <card :title="currentQuestionnaireView.name" :icon="Avatar" :desc="currentQuestionnaireView.desc" v-if="isViewing">
        <!--        这里编写填写问卷的逻辑-->
        <template v-if="currentQuestionnaireDetailsView && currentQuestionnaireDetailsView.length > 0">
          <div v-for="(question, index ) in currentQuestionnaireDetailsView" :key="index">
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
        <el-button type="primary" @click="endView" class="questionnaire-details-item">返回</el-button>
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
        <card :icon="DocumentAdd" title="新增题目" v-if="isEditingQuestionnaire" style="margin-top: 20px;">
          <div class="button-container">
            <el-button @click="addQuestion(0)" type="primary">新增单选题</el-button>
          </div>
          <div class="button-container">
            <el-button @click="addQuestion(1)" type="primary">新增多选题</el-button>
          </div>
          <div class="button-container">
            <el-button @click="addQuestion(2)" type="primary">新增文本题</el-button>
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
  margin: 8px 10px 10px 10px;
}

.switch-item{
  margin-left: 15px;
}

.button-container {
  display: flex;
  justify-content: center;
  margin: 10px 0 0 10px;
}
</style>
