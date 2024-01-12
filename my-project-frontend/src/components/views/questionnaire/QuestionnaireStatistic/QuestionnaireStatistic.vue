<script setup>

import Card from "@/components/components/Card.vue";
import BasicChartsItem from "@/components/components/BasicChartsItem.vue";
import {Refresh, Select, Message, User, MessageBox} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {reactive,computed,ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";
import router from "@/router";
import {useRoute} from "vue-router";
import {Avatar} from "@element-plus/icons-vue";
import QuestionnaireItem from "@/components/components/QuestionnaireItem.vue";

const store = useStore();

const registerTime = computed(()=>new Date(store.user.registerTime).toLocaleString());

const desc = ref()

const route = useRoute();
const qid = route.params.questionnaireId;

const loading = reactive({
  form: true,
  base: false
})

const questionnaireStatistics = reactive({
  questionnaireName: "", // 问卷名称
  questionnaireDesc: "", // 问卷描述
  questionnaireDetailsStatisticsVOList: ref([]) // 问卷详情统计列表
});

// 从后端拉取当前选中的问卷的详细信息
get(`api/admin/getQuestionnaireStatistics/${qid}`, data => {
  questionnaireStatistics.questionnaireName = data.questionnaireName;
  questionnaireStatistics.questionnaireDesc = data.questionnaireDesc;
  questionnaireStatistics.questionnaireDetailsStatisticsVOList = data.questionnaireDetailsStatisticsVOList;

  loading.form = false; // 结束加载动画
});

</script>


<template>
  <div style="display: flex">
    <div class="settings-left">
      <card
          :icon="MessageBox"
          :title="questionnaireStatistics.questionnaireName"
          :desc="questionnaireStatistics.questionnaireDesc"
          v-loading="loading.form">
        <div v-for="item in questionnaireStatistics.questionnaireDetailsStatisticsVOList" :key="item.questionnaireDetailsId" style="; justify-content: center;" >
          <basic-charts-item :icon="Avatar" :statistics="item"></basic-charts-item>
        </div>
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
                  :headers="accessHeader()"
              >
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