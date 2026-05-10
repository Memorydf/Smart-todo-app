<template>
  <view class="page">
    <view class="hero">
      <text class="t">数据概览</text>
      <text class="d">基于当前清单数据的即时统计（远程模式下与 MySQL 同步）</text>
    </view>

    <view class="grid">
      <view class="cell">
        <text class="n">{{ total }}</text>
        <text class="l">全部任务</text>
      </view>
      <view class="cell">
        <text class="n">{{ done }}</text>
        <text class="l">已完成</text>
      </view>
      <view class="cell">
        <text class="n">{{ rate }}%</text>
        <text class="l">完成率</text>
      </view>
    </view>

    <view class="card">
      <text class="h">完成进度</text>
      <view class="ring-wrap">
        <view class="ring" :style="ringStyle" />
        <view class="ring-core">
          <text class="big">{{ rate }}%</text>
          <text class="sm">已完成</text>
        </view>
      </view>
    </view>

    <view class="card">
      <text class="h">进行中 · 分类分布</text>
      <view v-for="c in CATEGORY_LIST" :key="c.key" class="bar-row">
        <text class="bn">{{ c.label }}</text>
        <view class="track">
          <view class="fill" :style="{ width: barWidth(c.key) }" />
        </view>
        <text class="bv">{{ pendingMap[c.key] || 0 }}</text>
      </view>
      <text v-if="maxPending === 0" class="empty">暂无进行中任务</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useTodoStore } from '@/store/todo'
import { CATEGORY_LIST } from '@/utils/constants'
import { TODO_DATA_SOURCE } from '@/utils/config'

const store = useTodoStore()

const total = computed(() => store.todos.length)
const done = computed(() => store.stats.completed)
const rate = computed(() => {
  if (!total.value) return 0
  return Math.round((done.value / total.value) * 100)
})

const pendingMap = computed(() => store.categoryPending)

const maxPending = computed(() =>
  Math.max(1, ...CATEGORY_LIST.map((c) => pendingMap.value[c.key] || 0))
)

function barWidth(key) {
  const v = pendingMap.value[key] || 0
  const pct = Math.round((v / maxPending.value) * 100)
  return `${pct}%`
}

const ringStyle = computed(() => {
  const r = rate.value
  return {
    background: `conic-gradient(#2563eb ${r}%, #e2e8f0 ${r}% 100%)`,
  }
})

onShow(async () => {
  await store.hydrate()
  if (TODO_DATA_SOURCE === 'remote') {
    try {
      await store.fetchList()
    } catch {
      /* ignore */
    }
  }
})
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24rpx;
  box-sizing: border-box;
}
.hero {
  margin-bottom: 20rpx;
}
.t {
  display: block;
  font-size: 36rpx;
  font-weight: 800;
  color: #0f172a;
}
.d {
  display: block;
  margin-top: 10rpx;
  font-size: 26rpx;
  color: #64748b;
  line-height: 1.45;
}
.grid {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}
.cell {
  flex: 1;
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx 12rpx;
  text-align: center;
  border: 1rpx solid rgba(148, 163, 184, 0.22);
  box-shadow: 0 8rpx 28rpx rgba(15, 23, 42, 0.05);
}
.n {
  display: block;
  font-size: 36rpx;
  font-weight: 800;
  color: #0f172a;
}
.l {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #64748b;
}
.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx 24rpx;
  margin-bottom: 20rpx;
  border: 1rpx solid rgba(148, 163, 184, 0.22);
  box-shadow: 0 8rpx 28rpx rgba(15, 23, 42, 0.05);
}
.h {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 24rpx;
}
.ring-wrap {
  display: flex;
  justify-content: center;
  padding: 12rpx 0 8rpx;
  position: relative;
}
.ring {
  width: 280rpx;
  height: 280rpx;
  border-radius: 50%;
}
.ring-core {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 200rpx;
  height: 200rpx;
  border-radius: 50%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 2rpx rgba(226, 232, 240, 0.9);
}
.big {
  font-size: 44rpx;
  font-weight: 800;
  color: #1d4ed8;
}
.sm {
  margin-top: 6rpx;
  font-size: 24rpx;
  color: #64748b;
}
.bar-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 18rpx;
}
.bn {
  width: 72rpx;
  font-size: 26rpx;
  color: #475569;
}
.track {
  flex: 1;
  height: 16rpx;
  border-radius: 999rpx;
  background: #f1f5f9;
  overflow: hidden;
}
.fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #2563eb 0%, #38bdf8 100%);
  min-width: 8rpx;
}
.bv {
  width: 48rpx;
  text-align: right;
  font-size: 26rpx;
  color: #0f172a;
  font-weight: 700;
}
.empty {
  display: block;
  text-align: center;
  font-size: 26rpx;
  color: #94a3b8;
  padding: 12rpx 0;
}
</style>
