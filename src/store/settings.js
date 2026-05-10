import { defineStore } from 'pinia'
import { STORAGE_SETTINGS } from '@/utils/constants'
import { getJson, setJson } from '@/utils/storage'

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    compactList: false,
    hydrated: false,
  }),
  actions: {
    hydrate() {
      if (this.hydrated) return
      const s = getJson(STORAGE_SETTINGS, {})
      this.compactList = !!s.compactList
      this.hydrated = true
    },
    persist() {
      setJson(STORAGE_SETTINGS, { compactList: this.compactList })
    },
    setCompact(v) {
      this.compactList = !!v
      this.persist()
    },
  },
})
