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
          <div class="label">{{ listing.ppl }}人 · {{ listing.bed.replace(/張\s+/g, '張') }}</div>
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
              @click="openModal"
            >
              顯示更多內容
            </button>
          </div>
          <br />
          <hr />

          <!-- 設備列表 -->
          <div class="amenities" v-if="listing.equipments?.length">
            <h4>有提供的設備與服務</h4>
            <div class="amenity-list">
              <div
                v-for="equip in limitedEquipments"
                :key="equip.equip_id"
                class="amenity-item"
              >
                <!-- 判斷 Font Awesome / Vuetify -->
                <i
                  v-if="isFontAwesome(equip.equip_icon)"
                  :class="[equip.equip_icon, 'amenity-icon']"
                ></i>
                <v-icon
                  v-else
                  class="amenity-icon"
                  size="25"
                >
                  {{ equip.equip_icon }}
                </v-icon>

                <span>{{ equip.equip_name }}</span>
              </div>
            </div>
            <button
              v-if="listing.equipments.length > limitCount"
              class="show-more-btn"
              @click="openAmenitiesModal"
            >
              查看更多
            </button>
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

    <!-- 房源介紹彈窗 -->
    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h4>房源介紹</h4>
        <p class="description-text">{{ listing.describe }}</p>
        <button class="close-btn" @click="closeModal">關閉</button>
      </div>
    </div>

    <!-- 設備彈窗 -->
    <div v-if="isAmenitiesModalOpen" class="modal-overlay" @click.self="closeAmenitiesModal">
      <div class="modal-content large">
        <h3>有提供的設備與服務</h3>
        <br>
        <div
          v-for="(items, category, index) in groupedEquipments"
          :key="category"
          class="amenity-category"
        >
          <h5>{{ category }}</h5>
          <div class="amenity-list">
            <div
              v-for="equip in items"
              :key="equip.equip_id"
              class="amenity-item"
            >
              <i
                v-if="isFontAwesome(equip.equip_icon)"
                :class="[equip.equip_icon, 'amenity-icon']"
              ></i>
              <v-icon
                v-else
                class="amenity-icon"
                size="28"
              >
                {{ equip.equip_icon }}
              </v-icon>

              <span>{{ equip.equip_name }}</span>
            </div>
          </div>
          <hr v-if="index < Object.keys(groupedEquipments).length - 1" />
        </div>
        <button class="close-btn" @click="closeAmenitiesModal">關閉</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import Navbar from '../components/Navbar.vue'
import axios from '../api/axios'
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

const isAmenitiesModalOpen = ref(false)
const limitCount = 6

const baseImageUrl = 'http://localhost:8080/images/listings/'

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
  router.push(`/edit/${listId}`)
}

function openModal() {
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

function openAmenitiesModal() {
  isAmenitiesModalOpen.value = true
}

function closeAmenitiesModal() {
  isAmenitiesModalOpen.value = false
}

const truncatedText = computed(() => {
  if (!listing.value?.describe) return ''
  return listing.value.describe.length > 100
    ? listing.value.describe.substring(0, 100) + '...'
    : listing.value.describe
})

const limitedEquipments = computed(() => {
  return listing.value?.equipments?.slice(0, limitCount) || []
})

const groupedEquipments = computed(() => {
  if (!listing.value?.equipments) return {}
  return listing.value.equipments.reduce((acc, equip) => {
    const category = equip.equip_category || '其他'
    if (!acc[category]) acc[category] = []
    acc[category].push(equip)
    return acc
  }, {})
})

// 判斷是不是 Font Awesome icon
function isFontAwesome(icon) {
  return icon?.startsWith('fa') || icon?.includes('fa-')
}

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await axios.get(`/listings/${id}`)
    listing.value = res.data

    photos.value = []
    for (let i = 1; i <= 10; i++) {
      const key = `photo${i}`
      if (listing.value[key]) {
        photos.value.push(`${baseImageUrl}${listing.value[key]}`)
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
@import "../assets/list1.css";

/* 設備樣式 */
.amenity-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.amenity-item {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 140px;
}

.amenity-icon {
  font-size: 20px;
  color: #555;
}

.amenity-category {
  margin-bottom: 24px;
}

hr {
  border: none;
  border-top: 1px solid #696767;
  margin: 16px 0;
}

.modal-content.large {
  width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}
</style>
