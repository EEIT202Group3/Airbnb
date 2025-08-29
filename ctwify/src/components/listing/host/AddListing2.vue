<template>
  <div class="container">
    <!-- form title + 一鍵填入按鈕，左右對齊 -->
<div class="d-flex justify-content-between align-items-center mb-2">
  <v-btn
    color="blue"
    size="small"
    class="text-white"
    @click="fillDemoData"
  >
    一鍵填入
  </v-btn>
</div>
    <div class="form-container">
      <div class="form-title">新增房源</div>
      <form @submit.prevent="submitForm" enctype="multipart/form-data">
        <div class="mb-3">
  <label class="form-label">房屋名稱</label>
  <input 
    v-model="form.houseName" 
    type="text" 
    class="form-control" 
    required 
    @input="checkHouseNameLength"
  />
</div>
        <!-- 地址輸入 + Google Maps -->
<label class="form-label">地址</label>
<div class="col">
  <input
    type="text"
    class="form-control"
    :value="address"
    readonly
    placeholder="點擊選擇地址"
    @click="showDialog = true"
  />

  <!-- 彈窗 -->
  <div v-if="showDialog" class="dialog-overlay">
    <div class="dialog">
      <h3>選擇地址</h3>

      <!-- Google Maps Autocomplete -->
      <GMapAutocomplete
        placeholder="輸入地址"
        @place_changed="setPlace"
        class="form-control"
      />

      <!-- Google Map -->
      <GMapMap
        :center="mapCenter"
        :zoom="15"
        style="width: 100%; height: 400px;"
      >
        <GMapMarker :position="mapCenter" />
      </GMapMap>

      <div class="dialog-buttons">
        <button @click="confirmAddress">確認地址</button>
        <button @click="closeDialog">取消</button>
      </div>
    </div>
  </div>
</div>

        <!-- 房型 + 床型 + 床數 -->
        <div class="mb-3 row">
          <div class="col">
            <label class="form-label">房型</label>
            <select v-model="form.room" class="form-select" required>
              <option disabled value="">請選擇房型</option>
              <option value="整套房屋">整套房屋</option>
              <option value="獨立房間">獨立房間</option>
              <option value="共享房間">共享房間</option>
              <option value="飯店式房間">飯店式房間</option>
              <option value="獨立套房">獨立套房</option>
              <option value="公寓">公寓</option>
              <option value="別墅">別墅</option>
              <option value="度假屋">度假屋</option>
              <option value="農舍">農舍</option>
              <option value="樓中樓">樓中樓</option>
              <option value="共管公寓">共管公寓</option>
              <option value="露營車/帳篷">露營車/帳篷</option>
              <option value="青年旅館床位">青年旅館床位</option>
              <option value="小屋">小屋</option>
              <option value="船屋">船屋</option>
            </select>
          </div>
          <div class="col">
            <label class="form-label">床型</label>
            <select v-model="form.bedType" class="form-select" required>
              <option disabled value="">請選擇床型</option>
              <option value="單人床">單人床</option>
              <option value="雙人床">雙人床</option>
              <option value="加大雙人床">加大雙人床</option>
              <option value="特大雙人床">特大雙人床</option>
              <option value="沙發床">沙發床</option>
            </select>
          </div>
          <div class="col">
            <label class="form-label">床數</label>
            <input v-model.number="form.bedCount" type="number" min="1" class="form-control" required />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">描述</label>
          <textarea v-model="form.describe" class="form-control" rows="3"></textarea>
        </div>

        <div class="mb-3 row">
          <div class="col">
            <label class="form-label">電話</label>
            <input v-model="form.tel" type="text" class="form-control" />
          </div>
          <div class="col">
            <label class="form-label">人數</label>
            <input v-model="form.ppl" type="number" min="1" class="form-control" />
          </div>
          <div class="col">
            <label class="form-label">價格</label>
            <input v-model="form.price" type="number" min="0" class="form-control" />
          </div>
        </div>

        <!-- 照片上傳 -->
        <div class="mb-3">
          <label class="form-label">照片上傳（最多十張）</label>
          <input type="file" multiple accept="image/*" @change="handleFileUpload" class="form-control" />
        </div>

        <!-- 設備選擇（依分類排版，點擊圖示選擇） -->
        <div class="mb-3">
          <label class="form-label">請勾選設備：</label>
          <div v-for="(equipments, category) in groupedEquipments" :key="category" class="mb-4">
            <h5 class="equip-category-title">{{ category }}</h5>
            <div class="d-flex flex-wrap gap-3">
              <div
                v-for="eq in equipments"
                :key="eq.equip_id"
                class="equip-item"
                :class="{ selected: form.equipments.includes(eq.equip_id) }"
                @click="toggleEquipment(eq.equip_id)"
                title="點擊選擇設備"
              >
                <!-- Font Awesome icon -->
                <i
                  v-if="isFontAwesome(eq.equip_icon)"
                  :class="[eq.equip_icon, 'equip-icon']"
                  aria-hidden="true"
                ></i>

                <!-- Vuetify (mdi) icon -->
                <v-icon v-else class="equip-icon" size="25">
                  {{ eq.equip_icon }}
                </v-icon>

                <div class="equip-name">{{ eq.equip_name }}</div>
                <input
                  type="checkbox"
                  :value="eq.equip_id"
                  v-model="form.equipments"
                  class="d-none"
                />
              </div>
            </div>
          </div>
        </div>

       <div class="text-center">
  <v-btn
    type="submit"
    class="orange-btn px-6"
    rounded="lg"
    elevation="2"
    size="large"
  >
    新增房源
  </v-btn>
</div>

        <!-- 表單結尾 -->
      </form>
    </div>
  </div>
</template>

<script>
import axios from '@/api2';
import Swal from 'sweetalert2';


export default {
  data() {
    return {
      address: "",
      showDialog: false,
      mapCenter: { lat: 25.0330, lng: 121.5654 }, // 台北101 預設
      selectedPlace: null,
      equipList: [],
      form: {
        hostId: "",
        houseName: "",
        detailAddress: "",
        room: "",
        bedType: "",
        bedCount: 1,
        describe: "",
        tel: "",
        ppl: 1,
        price: 0,
        photos: [],
        equipments: [],
      },
    };
  },
  computed: {
    // 根據 equip_category 分組設備
    groupedEquipments() {
      return this.equipList.reduce((groups, eq) => {
        if (!groups[eq.equip_category]) groups[eq.equip_category] = [];
        groups[eq.equip_category].push(eq);
        return groups;
      }, {});
    }
  },
 methods: {
  fillDemoData() {
  this.form.houseName = "全新翻修/當天可訂/美麗島捷運/六合夜市";
  this.address = "800台灣高雄市新興區六合二路";
  this.form.room = "整套房屋";
  this.form.bedType = "加大雙人床";
  this.form.bedCount = 2;
  this.form.describe = "";
  this.form.tel = "02-12345678";
  this.form.ppl = 4;
  this.form.price = 1960;

  // 根據設備名稱選擇幾項具代表性的設備
  const wantedNames = [
    "電視",       // 客廳
    "吹風機",     // 衛浴
    "洗衣機",     // 臥室和洗衣
    "電動車充電", // 停車位和設施
    "冷氣",       // 冷氣和暖氣
    "冰箱",       // 廚房和餐飲
    "滅火器",     // 居家安全
    "無線藍芽音響" // 娛樂
  ];

  this.form.equipments = this.equipList
    .filter(eq => wantedNames.includes(eq.equip_name))
    .map(eq => eq.equip_id);
},
    setPlace(place) {
      if (place && place.geometry) {
        this.mapCenter = {
          lat: place.geometry.location.lat(),
          lng: place.geometry.location.lng()
        };
        this.selectedPlace = place;
      }
    },
  checkHouseNameLength() {
    if (this.form.houseName.length >30) {
      // 確保用原生 alert
      window.alert('房屋名稱最多 30 個字');
      // 自動截斷字數
      this.form.houseName = this.form.houseName.slice(0, 30);
    }
  },
    closeDialog() {
      this.showDialog = false;
    },
   confirmAddress() {
      if (this.selectedPlace) {
        this.address = this.selectedPlace.formatted_address;
      }
      this.showDialog = false;
    },
    closeDialog() {
      this.showDialog = false;
    },
    initMap() {
      this.map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 25.0330, lng: 121.5654 },
        zoom: 12
      });
      // 如果有地址，透過 Geocoder 定位地圖
  if (this.tempAddress) {
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({ address: this.tempAddress }, (results, status) => {
      if (status === "OK" && results[0]) {
        const location = results[0].geometry.location;
        this.map.setCenter(location);
        this.map.setZoom(15);

        if (this.marker) {
          this.marker.setMap(null);
        }

        this.marker = new google.maps.Marker({
          map: this.map,
          position: location
        });
      }
    });
  }
    },
   initAutocomplete() {
  const input = this.$refs.dialogInput;

  // 先解除舊的 autocomplete 綁定
  if (this.autocomplete) {
    google.maps.event.clearInstanceListeners(this.autocomplete);
    this.autocomplete.unbindAll?.();
    this.autocomplete = null;
  }

  // 重新初始化 Autocomplete
  this.autocomplete = new google.maps.places.Autocomplete(input, { types: ['geocode'] });
  this.autocomplete.addListener('place_changed', this.onPlaceChanged);
},
    onPlaceChanged() {
      const place = this.autocomplete.getPlace();
      if (place.geometry && place.geometry.location) {
        this.map.setCenter(place.geometry.location);
        this.map.setZoom(15);

        if (this.marker) {
          this.marker.setMap(null);
        }
        this.marker = new google.maps.Marker({
          map: this.map,
          position: place.geometry.location
        });

        this.tempAddress = place.formatted_address;
      }
    },
  updateDistricts() {
      this.availableDistricts = this.cityData[this.selectedCity] || [];
      this.selectedDistrict = '';
    },
    isFontAwesome(icon) {
    // 判斷是否為 Font Awesome class
    return icon.startsWith('fa') || icon.includes('fa-');
    },

    async fetchEquipments() {
      try {
        const res = await axios.get("http://localhost:8080/api/equipment/all");
        this.equipList = res.data;
      } catch (err) {
        console.error("設備讀取失敗", err);
      }
    },

    handleFileUpload(event) {
      let files = Array.from(event.target.files).filter(f => f.size > 0);
      if (files.length > 10) {
        alert("最多只能上傳 10 張圖片");
        files = files.slice(0, 10);
      }
      this.form.photos = files;
    },

    toggleEquipment(equipId) {
      const index = this.form.equipments.indexOf(equipId);
      if (index === -1) {
        this.form.equipments.push(equipId);
      } else {
        this.form.equipments.splice(index, 1);
      }
    },

    async submitForm() {
      const formData = new FormData();
      formData.append("host_id", this.form.hostId);
      formData.append("houseName", this.form.houseName);
      const fullAddress = `${this.address}${this.form.detailAddress}`;
      formData.append("ads", fullAddress);
      formData.append("room", this.form.room);
      formData.append("bed", `${this.form.bedCount} 張 ${this.form.bedType}`);
      formData.append("describe", this.form.describe);
      formData.append("tel", this.form.tel);
      formData.append("ppl", this.form.ppl);
      formData.append("price", this.form.price);

      
      formData.append("replacePhotos", true);

     this.form.photos.forEach(file => {
        formData.append("photos", file);
      });

      this.form.equipments.forEach(id => {
        formData.append("equipments", id);
      });
      try {
        const res = await axios.post("/api/hosts/listings/add", formData,{
          withCredentials:true,
        }); 
      if (res.status === 200) {
        Swal.fire("新增成功", "房源新增成功！", "success").then(() => {
        this.$router.push("/host/listing") 
        })
        this.resetForm();
        }
      } catch (err) {
        Swal.fire("錯誤", "新增失敗，請稍後再試。", "error");
        console.error(err);
      }
    },
  
    resetForm() {
      this.form = {
        hostId:"",
        houseName: "",
        detailAddress: "",
        room: "",
        bedType: "",
        bedCount: 1,
        describe: "",
        tel: "",
        ppl: 1,
        price: 0,
        photos: [],
        equipments: [],
      };
      this.selectedCity = '';
      this.selectedDistrict = '';
      this.availableDistricts = [];
    }
  },

  mounted() {
    this.fetchEquipments();
  }
};
</script>

<style>
@import "/src/assets/listing/list3.css";
/* 設備分類標題 */
.equip-category-title {
  font-weight: 600;
  margin-bottom: 0.75rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.25rem;
}

/* 設備項目容器 */
.equip-item {
  width: 80px;
  cursor: pointer;
  text-align: center;
  user-select: none;
  border-radius: 8px;
  padding: 6px;
  transition: background-color 0.3s, border 0.3s;
  border: 2px solid transparent;
}
.equip-item:hover {
  background-color: #f0f0f0;
}
.equip-item.selected {
  background-color: #959595;
  border-color: none;
  color: #fff;
}

/* 設備圖示 */
.equip-icon {
  font-size: 20px;  
  margin-bottom: 10px;
  color: inherit;
}

/* 設備名稱 */
.equip-name {
  font-size: 0.85rem;
  line-height: 1.2;
  white-space: normal;
  user-select: none;
}


/* 遮罩背景 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

/* 彈窗本體 */
.dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 700px;
  max-height: 900px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 文字框 */
.dialog input {
  margin-bottom: 30px;
}

#map {
  flex: 1;
  min-height: 400px;
  max-height: 400px; /* 限制最大高度 */
}
/* 按鈕區塊 */
.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
.pac-container {
  z-index: 3000 !important;
}

body {
  padding-right: 0 !important;
}

/* 停用設備卡片在 focus/active 狀態下的橘框 */
.equip-item:focus,
.equip-item:focus-visible,
.equip-item:active {
  outline: none !important;
  border-color: transparent !important;
  box-shadow: none !important;
}

</style>
