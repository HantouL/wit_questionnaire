<script setup>
import {MessageBox} from "@element-plus/icons-vue";

defineProps({
  title: String,
  desc: String,
  createTime: Date
})
</script>


<template>
  <div class="questionnaireItem">
    <div class="card-header" v-if="title">
      <div>
        <el-icon style="margin-right: 3px;translate: 0 2px">
          <MessageBox />
        </el-icon>
        {{ title }}
      </div>
      <div v-if="createTime">
        发布日期: {{createTime}}
      </div>
    </div>
    <div class="desc">
      {{ desc }}
    </div>
    <slot/>
  </div>
</template>


<style lang="less" scoped>

.questionnaireItem{
  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  background-color: var(--el-bg-color);
  box-sizing: border-box;
  width: 100%;
  min-height: 20px;
  padding: 10px;
  margin: 1% 1% 1% 1%;
}

.card-header{
  border-bottom: solid 1px var(--el-border-color);
  padding-bottom: 5px;
  margin-bottom: 10px;
  overflow: hidden;         /* 超出部分隐藏 */
  white-space: nowrap;      /* 不换行 */
  text-overflow: ellipsis;  /* 超出部分显示省略号 */

  :first-child{
    font-size: 18px;
    font-weight: bold;
  }

  :last-child{
    font-size: 13px;
    color: gray;
  }
}
.desc{
  font-size: 13px;
  color: gray;
  margin-bottom: 7px;
  overflow: hidden;         /* 超出部分隐藏 */
  white-space: nowrap;      /* 不换行 */
  text-overflow: ellipsis;  /* 超出部分显示省略号 */
}

</style>