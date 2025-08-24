<template>
    <br>
  <div class="search-results-container">
    <!-- 左邊搜尋結果 -->
    <div class="results-list">
      <h3>搜尋結果</h3>
        <!-- 新增總數 -->
  <p class="result-count">共找到 {{ listings.length }} 筆房源</p>
      <router-link
        v-for="listing in paginatedListings"
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
          <h5>
            位於{{ getCityName(listing.ads) }}的一間{{ listing.room }}
          </h5>
            <p class="listing-houseName">{{ listing.houseName}}</p>

          <p>
            <i class="fa-solid fa-star" style="color: #878787;"></i>
            {{ listing.reviewCount.toFixed(1) }}
          </p>
          <p class="price">NT${{ listing.price }}</p>
        </div>
      </router-link>
      <!-- 分頁按鈕 -->
  <div class="pagination">
    <button 
      :disabled="currentPage === 1" 
      @click="currentPage--">
      &lt;
    </button>
    <button
      v-for="page in totalPages"
      :key="page"
      :class="{ active: page === currentPage }"
      @click="currentPage = page">
      {{ page }}
    </button>
    <button 
      :disabled="currentPage === totalPages" 
      @click="currentPage++">
      &gt;
    </button>
  </div>
    </div>

    <!-- 右邊地圖 -->
    <div id="map" class="results-map"></div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SearchResults',
  data() {
    return {
      listings: [],          // 所有房源
      currentPage: 1,        // 當前頁
      perPage: 8,            // 每頁顯示幾個房源
      map: null,
      markers: [],
      geocodeCache: {}
    };
  },
  async mounted() {
    console.log('搜尋條件:', JSON.stringify(this.$route.query));

    // 1️⃣ 先抓資料
    await this.fetchListingsFromQuery();

    // 2️⃣ 等 DOM 渲染後初始化地圖
    this.$nextTick(() => {
      this.waitForGoogleMaps()
        .then(() => {
          this.initMap();
          this.showMarkersOnMap();
        })
        .catch((err) => console.error('Google Maps API 尚未載入', err));
    });
  },
  methods: {
    // 抓房源
    async fetchListingsFromQuery() {
      const query = this.$route.query;
      const res = await axios.get('http://localhost:8080/listings/search', {
        params: query
      });
      this.listings = res.data;
    },

    // 等 Google Maps API 載入完成
    waitForGoogleMaps() {
      return new Promise((resolve, reject) => {
        const check = () => {
          if (window.google && window.google.maps) {
            resolve();
          } else {
            setTimeout(check, 100); // 每 100ms 檢查一次
          }
        };
        check();
      });
    },

    // 初始化地圖
    initMap() {
      if (!window.google || !window.google.maps) return;

      this.map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 25.0330, lng: 121.5654 }, // 預設台北
        zoom: 13
      });

      // 確保地圖載入後 resize
      google.maps.event.addListenerOnce(this.map, "idle", () => {
        google.maps.event.trigger(this.map, "resize");
      });
    },

    // 地址轉座標
    async geocodeAddress(address) {
      if (this.geocodeCache[address]) return this.geocodeCache[address];

      try {
        const geocoder = new google.maps.Geocoder();
        const result = await new Promise((resolve, reject) => {
          geocoder.geocode({ address: address }, (results, status) => {
            if (status === "OK") resolve(results);
            else reject(status);
          });
        });

        const location = result[0].geometry.location;
        this.geocodeCache[address] = location;
        return location;
      } catch (error) {
        console.warn("無法取得座標:", address, error);
        return null;
      }
    },

    // 顯示 markers
    async showMarkersOnMap() {
      if (!this.map) return;

      // 清除舊標記
      this.markers.forEach((m) => m.setMap(null));
      this.markers = [];

      const bounds = new google.maps.LatLngBounds();

      for (const listing of this.paginatedListings) {
        const address = listing.address || listing.ads;
        const loc = await this.geocodeAddress(address);
        if (!loc) continue;

        const lat = loc.lat();
        const lng = loc.lng();

        // 模糊化中心
        const fuzzLat = lat + (Math.random() - 0.5) * 0.002;
        const fuzzLng = lng + (Math.random() - 0.5) * 0.002;
        const fuzzyCenter = new google.maps.LatLng(fuzzLat, fuzzLng);

        // 畫模糊圈
        const circle = new google.maps.Circle({
          strokeColor: '#FF5722',
          strokeOpacity: 0.7,
          strokeWeight: 2,
          fillColor: '#FF8A65',
          fillOpacity: 0.2,
          map: this.map,
          center: fuzzyCenter,
          radius: 300
        });

        // infoWindow
        const infoWindow = new google.maps.InfoWindow({
          content: `<strong>${listing.houseName || listing.room}</strong><br>${address}`,
        });

        circle.addListener("click", (e) => {
          infoWindow.setPosition(e.latLng);
          infoWindow.open(this.map);
        });

        this.markers.push(circle);
        bounds.extend(fuzzyCenter);
      }

      if (!bounds.isEmpty()) {
        this.map.fitBounds(bounds);

        // 限制最大 zoom
        const listener = google.maps.event.addListener(this.map, "idle", () => {
          if (this.map.getZoom() > 14) this.map.setZoom(14);
          google.maps.event.removeListener(listener);
        });
      }
    },

    getCityName(address) {
      if (!address) return '國外地區';
      const cleanedAddress = address.replace(/台灣/, '');
      const match = cleanedAddress.match(/([\u4e00-\u9fa5]{2,3}[縣市])/);
      return match ? match[1] : '國外地區';
    }
  },

  // 換頁自動更新 marker
  watch: {
    currentPage() {
      this.showMarkersOnMap();
    },
  },

  computed: {
    paginatedListings() {
      const start = (this.currentPage - 1) * this.perPage;
      return this.listings.slice(start, start + this.perPage);
    },
    totalPages() {
      return Math.ceil(this.listings.length / this.perPage);
    }
  }
};
</script>





<style scoped>
@import "/src/assets/listing/list1.css";
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css";
@import "/src/assets/listing/listingCard.css";


.listing-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  height: 160px; 
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background-color: white;
  margin-bottom: 16px;
  padding: 12px; 
  gap: 16px; 
  text-decoration: none;  
  color: inherit;        
}

.listing-img {
  width: 140px;    
  height: 140px;   
  object-fit: cover;
  border-radius: 12px;
  flex-shrink: 0;
  margin-top: 10px;  
  margin-bottom: 10px;
}

.listing-info {
  padding: 12px;
}

.search-results-container {
   gap:1px; 
  padding-right: 50px; 
  display: flex;
  height: 100vh;
}
.results-list {
  flex: 1;           
  overflow-y: auto;
  padding: 50px;   
}
.results-map {
  flex: 0.8;
  height: 80vh;
  border-radius: 20px;
}

/* 價格放右下角 */
.price {
  position: absolute;
  right: 16px;
  bottom: 16px;
  font-weight: bold;
  font-size: 25px;
}

.listing-houseName {
  font-size: 16px;
  color: #6f6f6f;
  margin: 4px 0;
}


.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 5px;
}

.pagination button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
}

.pagination button.active {
  background: #e7630b;
  color: white;
  border-color: #e65d03;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

</style>
