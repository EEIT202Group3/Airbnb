<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
axios.defaults.withCredentials = true // 若你用 cookie JWT

// ---- 假資料：若後端還沒接好會用這份 ----
const fallback = {
  id: 101,
  title: '日式和風套房｜近地鐵',
  address: '日本大阪',
  type: '獨立套房',
  bed: '雙人床',
  capacity: 2,
  phone: '0988777333',
  pricePerNight: 1000,
  images: [
    'https://picsum.photos/seed/room1/1200/600',
    'https://picsum.photos/seed/room2/800/600',
    'https://picsum.photos/seed/room3/800/600',
    'https://picsum.photos/seed/room4/800/600',
    'https://picsum.photos/seed/room5/800/600'
  ],
  intro: [
    '4層獨立公寓，每層兩套，完全獨立，衛浴與廚房專用。',
    '這是三樓的房間，沒有電梯，1樓入口有電子鎖。',
    '房間內有2張單人式被褥，適合2人入住。'
  ],
  amenities: ['冰箱','空調','浴缸','電視','餐桌','吹風機','淋浴設備','牙刷']
}

type Listing = typeof fallback

const listing = ref<Listing | null>(null)
const loading = ref(false)
const activeImg = ref(0)

// 表單欄位
const checkIn = ref<string | null>(null)
const checkOut = ref<string | null>(null)
const guests = ref(2)

const nights = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0
  const inD  = new Date(checkIn.value)
  const outD = new Date(checkOut.value)
  const ms = outD.getTime() - inD.getTime()
  return ms > 0 ? Math.ceil(ms / (1000*60*60*24)) : 0
})

const total = computed(() => {
  if (!listing.value) return 0
  return nights.value * listing.value.pricePerNight
})

function fmt(d?: string | null) {
  if (!d) return ''
  const x = new Date(d)
  return `${x.getFullYear()}/${x.getMonth()+1}/${x.getDate()}`
}

async function fetchListing(id: string | number) {
  loading.value = true
  try {
    // 如果你的後端已就緒，打開這段：
    // const { data } = await axios.get(`/api/listings/${id}`)
    // listing.value = data

    // 先用假資料跑版
    listing.value = { ...fallback, id: Number(id) }
  } catch (e) {
    // 失敗也用假資料
    listing.value = { ...fallback, id: Number(id) }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const id = route.params.id ?? 101
  fetchListing(id as string)
})

// 點擊預定
async function reserve() {
  if (!listing.value) return
  if (!checkIn.value || !checkOut.value || nights.value <= 0) {
    alert('請選擇有效的入住/退房日期')
    return
  }
  try {
    // 👉 串接你的後端「預覽/建立訂單」API
    // 建議先走「預覽」：由後端回傳計價、訂單編號再導頁到結帳頁
    // const { data } = await axios.post('/api/orders/preview', {
    //   listId: listing.value.id,
    //   checkInDate: checkIn.value,
    //   checkOutDate: checkOut.value,
    //   guests: guests.value
    // })

    // 假回應（示範用）
    const data = {
      bookingId: crypto.randomUUID(),
      totalAmount: total.value,
      listId: listing.value.id,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      guests: guests.value
    }

    // 導向你的「確認/付款」頁（之後你可在該頁呼叫藍新 gopay）
    router.push({ name: 'BookingStart', query: { bookingId: data.bookingId } })
  } catch (err:any) {
    console.error(err)
    alert('預定失敗，請稍後再試')
  }
}
</script>

<template>
  <v-container class="py-6" v-if="listing">
    <v-row>
      <!-- 圖片區 -->
      <v-col cols="12">
        <v-img
          :src="listing.images[activeImg]"
          aspect-ratio="2.1"
          class="rounded-lg mb-4"
          cover
        />
        <v-slide-group show-arrows>
          <v-slide-group-item
            v-for="(img, i) in listing.images"
            :key="img"
          >
            <v-img
              :src="img"
              width="140"
              height="90"
              class="rounded-md mr-3 cursor-pointer"
              cover
              @click="activeImg = i"
            />
          </v-slide-group-item>
        </v-slide-group>
      </v-col>
    </v-row>

    <v-row>
      <!-- 左：房源資訊 -->
      <v-col cols="12" md="8">
        <div class="text-h6 mb-1">{{ listing.title }}</div>
        <div class="text-body-2 text-medium-emphasis">
          地址：{{ listing.address }}
        </div>
        <div class="text-body-2 text-medium-emphasis">
          房型：{{ listing.type }}　床位：{{ listing.bed }}　入住人數：{{ listing.capacity }}
        </div>
        <div class="text-body-2 text-medium-emphasis mb-4">
          聯絡電話：{{ listing.phone }}
        </div>

        <v-divider class="my-4"></v-divider>

        <div class="text-subtitle-1 font-weight-medium mb-2">房源介紹</div>
        <ul class="pa-0 ml-4">
          <li v-for="t in listing.intro" :key="t" class="mb-1">{{ t }}</li>
        </ul>

        <v-divider class="my-4"></v-divider>

        <div class="text-subtitle-1 font-weight-medium mb-2">提供設備</div>
        <div class="d-flex flex-wrap ga-2">
          <v-chip v-for="a in listing.amenities" :key="a" variant="tonal" size="small">{{ a }}</v-chip>
        </div>
      </v-col>

      <!-- 右：價格與預定卡 -->
      <v-col cols="12" md="4">
        <v-card elevation="1" class="pa-4">
          <div class="d-flex align-center justify-space-between mb-2">
            <div class="text-h6">NT$ {{ listing.pricePerNight * (nights || 1) }}</div>
            <div class="text-body-2 text-medium-emphasis">
              {{ nights || 1 }} 晚
            </div>
          </div>

          <v-row class="mb-2">
            <v-col cols="12" md="6">
              <v-text-field
                v-model="checkIn"
                label="入住"
                type="date"
                density="comfortable"
                hide-details
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="checkOut"
                label="退房"
                type="date"
                density="comfortable"
                hide-details
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model.number="guests"
                label="人數"
                type="number"
                min="1"
                :max="listing.capacity"
                density="comfortable"
                hide-details
              />
            </v-col>
          </v-row>

          <v-divider class="my-2"></v-divider>

          <div class="d-flex justify-space-between mb-2">
            <span class="text-body-2">晚數</span>
            <span class="text-body-2">{{ nights }} 晚</span>
          </div>
          <div class="d-flex justify-space-between mb-4">
            <span class="text-subtitle-2">總金額</span>
            <span class="text-subtitle-2">NT$ {{ total }}</span>
          </div>

          <v-btn block color="orange-darken-2" size="large" @click="reserve">
            預定
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>

  <v-container v-else class="py-12">
    <v-skeleton-loader type="image, article, actions"></v-skeleton-loader>
  </v-container>
</template>

<style scoped>
.cursor-pointer { cursor: pointer; }
</style>