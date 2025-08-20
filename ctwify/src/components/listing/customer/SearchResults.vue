<template>
    <br>
  <div class="search-results-container">
    <!-- 左邊搜尋結果 -->
    <div class="results-list">
      <h3>搜尋結果</h3>
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
    await this.fetchListingsFromQuery();
    this.initMap();
    this.showMarkersOnMap();
  },
  methods: {
    async fetchListingsFromQuery() {
      const query = this.$route.query;
      const res = await axios.get('http://localhost:8080/listings/search', {
        params: query
      });
      this.listings = res.data;

        // 更新地圖標記
      this.showMarkersOnMap();
    },

    initMap() {
      this.map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 25.0330, lng: 121.5654 }, // 台北
        zoom: 12
      });
    },

   async geocodeAddress(address) {
  if (this.geocodeCache[address]) return this.geocodeCache[address];

  try {
    const geocoder = new google.maps.Geocoder();
    const result = await new Promise((resolve, reject) => {
      geocoder.geocode({ address: address }, (results, status) => {
        if (status === "OK") {
          resolve(results);
        } else {
          reject(status);
        }
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
      fillOpacity: 0.4,
      map: this.map,
      center: fuzzyCenter,
      radius: 1000 // 半徑 350 公尺，可改成隨機
    });

    // infoWindow
    const infoWindow = new google.maps.InfoWindow({
      content: `<strong>${listing.houseName || listing.room}</strong><br>${address}`,
    });

    // 點擊圈顯示 infoWindow
    circle.addListener("click", (e) => infoWindow.setPosition(e.latLng) || infoWindow.open(this.map));

    this.markers.push(circle);
    bounds.extend(fuzzyCenter);
  }

  if (!bounds.isEmpty()) {
    this.map.fitBounds(bounds);
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
@import "/src/assets/listing/searchBar.css";
@import "@/assets/listing/datepicker.css"; 


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
  padding: 12px; /* 卡片內邊距，讓圖片不貼邊 */
  gap: 16px; /* 文字與圖片之間留空 */
  text-decoration: none;  /* 移除底線 */
  color: inherit;         /* 保持原本的文字顏色 */
}

.listing-img {
  width: 140px;    /* 固定寬度 */
  height: 140px;   /* 留下上下空隙 */
  object-fit: cover;
  border-radius: 12px;
  flex-shrink: 0;
  margin-top: 10px;   /* 上下空隙 */
  margin-bottom: 10px;
}

.listing-info {
  padding: 12px;
}

.search-results-container {
   gap:1px; /* 左右之間留空隙 */
  padding-right: 50px; /* 右邊再多留一些空隙 */
  display: flex;
  height: 100vh;
}
.results-list {
  flex: 1;             /* 左側列表撐大一點 */
  overflow-y: auto;
  padding: 50px;       /* 左右內邊距，卡片不貼邊 */
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
