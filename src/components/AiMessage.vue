<template>
  <view class="wrap" :class="message.role">
    <view class="bubble">
      <text class="txt">{{ message.content }}</text>
      <text class="time">{{ time }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: { type: Object, required: true },
})

const time = computed(() => {
  const d = new Date(props.message.createdAt)
  const pad = (n) => (n < 10 ? `0${n}` : `${n}`)
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`
})
</script>

<style scoped lang="scss">
.wrap {
  display: flex;
  margin-bottom: 24rpx;
}
.wrap.user {
  justify-content: flex-end;
}
.bubble {
  max-width: 82%;
  padding: 22rpx 26rpx;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 28rpx rgba(15, 23, 42, 0.06);
}
.assistant .bubble {
  background: #fff;
  border: 1rpx solid rgba(148, 163, 184, 0.28);
  border-top-left-radius: 8rpx;
}
.user .bubble {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-top-right-radius: 8rpx;
}
.txt {
  font-size: 28rpx;
  line-height: 1.55;
  color: #0f172a;
  white-space: pre-wrap;
  word-break: break-word;
}
.user .txt {
  color: #fff;
}
.time {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  color: #94a3b8;
}
.user .time {
  color: rgba(255, 255, 255, 0.85);
  text-align: right;
}
</style>
