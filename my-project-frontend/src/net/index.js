import axios from 'axios'
import {ElMessage} from "element-plus";

const authItemName = "access_token"

function internalPost(url,data,header,success,failure,error=defaultError){
    axios.post(url,data,{headers: header}).then(({data})=>{
        if(data.code === 200){
            success(data.data)
        }else{
            defaultFailure(data.message,data.code,url)
        }
    }).catch(err => error(err))
}

function internalGet(url,header,success,failure,error=defaultError){
    axios.get(url,{headers: header}).then(({data})=>{
        if(data.code === 200){
            success(data.data)
        }else{
            defaultFailure(data.message,data.code,url)
        }
    }).catch(err => error(err))
}

function login(username,password,remember,success,failure=defaultFailure){
    internalPost('/api/auth/login',{
        username: username,
        password: password,
        },{
        'Content-Type': 'application/x-www-form-urlencoded'
    },(data)=>{
        storeAccessToken(data.token,remember,data.expireDate)
        ElMessage.success(`登陆成功,欢迎 ${data.username} 进入!`)
        success(data)
    },failure)
}

const defaultFailure = (message,code,url) => {
    console.warn(`请求地址: ${url}, 状态码: ${code}, 错误消息: ${message}`)
    ElMessage.warning(message)
}

const defaultError = (err) => {
    console.error(err)
    ElMessage.warning("未知错误,请联系管理员")
}

function storeAccessToken(token,rememberMe,expire){
    const authObj = {token:token,expire:expire}
    const str = JSON.stringify(authObj);
    if(rememberMe){
        localStorage.setItem(authItemName,str)
    }else{
        sessionStorage.setItem(authItemName,str)
    }
}

function takeAccessToken(){
    const str = localStorage.getItem(authItemName)||sessionStorage.getItem(authItemName);
    if(!str) return null;
    const authObj = JSON.parse(str)
    if(new Date(authObj.expire)<=new Date()){
        deleteAccessToken()
        ElMessage.warning("登录状态已过期,请重新登陆")
        return null;
    }
    return authObj.token
}

function deleteAccessToken(){
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

/**
 * 获取请求头
 */
function accessHeader(){
    if(takeAccessToken())
        return{
            'Authorization': `Bearer ${takeAccessToken()}`
        }
    else return {};
}

function get(url,success,failure=defaultFailure){
    internalGet(url,accessHeader(),success,failure)
}

function post(url,data,success,failure=defaultFailure){
    internalPost(url,data,accessHeader(),success,failure)
}

function logout(success,failure=defaultFailure) {
    get('/api/auth/logout', ()=>{
        deleteAccessToken()
        ElMessage.success(`退出登陆成功`)
        success()
    },failure)
}

/**
 * 判断是否未验证
 */
function unauthorized(){
    return !takeAccessToken();
}
export {login,logout,get,post,unauthorized,accessHeader}