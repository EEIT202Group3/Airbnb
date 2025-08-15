<template>
  <div>
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

          <!-- 星星評價區 -->
          <div class="reviews">
            <div class="review-box">
              <div class="review-score">{{ listing.reviewCount.toFixed(2) }}</div>
              <div class="stars">
                <span v-for="(star, index) in starIcons" :key="index" class="star-svg">
                  <template v-if="star === 'full'">
                    <svg viewBox="0 0 24 24" class="star-icon full">
                      <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                    </svg>
                  </template>
                  <template v-else-if="star === 'half'">
                    <svg viewBox="0 0 24 24" class="star-icon half">
                      <defs>
                        <linearGradient id="halfGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                          <stop offset="50%" stop-color="gold" />
                          <stop offset="50%" stop-color="#ddd" />
                        </linearGradient>
                      </defs>
                      <path fill="url(#halfGradient)" d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                    </svg>
                  </template>
                  <template v-else>
                    <svg viewBox="0 0 24 24" class="star-icon empty">
                      <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                    </svg>
                  </template>
                </span>
              </div>
            </div>
          </div>

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
            <br />
            <button
              v-if="listing.equipments.length > limitCount"
              class="show-more-btn"
              @click="openAmenitiesModal"
            >
              顯示全部設備與服務
            </button>
          </div>
        </div>

        <!-- 右邊價格和預定 -->
        <div class="info-right booking-card">
          <div class="total-price">
            <span class="price">${{ totalPrice.toLocaleString() }}</span> TWD
            <span class="nights">{{ nights }} 晚</span>
          </div>

         <div class="date-section">
        <label>入住 / 退房</label>
          <Datepicker 
          v-model="dateRange"
          :range="true"
          :multi-calendars="true"
          :hide-input-icon="true"
          :format="'MM-dd'"
          placeholder="選擇入住與退房日期"
          class="airbnb-datepicker"
        
          />
          </div>

          <div class="guest-section">
            <label>旅客</label>
            <select v-model="guests">
              <option v-for="n in listing.ppl" :key="n" :value="n">{{ n }} 人</option>
            </select>
          </div>

          <button class="reserve-btn">預訂</button>
        </div>
      </div> <!-- info-flex end -->

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
        <br />
        <div
          v-for="(items, category, index) in groupedEquipments"
          :key="category"
          class="amenity-category"
        >
          <h5>{{ category }}</h5>
          <br />
          <div class="amenity-orge">
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
import axios from '@/api';
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import { ref, onMounted,watch, computed } from 'vue'
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

// 日期 & 旅客數
const checkIn = ref('')
const checkOut = ref('')
const guests = ref(1)
// 日期範圍
const dateRange = ref([new Date(), new Date(new Date().getTime() + 86400000)]) // 預設今天 + 明天


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

const reviewCount = computed(() => {
  return listing.value?.reviewCount ?? 0
})

const starIcons = computed(() => {
  const stars = []
  const avg = reviewCount.value

  for (let i = 1; i <= 5; i++) {
    if (avg >= i) {
      stars.push('full')
    } else if (avg >= i - 0.5) {
      stars.push('half')
    } else {
      stars.push('empty')
    }
  }

  return stars
})

// 計算晚數
const nights = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0
  const start = new Date(checkIn.value)
  const end = new Date(checkOut.value)
  const diffTime = end - start
  return diffTime > 0 ? diffTime / (1000 * 60 * 60 * 24) : 0
})

// 總價計算
const totalPrice = computed(() => {
  if (!listing.value) return 0
  return listing.value.price * nights.value
})

// 預設日期：今天 + 明天
onMounted(() => {
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(today.getDate() + 1)
  checkIn.value = today.toISOString().split('T')[0]
  checkOut.value = tomorrow.toISOString().split('T')[0]
})
// 監聽 dateRange 變動，自動更新 checkIn / checkOut
watch(dateRange, (newVal) => {
  if (newVal && newVal.length === 2) {
    checkIn.value = newVal[0]?.toISOString().split('T')[0] || ''
    checkOut.value = newVal[1]?.toISOString().split('T')[0] || ''
  }
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

     // 測試用分數
    listing.value.reviewCount = 4.5
  

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

<style>
@import "@/assets/listing/list1.css";
@import "@/assets/listing/reviews.css";
@import "@/assets/listing/equipment.css";
@import "@/assets/listing/datepicker.css";

/* 右側預訂卡片 */
.booking-card {
  background: white;
  border-radius: 16px;
  padding: 28px 24px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 18px; /* 區塊間距 */
}

/* 總價與晚數 */
.total-price {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.total-price .price {
  font-size: 32px;
  color: #222;
}

.nights {
  font-size: 14px;
  color: #717171;
}

/* 日期與旅客欄 */
.date-section,
.guest-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.date-section label,
.guest-section label {
  font-size: 14px;
  font-weight: 600;
  color: #222;
}

/* 輸入框通用樣式 */
.airbnb-datepicker input,
.guest-section select {
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: white;
  font-size: 16px;
  color: #222;
  transition: all 0.2s ease;
}

.airbnb-datepicker input:hover,
.guest-section select:hover {
  border-color: #aaa;
}

.airbnb-datepicker input:focus,
.guest-section select:focus {
  border-color: #e8711d;
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 56, 92, 0.2);
}

/* 預訂按鈕美化 */
.reserve-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 30px;
  background: #f07b2d;
  color: white;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s ease, transform 0.1s ease;
}

.reserve-btn:hover {
  background: #fc6d0e;
  transform: translateY(-1px);
}

.reserve-btn:active {
  transform: translateY(0);
}


</style>
