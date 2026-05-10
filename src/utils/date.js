export function pad(n) {
  return n < 10 ? `0${n}` : `${n}`
}

export function formatDate(d) {
  const x = d instanceof Date ? d : new Date(d)
  return `${x.getFullYear()}-${pad(x.getMonth() + 1)}-${pad(x.getDate())}`
}

export function isSameDay(a, b) {
  return formatDate(a) === formatDate(b)
}

export function todayStr() {
  return formatDate(new Date())
}

/** 本周起始日 offset：0=周日 */
export function weekDates(base = new Date(), weekStartsOn = 0) {
  const day = base.getDay()
  const diff = (day - weekStartsOn + 7) % 7
  const start = new Date(base)
  start.setDate(base.getDate() - diff)
  start.setHours(0, 0, 0, 0)
  return Array.from({ length: 7 }, (_, i) => {
    const t = new Date(start)
    t.setDate(start.getDate() + i)
    return t
  })
}
