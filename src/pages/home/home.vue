<template>
  <view class="page">
    <view class="head">
      <view>
        <text class="hi">{{ greeting }}</text>
        <text class="date">{{ todayLabel }}</text>
      </view>
      <view class="head-btns">
        <view v-if="todoRemote" class="ai-btn" @click="openAiPanel">
          <text>AI 生成</text>
        </view>
        <view class="add-btn" @click="goAdd">
          <text class="plus">＋</text>
          <text>新建</text>
        </view>
      </view>
    </view>

    <view v-if="aiPanel" class="ai-mask" @click="closeAiPanel">
      <view class="ai-sheet" @click.stop>
        <text class="ai-title">AI 智能生成待办</text>
        <text class="ai-sub">描述你的目标或场景，将调用 DeepSeek-V4-Flash 拆解并写入数据库</text>
        <textarea
          v-model="aiPrompt"
          class="ai-area"
          placeholder="例如：本周准备英语四级，还要兼顾两门专业课作业…"
          :disabled="aiBusy"
        />
        <view class="ai-actions">
          <button class="ai-cancel" :disabled="aiBusy" @click="closeAiPanel">取消</button>
          <button class="ai-ok" :disabled="aiBusy || !aiPrompt.trim()" @click="runAiGenerate">
            {{ aiBusy ? '生成中…' : '生成并保存' }}
          </button>
        </view>
      </view>
    </view>

    <view class="stats">
      <view class="stat">
        <text class="n">{{ store.stats.todayPending }}</text>
        <text class="l">今日待办</text>
      </view>
      <view class="stat">
        <text class="n">{{ store.stats.pending }}</text>
        <text class="l">进行中</text>
      </view>
      <view class="stat warn" :class="{ on: store.stats.overdue > 0 }">
        <text class="n">{{ store.stats.overdue }}</text>
        <text class="l">已逾期</text>
      </view>
      <view class="stat">
        <text class="n">{{ store.stats.completed }}</text>
        <text class="l">已完成</text>
      </view>
    </view>

    <scroll-view scroll-x class="week" :show-scrollbar="false">
      <view class="week-inner">
        <view
          v-for="(d, i) in week"
          :key="i"
          class="day"
          :class="{ active: isToday(d), dot: hasDueOn(d) }"
        >
          <text class="w">{{ weekShort[i] }}</text>
          <text class="num">{{ d.getDate() }}</text>
        </view>
      </view>
    </scroll-view>

    <view class="tabs">
      <scroll-view scroll-x class="tab-scroll" :show-scrollbar="false">
        <view class="tab-inner">
          <view
            v-for="t in catTabs"
            :key="t.key"
            class="chip"
            :class="{ on: cat === t.key }"
            @click="cat = t.key"
          >
            <text>{{ t.label }}</text>
          </view>
        </view>
      </scroll-view>
      <view class="tog" @click="hideDone = !hideDone">
        <text>{{ hideDone ? '显示已完成' : '隐藏已完成' }}</text>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="list"
      :class="{ 'list--compact': settings.compactList }"
    >
      <block v-if="displayList.length">
        <TodoCard
          v-for="item in displayList"
          :key="item.id"
          :todo="item"
          @toggle="onToggleTodo"
          @open="openDetail"
        />
      </block>
      <Empty
        v-else
        title="还没有待办"
        hint="点击右上角「新建」添加第一条任务"
      />
      <view class="bottom-space" />
    </scroll-view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import TodoCard from '@/components/TodoCard.vue'
import Empty from '@/components/Empty.vue'
import { useTodoStore } from '@/store/todo'
import { useSettingsStore } from '@/store/settings'
import { CATEGORY_LIST } from '@/utils/constants'
import { TODO_DATA_SOURCE } from '@/utils/config'

const todoRemote = TODO_DATA_SOURCE === 'remote'
import { formatDate, weekDates } from '@/utils/date'

const store = useTodoStore()
const settings = useSettingsStore()
const cat = ref('all')
const hideDone = ref(false)

const aiPanel = ref(false)
const aiPrompt = ref('')
const aiBusy = ref(false)

function openAiPanel() {
  aiPrompt.value = ''
  aiPanel.value = true
}

function closeAiPanel() {
  if (aiBusy.value) return
  aiPanel.value = false
}

async function runAiGenerate() {
  const p = aiPrompt.value.trim()
  if (!p || aiBusy.value) return
  aiBusy.value = true
  try {
    const n = await store.generateTodosFromAi(p)
    uni.showToast({ title: n > 0 ? `已保存 ${n} 条待办` : '已刷新列表', icon: 'none' })
    aiPanel.value = false
    aiPrompt.value = ''
  } catch {
    // 全局 toast
  } finally {
    aiBusy.value = false
  }
}

const weekShort = ['日', '一', '二', '三', '四', '五', '六']
const week = weekDates(new Date(), 0)

const catTabs = computed(() => [
  { key: 'all', label: '全部' },
  ...CATEGORY_LIST.map((c) => ({ key: c.key, label: c.label })),
])

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '上午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const todayLabel = computed(() => {
  const t = new Date()
  const w = weekShort[t.getDay()]
  return `${t.getMonth() + 1}月${t.getDate()}日 周${w}`
})

function isToday(d) {
  return formatDate(d) === formatDate(new Date())
}

function hasDueOn(d) {
  const ds = formatDate(d)
  return store.todos.some((x) => x.dueDate === ds && !x.completed)
}

const displayList = computed(() => {
  let list = store.todos.filter((t) => {
    if (cat.value !== 'all' && t.category !== cat.value) return false
    if (hideDone.value && t.completed) return false
    return true
  })
  list = [...list].sort((a, b) => {
    if (a.completed !== b.completed) return a.completed ? 1 : -1
    const ad = a.dueDate || '9999-12-31'
    const bd = b.dueDate || '9999-12-31'
    if (ad !== bd) return ad.localeCompare(bd)
    return b.updatedAt - a.updatedAt
  })
  return list
})

function goAdd() {
  uni.navigateTo({ url: '/pages/todo-add/todo-add' })
}

function openDetail(todo) {
  uni.navigateTo({ url: `/pages/todo-detail/todo-detail?id=${todo.id}` })
}

async function onToggleTodo(id) {
  try {
    await store.toggleTodo(id)
  } catch {
    /* 已 toast */
  }
}

onShow(async () => {
  settings.hydrate()
  await store.hydrate()
  if (TODO_DATA_SOURCE === 'remote') {
    try {
      await store.fetchList()
    } catch {
      /* hydrate 已处理首次失败 */
    }
  }
})
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24rpx 24rpx 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8rpx 8rpx 16rpx;
}
.hi {
  display: block;
  font-size: 40rpx;
  font-weight: 800;
  color: #0f172a;
}
.date {
  display: block;
  margin-top: 8rpx;
  font-size: 26rpx;
  color: #64748b;
}
.head-btns {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.ai-btn {
  padding: 16rpx 22rpx;
  border-radius: 999rpx;
  background: #fff;
  border: 1rpx solid rgba(37, 99, 235, 0.45);
  color: #1d4ed8;
  font-size: 26rpx;
  font-weight: 700;
}
.add-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 28rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  font-size: 26rpx;
  font-weight: 700;
  box-shadow: 0 12rpx 32rpx rgba(37, 99, 235, 0.28);
}
.ai-mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.45);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 24rpx;
  box-sizing: border-box;
}
.ai-sheet {
  width: 100%;
  max-width: 700rpx;
  background: #fff;
  border-radius: 28rpx 28rpx 20rpx 20rpx;
  padding: 32rpx 28rpx calc(32rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -8rpx 40rpx rgba(15, 23, 42, 0.12);
}
.ai-title {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #0f172a;
}
.ai-sub {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #64748b;
  line-height: 1.45;
}
.ai-area {
  margin-top: 24rpx;
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx 24rpx;
  box-sizing: border-box;
  border-radius: 16rpx;
  background: #f8fafc;
  border: 1rpx solid rgba(148, 163, 184, 0.35);
  font-size: 28rpx;
}
.ai-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}
.ai-cancel,
.ai-ok {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  font-weight: 700;
  border: none;
}
.ai-cancel {
  background: #f1f5f9;
  color: #475569;
}
.ai-ok {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
}
.plus {
  font-size: 34rpx;
  line-height: 1;
}
.stats {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}
.stat {
  flex: 1;
  background: #fff;
  border-radius: 24rpx;
  padding: 20rpx 12rpx;
  text-align: center;
  box-shadow: 0 8rpx 28rpx rgba(15, 23, 42, 0.05);
  border: 1rpx solid rgba(148, 163, 184, 0.2);
}
.stat.warn.on .n {
  color: #dc2626;
}
.n {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #0f172a;
}
.l {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #64748b;
}
.week {
  white-space: nowrap;
  margin-bottom: 16rpx;
}
.week-inner {
  display: inline-flex;
  gap: 16rpx;
  padding: 4rpx 4rpx 12rpx;
}
.day {
  width: 96rpx;
  padding: 16rpx 0;
  border-radius: 20rpx;
  background: #fff;
  text-align: center;
  border: 1rpx solid rgba(148, 163, 184, 0.22);
  box-shadow: 0 6rpx 20rpx rgba(15, 23, 42, 0.04);
  position: relative;
}
.day.active {
  border-color: rgba(37, 99, 235, 0.55);
  background: rgba(37, 99, 235, 0.06);
}
.day.dot::after {
  content: '';
  position: absolute;
  bottom: 12rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: #2563eb;
}
.w {
  display: block;
  font-size: 22rpx;
  color: #94a3b8;
}
.num {
  display: block;
  margin-top: 8rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #0f172a;
}
.tabs {
  margin-bottom: 12rpx;
}
.tab-scroll {
  white-space: nowrap;
}
.tab-inner {
  display: inline-flex;
  gap: 12rpx;
  padding: 4rpx 0 12rpx;
}
.chip {
  padding: 12rpx 28rpx;
  border-radius: 999rpx;
  background: #fff;
  border: 1rpx solid rgba(148, 163, 184, 0.35);
  font-size: 26rpx;
  color: #475569;
}
.chip.on {
  background: rgba(37, 99, 235, 0.12);
  border-color: rgba(37, 99, 235, 0.35);
  color: #1d4ed8;
  font-weight: 700;
}
.tog {
  align-self: flex-end;
  font-size: 24rpx;
  color: #64748b;
  padding: 4rpx 0 8rpx;
}
.list {
  flex: 1;
  height: 0;
  padding-top: 8rpx;
}
.list--compact {
  padding-top: 4rpx;
}
.list--compact .bottom-space {
  height: 112rpx;
}
.bottom-space {
  height: 140rpx;
}
</style>
