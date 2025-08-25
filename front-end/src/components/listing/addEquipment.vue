<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const equipmentList = ref([])
const newEquipName = ref('')
const newEquipIcon = ref('')
const newEquipCategory = ref('')
const filterCategory = ref('') // 用於篩選表格
const router = useRouter()

// 設備分類選項
const categoryOptions = [
  '娛樂',
  '衛浴',
  '廚房和餐飲',
  '臥室和洗衣',
  '停車位和設施',
  '客廳',
  '居家安全'
]

// 取得設備清單
const fetchEquipments = async () => {
  const res = await fetch('http://localhost:8080/api/equipment/all', {
    method: 'GET',
    credentials: 'include'
  })
  if (res.status === 401 || res.status === 403) {
    alert('請先登入')
    router.push({ name: 'Homepage' })
    return
  }
  equipmentList.value = await res.json()
}

// 新增設備
const addEquipment = async () => {
  if (!newEquipName.value) return alert('請輸入設備名稱')
  if (!newEquipIcon.value) return alert('請輸入設備圖示名稱')
  if (!newEquipCategory.value) return alert('請輸入設備分類')

  const iconClass = `mdi-${newEquipIcon.value.trim()}`

  const params = new URLSearchParams()
  params.append('equipName', newEquipName.value)
  params.append('equipIcon', iconClass)
  params.append('equipCategory', newEquipCategory.value)

  const res = await fetch('http://localhost:8080/api/equipment/add?' + params.toString(), {
    method: 'POST'
  })
  const result = await res.text()
  alert(result)
  if (result === '新增成功') {
    newEquipName.value = ''
    newEquipIcon.value = ''
    newEquipCategory.value = ''
    fetchEquipments()
  }
}

// 刪除設備
const deleteEquipment = async (id) => {
  if (!confirm('確定要刪除這個設備嗎？')) return

  const res = await fetch(`http://localhost:8080/api/equipment/delete/${id}`, {
    method: 'DELETE'
  })
  const result = await res.text()
  alert(result)
  if (result === '刪除成功') {
    fetchEquipments()
  }
}

// 設備篩選計算屬性
const filteredEquipmentList = computed(() => {
  if (!filterCategory.value || filterCategory.value === '全部') return equipmentList.value
  return equipmentList.value.filter(e => e.equip_category === filterCategory.value)
})
onMounted(fetchEquipments)
</script>


<template>
  <h1>設備管理</h1>

  <!-- 新增設備表單 + 篩選分類 -->
  <v-row class="mb-4">
    <!-- 設備名稱 -->
    <v-col cols="4">
      <v-text-field
        v-model="newEquipName"
        label="設備名稱"
        variant="solo"
        clearable
        required
      ></v-text-field>
    </v-col>

    <!-- 設備圖示 -->
    <v-col cols="3">
      <v-text-field
        v-model="newEquipIcon"
        label="設備圖示名稱 (例如: wifi, car, bed)"
        variant="solo"
        clearable
        required
      ></v-text-field>
      <div class="mt-1">
        預覽：
        <v-icon v-if="newEquipIcon" size="20">
          {{ `mdi-${newEquipIcon}` }}
        </v-icon>
        <span v-else style="color: #888;">請輸入有效的圖示名稱</span>
      </div>
    </v-col>

    <!-- 新增設備分類 -->
    <v-col cols="3">
      <v-select
        v-model="newEquipCategory"
        :items="categoryOptions"
        label="設備分類"
        variant="solo"
        clearable
        required
      ></v-select>
    </v-col>

    <!-- 新增設備按鈕 -->
    <v-col cols="2">
      <v-btn
        color="success"
        @click="addEquipment"
        style="height: 50px; margin-top: 3px; width: 60%;"
      >
        <v-icon start>mdi-plus</v-icon>
        新增
      </v-btn>
    </v-col>
    </v-row>

        <!-- 篩選分類（圖示按鈕） -->
    <v-row cols="2" justify="end">
      <v-menu offset-y>
        <template #activator="{ props }">
          <v-btn v-bind="props" icon class="filter-btn">
            <v-icon>mdi-tune</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="filterCategory = ''">
            <v-list-item-title>全部</v-list-item-title>
          </v-list-item>
          <v-list-item
            v-for="cat in categoryOptions"
            :key="cat"
            @click="filterCategory = cat"
          >
            <v-list-item-title>{{ cat }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
  </v-row>

  <!-- 設備表格 -->
  <v-table v-if="filteredEquipmentList && filteredEquipmentList.length > 0">
    <thead>
      <tr>
        <th>設備ID</th>
        <th>設備名稱</th>
        <th>圖示</th>
        <th>分類</th>
        <th>操作</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="equip in filteredEquipmentList" :key="equip.equip_id">
        <td>{{ equip.equip_id }}</td>
        <td>{{ equip.equip_name }}</td>
        <td>
          <v-icon v-if="equip.equip_icon" size="25">
            {{ equip.equip_icon }}
          </v-icon>
          <span v-else style="color:#888;">無效圖示</span>
        </td>
        <td>{{ equip.equip_category }}</td>
        <td>
          <v-btn
            color="error"
            size="small"
            @click="deleteEquipment(equip.equip_id)"
          >
            刪除
          </v-btn>
        </td>
      </tr>
    </tbody>
  </v-table>

  <!-- 無資料提示 -->
  <div v-else class="text-center">目前沒有設備資料</div>
</template>

<style scoped>
h1 {
  margin-bottom: 20px;
}

.filter-btn {
  background-color: transparent !important;
  box-shadow: none !important;
}
.filter-btn:hover {
  background-color: transparent !important;
}

</style>
