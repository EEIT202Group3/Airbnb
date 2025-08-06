<template>
  <div>
    <Navbar />
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

          <div class="mb-3">
            <label class="form-label">設備</label>
            <div class="row">
              <div v-for="eq in equipmentList" :key="eq.equip_id" class="col-6 col-md-4 mb-2">
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    :value="eq.equip_id"
                    v-model="selectedEquipments"
                    :id="'equip-' + eq.equip_id"
                  />
                  <label class="form-check-label" :for="'equip-' + eq.equip_id">{{ eq.equip_name }}</label>
                </div>
              </div>
            </div>
          </div>

          <div class="text-center">
            <button type="submit" class="btn btn-orange btn-lg px-5">更新房源</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '@/layouts/Navbar.vue'
import Swal from "sweetalert2";

const route = useRoute()
const router = useRouter()

const listing = ref({})
const equipmentList = ref([])
const selectedEquipments = ref([])
const photos = ref([])
const currentPhotos = ref([])

const fetchListing = async () => {
  const { id } = route.params
  const res = await axios.get(`http://localhost:8080/listings/${id}`)
  listing.value = res.data

  // 現有圖片預覽
  currentPhotos.value = []
  for (let i = 1; i <= 10; i++) {
    const p = res.data[`photo${i}`]
    if (p) {
      currentPhotos.value.push(`http://localhost:8080/images/${p}`)
    }
  }

  // 現有設備ID預設勾選
  selectedEquipments.value = res.data.equipments?.map(e => e.equip_id) || []
}
//顯示全部設備
const fetchEquipments = async () => {
  const res = await axios.get('http://localhost:8080/equipment/all')
  equipmentList.value = res.data
}

const handlePhotos = (e) => {
  photos.value = Array.from(e.target.files)
}

const updateListing = async () => {
  const formData = new FormData()

  // 基本資料
  formData.append('houseName', listing.value.houseName)
  formData.append('ads', listing.value.ads)
  formData.append('room', listing.value.room)
  formData.append('bed', listing.value.bed)
  formData.append('describe', listing.value.describe)
  formData.append('tel', listing.value.tel)
  formData.append('ppl', listing.value.ppl)
  formData.append('price', listing.value.price)

  // 傳送設備 id
  selectedEquipments.value.forEach(id => {
    formData.append('equipments', id)
  })

  // 照片有選才送
  if (photos.value.length > 0) {
    photos.value.forEach(file => formData.append('photos', file))
  }

  try {
    await axios.put(`http://localhost:8080/listings/${listing.value.listId}/update`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    Swal.fire("成功", "房源編輯成功", "success");
    router.push('/')
  } catch (err) {
    Swal.fire("錯誤", "新增失敗，請檢查輸入", "error");
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
</style>
