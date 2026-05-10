<template>
  <view
    class="card"
    :class="{ done: todo.completed, compact: settings.compactList }"
    @click="$emit('open', todo)"
  >
    <view class="row">
      <view class="check" @click.stop="$emit('toggle', todo.id)">
        <view class="dot" :class="{ on: todo.completed }">
          <text v-if="todo.completed" class="tick">✓</text>
        </view>
      </view>
      <view class="main">
        <text class="title">{{ todo.title }}</text>
        <view class="meta">
          <text class="chip">{{ cat }}</text>
          <text v-if="todo.dueDate" class="due" :class="{ warn: isOverdue }">{{
            todo.dueDate
          }}</text>
          <text class="pri" :class="todo.priority">{{ priLabel }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { useSettingsStore } from '@/store/settings'
import { categoryLabel } from '@/utils/constants'

const settings = useSettingsStore()

const props = defineProps({
  todo: { type: Object, required: true },
})

defineEmits(['toggle', 'open'])

const cat = computed(() => categoryLabel(props.todo.category))

const priLabel = computed(() => {
  const m = { low: '低', medium: '中', high: '高' }
  return m[props.todo.priority] || '中'
})

const isOverdue = computed(() => {
  if (!props.todo.dueDate || props.todo.completed) return false
  const t = new Date()
  const pad = (n) => (n < 10 ? `0${n}` : `${n}`)
  const today = `${t.getFullYear()}-${pad(t.getMonth() + 1)}-${pad(t.getDate())}`
  return props.todo.dueDate < today
})
</script>

<style scoped lang="scss">
.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(148, 163, 184, 0.25);
}
.card.done {
  opacity: 0.72;
}
.row {
  display: flex;
  align-items: flex-start;
}
.check {
  padding: 4rpx 16rpx 0 0;
}
.dot {
  width: 44rpx;
  height: 44rpx;
  border-radius: 12rpx;
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
  font-size: 26rpx;
  font-weight: 700;
}
.main {
  flex: 1;
  min-width: 0;
}
.title {
  font-size: 30rpx;
  color: #0f172a;
  font-weight: 600;
  line-height: 1.35;
}
.done .title {
  text-decoration: line-through;
  color: #64748b;
}
.meta {
  margin-top: 12rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  align-items: center;
}
.chip {
  font-size: 22rpx;
  color: #2563eb;
  background: rgba(37, 99, 235, 0.08);
  padding: 4rpx 14rpx;
  border-radius: 999rpx;
}
.due {
  font-size: 22rpx;
  color: #64748b;
}
.due.warn {
  color: #dc2626;
}
.pri {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
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

.card.compact {
  padding: 18rpx 20rpx;
  margin-bottom: 10rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 18rpx rgba(15, 23, 42, 0.05);
}
.card.compact .check {
  padding: 2rpx 12rpx 0 0;
}
.card.compact .dot {
  width: 38rpx;
  height: 38rpx;
  border-radius: 10rpx;
}
.card.compact .tick {
  font-size: 22rpx;
}
.card.compact .title {
  font-size: 28rpx;
  line-height: 1.3;
}
.card.compact .meta {
  margin-top: 8rpx;
  gap: 8rpx;
}
.card.compact .chip,
.card.compact .due,
.card.compact .pri {
  font-size: 20rpx;
}
.card.compact .chip {
  padding: 2rpx 10rpx;
}
.card.compact .pri {
  padding: 2rpx 10rpx;
}
</style>
