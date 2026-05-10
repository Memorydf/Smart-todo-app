<template>
  <view class="page">
    <view class="card">
      <text class="h">展示</text>
      <view class="row">
        <view class="info">
          <text class="t">紧凑列表</text>
          <text class="d">缩小待办卡片间距与字号，一屏可浏览更多条</text>
        </view>
        <switch :checked="settings.compactList" color="#2563eb" @change="onSwitch" />
      </view>
    </view>

    <view class="card">
      <text class="h">数据</text>
      <button class="cell danger" @click="clearTodos">清空所有待办</button>
      <button class="cell" @click="clearAi">清空 AI 对话记录</button>
    </view>

    <view class="card">
      <text class="h">关于</text>
      <view class="about-block">
        <text class="about-k">程序名称</text>
        <text class="about-v">智能待办</text>
      </view>
      <view class="about-block">
        <text class="about-k">前端技术</text>
        <text class="about-v">Vue 3、uni-app、Pinia、Axios</text>
      </view>
      <view class="about-block">
        <text class="about-k">后端技术</text>
        <text class="about-v">Spring Boot 3、MyBatis-Plus、MySQL 8、RESTful API</text>
      </view>
      <view class="about-block about-last">
        <text class="about-k">接入的 AI 能力</text>
        <text class="about-v">
          DeepSeek-V4-Flash API；智能对话（任务拆解、学习/时间规划建议）；根据描述批量生成待办并写入数据库
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onShow } from '@dcloudio/uni-app'
import { useAiStore } from '@/store/ai'
import { useTodoStore } from '@/store/todo'
import { useSettingsStore } from '@/store/settings'
import { TODO_DATA_SOURCE } from '@/utils/config'

const todos = useTodoStore()
const ai = useAiStore()
const settings = useSettingsStore()

onShow(async () => {
  await todos.hydrate()
  ai.hydrate()
  settings.hydrate()
  if (TODO_DATA_SOURCE === 'remote') {
    try {
      await todos.fetchList()
    } catch {
      /* ignore */
    }
  }
})

function onSwitch(e) {
  settings.setCompact(e.detail.value)
}

function clearTodos() {
  uni.showModal({
    title: '清空待办',
    content:
      TODO_DATA_SOURCE === 'remote'
        ? '将删除服务器上全部待办，不可恢复。'
        : '将删除本地全部任务，不可恢复。',
    async success(res) {
      if (!res.confirm) return
      try {
        await todos.clearAll()
        uni.showToast({ title: '已清空', icon: 'none' })
      } catch {
        /* 已 toast */
      }
    },
  })
}

function clearAi() {
  uni.showModal({
    title: '清空对话',
    content: '将重置 AI 聊天记录为默认欢迎语。',
    success(res) {
      if (!res.confirm) return
      ai.resetChat()
      uni.showToast({ title: '已重置', icon: 'none' })
    },
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24rpx;
  box-sizing: border-box;
}
.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  border: 1rpx solid rgba(148, 163, 184, 0.22);
  box-shadow: 0 8rpx 28rpx rgba(15, 23, 42, 0.05);
}
.h {
  display: block;
  font-size: 26rpx;
  color: #94a3b8;
  margin-bottom: 16rpx;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24rpx;
}
.info {
  flex: 1;
  min-width: 0;
}
.t {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #0f172a;
}
.d {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #64748b;
  line-height: 1.45;
}
.cell {
  width: 100%;
  text-align: left;
  height: 96rpx;
  line-height: 96rpx;
  padding: 0 24rpx;
  border-radius: 16rpx;
  background: #f8fafc;
  border: 1rpx solid rgba(148, 163, 184, 0.28);
  font-size: 28rpx;
  color: #0f172a;
  margin-bottom: 16rpx;
}
.cell.danger {
  color: #b91c1c;
  background: #fef2f2;
  border-color: rgba(248, 113, 113, 0.35);
}
.about-block {
  margin-top: 20rpx;
}
.about-block:first-of-type {
  margin-top: 8rpx;
}
.about-k {
  display: block;
  font-size: 24rpx;
  color: #94a3b8;
  margin-bottom: 6rpx;
}
.about-v {
  display: block;
  font-size: 28rpx;
  color: #334155;
  line-height: 1.5;
  font-weight: 500;
}
.about-last {
  margin-bottom: 4rpx;
}
</style>
