<template>
  <div>
    <!-- Navbar -->
    <Navbar />

    <div class="come" v-if="listing">
      <h3>{{ listing.houseName }}</h3>

      <!-- 照片區 -->
      <div class="photo-section">
        <img
          id="mainPhoto"
          class="main-photo"
          :src="mainPhoto"
          alt="主圖片"
          v-if="mainPhoto"
        />
        <div class="thumbs" v-if="photos.length > 0">
          <img
            v-for="(photo, index) in photos"
            :key="index"
            :src="photo"
            @click="switchMainPhoto(photo)"
            class="thumb-img"
            :class="{ 'selected-thumb': selectedPhoto === photo }"
            alt="縮圖"
          />
        </div>
      </div>

      <!-- 資訊區 -->
      <div class="info-flex">
        <div class="info-left">
          <h4>位於{{ cityName }}的一間{{ listing.room }}</h4>
          <div class="label">{{ listing.ppl }}人 · {{ listing.bed?.replace(/張\s+/g, '張') || '' }}</div>
          <div class="label"><span>聯絡電話：</span>{{ listing.tel }}</div>
          <br />
          <hr />

          <!-- 房源介紹 -->
          <div class="description">
            <h4>房源介紹</h4>
            <p class="description-text">
              {{ showFullDescription ? listing.describe : truncatedText }}
            </p>
            <button 
              v-if="listing.describe && listing.describe.length > 100 && !showFullDescription"
              class="show-more-btn" 
              @click="openModal">
              顯示更多內容
            </button>
          </div>
          <br />
          <hr />

          <!-- 設備列表 -->
          <div
            class="amenities"
            v-if="Array.isArray(listing.equipments) && listing.equipments.length > 0"
          >
            <h4>提供設備</h4>
            <div class="amenity-list">
              <div
                class="amenity-item"
                v-for="equip in listing.equipments"
                :key="equip.equip_id"
              >
                {{ equip.equip_name }}
              </div>
            </div>
          </div>
        </div>

        <!-- 右邊價格和預定 -->
        <div class="info-right">
          <div class="price1">NT$ {{ listing.price }}</div>
          <div>每晚價格</div>
          <div style="margin: 12px 0;">人數：{{ listing.ppl }} 人</div>
          <button class="book-btn" @click="editHouse(listing.listId)">編輯</button>
        </div>
      </div>
    </div>

    <div v-else>
      <p>資料載入中...</p>
    </div>

    <!-- 彈窗 -->
    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h4>房源介紹</h4>
        <p class="description-text">{{ listing.describe }}</p>
        <button class="close-btn" @click="closeModal">關閉</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import api from '@/api'
import Navbar from '@/layouts/Navbar.vue'
import axios from 'axios'
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const listing = ref(null)
const mainPhoto = ref('')
const selectedPhoto = ref('')
const photos = ref([])

const isModalOpen = ref(false)
const showFullDescription = ref(false)

const baseImageUrl = 'http://localhost:8080/images/'

function switchMainPhoto(photo) {
  mainPhoto.value = photo
  selectedPhoto.value = photo
}

const cityName = computed(() => {
  if (!listing.value) return ''
  const address = listing.value.ads || ''
  const match = address.match(/(.{2,3}[縣市])/)
  return match ? match[0] : '某地區'
})

function editHouse(listId) {
  router.push(`/listing/edit/${listId}`)
}

function openModal() {
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

const truncatedText = computed(() => {
  if (!listing.value || !listing.value.describe) return ''
  return listing.value.describe.length > 100
    ? listing.value.describe.substring(0, 100) + '...'
    : listing.value.describe
})

onMounted(async () => {
  const id = route.params.id
  console.log('URL 參數 id:', id) 

  try {
    const res = await axios.get(`http://localhost:8080/listings/${id}`)
    listing.value = res.data
    console.log(listing);
    
    photos.value = []
    for (let i = 1; i <= 10; i++) {
      const key = `photo${i}`
      if (listing.value[key]) {
        const fullPath = `${baseImageUrl}${listing.value[key]}`
        photos.value.push(fullPath)
      }
    }

    mainPhoto.value = photos.value.length > 0 ? photos.value[0] : ''
    selectedPhoto.value = mainPhoto.value
  } catch (error) {
    console.error('取得房源資料失敗', error)
  }
})
</script>

<style scoped>
@import '/src/assets/listing/list1.css';

</style>
