import http from './request'

/** DeepSeek-V4-Flash 经 Spring Boot 转发：POST /ai/chat */
export function chatAi(question) {
  return http.post('/ai/chat', { question }, { timeout: 180000 })
}

/** POST /ai/generateTodo，返回 data.suggestions */
export function generateTodoAi(prompt) {
  return http.post('/ai/generateTodo', { prompt }, { timeout: 180000 })
}

/** AI 生成并写入 MySQL，返回 data 为 Todo[] */
export function generateTodosAndSave(prompt) {
  return http.post('/ai/generateTodosAndSave', { prompt }, { timeout: 180000 })
}
