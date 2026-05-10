<template>
  <view class="page">
    <view v-if="todo" class="card">
      <view class="top">
        <view class="check" @click="toggle">
          <view class="dot" :class="{ on: todo.completed }">
            <text v-if="todo.completed" class="tick">✓</text>
          </view>
        </view>
        <view class="main">
          <text class="title" :class="{ done: todo.completed }">{{ todo.title }}</text>
          <view class="meta">
            <text class="chip">{{ cat }}</text>
            <text class="pri" :class="todo.priority">{{ priLabel }}</text>
            <text v-if="todo.dueDate" class="due">截止 {{ todo.dueDate }}</text>
          </view>
        </view>
      </view>
      <view v-if="todo.description" class="desc">
        <text class="dl">描述</text>
        <text class="dc">{{ todo.description }}</text>
      </view>
      <view class="actions">
        <button class="ghost" @click="goEdit">编辑</button>
        <button class="danger" @click="remove">删除</button>
      </view>
    </view>
    <Empty v-else title="未找到该待办" hint="可能已被删除" />
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import Empty from '@/components/Empty.vue'
import { useTodoStore } from '@/store/todo'
import { categoryLabel } from '@/utils/constants'

const store = useTodoStore()
store.hydrate().catch(() => {})

const id = ref('')
const todo = computed(() => store.todos.find((t) => t.id === id.value))

const cat = computed(() => categoryLabel(todo.value?.category))
const priLabel = computed(() => {
  const m = { low: '低优先级', medium: '中优先级', high: '高优先级' }
  return m[todo.value?.priority] || '中优先级'
})

onLoad((q) => {
  id.value = q.id || ''
})

async function toggle() {
  if (!todo.value) return
  try {
    await store.toggleTodo(todo.value.id)
  } catch {
    /* 已 toast */
  }
}

function goEdit() {
  uni.navigateTo({ url: `/pages/todo-edit/todo-edit?id=${id.value}` })
}

function remove() {
  uni.showModal({
    title: '删除待办',
    content: '删除后无法恢复，确定继续？',
    async success(res) {
      if (!res.confirm || !todo.value) return
      try {
        await store.removeTodo(todo.value.id)
        uni.showToast({ title: '已删除', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 400)
      } catch {
        /* 已 toast */
      }
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
  padding: 28rpx 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(148, 163, 184, 0.22);
}
.top {
  display: flex;
  gap: 16rpx;
}
.check {
  padding-top: 6rpx;
}
.dot {
  width: 48rpx;
  height: 48rpx;
  border-radius: 14rpx;
  border: 2rpx solid #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}
.dot.on {
  background: #2563eb;
  border-color: #2563eb;
}
.tick {
  color: #fff;
  font-size: 28rpx;
  font-weight: 700;
}
.main {
  flex: 1;
  min-width: 0;
}
.title {
  font-size: 34rpx;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.35;
}
.title.done {
  text-decoration: line-through;
  color: #64748b;
}
.meta {
  margin-top: 16rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  align-items: center;
}
.chip {
  font-size: 22rpx;
  color: #2563eb;
  background: rgba(37, 99, 235, 0.08);
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
}
.pri {
  font-size: 22rpx;
  padding: 6rpx 14rpx;
  border-radius: 10rpx;
}
.pri.low {
  color: #64748b;
  background: #f1f5f9;
}
.pri.medium {
  color: #b45309;
  background: #fffbeb;
}
.pri.high {
  color: #b91c1c;
  background: #fef2f2;
}
.due {
  font-size: 24rpx;
  color: #64748b;
}
.desc {
  margin-top: 28rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid rgba(148, 163, 184, 0.25);
}
.dl {
  display: block;
  font-size: 24rpx;
  color: #94a3b8;
  margin-bottom: 12rpx;
}
.dc {
  font-size: 28rpx;
  color: #334155;
  line-height: 1.55;
  white-space: pre-wrap;
}
.actions {
  margin-top: 36rpx;
  display: flex;
  gap: 20rpx;
}
.ghost,
.danger {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  font-weight: 700;
  border: none;
}
.ghost {
  background: #f1f5f9;
  color: #0f172a;
}
.danger {
  background: #fef2f2;
  color: #b91c1c;
}
</style>
