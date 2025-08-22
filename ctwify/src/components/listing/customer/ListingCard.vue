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
            <input
              v-model="location"
              placeholder="輸入地點"
              class="search-input"
            />
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
            <h6>位於{{ getCityName(listing.ads) }}的一間{{ listing.room }}</h6>
            <p>
              <i class="fa-solid fa-star" style="color: #878787"></i>
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

  <router-link to="/reviews/insert" custom v-slot="{ navigate }">
    <v-btn @click="navigate" icon>
      <v-icon>mdi-car</v-icon>
    </v-btn>
  </router-link>
</template>

<script>
import axios from "@/api2";
import DatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";

export default {
  components: {
    DatePicker,
  },
  name: "ListingList",
  data() {
    return {
      categories: [
        { name: "台北市熱門房源", api: "/listings/city/台北市/top-rated" },
        { name: "台南市熱門房源", api: "/listings/city/台南市/top-rated" },
        { name: "桃園市熱門房源", api: "/listings/city/桃園市/top-rated" },
        { name: "台中市熱門房源", api: "/listings/city/台中市/top-rated" },
        { name: "高雄市熱門房源", api: "/listings/city/高雄市/top-rated" },

        // 想擴充就加
      ],
      listingsByCategory: {},
      listings: [],
      location: "",
      dateRange: [],
      guestCount: "",
    };
  },
  mounted() {
    this.loadListings();
    this.loadAllCategories();
  },
  methods: {
    async loadAllCategories() {
      for (const category of this.categories) {
        try {
          const res = await fetch(`http://localhost:8080${category.api}`);
          if (!res.ok) throw new Error("Network response was not ok");
          const data = await res.json();
          this.listingsByCategory[category.name] = data;
        } catch (error) {
          console.error(`載入分類失敗: ${category.name}`, error);
          this.$set(this.listingsByCategory, category.name, []);
        }
      }
    },
    getCityName(address) {
      if (!address) return "國外地區";

      const cleanedAddress = address.replace(/台灣/, "");
      const match = cleanedAddress.match(/([\u4e00-\u9fa5]{2,3}[縣市])/);

      return match ? match[1] : "國外地區";
    },
    loadListings() {
      axios.get("http://localhost:8080/listings/simple").then((res) => {
        this.listings = res.data;
      });
    },
    scrollLeft() {
      this.$refs.scrollContainer.scrollBy({ left: -740, behavior: "smooth" });
    },
    scrollRight() {
      this.$refs.scrollContainer.scrollBy({ left: 740, behavior: "smooth" });
    },
    onDateChange(value) {
      this.dateRange = value;
      console.log("已選擇日期:", this.dateRange);
    },
    formatDate([start, end]) {
      if (!start || !end) return "";
      const format = (date) => `${date.getMonth() + 1}月${date.getDate()}日`;
      return `${format(start)} - ${format(end)}`;
    },
    //搜尋條件
    submitSearch() {
      const params = {
        location: this.location || null,
        guest: this.guestCount ? parseInt(this.guestCount) : null,
        checkIn: this.dateRange[0]
          ? this.dateRange[0].toISOString().split("T")[0]
          : null,
        checkOut: this.dateRange[1]
          ? this.dateRange[1].toISOString().split("T")[0]
          : null,
      };

      // 跳轉路由並帶 query
      this.$router.push({ path: "/search", query: params });

      // 同步呼叫搜尋 API
      axios
        .get("http://localhost:8080/listings/search", { params })
        .then((res) => {
          this.listings = res.data;
        })
        .catch((err) => {
          console.error("搜尋錯誤:", err);
        });
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
  background-color: rgba(184, 184, 184, 0.5); /* 灰色半透明 */
  background: url("/src/icon/sone.png") center/cover no-repeat; /* 設背景圖 */
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

.listing-card {
  text-decoration: none; /* 移除底線 */
  color: inherit; /* 保持原本的文字顏色 */
  display: block; /* 讓整個卡片可以點擊 */
}
</style>
