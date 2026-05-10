<template>
  <view v-if="show" class="mask">
    <view class="box">
      <view class="spin" />
      <text v-if="text" class="txt">{{ text }}</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  show: { type: Boolean, default: false },
  text: { type: String, default: '' },
})
</script>

<style scoped lang="scss">
.mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.25);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.box {
  background: #fff;
  padding: 40rpx 48rpx;
  border-radius: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  box-shadow: 0 16rpx 48rpx rgba(15, 23, 42, 0.12);
}
.spin {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  border: 4rpx solid #e2e8f0;
  border-top-color: #2563eb;
  animation: spin 0.8s linear infinite;
}
.txt {
  font-size: 26rpx;
  color: #64748b;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
