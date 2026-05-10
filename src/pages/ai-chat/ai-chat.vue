<template>
  <view class="page">
    <scroll-view
      scroll-y
      class="thread"
      scroll-with-animation
      :scroll-into-view="intoView"
    >
      <view class="pad">
        <AiMessage v-for="m in store.messages" :key="m.id" :message="m" />
        <view v-if="thinking" class="typing">
          <text class="dot" />
          <text class="dot" />
          <text class="dot" />
        </view>
        <view id="msg-end" class="anchor" />
      </view>
    </scroll-view>

    <view class="composer">
      <input
        v-model="text"
        class="inp"
        placeholder="问问学习计划、拆解任务…"
        confirm-type="send"
        @confirm="send"
      />
      <button class="send" :disabled="!canSend || thinking" @click="send">发送</button>
    </view>

  </view>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AiMessage from '@/components/AiMessage.vue'
import { useAiStore } from '@/store/ai'

const store = useAiStore()
const text = ref('')
const thinking = ref(false)
const intoView = ref('')

const canSend = computed(() => text.value.trim().length > 0)

async function scrollBottom() {
  await nextTick()
  intoView.value = ''
  await nextTick()
  intoView.value = 'msg-end'
}

async function send() {
  if (!canSend.value || thinking.value) return
  const msg = text.value.trim()
  text.value = ''
  store.pushUser(msg)
  await scrollBottom()
  thinking.value = true
  try {
    await store.replyTo(msg)
  } finally {
    thinking.value = false
    await scrollBottom()
  }
}

onShow(() => {
  store.hydrate()
  scrollBottom()
})
</script>

<style scoped lang="scss">
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #eef2ff 0%, #f5f7fb 28%, #f5f7fb 100%);
}
.thread {
  flex: 1;
  height: 0;
}
.pad {
  padding: 24rpx 24rpx 24rpx;
}
.anchor {
  height: 24rpx;
}
.typing {
  display: inline-flex;
  gap: 10rpx;
  padding: 20rpx 28rpx;
  border-radius: 24rpx;
  background: #fff;
  border: 1rpx solid rgba(148, 163, 184, 0.28);
  margin-bottom: 24rpx;
}
.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #cbd5e1;
  animation: bounce 1s infinite ease-in-out;
}
.dot:nth-child(2) {
  animation-delay: 0.15s;
}
.dot:nth-child(3) {
  animation-delay: 0.3s;
}
@keyframes bounce {
  0%,
  80%,
  100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  40% {
    transform: translateY(-8rpx);
    opacity: 1;
  }
}
.composer {
  display: flex;
  gap: 16rpx;
  align-items: center;
  padding: 16rpx 20rpx calc(16rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  border-top: 1rpx solid rgba(148, 163, 184, 0.25);
}
.inp {
  flex: 1;
  height: 80rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background: #f8fafc;
  border: 1rpx solid rgba(148, 163, 184, 0.35);
  font-size: 28rpx;
}
.send {
  height: 80rpx;
  line-height: 80rpx;
  padding: 0 32rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  font-size: 28rpx;
  font-weight: 700;
  border: none;
}
.send[disabled] {
  opacity: 0.45;
}
</style>
