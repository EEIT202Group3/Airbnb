<template>
  <br>
  <div class="container1">
<div class="background">
    <div class="search-bar">
      <!-- 地點 -->
      <div class="search-section">
        <i class="fa fa-search"></i>
        <div class="search-text">
          <div class="search-label">地點</div>
          <input v-model="location" placeholder="輸入地點" class="search-input" />
        </div>
      </div>

      <!-- 日期選擇器 -->
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

      <!-- 旅客人數 -->
      <div class="search-section">
        <i class="fa fa-user-friends"></i>
        <div class="search-text">
          <div class="search-label">旅客人數</div>
          <input
            v-model="guestInfo"
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


  <div class="listing-wrapper">
    <br>
    <h4>熱門房源</h4>
    <!-- 左箭頭 -->
    <button class="arrow-icon left" @click="scrollLeft">
      <i class="fa-solid fa-chevron-left"></i>
    </button>

    <div class="listing-container">
      <div class="listing-scroll" ref="scrollContainer">
        <div
          v-for="listing in listings.slice(0, 10)"
          :key="listing.listId"
          class="listing-card"
        >
          <img
            :src="`http://localhost:8080/images/listings/${listing.photo1}`"
            class="listing-img"
            alt="房源圖片"
          />
          <div class="listing-info">
           <h6>位於{{ getCityName(listing.ads) }}的一間{{ listing.room }}</h6>
            <p>
              <i class="fa-solid fa-star" style="color: #878787;"></i>
              {{ listing.reviewCount }}
            </p>
            <p class="price">NT${{ listing.price }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右箭頭 -->
    <button class="arrow-icon right" @click="scrollRight">
      <i class="fa-solid fa-chevron-right"></i>
    </button>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from '@/layouts/Navbar.vue';
import DatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

export default {
  components: {
    Navbar,
    DatePicker
  },
  name: 'ListingList',
  data() {
    return {
      listings: [],
      location: '',
      dateRange: [],
      guestInfo: ''
    };
  },
  mounted() {
    this.loadListings();
  },
methods: {
  getCityName(address) {
    if (!address) return '某地區'
    const match = address.match(/(.{2,3}[縣市])/)
    return match ? match[0] : '某地區'
  },
    loadListings() {
      axios.get('http://localhost:8080/listings/simple')
        .then(res => {
          this.listings = res.data;
        });
    },
    scrollLeft() {
      this.$refs.scrollContainer.scrollBy({ left: -740, behavior: 'smooth' });
    },
    scrollRight() {
      this.$refs.scrollContainer.scrollBy({ left: 740, behavior: 'smooth' });
    },
    onDateChange(value) {
      this.dateRange = value;
      console.log("已選擇日期:", this.dateRange);
    },
    formatDate([start, end]) {
      if (!start || !end) return '';
      const format = date => `${date.getMonth() + 1}月${date.getDate()}日`;
      return `${format(start)} - ${format(end)}`;
    },
    
    submitSearch() {
      axios.get('http://localhost:8080/listings/search', {
        params: {
          location: this.location,
          checkIn: this.dateRange[0] || '',
          checkOut: this.dateRange[1] || '',
          guestInfo: this.guestInfo
        }
      }).then(res => {
        this.listings = res.data;
      }).catch(err => {
        console.error('搜尋錯誤:', err);
      });
    }
  }
};
</script>

<style>
@import "/src/assets/listing/list1.css";
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css";
@import "/src/assets/listing/listingCard.css";
@import "/src/assets/listing/searchBar.css";
@import "@/assets/listing/datepicker.css";


.background{
  width: 100%;         
  height: 300px;      
  box-sizing: border-box;
  background-color: rgba(184, 184, 184, 0.5); /* 灰色半透明 */
  background: url('/src/icon/sone.png') center/cover no-repeat; /* 設背景圖 */
  /* border: 1px solid rgb(136, 136, 135); */
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
</style>
