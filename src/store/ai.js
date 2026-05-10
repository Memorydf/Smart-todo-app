import { defineStore } from 'pinia'
import { chatAi } from '@/api/ai'
import { STORAGE_AI } from '@/utils/constants'
import { getJson, setJson } from '@/utils/storage'

const welcomeMsg = () => ({
  id: `welcome_${Date.now()}`,
  role: 'assistant',
  content:
    '你好，我是智能待办助手（后端通过 DeepSeek-V4-Flash API 提供服务）。直接输入问题即可；若长时间无响应，请检查网络与后端服务状态。',
  createdAt: Date.now(),
})

export const useAiStore = defineStore('ai', {
  state: () => ({
    messages: [],
    hydrated: false,
  }),
  actions: {
    hydrate() {
      if (this.hydrated) return
      const list = getJson(STORAGE_AI, [])
      this.messages = Array.isArray(list) && list.length ? list : [welcomeMsg()]
      if (!Array.isArray(list) || !list.length) this.persist()
      this.hydrated = true
    },
    persist() {
      setJson(STORAGE_AI, this.messages)
    },
    pushUser(text) {
      const m = {
        id: `${Date.now()}_u`,
        role: 'user',
        content: text.trim(),
        createdAt: Date.now(),
      }
      this.messages.push(m)
      this.persist()
      return m
    },
    pushAssistant(text) {
      this.messages.push({
        id: `${Date.now()}_a`,
        role: 'assistant',
        content: text,
        createdAt: Date.now(),
      })
      this.persist()
    },
    /** 离线兜底（网络或后端不可用时） */
    replyToLocal(userText) {
      let reply = ''
      if (/计划|学习|复习/.test(userText)) {
        reply =
          '建议用「目标 → 本周重点 → 每日最小任务」三层拆解。先选一个 25 分钟内能完成的小步骤开始。'
      } else if (/累|焦虑|拖延|不想/.test(userText)) {
        reply = '可以试试两分钟法则：告诉自己只做两分钟，降低启动阻力。'
      } else if (/待办|提醒|任务|截止/.test(userText)) {
        reply = '在「待办」页可新增任务并设置截止日期与优先级。'
      } else if (/你好|hi|hello/i.test(userText)) {
        reply = '你好！说说你今天最想完成的一件事。'
      } else {
        reply = '暂时无法连接 AI 服务，请检查网络与后端；这是本地简要回复。'
      }
      this.pushAssistant(reply)
    },
    async replyTo(userText) {
      try {
        const res = await chatAi(userText)
        const answer = res?.data?.answer
        if (answer == null || String(answer).trim() === '') {
          throw new Error('AI 未返回内容')
        }
        this.pushAssistant(String(answer).trim())
      } catch {
        this.replyToLocal(userText)
      }
    },
    resetChat() {
      this.messages = [welcomeMsg()]
      this.persist()
    },
  },
})
