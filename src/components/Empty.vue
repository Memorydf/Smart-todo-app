<template>
  <view class="empty">
    <view class="ico">📋</view>
    <text class="t">{{ title }}</text>
    <text v-if="hint" class="h">{{ hint }}</text>
  </view>
</template>

<script setup>
defineProps({
  title: { type: String, default: '暂无内容' },
  hint: { type: String, default: '' },
})
</script>

<style scoped lang="scss">
.empty {
  padding: 80rpx 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.ico {
  font-size: 72rpx;
  margin-bottom: 16rpx;
  opacity: 0.85;
}
.t {
  font-size: 30rpx;
  color: #475569;
  font-weight: 600;
}
.h {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: #94a3b8;
  line-height: 1.5;
}
</style>
