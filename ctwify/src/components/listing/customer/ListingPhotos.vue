<template>
  <div class="photo-section-airbnb" v-if="photos && photos.length">
    <!-- 不同圖片數量排版 -->
    <div class="photo-grid" :class="'layout-' + layoutType">
      <!-- 顯示圖片 (前五張) -->
      <div
        v-for="(photo, index) in displayPhotos"
        :key="index"
        class="photo-item"
        @click="openModal(index)"
      >
        <img :src="photo" alt="房源圖片" />
        <!-- 超過 5 張時最後一張加遮罩 -->
        <div
          v-if="index === 4 && photos.length > 5"
          class="overlay-btn"
        >
          +{{ photos.length - 5 }} 張
        </div>
      </div>
    </div>

    <!-- 全圖 modal -->
    <div v-show="isModalOpen" class="ctwify-modal-overlay" @click.self="closeModal">
      <div class="ctwify-modal-content">
        <button class="ctwify-close-btn" @click="closeModal">✕</button>
        <div class="ctwify-modal-carousel">
          <button class="arrow left" @click="prevPhoto">‹</button>
          <img :src="photos[modalIndex]" class="ctwify-modal-main-photo" />
          <button class="arrow right" @click="nextPhoto">›</button>
        </div>
        <div class="ctwify-modal-thumbs">
          <img
            v-for="(photo, index) in photos"
            :key="index"
            :src="photo"
            :class="{ 'selected-thumb': modalIndex === index }"
            @click="modalIndex = index"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps } from "vue"

const props = defineProps({
  photos: { type: Array, default: () => [] }
})

const isModalOpen = ref(false)
const modalIndex = ref(0)

const layoutType = computed(() => {
  const len = props.photos.length
  if (len === 1) return 1
  if (len === 2) return 2
  if (len === 3) return 3
  if (len === 4) return 4
  if (len === 5) return 5
  return 6 // 超過 5 張
})

const displayPhotos = computed(() =>
  props.photos.length > 5 ? props.photos.slice(0, 5) : props.photos
)

function openModal(index) {
  modalIndex.value = index
  isModalOpen.value = true
}
function closeModal() {
  isModalOpen.value = false
}
function prevPhoto() {
  modalIndex.value = (modalIndex.value - 1 + props.photos.length) % props.photos.length
}
function nextPhoto() {
  modalIndex.value = (modalIndex.value + 1) % props.photos.length
}
</script>

<style scoped>
.photo-section-airbnb {
  width: 100%;
  height: 500px;
}

.photo-grid {
  display: grid;
  gap: 6px;
  height: 100%;
}

/* Layouts */
.layout-1 {
  grid-template-columns: 1fr;
}

.layout-2 {
  grid-template-columns: 1fr 1fr;
}

.layout-3 {
  grid-template-columns: 2fr 1fr;
  grid-template-rows: 1fr 1fr;
}
.layout-3 .photo-item:nth-child(1) {
  grid-row: span 2;
}

.layout-4 {
  grid-template-columns: 2fr 1fr;
  grid-template-rows: repeat(3, 1fr);
}
.layout-4 .photo-item:nth-child(1) {
  grid-row: span 3;
}

.layout-5 {
  grid-template-columns: 2fr 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
}
.layout-5 .photo-item:nth-child(1) {
  grid-row: span 2;
}

.layout-6 {
  grid-template-columns: 2fr 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
}
.layout-6 .photo-item:nth-child(1) {
  grid-row: span 2;
}

/* ----------- 修正圓角邏輯 ----------- */
.photo-item {
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0; /* 預設直角 */
}

/* Layout-1：單張圖片 → 全部圓角 */
.layout-1 .photo-item img {
  border-radius: 12px;
}

/* Layout-2：左右 */
.layout-2 .photo-item:first-child img { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.layout-2 .photo-item:last-child img { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }

/* Layout-3：左大右上下 */
.layout-3 .photo-item:nth-child(1) img { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.layout-3 .photo-item:nth-child(2) img { border-top-right-radius: 12px; }
.layout-3 .photo-item:nth-child(3) img { border-bottom-right-radius: 12px; }

/* Layout-4：左大右三格 */
.layout-4 .photo-item:nth-child(1) img { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.layout-4 .photo-item:nth-child(2) img { border-top-right-radius: 12px; }
.layout-4 .photo-item:nth-child(4) img { border-bottom-right-radius: 12px; }

/* Layout-5：左大右四格 */
.layout-5 .photo-item:nth-child(1) img { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.layout-5 .photo-item:nth-child(3) img { border-top-right-radius: 12px; }
.layout-5 .photo-item:nth-child(5) img { border-bottom-right-radius: 12px; }

/* Layout-6：左大右四格 (跟 5 類似) */
.layout-6 .photo-item:nth-child(1) img { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.layout-6 .photo-item:nth-child(3) img { border-top-right-radius: 12px; }
.layout-6 .photo-item:nth-child(5) img { border-bottom-right-radius: 12px; }

/* Overlay 數字按鈕 */
.overlay-btn {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  color: white;
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: inherit; /* 跟圖片一致 */
  border-bottom-right-radius: 12px;
}

/* Modal */
.ctwify-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.ctwify-modal-content {
  position: relative;
  width: 90%;
  max-width: 1000px;
  background: #000;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ctwify-close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  font-size: 24px;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  cursor: pointer;
   z-index: 11000;
}

.ctwify-modal-carousel {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.ctwify-modal-main-photo {
  max-height: 70vh;
  object-fit: contain;
  border-radius: 12px;
}

.arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  font-size: 36px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
}
.arrow.left { left: -60px; }
.arrow.right { right: -60px; }

.ctwify-modal-thumbs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.ctwify-modal-thumbs img {
  height: 60px;
  border-radius: 8px;
  cursor: pointer;
    outline: none !important;
  border: none !important;
}
</style>
