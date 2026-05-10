import { defineStore } from 'pinia'
import { generateTodosAndSave } from '@/api/ai'
import {
  addTodoRemote,
  clearTodosRemote,
  deleteTodoRemote,
  fetchTodoList,
  isRemoteTodo,
  updateTodoRemote,
} from '@/api/todo'
import { STORAGE_TODOS } from '@/utils/constants'
import { TODO_DATA_SOURCE } from '@/utils/config'
import { getJson, setJson } from '@/utils/storage'
import { todayStr } from '@/utils/date'

function uid() {
  return `${Date.now()}_${Math.random().toString(36).slice(2, 9)}`
}

/** 后端 Todo → 前端列表项 */
export function fromServer(row) {
  if (!row) return null
  const deadline = row.deadline
  let due = null
  if (deadline != null && deadline !== '') {
    due = String(deadline).slice(0, 10)
  }
  return {
    id: String(row.id),
    title: row.title || '',
    description: row.content || '',
    category: row.category || 'other',
    priority: row.priority || 'medium',
    dueDate: due,
    completed: row.status === 1,
    createdAt: row.createTime ? Date.parse(row.createTime) : Date.now(),
    updatedAt: row.updateTime ? Date.parse(row.updateTime) : Date.now(),
  }
}

function toServerDto(t) {
  const numId = Number(t.id)
  return {
    id: Number.isFinite(numId) ? numId : undefined,
    title: t.title,
    content: t.description || '',
    category: t.category || 'other',
    priority: t.priority || 'medium',
    deadline: t.dueDate || null,
    status: t.completed ? 1 : 0,
  }
}

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todos: [],
    hydrated: false,
  }),
  getters: {
    stats(state) {
      const today = todayStr()
      let todayPending = 0
      let overdue = 0
      let completed = 0
      let pending = 0
      for (const t of state.todos) {
        if (t.completed) {
          completed++
          continue
        }
        pending++
        if (t.dueDate && t.dueDate < today) overdue++
        if (t.dueDate === today) todayPending++
      }
      return {
        total: state.todos.length,
        completed,
        pending,
        overdue,
        todayPending,
      }
    },
    categoryPending(state) {
      const m = { work: 0, study: 0, life: 0, other: 0 }
      for (const t of state.todos) {
        if (t.completed) continue
        const k = m[t.category] !== undefined ? t.category : 'other'
        m[k]++
      }
      return m
    },
  },
  actions: {
    async hydrate() {
      if (this.hydrated) return
      if (TODO_DATA_SOURCE === 'remote') {
        try {
          await this.fetchList()
        } catch {
          this.todos = getJson(STORAGE_TODOS, [])
          uni.showToast({
            title: '无法连接待办服务，已显示本地缓存',
            icon: 'none',
            duration: 2500,
          })
        }
        this.hydrated = true
        return
      }
      const list = getJson(STORAGE_TODOS, [])
      this.todos = Array.isArray(list) ? list : []
      this.hydrated = true
    },

    async fetchList() {
      const res = await fetchTodoList()
      const rows = res?.data
      this.todos = Array.isArray(rows) ? rows.map(fromServer) : []
      if (isRemoteTodo()) {
        setJson(STORAGE_TODOS, this.todos)
      }
    },

    persist() {
      if (TODO_DATA_SOURCE === 'remote') return
      setJson(STORAGE_TODOS, this.todos)
    },

    async addTodo(payload) {
      const title = payload.title.trim()
      const description = (payload.description || '').trim()
      const category = payload.category || 'other'
      const priority = payload.priority || 'medium'
      const dueDate = payload.dueDate || null

      if (TODO_DATA_SOURCE === 'remote') {
        const res = await addTodoRemote({
          title,
          content: description || undefined,
          category,
          priority,
          deadline: dueDate || undefined,
          status: 0,
        })
        const item = fromServer(res.data)
        this.todos.unshift(item)
        setJson(STORAGE_TODOS, this.todos)
        return item
      }

      const now = Date.now()
      const item = {
        id: uid(),
        title,
        description,
        category,
        priority,
        dueDate,
        completed: false,
        createdAt: now,
        updatedAt: now,
      }
      this.todos.unshift(item)
      this.persist()
      return item
    },

    async updateTodo(id, patch) {
      const i = this.todos.findIndex((t) => t.id === id)
      if (i === -1) return
      const merged = { ...this.todos[i], ...patch, updatedAt: Date.now() }

      if (TODO_DATA_SOURCE === 'remote') {
        const body = toServerDto(merged)
        if (body.id == null) {
          uni.showToast({ title: '无效的待办 id', icon: 'none' })
          return
        }
        const res = await updateTodoRemote(body)
        this.todos[i] = fromServer(res.data)
        setJson(STORAGE_TODOS, this.todos)
        return
      }

      this.todos[i] = merged
      this.persist()
    },

    async removeTodo(id) {
      if (TODO_DATA_SOURCE === 'remote') {
        const num = Number(id)
        if (Number.isFinite(num)) {
          await deleteTodoRemote(num)
        }
      }
      this.todos = this.todos.filter((t) => t.id !== id)
      if (TODO_DATA_SOURCE === 'remote') {
        setJson(STORAGE_TODOS, this.todos)
      } else {
        this.persist()
      }
    },

    async toggleTodo(id) {
      const t = this.todos.find((x) => x.id === id)
      if (!t) return
      await this.updateTodo(id, { completed: !t.completed })
    },

    async clearAll() {
      if (TODO_DATA_SOURCE === 'remote') {
        await clearTodosRemote()
      }
      this.todos = []
      setJson(STORAGE_TODOS, [])
      if (TODO_DATA_SOURCE !== 'remote') {
        this.persist()
      }
    },

    /** AI：生成并入库后刷新列表（需 TODO_DATA_SOURCE=remote） */
    async generateTodosFromAi(prompt) {
      if (TODO_DATA_SOURCE !== 'remote') {
        uni.showToast({ title: '请将 TODO_DATA_SOURCE 设为 remote 以使用 AI 写库', icon: 'none' })
        return 0
      }
      const res = await generateTodosAndSave(prompt)
      const n = Array.isArray(res?.data) ? res.data.length : 0
      await this.fetchList()
      return n
    },

    guessCategory(text) {
      const s = String(text)
      if (/会议|项目|需求|邮件|工作|汇报|客户|工单/.test(s)) return 'work'
      if (/学习|课程|考试|作业|单词|阅读|复习/.test(s)) return 'study'
      if (/电影|购物|锻炼|家人|旅行|生活|做饭/.test(s)) return 'life'
      return 'other'
    },
  },
})
