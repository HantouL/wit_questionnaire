<script setup>
import {logout,get} from "@/net";
import {ref} from "vue";
import router from "@/router";
import {useStore} from "@/store";
import {Location, Memo, Setting} from "@element-plus/icons-vue";

const store = useStore()
const loading = ref(true)

get('/api/account/info',(data)=>{
  //这里使用pinia持久化数据
  store.user = data
  loading.value = false;
})

function userLogout(){
  logout(()=>router.push('/'))
}
</script>

<template>
      <div class="main-container" v-loading = "loading" element-loading-text="正在进入,请稍后...">
        <el-container style="height: 100%">
          <el-header class="main-container-header">
            <el-image class="logo" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB8AAAAgCAMAAADdXFNzAAAAsVBMVEVHcEw8psfT6O+x1eGu09xyu9UJbKzG4emk0N7L5Oud2OfC3+qw1+SSxdzg7/Pn9PX///8AP5js9vXx+/oARqHa5+0BUad9hrKoscyyvtIBW68AJY34//3F0N/m7e8COZjX3+cAfMkCZLy+yNhpdKsBcsQAg8/L2eNXX6Gbo8IAMZKMlbxZjMIAVbNxg7ORsNJCY6VRca1BUJl8l8BAcLYhTJ11os2gu9UhbrcACocsPJE82g1UAAAADnRSTlMALfevkVQY1HvsWb+7h30EvYUAAAJxSURBVCiRbZPZeqsgFIVNYqp2AAURUECqpk7RzEPz/g92tp2/nq4LvfjXBvZm4Tjfulv6ruv6yzvnLy09ZjHBmBjmLf+nATG4LxFCrRCYBL8cCyYIwqInqK44mDBb/MReKW8GS62IkpZbhA0n3vcxHjCT1Yip3WzOG8kI6RuJjfeFCSK9ZWIbJ8MwFMW6ZiVDiHxscd9qimu7y5M0TVeTigtFDGF6/8bnouoRPkWAk81q9QwqzkoK3pYTfqLlntfbKE7SRIfD8/PLCxg2lvO99qdyrHl5yOIkictwPgAGw6podUtR4Dgzg2hpMyhPTrUdVp98TQkcceb48FO8i+I4PrDzej0ZAF/TOYyJ+M4CI0S3E0+SkoUYDFB8WQ8bCmN8dDywsS6KYIM41pe0KIozq49pugaOPCeAb51NPN+xQzxcNzg052uRHiceAMfkjWe9qrrjhYRhWK+vaQIcA/dw3VpYP5Oh2ZYUqNLHIU1jWF+SwHms95zs86wMPySPMQwyiQ8UN/zB8VXVmvFVvkMlT9DHpFwTfdOusyS6orp5o6bPsziPp1nEJ4Ilb+GGWNlwcghDK/avWT7uLrvNeMgzSSzvEczfVf2obNZlWR5FebWVu1HssooS3mB3SjWrm1LpqUXQKdqeul2+x5hVAr1F7En1YKhv3bsjj/LXClNSVcb/SCfpW2EM77Kuy2GfmySk5Q37DOAsMHIvqDJi3G53h5JSbPmog68Az+ZGNBXEmypKFdaV2I92PvvO/8yjJW+q0bRwmVwK0SrvB566xEq0TdU0sJDAlrq/H+Cdi5SxVjNm6Nz98wkv/YXneY/+z7f5DwjHUg+cAQSQAAAAAElFTkSuQmCC"></el-image>
            <el-text tag="b" style="margin-left: 15px; font-size: x-large">武汉工程大学问卷调查系统</el-text>
            <div style="flex: 1" class="user-info">
              <div class="profile">
                <div>{{store.user.username}}<el-tag>{{store.user.role}}</el-tag></div>
                <div>{{ store.user.email}}</div>
              </div>
              <el-avatar :src="store.avatarUrl"></el-avatar>
            </div>
          </el-header>
          <el-container>
            <el-aside width="230px" class="sidebar">
              <el-menu
                  style="height: 100%"
                  router
                  :default-active="$route.path">
                <el-sub-menu index="1">
                  <template #title>
                    <el-icon><Memo /></el-icon>
                    <span><b>调查问卷</b></span>
                  </template>
                  <el-menu-item index="/index/questionnaire-filling">
                    <template #title>
                      填写问卷
                    </template>
                  </el-menu-item>
                  <el-menu-item index="/index/questionnaire-checking">
                    <template #title>
                      我填写的问卷
                    </template>
                  </el-menu-item>
                  <el-menu-item index="/index/questionnaire-releasing" v-if="store.user.role==='admin'||store.user.role === 'superAdmin'">
                    <template #title>
                      我发布的问卷
                    </template>
                  </el-menu-item>
                </el-sub-menu>
                <el-sub-menu index="2">
                  <template #title>
                    <el-icon><Setting /></el-icon>
                    <span><b>设置</b></span>
                  </template>
                  <el-menu-item index="/index/user-setting">
                    <template #title>
                      用户信息设置
                    </template>
                  </el-menu-item>
                  <el-menu-item index="/index/user-management" v-if="store.user.role==='admin'||store.user.role === 'superAdmin'">
                    <template #title>
                      用户管理/填写结果查询
                    </template>
                  </el-menu-item>
                </el-sub-menu>
              </el-menu>
              <el-button class="logoutButton" @click="userLogout" type="warning" style="width: 100%" plain>退出登录</el-button>
            </el-aside>

            <el-main style="padding: 0">
              <el-scrollbar style="height: calc(100vh - 55px)">
                <router-view v-slot="{ Component }">
                  <transition name="el-fade-in-linear" mode="out-in">
                    <component :is="Component" style="height: 100%"/>
                  </transition>
                </router-view>
              </el-scrollbar>
            </el-main>
          </el-container>
        </el-container>
      </div>
</template>

<style lang="less" scoped>

.main-container {
  height: 100vh;
  width: 100vw;
}

.main-container-header{
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo{
    height: 32px;
    align-items: center;
  }

  .user-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .profile{
      text-align: right;
      margin-right: 20px;

      :first-child{
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }

      :last-child{
        font-size: 10px;
        color: gray;
      }
    }
  }
}

.sidebar{
  position: relative;
  overflow-y: hidden; /* 隐藏垂直滚动条 */

  .logoutButton{
    position: absolute;
    bottom: 20px; /* 调整按钮距离底部的位置 */
    left: 0;
    width: 100%;
    padding: 20px;
  }
}



</style>