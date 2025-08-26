<template>
  <div class="container">
    <div v-if="loading" class="text-center py-4">資料載入中...</div>
    <div v-else class="listings-grid">
      <router-link
        v-for="house in houseList"
        :key="house.listId"
        :to="`details/${house.listId}`"
        class="listing"
      >
        <img
          :src="`http://localhost:8080/images/listings/${house.photo1}`"
          alt="房源圖片"
          class="listing-img"
        />

     
  <!-- 只有審核通過才顯示上架 / 下架 -->
  <p
    v-if="house.approved === true"
    class="listing-status-placeholder"
    :class="house.published ? 'published' : 'unpublished'"
  >
    {{ house.published ? '已上架' : '已下架' }}
  </p>
     <div class="listing-info">
  <h5 class="listing-title">{{ house.houseName }}</h5>
  <p class="listing-ads">
    <v-icon small class="me-1">mdi-map-marker</v-icon>
    {{ house.ads }}
  </p>
</div>

           <!-- 審核狀態燈標籤 -->
    <div class="status-badge">
      <span
        v-if="house.approved === null"
        class="status-light yellow"
      ></span>
      <span
        v-else-if="house.approved === true"
        class="status-light green"
      ></span>
      <span
        v-else
        class="status-light red"
      ></span>
      <span class="status-text">
        {{ house.approved === null ? "審核中" : (house.approved ? "已通過" : "未通過") }}
      </span>


  </div>
      </router-link>

      <div v-if="houseList.length === 0" class="mt-4 text-muted text-center">
        目前沒有房源
      </div>
    </div>
  </div>
</template>
<script>
import axios from "@/api2";


export default {
  components: {
  },
  name: "list",
  data() {
    return {
      houseList: [],
      loading: false,
      hostId: "",
    };
  },
  methods: {
    fetchListings() {
      this.loading = true;
      axios
        .get(`http://localhost:8080/listings/host`)
        .then((res) => {

          this.houseList = res.data.filter(house => house.deleted !== true);

        })
        .catch((err) => {
          console.error("查詢會員房源失敗", err);
          alert("查詢會員房源失敗");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    editHouse(listId) {
      this.$router.push(`/edit/${listId}`);
    },
    unpublishHouse(house) {
      if (confirm(`確定要移除房源「${house.houseName}」嗎？`)) {
        axios
          .put(`http://localhost:8080/listings/${house.listId}/unpublish`)
          .then(() => {
            alert("房源已移除");
            this.fetchListings(); // 重新載入列表
          })
          .catch((err) => {
            console.error(err);
            alert("移除失敗");
          });
      }
    },
  },
  mounted() {
    this.fetchListings();
  },
};
</script>

<style scoped>
@import "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css";
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css";
@import "/src/assets/listing/list3.css";

.container {
  width: 100%;
  max-width: 1100px; /* 與 navbar 對齊 */
  margin: 0 auto;    /* 置中 */
  padding: 0 16px;   /* 預留左右邊距 */
  box-sizing: border-box;
}

.listing {
  position: relative; /* 讓內部絕對定位以 listing 為基準 */
  padding: 16px;
  border: 1px solid #ddd;
  margin-bottom: 20px;
  border-radius: 8px;
}
.listings-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 每行三個卡片 */
  gap: 20px; /* 卡片之間的間距 */
}
.listing-img-wrapper {
  position: relative;
}

.status-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #fff;
  padding: 4px 10px;
  border-radius: 20px; /* 圓角標籤 */
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #333;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

.status-light {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

/* 狀態顏色 */
.status-light.green {
  background-color: #28a745;
}
.status-light.yellow {
  background-color: #ffc107;
}
.status-light.red {
  background-color: #dc3545;
}

.listing-info {
  display: flex;
  flex-direction: column;
}


.listing-status {
  margin-top: 6px;
  font-size: 14px;
  font-weight: 500;
}


.listing-status-placeholder.published {
  color: #28a745; /* 綠色上架 */
}

.listing-status-placeholder.unpublished {
  color: #dc3545; /* 紅色下架 */
}


.listing-status-placeholder {
  height: 18px; /* 固定高度，保持名稱位置不跳動 */
  line-height: 18px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

/* 房源名稱 */
.listing-title {
  font-size: 16px;
  font-weight: bold;
  line-height: 1.4;
  height: 80px; /* 固定高度 ≈ 兩行字 */
  overflow: hidden;
  display: -webkit-box;
  /* -webkit-line-clamp: 2; 最多顯示兩行 */
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
}

/* 地址 */
.listing-ads {
  font-size: 14px;
  color: #828282;
  height: 20px; /* 固定一行高度 */
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}


</style>
