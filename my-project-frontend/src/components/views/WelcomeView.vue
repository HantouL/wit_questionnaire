<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1;background-color: antiquewhite">
      <el-image style="width: 100%;height: 100%" fit="cover"
          src="https://www.wit.edu.cn/images/container05_bg.jpg"></el-image>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">武汉工程大学问卷调查系统</div>
      <div style="margin-top: 5px">格物明理、致知笃行</div>
    </div>
    <div class="right-card">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" style="height: 100%"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: "WelcomeView"
}
</script>

<style scoped>
  .right-card{
    width: 400px;
    z-index: 1;
    background-color: white;
  }

  .welcome-title{
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
  }
</style>