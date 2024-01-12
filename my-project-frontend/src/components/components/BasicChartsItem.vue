<script setup>
import { ref } from 'vue'
const props = defineProps({
  icon: Object,
  statistics: {
    type: Object,
    default: () => ({
      questionnaireDetailsDesc: '',
      type: 0,
      questionnaireDetailsId: 0,
      data: {},
      base64Img: null
    })
  }
})

const data = ref({
  title: {
    text: "",
    left: "left"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: "vertical",
    left: "right",
    data: ["Direct", "Email", "Ad Networks", "Video Ads", "Search Engines"]
  },
  series: [
    {
      name: "",
      type: "pie",
      radius: "55%",
      center: ["50%", "60%"],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      }
    }
  ]
})

if (props.statistics) {
  data.value.title.text = props.statistics.questionnaireDetailsDesc;
  data.value.series[0].name = props.statistics.questionnaireDetailsDesc;
  if(props.statistics.data) {
    data.value.series[0].data = Object.entries(props.statistics.data).map(([name, value]) => ({ name, value }));
    data.value.legend.data = Array.from(Object.keys(props.statistics.data));
  }

}

if(props.statistics.type===2){
  console.log(props.statistics.base64Img)
}

</script>


<template>
  <div v-if="props.statistics.type===0||props.statistics.type===1">
    <div>
      <v-chart
          class="vuechart"
          :option="data"
          width="100"
          height="100"
      />
    </div>
  </div>
  <div v-if="props.statistics.type===2">
    <el-row>
      <el-text size="large" style="font-size: 18px; color: #464646; margin-bottom: 10px" tag="b" >{{props.statistics.questionnaireDetailsDesc}}</el-text>
    </el-row>
    <el-image :src="'data:image/png;base64,'+ props.statistics.base64Img" fit="contain" alt="词云图片" v-if="props.statistics.base64Img!==null">
    </el-image>
    <el-text v-if="props.statistics.base64Img==null" size="large" style="color: gray;" >本问题还没有人填写回答,快去邀请填写吧!</el-text>
  </div>
  <el-divider/>
</template>


<style lang="less" scoped>

.card{
  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  background-color: var(--el-bg-color);
  box-sizing: border-box;
  min-height: 20px;
  padding: 10px;
  width: 65%;
}

.card-header{
  border-bottom: solid 1px var(--el-border-color);
  padding-bottom: 5px;
  margin-bottom: 10px;

  :first-child{
    font-size: 12px;
    font-weight: bold;
  }
}

.vuechart{
  height:300px;
}

</style>