<template>
  <div>
    <div class="container">
      <div class="form-container">
        <div class="form-title">編輯房源</div>
        <form @submit.prevent="updateListing" enctype="multipart/form-data">
          <input type="hidden" v-model="listing.listId" />

          <div class="mb-3">
            <label class="form-label">房屋名稱</label>
            <input type="text" v-model="listing.houseName" class="form-control" required />
          </div>

          <div class="mb-3">
            <label class="form-label">地址</label>
            <input type="text" v-model="listing.ads" class="form-control" />
          </div>

          <div class="mb-3 row">
            <div class="col">
              <label class="form-label">房型</label>
              <input type="text" v-model="listing.room" class="form-control" />
            </div>
            <div class="col">
              <label class="form-label">床位</label>
              <input type="text" v-model="listing.bed" class="form-control" />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">描述</label>
            <textarea v-model="listing.describe" class="form-control" rows="3"></textarea>
          </div>

          <div class="mb-3 row">
            <div class="col">
              <label class="form-label">電話</label>
              <input type="text" v-model="listing.tel" class="form-control" />
            </div>
            <div class="col">
              <label class="form-label">人數</label>
              <input type="number" v-model="listing.ppl" class="form-control" />
            </div>
            <div class="col">
              <label class="form-label">價格</label>
              <input type="number" v-model="listing.price" class="form-control" />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">照片上傳（最多十張，可不選表示不更換）</label>
            <input type="file" @change="handlePhotos" multiple accept="image/*" class="form-control" />
            <small class="form-text text-muted">不選擇圖片即保留原照片</small>
          </div>

          <div class="mb-3">
            <label class="form-label">現有照片預覽</label>
            <div class="d-flex flex-wrap gap-2">
              <img
                v-for="url in currentPhotos"
                :src="url"
                :key="url"
                style="width:100px; height:70px; object-fit:cover; border-radius:8px;"
              />
            </div>
          </div>

          <!-- 設備選擇 -->
          <div class="mb-3">
            <label class="form-label">請勾選設備：</label>
            <div v-for="(equipments, category) in groupedEquipments" :key="category" class="mb-4">
              <h5 class="equip-category-title">{{ category }}</h5>
              <div class="d-flex flex-wrap gap-3">
                <div
                  v-for="eq in equipments"
                  :key="eq.equip_id"
                  class="equip-item"
                  :class="{ selected: selectedEquipments.includes(eq.equip_id) }"
                  @click="toggleEquipment(eq.equip_id)"
                  title="點擊選擇設備"
                >
                  <!-- Font Awesome icon -->
                  <i
                    v-if="isFontAwesome(eq.equip_icon)"
                    :class="[eq.equip_icon, 'equip-icon']"
                    aria-hidden="true"
                  ></i>

                  <!-- Vuetify (mdi) icon -->
                  <v-icon
                    v-else
                    class="equip-icon"
                    size="25"
                  >
                    {{ eq.equip_icon }}
                  </v-icon>

                  <div class="equip-name">{{ eq.equip_name }}</div>
                  <input
                    type="checkbox"
                    :value="eq.equip_id"
                    v-model="selectedEquipments"
                    class="d-none"
                  />
                </div>
              </div>
            </div>
          </div>
<br>
          <div class="text-center">
            <button type="submit" class="btn btn-orange btn-lg px-5">更新房源</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import Swal from "sweetalert2"
import _ from 'lodash'

const route = useRoute()
const router = useRouter()

const listing = ref({})
const originalListing = ref({})
const equipmentList = ref([])
const selectedEquipments = ref([])
const originalEquipments = ref([])
const photos = ref([])
const currentPhotos = ref([])

// 設備分組
const groupedEquipments = computed(() => {
  return equipmentList.value.reduce((groups, eq) => {
    if (!groups[eq.equip_category]) groups[eq.equip_category] = []
    groups[eq.equip_category].push(eq)
    return groups
  }, {})
})

// 判斷是否為 Font Awesome
const isFontAwesome = (icon) => {
  return icon.startsWith('fa') || icon.includes('fa-')
}

// 切換設備勾選
const toggleEquipment = (equipId) => {
  const index = selectedEquipments.value.indexOf(equipId)
  if (index === -1) {
    selectedEquipments.value.push(equipId)
  } else {
    selectedEquipments.value.splice(index, 1)
  }
}

const fetchListing = async () => {
  const { id } = route.params
  const res = await axios.get(`http://localhost:8080/listings/${id}`)
  listing.value = res.data
  originalListing.value = _.cloneDeep(res.data)

  // 現有圖片預覽
  currentPhotos.value = []
  for (let i = 1; i <= 10; i++) {
    const p = res.data[`photo${i}`]
    if (p) {
      currentPhotos.value.push(`http://localhost:8080/images/listings/${p}`)
    }
  }

  // 現有設備 ID 預設勾選
  selectedEquipments.value = res.data.equipments?.map(e => e.equip_id) || []
  originalEquipments.value = [...selectedEquipments.value]
}

const fetchEquipments = async () => {
  const res = await axios.get('http://localhost:8080/api/equipment/all')
  equipmentList.value = res.data
}

const handlePhotos = (e) => {
  photos.value = Array.from(e.target.files)
}

const updateListing = async () => {
  // 判斷是否有更動
  const changed = !_.isEqual(_.omit(listing.value, ['equipments']), _.omit(originalListing.value, ['equipments']))
  const equipmentsChanged = !_.isEqual([...selectedEquipments.value].sort(), [...originalEquipments.value].sort())

  // if (!changed && !equipmentsChanged && photos.value.length === 0) {
  //   Swal.fire("提醒", "請修改內容後再提交", "info")
  //   return
  // }


    // 檢查是否修改了地址 & 原本狀態是已審核
  const addressChanged = listing.value.ads !== originalListing.value.ads
  const isApproved = originalListing.value.approved === true

  if (addressChanged && isApproved) {
    const result = await Swal.fire({
      title: "提醒",
      text: "更改地址需要重新審核房源，確定要送出嗎？",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "確定送出",
      cancelButtonText: "取消"
    })
    if (!result.isConfirmed) {
      return // 使用者取消送出
    }
  }

  const formData = new FormData()
  formData.append('houseName', listing.value.houseName)
  formData.append('ads', listing.value.ads)
  formData.append('room', listing.value.room)
  formData.append('bed', listing.value.bed)
  formData.append('describe', listing.value.describe)
  formData.append('tel', listing.value.tel)
  formData.append('ppl', listing.value.ppl)
  formData.append('price', listing.value.price)
  formData.append('approved', '') // 設為待審核

  selectedEquipments.value.forEach(id => {
    formData.append('equipments', id)
  })

  if (photos.value.length > 0) {
    photos.value.forEach(file => formData.append('photos', file))
  }

  try {
    await axios.put(`http://localhost:8080/listings/${listing.value.listId}/update`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    Swal.fire("成功", "編輯成功", "success")
    router.push('/host/listing')
  } catch (err) {
    Swal.fire("錯誤", "編輯失敗，請檢查輸入", "error")
  }
}

onMounted(() => {
  fetchListing()
  fetchEquipments()
})
</script>

<style scoped>
@import "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css";
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css";
@import '/src/assets/listing/list3.css';

/* 設備分類標題 */
.equip-category-title {
  font-weight: 600;
  margin-bottom: 0.75rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.25rem;
}

/* 設備項目容器 */
.equip-item {
  width: 80px;
  cursor: pointer;
  text-align: center;
  user-select: none;
  border-radius: 8px;
  padding: 6px;
  transition: background-color 0.3s, border 0.3s;
  border: 2px solid transparent;
}
.equip-item:hover {
  background-color: #f0f0f0;
}
.equip-item.selected {
  background-color: #959595;
  color: #fff;
}

/* 設備圖示 */
.equip-icon {
  font-size: 20px;  
  margin-bottom: 10px;
  color: inherit;
}

/* 設備名稱 */
.equip-name {
  font-size: 0.85rem;
  line-height: 1.2;
  white-space: normal;
  user-select: none;
}
</style>
