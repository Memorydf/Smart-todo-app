import http from './request'
import { TODO_DATA_SOURCE } from '@/utils/config'

export function isRemoteTodo() {
  return TODO_DATA_SOURCE === 'remote'
}

/** GET /todo/list */
export function fetchTodoList() {
  return http.get('/todo/list')
}

/** POST /todo/add */
export function addTodoRemote(body) {
  return http.post('/todo/add', body)
}

/** PUT /todo/update */
export function updateTodoRemote(body) {
  return http.put('/todo/update', body)
}

/** DELETE /todo/delete/:id */
export function deleteTodoRemote(id) {
  return http.delete(`/todo/delete/${id}`)
}

/** PUT /todo/finish/:id */
export function finishTodoRemote(id) {
  return http.put(`/todo/finish/${id}`)
}

/** DELETE /todo/clear */
export function clearTodosRemote() {
  return http.delete('/todo/clear')
}
