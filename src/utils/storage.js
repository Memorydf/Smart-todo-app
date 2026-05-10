export function getJson(key, fallback = null) {
  try {
    const raw = uni.getStorageSync(key)
    if (raw === '' || raw === undefined || raw === null) return fallback
    return typeof raw === 'string' ? JSON.parse(raw) : raw
  } catch {
    return fallback
  }
}

export function setJson(key, value) {
  uni.setStorageSync(key, JSON.stringify(value))
}
