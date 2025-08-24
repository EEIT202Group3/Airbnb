<template>
  <div class="container1">
    <!-- 搜尋欄 -->
    <br />
    <div class="background">
      <div class="search-bar">
        <!-- 地點 -->
        <div class="search-section">
          <i class="fa fa-search"></i>
          <div class="search-text">
            <div class="search-label">地點</div>
            <div class="input-wrapper">
              <input
                type="text"
                v-model="locationInput"
                placeholder="輸入縣市"
                class="search-input"
                @focus="dropdownOpen = true"
                @input="dropdownOpen = true"
                @blur="handleBlur"
              />
              <ul v-show="dropdownOpen && filteredCities.length" class="dropdown-list">
                <li
                  v-for="city in filteredCities"
                  :key="city"
                  @mousedown.prevent="selectCity(city)"
                >
                  {{ city }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 日期 -->
        <div class="search-section">
          <i class="fa fa-calendar-alt"></i>
          <div class="search-text">
            <div class="search-label1">入住日期 / 退房日期</div>
            <div class="date-section">
              <DatePicker
                v-model="dateRange"
                :range="true"
                :multi-calendars="true"
                :hide-input="true"
                :hide-input-icon="true"
                :teleport="true"
                :format="'MM-dd'"
                placeholder="選擇入住與退房日期"
                class="card-datepicker"
                @change="onDateChange"
              />
            </div>
          </div>
        </div>

        <!-- 人數 -->
        <div class="search-section">
          <i class="fa fa-user-friends"></i>
          <div class="search-text">
            <div class="search-label">旅客人數</div>
            <input
              v-model="guestCount"
              type="number"
              min="1"
              placeholder="如：2 位旅客"
              class="search-input"
            />
          </div>
        </div>

        <!-- 搜尋按鈕 -->
        <div class="search-button-wrapper">
          <button class="search-button" @click="submitSearch">搜尋</button>
        </div>
      </div>
    </div>
  </div>

  <!-- 房源卡片分類-->
  <div
    v-for="(category, index) in categories"
    :key="category.name"
    class="listing-wrapper"
  >
    <br />
    <h4>{{ category.name }}</h4>

    <!--左箭頭 -->
    <button class="arrow-icon left" @click="scrollLeft(index)">
      <i class="fa-solid fa-chevron-left"></i>
    </button>

    <!-- 卡片滑動區 -->
    <div class="listing-container">
      <div class="listing-scroll" :ref="'scrollContainer' + index">
        <router-link
          v-for="listing in listingsByCategory[category.name]?.slice(0, 10)"
          :key="listing.listId"
          :to="`/coustomerlistings/${listing.listId}`"
          class="listing-card"
        >
          <img
            :src="`http://localhost:8080/images/listings/${listing.photo1}`"
            class="listing-img"
            alt="房源圖片"
          />
          <div class="listing-info">
            <h6>
              位於{{ getCityName(listing.ads) }}的一間{{ listing.room }}
            </h6>
            <p>
              <i class="fa-solid fa-star" style="color: #878787;"></i>
              {{ listing.reviewCount.toFixed(1) }}
            </p>
            <p class="price">NT${{ listing.price }}</p>
          </div>
        </router-link>
      </div>
    </div>

    <!--右箭頭 -->
    <button class="arrow-icon right" @click="scrollRight(index)">
      <i class="fa-solid fa-chevron-right"></i>
    </button>
  </div>
</template>

<script>
import axios from '@/api2';
import DatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

export default {
  components: { DatePicker },
  name: 'ListingList',
  data() {
    return {
      categories: [
        { name: '台北市熱門房源', api: '/listings/city/台北市/top-rated' },
        { name: '台南市熱門房源', api: '/listings/city/台南市/top-rated' },
        { name: '桃園市熱門房源', api: '/listings/city/桃園市/top-rated' },
        { name: '台中市熱門房源', api: '/listings/city/台中市/top-rated' },
        { name: '高雄市熱門房源', api: '/listings/city/高雄市/top-rated' },
      ],
      listingsByCategory: {},
      listings: [],
      locationInput: '',
      location: '',
      dropdownOpen: false,
      dateRange: [],
      guestCount: '',
      taiwanCities: [
        '台北市','新北市','桃園市','台中市','台南市','高雄市',
        '基隆市','新竹市','新竹縣','苗栗縣','彰化縣','南投縣','雲林縣',
        '嘉義市','嘉義縣','屏東縣','宜蘭縣','花蓮縣','台東縣','澎湖縣','金門縣','連江縣'
      ],
    };
  },
  computed: {
    filteredCities() {
      return this.taiwanCities.filter(city =>
        city.includes(this.locationInput)
      );
    }
  },
  mounted() {
    this.loadListings();
    this.loadAllCategories();
  },
  methods: {
    selectCity(city) {
      this.locationInput = city;
      this.location = city;
      this.dropdownOpen = false;
    },
    handleBlur() {
      setTimeout(() => { this.dropdownOpen = false; }, 100);
    },
    async loadAllCategories() {
      for (const category of this.categories) {
        try {
          const res = await fetch(`http://localhost:8080${category.api}`);
          if (!res.ok) throw new Error('Network response was not ok');
          const data = await res.json();
          this.listingsByCategory[category.name] = data;
        } catch (error) {
          console.error(`載入分類失敗: ${category.name}`, error);
          this.$set(this.listingsByCategory, category.name, []);
        }
      }
    },
    getCityName(address) {
      if (!address) return '國外地區';
      const cleanedAddress = address.replace(/台灣/, '');
      const match = cleanedAddress.match(/([\u4e00-\u9fa5]{2,3}[縣市])/);
      return match ? match[1] : '國外地區';
    },
    loadListings() {
      axios.get('http://localhost:8080/listings/simple')
        .then(res => { this.listings = res.data; });
    },
    scrollLeft(index) {
      const container = this.$refs['scrollContainer' + index];
      if (container) {
        const scrollEl = Array.isArray(container) ? container[0] : container;
        scrollEl.scrollBy({ left: -740, behavior: 'smooth' });
      }
    },
    scrollRight(index) {
      const container = this.$refs['scrollContainer' + index];
      if (container) {
        const scrollEl = Array.isArray(container) ? container[0] : container;
        scrollEl.scrollBy({ left: 740, behavior: 'smooth' });
      }
    },
    onDateChange(value) {
      this.dateRange = value;
    },
    submitSearch() {
      this.location = this.locationInput;
      const params = {
        location: this.location || null,
        guest: this.guestCount ? parseInt(this.guestCount) : null,
        checkIn: this.dateRange[0] ? this.dateRange[0].toISOString().split('T')[0] : null,
        checkOut: this.dateRange[1] ? this.dateRange[1].toISOString().split('T')[0] : null,
      };
      this.$router.push({ path: '/search', query: params });

      axios.get('http://localhost:8080/listings/search', { params })
        .then(res => { this.listings = res.data; })
        .catch(err => { console.error('搜尋錯誤:', err); });
    },
  },
};
</script>

<style>
@import "/src/assets/listing/list1.css";
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css";
@import "/src/assets/listing/listingCard.css";
@import "/src/assets/listing/searchBar.css";
@import "@/assets/listing/datepicker.css";

.background {
  width: 100%;
  height: 300px;
  box-sizing: border-box;
  background-color: rgba(184, 184, 184, 0.5);
  background: url('/src/icon/sone.png') center/cover no-repeat;
  display: flex;
  border-radius: 15px;
  justify-content: center;
  align-items: center;
  font-size: 20px;
}
.sone {
  width: 100%;
  height: 200px;
  box-sizing: border-box;
  display: flex;
  border-radius: 10px;
  justify-content: center;
  align-items: center;
  font-size: 20px;
}
.card-datepicker .dp__input {
  border: none;
  background: transparent !important;
  box-shadow: none !important;
  padding-left: 0 !important;
  font-size: 16px;
  cursor: pointer;
}
.listing-card {
  text-decoration: none;
  color: inherit;
  display: block;
}

/* dropdown 自訂縣市 */
.input-wrapper {
  position: relative;
  width:280px;
}
.dropdown-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: 20;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  max-height: 280px;
  overflow-y: auto;
  padding: 6px 0;
  margin: 6px 0 0 0;
  list-style: none;
  font-size: 14px;
  animation: fadeIn 0.15s ease-in-out;
}
.dropdown-list li {
  padding: 10px 14px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  border-radius: 8px;
    font-size: 16px;  
  line-height: 1.5; 
}
.dropdown-list li:hover {
  background-color: #f5f5f5;
  color: #333;
}
/* 簡單淡入動畫 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}

</style>
