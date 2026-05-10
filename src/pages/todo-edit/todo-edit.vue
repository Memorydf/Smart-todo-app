<template>
  <view class="page">
    <view v-if="todo" class="card">
      <text class="label">标题</text>
      <input v-model="title" class="inp" />

      <text class="label">描述</text>
      <textarea v-model="description" class="area" auto-height />

      <text class="label">分类</text>
      <view class="row">
        <view
          v-for="c in CATEGORY_LIST"
          :key="c.key"
          class="pill"
          :class="{ on: category === c.key }"
          @click="category = c.key"
        >
          <text>{{ c.label }}</text>
        </view>
      </view>

      <text class="label">优先级</text>
      <view class="row">
        <view
          v-for="p in priOptions"
          :key="p.key"
          class="pill"
          :class="{ on: priority === p.key }"
          @click="priority = p.key"
        >
          <text>{{ p.label }}</text>
        </view>
      </view>

      <text class="label">截止日期</text>
      <picker mode="date" :value="dueDate || today" @change="onDue">
        <view class="picker">{{ dueDate || '未设置' }}</view>
      </picker>
    </view>
    <Empty v-else title="未找到该待办" />

    <button v-if="todo" class="save" :disabled="!canSave" @click="save">保存修改</button>
  </view>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import Empty from '@/components/Empty.vue'
import { useTodoStore } from '@/store/todo'
import { CATEGORY_LIST } from '@/utils/constants'
import { todayStr } from '@/utils/date'

const store = useTodoStore()
store.hydrate().catch(() => {})

const id = ref('')
const todo = computed(() => store.todos.find((t) => t.id === id.value))

const title = ref('')
const description = ref('')
const category = ref('work')
const priority = ref('medium')
const dueDate = ref('')
const today = todayStr()

const priOptions = [
  { key: 'low', label: '低' },
  { key: 'medium', label: '中' },
  { key: 'high', label: '高' },
]

watch(
  todo,
  (t) => {
    if (!t) return
    title.value = t.title
    description.value = t.description || ''
    category.value = t.category || 'other'
    priority.value = t.priority || 'medium'
    dueDate.value = t.dueDate || ''
  },
  { immediate: true }
)

const canSave = computed(() => title.value.trim().length > 0)

onLoad((q) => {
  id.value = q.id || ''
})

function onDue(e) {
  dueDate.value = e.detail.value
}

async function save() {
  if (!todo.value) return
  try {
    await store.updateTodo(todo.value.id, {
      title: title.value.trim(),
      description: description.value.trim(),
      category: category.value,
      priority: priority.value,
      dueDate: dueDate.value || null,
    })
    uni.showToast({ title: '已保存', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 400)
  } catch {
    /* request 拦截器已提示 */
  }
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
.label {
  display: block;
  font-size: 26rpx;
  color: #64748b;
  margin-bottom: 12rpx;
  margin-top: 8rpx;
}
.inp {
  height: 88rpx;
  padding: 0 24rpx;
  border-radius: 16rpx;
  background: #f8fafc;
  border: 1rpx solid rgba(148, 163, 184, 0.35);
  font-size: 28rpx;
  margin-bottom: 16rpx;
}
.area {
  min-height: 160rpx;
  padding: 20rpx 24rpx;
  border-radius: 16rpx;
  background: #f8fafc;
  border: 1rpx solid rgba(148, 163, 184, 0.35);
  font-size: 28rpx;
  margin-bottom: 16rpx;
  width: 100%;
  box-sizing: border-box;
}
.row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 16rpx;
}
.pill {
  padding: 14rpx 28rpx;
  border-radius: 999rpx;
  background: #f1f5f9;
  border: 1rpx solid transparent;
  font-size: 26rpx;
  color: #475569;
}
.pill.on {
  background: rgba(37, 99, 235, 0.12);
  border-color: rgba(37, 99, 235, 0.35);
  color: #1d4ed8;
  font-weight: 700;
}
.picker {
  height: 88rpx;
  line-height: 88rpx;
  padding: 0 24rpx;
  border-radius: 16rpx;
  background: #f8fafc;
  border: 1rpx solid rgba(148, 163, 184, 0.35);
  font-size: 28rpx;
  color: #0f172a;
  margin-bottom: 8rpx;
}
.save {
  margin-top: 32rpx;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 700;
  border: none;
}
.save[disabled] {
  opacity: 0.45;
}
</style>
