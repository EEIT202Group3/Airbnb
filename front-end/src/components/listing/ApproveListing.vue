<template>
  <v-container class="my-4">
    <h2 class="mb-4">房源管理</h2>

    <!-- 下拉選單 -->
    <v-select
      v-model="filterStatus"
      :items="statusOptions"
      label="選擇房源狀態"
      class="mb-4"
      dense
      outlined
      item-text="text"
      item-value="value"
    ></v-select>

    <v-alert v-if="filteredListings.length === 0" type="info" class="mb-4">
      目前沒有符合條件的房源。
    </v-alert>

    <v-row dense>
      <v-col
        v-for="listing in filteredListings"
        :key="listing.listId"
        cols="12"
      >
        <v-card class="d-flex flex-row align-center pa-3" outlined>
          <div
            class="listing-img-wrapper"
            @click="showDetail(listing.listId)"
            style="cursor:pointer"
          >
            <v-img
              :src="listing.photo1
                ? `http://localhost:8080/images/listings/${listing.photo1}`
                : 'https://via.placeholder.com/150x100?text=No+Image'"
              alt="房源圖片"
              class="listing-img"
              cover
            ></v-img>
          </div>

          <div class="flex-grow-1 ms-4">
            <div class="d-flex align-center justify-space-between">
              <div>
                <h5 class="mb-1">{{ listing.houseName }}</h5>
                <div class="text-subtitle-2 mb-1">
                  房東 ID：{{ listing.hostId }}
                </div>
                <div>地址：{{ listing.ads }}</div>
                <div>價格：NT${{ listing.price }}</div>
              </div>
              <div class="d-flex flex-column align-end">
                <!-- 狀態按鈕 -->
                <template v-if="filterStatus === 'approved'">
                  <v-btn
                    color="error"
                    @click="markAsError(listing.listId)"
                    class="mb-2"
                  >
                    資訊錯誤
                  </v-btn>
                  <v-btn
                    color="warning"
                    @click="unpublishListing(listing.listId)"
                  >
                    下架房源
                  </v-btn>
                </template>

                <template v-else-if="filterStatus === 'unpublished'">
                  <v-btn
                    color="success"
                    @click="publishListing(listing.listId)"
                  >
                    重新上架        
        </v-btn>
        <v-btn
        color="error"
         @click="deleteListing(listing.listId)"
    >
        刪除房源
       </v-btn>
      </template>

                <template v-else-if="filterStatus === 'rejected'">
                  <v-btn
                    color="success"
                    @click="approveListing(listing.listId)"
                    class="mb-2"
                  >
                    通過審核
                  </v-btn>
                  <v-btn
                    color="warning"
                    @click="unpublishListing(listing.listId)"
                  >
                    下架房源
                  </v-btn>
                </template>

                <template v-else-if="filterStatus === 'pending'">
                  <v-btn
                    color="success"
                    @click="approveListing(listing.listId)"
                    class="mb-2"
                  >
                    通過審核
                  </v-btn>
                  <v-btn
                    color="error"
                    @click="markAsError(listing.listId)"
                    class="mb-2"
                  >
                    資訊錯誤
                  </v-btn>
                  <v-btn
                    color="warning"
                    @click="unpublishListing(listing.listId)"
                  >
                    下架房源
                  </v-btn>
                </template>
              </div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 詳細資料 Modal -->
    <v-dialog v-model="modalVisible" max-width="960">
      <v-card>
        <v-card-title>{{ detailListing?.houseName }}</v-card-title>
        <v-card-text v-if="detailListing">
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <div><strong>房東ID:</strong> {{ detailListing.hostId }}</div>
              <div><strong>地址:</strong> {{ detailListing.ads }}</div>
              <div><strong>價格:</strong> NT${{ detailListing.price }}</div>
              <div><strong>房型:</strong> {{ detailListing.room }}</div>
              <div><strong>床位:</strong> {{ detailListing.bed }}</div>
              <div><strong>電話:</strong> {{ detailListing.tel }}</div>
              <div><strong>人數:</strong> {{ detailListing.ppl }}</div>
              <div><strong>評價數:</strong> {{ detailListing.reviewCount }}</div>
              <div class="mt-2">
                <strong>審核狀態:</strong>
                <v-chip
                  :color="
                    detailListing.approved === true
                      ? 'green'
                      : detailListing.approved === false
                      ? 'red'
                      : 'orange'
                  "
                  dark
                >
                  {{
                    detailListing.approved === true
                      ? '已通過'
                      : detailListing.approved === false
                      ? '資訊錯誤'
                      : '待審核'
                  }}
                </v-chip>
              </div>
              <div class="mt-2">
                <strong>上架狀態:</strong>
                <v-chip
                  :color="detailListing.published === false ? 'grey' : 'blue'"
                  dark
                >
                  {{ detailListing.published === false ? '已下架' : '已上架' }}
                </v-chip>
              </div>
            </v-col>

            <v-col cols="12" sm="6" md="8">
              <v-img
                v-if="mainPhoto"
                :src="mainPhoto"
                height="300"
                class="main-photo rounded mb-3"
                cover
              ></v-img>

              <v-row dense>
                <v-col
                  v-for="(photo, index) in photos"
                  :key="index"
                  cols="4"
                  sm="3"
                  md="2"
                >
                  <v-img
                    :src="photo"
                    height="80"
                    class="thumb-img rounded"
                    :class="{ 'selected-thumb': selectedPhoto === photo }"
                    @click="switchMainPhoto(photo)"
                    style="cursor: pointer;"
                    cover
                  ></v-img>
                </v-col>
              </v-row>
            </v-col>
          </v-row>

          <div class="mt-3">
            <strong>描述:</strong>
            <p>{{ detailListing.describe }}</p>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="secondary" @click="modalVisible = false">
            關閉
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "ApproveListing",
  data() {
    return {
      filterStatus: "pending",
      statusOptions: [
        { text: "審核通過", value: "approved" },
        { text: "資訊錯誤", value: "rejected" },
        { text: "待審核", value: "pending" },
        { text: "已下架", value: "unpublished" },
      ],
      allListings: [],
      detailListing: null,
      modalVisible: false,
      mainPhoto: "",
      photos: [],
      selectedPhoto: "",
    };
  },
  computed: {
    filteredListings() {
      switch (this.filterStatus) {
        case "approved":
          return this.allListings.filter(
            (l) => l.approved === true && l.published !== false
          );
        case "rejected":
          return this.allListings.filter(
            (l) => l.approved === false && l.published !== false
          );
        case "pending":
          return this.allListings.filter(
            (l) =>
              (l.approved === null || l.approved === undefined) &&
              l.published !== false
          );
        case "unpublished":
          return this.allListings.filter((l) => l.published === false);
        default:
          return [];
      }
    },
  },
  methods: {
    fetchAllListings() {
      axios
        .get("http://localhost:8080/listings")
        .then((res) => {
          this.allListings = res.data || [];
        })
        .catch((err) => {
          console.error("查詢失敗:", err);
          alert("查詢房源資料失敗");
        });
    },
    deleteListing(id) {
  if (!confirm("⚠️ 確定要永久刪除這筆房源嗎？刪除後無法恢復！")) return;
  axios
    .delete(`http://localhost:8080/listings/${id}`)
    .then(() => {
      alert("房源已永久刪除");
      this.fetchAllListings();
    })
    .catch((err) => {
      console.error("刪除失敗:", err);
      alert("刪除失敗");
    });
},
    approveListing(id) {
      if (!confirm("確定要通過這筆房源嗎？")) return;
      axios
        .put(`http://localhost:8080/listings/${id}/approve`)
        .then((res) => {
          alert(res.data);
          this.fetchAllListings();
        })
        .catch((err) => {
          console.error("審核失敗:", err);
          alert("審核失敗");
        });
    },
    markAsError(id) {
      if (!confirm("確定要標記這筆房源為資訊錯誤嗎？")) return;
      axios
        .put(`http://localhost:8080/listings/${id}/reject`)
        .then((res) => {
          alert(res.data);
          this.fetchAllListings();
        })
        .catch((err) => {
          console.error("標記失敗:", err);
          alert("標記失敗");
        });
    },
    unpublishListing(id) {
      if (!confirm("確定要下架這筆房源嗎？")) return;
      axios
        .put(`http://localhost:8080/listings/${id}/unpublish`)
        .then(() => {
          alert("房源已下架");
          this.fetchAllListings();
        })
        .catch((err) => {
          console.error("下架失敗:", err);
          alert("下架失敗");
        });
    },
    publishListing(id) {
      if (!confirm("確定要重新上架這筆房源嗎？")) return;
      axios
        .put(`http://localhost:8080/listings/${id}/publish`)
        .then(() => {
          alert("房源已重新上架");
          this.fetchAllListings();
        })
        .catch((err) => {
          console.error("上架失敗:", err);
          alert("上架失敗");
        });
    },
    showDetail(id) {
      this.detailListing = null;
      this.modalVisible = false;
      axios
        .get(`http://localhost:8080/listings/${id}`)
        .then((res) => {
          this.detailListing = res.data;
          this.photos = [];
          for (let i = 1; i <= 10; i++) {
            const photoKey = `photo${i}`;
            if (this.detailListing[photoKey]) {
              this.photos.push(
                `http://localhost:8080/images/listings/${this.detailListing[photoKey]}`
              );
            }
          }
          this.mainPhoto = this.photos.length > 0 ? this.photos[0] : "";
          this.selectedPhoto = this.mainPhoto;
          this.modalVisible = true;
        })
        .catch((err) => {
          console.error("取得詳細資料失敗", err);
          alert("取得詳細資料失敗");
        });
    },
    switchMainPhoto(photo) {
      this.mainPhoto = photo;
      this.selectedPhoto = photo;
    },
  },
  mounted() {
    this.fetchAllListings();
  },
};
</script>

<style scoped>
.listing-img {
  width: 150px !important;
  height: 100px !important;
  object-fit: cover !important;
}

.main-photo {
  object-fit: cover !important;
  width: 100% !important;
}

.thumb-img {
  object-fit: cover !important;
  width: 100% !important;
  height: 80px !important;
}

.selected-thumb {
  border: 2px solid #1976d2;
}
.listing-img-wrapper {
  width: 150px;
  height: 100px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 6px;
}
</style>
