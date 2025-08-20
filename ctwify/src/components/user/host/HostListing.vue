<template>
  <div>
    <div class="container">
      <div v-if="loading">資料載入中...</div>
      <div v-else>
        <div
          v-for="house in houseList"
          :key="house.listId"
          class="listing"
        >
          <router-link :to="`/host/details/${house.listId}`">
                <img :src="`http://localhost:8080/images/listings/${house.photo1}`" alt="房源圖片" />
            </router-link>
          <div class="listing-info">{{ house.houseName }}</div>
          <div class="listing-actions">
            <button @click="editHouse(house.listId)">編輯</button>
            <button @click="confirmDelete(house)">刪除</button>
          </div>
        </div>
        <div v-if="houseList.length === 0">目前沒有房源</div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '@/layouts/Navbar.vue';
import axios from "axios";  

export default {
     components: {
    Navbar   
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
        .get(`http://localhost:8080/listings/host`,{
          withCredentials:true,
        })
        .then((res) => {
          this.houseList = res.data;
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
      this.$router.push(`/host/edlistings/${listId}`);
    },
    confirmDelete(house) {
      if (confirm(`確定要刪除房源「${house.houseName}」嗎？`)) {
        axios
          .delete(`http://localhost:8080/listings/${house.listId}`)
          .then(() => {
            alert("刪除成功");
            this.fetchListings(); 
          })
          .catch((err) => {
            console.error(err);
            alert("刪除失敗");
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
/* @import "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"; */
/* @import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"; */
@import "/src/assets/listing/list3.css";

</style>