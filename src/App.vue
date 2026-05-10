<script>
import { useTodoStore } from './store/todo'
import { useAiStore } from './store/ai'
import { useSettingsStore } from './store/settings'

export default {
  onLaunch() {
    useTodoStore().hydrate().catch(() => {})
    useAiStore().hydrate()
    useSettingsStore().hydrate()
  },
}
</script>

<style>
page {
  background-color: #f5f7fb;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
    'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}
</style>
