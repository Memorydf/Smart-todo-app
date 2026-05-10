/** 本地存储键（无后端时的持久化） */
export const STORAGE_TODOS = 'smart_todo_list'
export const STORAGE_AI = 'smart_todo_ai_messages'
export const STORAGE_SETTINGS = 'smart_todo_settings'

export const CATEGORY_LIST = [
  { key: 'work', label: '工作' },
  { key: 'study', label: '学习' },
  { key: 'life', label: '生活' },
  { key: 'other', label: '其他' },
]

export function categoryLabel(key) {
  return CATEGORY_LIST.find((c) => c.key === key)?.label || '其他'
}
