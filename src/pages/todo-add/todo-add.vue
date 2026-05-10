<template>
  <view class="page">
    <view class="card">
      <text class="label">标题</text>
      <input v-model="title" class="inp" placeholder="想完成的一件事" />

      <text class="label">描述（可选）</text>
      <textarea
        v-model="description"
        class="area"
        placeholder="补充细节、验收标准…"
        auto-height
      />

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

      <view class="ai-row">
        <text class="hint">根据标题猜测分类（离线规则）</text>
        <button class="mini" size="mini" @click="autoCat">智能分类</button>
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

    <button class="save" :disabled="!canSave" @click="save">保存</button>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useTodoStore } from '@/store/todo'
import { CATEGORY_LIST } from '@/utils/constants'
import { todayStr } from '@/utils/date'

const store = useTodoStore()
store.hydrate().catch(() => {})

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

const canSave = computed(() => title.value.trim().length > 0)

function onDue(e) {
  dueDate.value = e.detail.value
}

function autoCat() {
  if (!title.value.trim()) {
    uni.showToast({ title: '请先填写标题', icon: 'none' })
    return
  }
  category.value = store.guessCategory(title.value)
  uni.showToast({ title: '已更新分类', icon: 'none' })
}

async function save() {
  try {
    await store.addTodo({
      title: title.value,
      description: description.value,
      category: category.value,
      priority: priority.value,
      dueDate: dueDate.value || null,
    })
    uni.showToast({ title: '已添加', icon: 'success' })
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
.ai-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
  gap: 16rpx;
}
.hint {
  flex: 1;
  font-size: 24rpx;
  color: #94a3b8;
}
.mini {
  background: #fff;
  color: #2563eb;
  border: 1rpx solid rgba(37, 99, 235, 0.45);
  border-radius: 999rpx;
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
