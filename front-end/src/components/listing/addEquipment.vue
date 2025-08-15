<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const equipmentList = ref([])
const newEquipName = ref('')
const newEquipIcon = ref('')
const newEquipCategory = ref('')
const router = useRouter()

// 判斷 icon 類型的計算屬性
const iconType = computed(() => {
  const icon = newEquipIcon.value.trim()
  if (icon.startsWith('fa-') || icon.startsWith('fas') || icon.startsWith('fab') || icon.startsWith('fa ')) {
    return 'fa'
  }
  if (icon.startsWith('mdi-')) {
    return 'mdi'
  }
  return 'none'
})

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
  if (!newEquipIcon.value) return alert('請輸入設備圖示')
  if (!newEquipCategory.value) return alert('請輸入設備分類')

  const params = new URLSearchParams()
  params.append('equipName', newEquipName.value)
  params.append('equipIcon', newEquipIcon.value)
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

onMounted(fetchEquipments)
</script>

<template>
  <h1>設備管理</h1>

  <!-- 新增設備表單 -->
  <v-row>
    <v-col cols="4">
      <v-text-field
        v-model="newEquipName"
        label="設備名稱"
        variant="solo"
        clearable
        required
      ></v-text-field>
    </v-col>
    <v-col cols="4">
      <v-text-field
        v-model="newEquipIcon"
        label="設備圖示 (Font Awesome 或 MDI class)"
        variant="solo"
        clearable
        required
      ></v-text-field>
      <div class="mt-1">
        預覽：
        <span v-if="iconType === 'fa'">
          <i :class="newEquipIcon" style="font-size:20px;"></i>
        </span>
        <v-icon v-else-if="iconType === 'mdi'" size="20">{{ newEquipIcon }}</v-icon>
        <span v-else style="color: #888;">請輸入有效的 icon class</span>
      </div>
    </v-col>
    <v-col cols="2">
      <v-text-field
        v-model="newEquipCategory"
        label="設備分類"
        variant="solo"
        clearable
        required
      ></v-text-field>
    </v-col>
    <v-col cols="2">
      <v-btn
        color="success"
        @click="addEquipment"
        style="height: 50px; margin-top: 3px; width: 100%;"
      >
        <i class="fa-solid fa-plus" style="margin-right: 5px;"></i>
        新增
      </v-btn>
    </v-col>
  </v-row>

  <!-- 設備表格 -->
  <v-table v-if="equipmentList && equipmentList.length > 0">
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
      <tr v-for="equip in equipmentList" :key="equip.equip_id">
        <td>{{ equip.equip_id }}</td>
        <td>{{ equip.equip_name }}</td>
        <td>
          <span v-if="equip.equip_icon.startsWith('fa-') || equip.equip_icon.startsWith('fas') || equip.equip_icon.startsWith('fab') || equip.equip_icon.startsWith('fa ')">
            <i :class="equip.equip_icon" style="font-size:20px;"></i>
          </span>
          <v-icon v-else-if="equip.equip_icon.startsWith('mdi-')" size="25">{{ equip.equip_icon }}</v-icon>
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
</style>
