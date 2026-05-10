import axios from 'axios'
import { API_BASE_URL } from '@/utils/config'

/** 小程序可用的 axios adapter */
function uniAdapter(config) {
  return new Promise((resolve, reject) => {
    const url = `${config.baseURL || ''}${config.url || ''}`
    uni.request({
      url,
      method: (config.method || 'get').toUpperCase(),
      data: config.data,
      header: config.headers,
      timeout: config.timeout || 20000,
      success(res) {
        resolve({
          data: res.data,
          status: res.statusCode,
          statusText: 'OK',
          headers: res.header,
          config,
          request: {},
        })
      },
      fail: reject,
    })
  })
}

const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
  adapter: uniAdapter,
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 200) {
      const msg = body.msg || '请求失败'
      uni.showToast({ title: String(msg), icon: 'none' })
      return Promise.reject(new Error(msg))
    }
    return body
  },
  (error) => {
    const msg =
      error?.response?.data?.message ||
      error?.message ||
      '网络异常，请稍后重试'
    uni.showToast({ title: msg, icon: 'none' })
    return Promise.reject(error)
  }
)

export default http
