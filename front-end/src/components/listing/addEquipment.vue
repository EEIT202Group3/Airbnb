<script setup>
import { ref, onMounted } from 'vue'

const equipmentList = ref([])
const newEquipName = ref('')

// 取得設備清單
const fetchEquipments = async () => {
  try {
    const res = await fetch('http://localhost:8080/equipment/all')
    equipmentList.value = await res.json()
  } catch (err) {
    alert('設備資料載入失敗')
  }
}

// 新增設備
const addEquipment = async () => {
  if (!newEquipName.value) return alert('請輸入設備名稱')
  const params = new URLSearchParams()
  params.append('equipName', newEquipName.value)

  const res = await fetch('http://localhost:8080/equipment/add?' + params.toString(), {
    method: 'POST'
  })
  const result = await res.text()
  alert(result)
  if (result === '新增成功') {
    newEquipName.value = ''
    fetchEquipments()
  }
}

// 刪除設備
const deleteEquipment = async (id) => {
  if (!confirm('確定要刪除這個設備嗎？')) return

  const res = await fetch(`http://localhost:8080/equipment/delete/${id}`, {
    method: 'DELETE'
  })
  const result = await res.text()
  alert(result)
  if (result === '刪除成功') {
    fetchEquipments()
  }
}

onMounted(() => {
  fetchEquipments()
})
</script>

<template>
  <div class="my-container mt-5 w-100">
    <!-- 新增設備卡片 -->
    <div class="card shadow-sm mb-4 " >
      <div class="card-header bg-primary text-white">
        <h4 class="mb-0">新增設備</h4>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <label for="equip_name" class="form-label">設備名稱</label>
          <input v-model="newEquipName" id="equip_name" class="form-control" required>
        </div>
        <button @click="addEquipment" class="btn btn-success">新增</button>
      </div>
    </div>

    <!-- 設備列表卡片 -->
    <div class="card shadow-sm w-100">
      <div class="card-header bg-secondary text-white">
        <h4 class="mb-0">目前設備列表</h4>
      </div>
      <div class="card-body p-0">
        <table class="table table-striped mb-0">
          <thead class="table-light">
            <tr>
              <th scope="col">設備ID</th>
              <th scope="col">設備名稱</th>
              <th scope="col">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="equip in equipmentList" :key="equip.equip_id">
              <td>{{ equip.equip_id }}</td>
              <td>{{ equip.equip_name }}</td>
              <td>
                <button @click="deleteEquipment(equip.equip_id)" class="btn btn-danger btn-sm">刪除</button>
              </td>
            </tr>
            <tr v-if="equipmentList.length === 0">
              <td colspan="3" class="text-center">目前沒有設備資料</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style>
@import 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css';

html, body, #app, .app-wrapper {
  width: 100%;
  margin: 0px;
  padding: 0;
}
  
.my-container {
  max-width: 600px;
  margin:  400px auto;
  padding: 20px;
}
</style>