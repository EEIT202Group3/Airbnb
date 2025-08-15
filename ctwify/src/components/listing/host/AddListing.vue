<template>
  <Navbar />
  <div class="container">
    <div class="form-container">
      <div class="form-title">新增房源</div>
      <form @submit.prevent="submitForm" enctype="multipart/form-data">
        <div class="mb-3">
          <label class="form-label">房屋名稱</label>
          <input v-model="form.houseName" type="text" class="form-control" required />
        </div>

        <!-- 地址：城市與區域 -->
        <div class="mb-3 row">
          <div class="col">
            <label class="form-label">城市</label>
            <select v-model="selectedCity" class="form-select" required @change="updateDistricts">
              <option disabled value="">請選擇縣市</option>
              <option v-for="(districts, city) in cityData" :key="city" :value="city">{{ city }}</option>
            </select>
          </div>
          <div class="col">
            <label class="form-label">區域</label>
            <select v-model="selectedDistrict" class="form-select" required>
              <option disabled value="">請選擇區域</option>
              <option v-for="d in availableDistricts" :key="d">{{ d }}</option>
            </select>
          </div>
          <div class="col">
            <label class="form-label">詳細地址</label>
            <input v-model="form.detailAddress" type="text" class="form-control" required />
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
          <input type="file" multiple accept="image/listings/*" @change="handleFileUpload" class="form-control" />
        </div>

        <!-- 設備選擇 -->
        <div class="mb-3">
          <label class="form-label">請勾選設備：</label>
          <div class="row">
            <div class="col-6 col-md-4 mb-2" v-for="eq in equipList" :key="eq.equip_id">
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="checkbox"
                  :value="eq.equip_id"
                  v-model="form.equipments"
                />
                <label class="form-check-label">{{ eq.equip_name }}</label>
              </div>
            </div>
          </div>
        </div>

        <div class="text-center">
          <button type="submit" class="btn btn-orange btn-lg px-5">新增房源</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import Navbar from '@/layouts/Navbar.vue';
import axios from 'axios';
import Swal from 'sweetalert2';
import { withCtx } from 'vue';

export default {
  components: { Navbar },
  data() {
    return {
      selectedCity: '',
      selectedDistrict: '',
      availableDistricts: [],
      cityData : {
  "台北市": ["中正區", "大同區", "中山區", "松山區", "大安區", "萬華區", "信義區", "士林區", "北投區", "內湖區", "南港區", "文山區"],
  "新北市": ["板橋區", "三重區", "中和區", "永和區", "新莊區", "新店區", "樹林區", "鶯歌區", "三峽區", "淡水區", "汐止區", "瑞芳區", "土城區", "蘆洲區", "五股區", "泰山區", "林口區", "八里區", "深坑區", "石碇區", "坪林區", "三芝區", "石門區", "金山區", "萬里區", "烏來區"],
  "基隆市": ["仁愛區", "信義區", "中正區", "中山區", "安樂區", "暖暖區", "七堵區"],
  "桃園市": ["桃園區", "中壢區", "平鎮區", "八德區", "楊梅區", "蘆竹區", "大溪區", "龍潭區", "龜山區", "觀音區", "新屋區", "復興區"],
  "新竹市": ["東區", "北區", "香山區"],
  "新竹縣": ["竹北市", "竹東鎮", "新埔鎮", "關西鎮", "湖口鄉", "橫山鄉", "北埔鄉", "寶山鄉", "峨眉鄉", "芎林鄉", "新豐鄉", "五峰鄉", "尖石鄉"],
  "苗栗縣": ["苗栗市", "頭份市", "苑裡鎮", "通霄鎮", "竹南鎮", "後龍鎮", "卓蘭鎮", "造橋鄉", "西湖鄉", "頭屋鄉", "三義鄉", "南庄鄉", "大湖鄉", "獅潭鄉", "三灣鄉", "泰安鄉"],
  "台中市": ["中區", "東區", "南區", "西區", "北區", "北屯區", "西屯區", "南屯區", "太平區", "大里區", "霧峰區", "烏日區", "豐原區", "后里區", "石岡區", "東勢區", "和平區", "新社區", "潭子區", "大雅區", "神岡區", "大肚區", "沙鹿區", "龍井區", "梧棲區", "清水區", "大甲區", "外埔區", "大安區"],
  "彰化縣": ["彰化市", "員林市", "和美鎮", "鹿港鎮", "溪湖鎮", "田中鎮", "北斗鎮", "二林鎮", "田尾鄉", "埤頭鄉", "溪州鄉", "竹塘鄉", "福興鄉", "秀水鄉", "花壇鄉", "芬園鄉", "大村鄉", "永靖鄉", "社頭鄉", "埔心鄉", "溪州鄉", "二水鄉"],
  "南投縣": ["南投市", "草屯鎮", "竹山鎮", "集集鎮", "名間鄉", "鹿谷鄉", "中寮鄉", "魚池鄉", "國姓鄉", "水里鄉", "信義鄉", "仁愛鄉"],
  "雲林縣": ["斗六市", "斗南鎮", "虎尾鎮", "西螺鎮", "土庫鎮", "北港鎮", "古坑鄉", "大埤鄉", "莿桐鄉", "林內鄉", "二崙鄉", "崙背鄉", "麥寮鄉", "臺西鄉", "東勢鄉", "褒忠鄉", "四湖鄉", "口湖鄉", "水林鄉"],
  "嘉義市": ["東區", "西區"],
  "嘉義縣": ["太保市", "朴子市", "布袋鎮", "大林鎮", "民雄鄉", "溪口鄉", "新港鄉", "六腳鄉", "東石鄉", "義竹鄉", "鹿草鄉", "水上鄉", "中埔鄉", "竹崎鄉", "梅山鄉", "番路鄉", "大埔鄉", "阿里山鄉"],
  "台南市": ["中西區", "東區", "南區", "北區", "安平區", "安南區", "永康區", "歸仁區", "新化區", "左鎮區", "玉井區", "楠西區", "南化區", "仁德區", "關廟區", "龍崎區", "官田區", "麻豆區", "佳里區", "西港區", "七股區", "將軍區", "學甲區", "北門區", "新營區", "後壁區", "白河區", "東山區", "六甲區", "下營區", "柳營區", "鹽水區", "善化區", "大內區", "山上區", "新市區", "安定區"],
  "高雄市": ["新興區", "前金區", "苓雅區", "鹽埕區", "鼓山區", "旗津區", "前鎮區", "三民區", "楠梓區", "小港區", "左營區", "仁武區", "大社區", "岡山區", "路竹區", "阿蓮區", "田寮區", "燕巢區", "橋頭區", "梓官區", "彌陀區", "永安區", "湖內區", "鳳山區", "大寮區", "林園區", "鳥松區", "大樹區", "旗山區", "美濃區", "六龜區", "內門區", "杉林區", "甲仙區", "桃源區", "那瑪夏區", "茂林區", "茄萣區"],
  "屏東縣": ["屏東市", "潮州鎮", "東港鎮", "恆春鎮", "萬丹鄉", "長治鄉", "麟洛鄉", "九如鄉", "里港鄉", "鹽埔鄉", "高樹鄉", "萬巒鄉", "內埔鄉", "竹田鄉", "新埤鄉", "枋寮鄉", "新園鄉", "崁頂鄉", "林邊鄉", "南州鄉", "佳冬鄉", "琉球鄉", "車城鄉", "滿州鄉", "枋山鄉", "三地門鄉", "霧臺鄉", "泰武鄉", "來義鄉", "春日鄉", "獅子鄉", "牡丹鄉"],
  "宜蘭縣": ["宜蘭市", "頭城鎮", "礁溪鄉", "壯圍鄉", "員山鄉", "羅東鎮", "三星鄉", "大同鄉", "五結鄉", "冬山鄉", "蘇澳鎮", "南澳鄉"],
  "花蓮縣": ["花蓮市", "新城鄉", "吉安鄉", "壽豐鄉", "鳳林鎮", "光復鄉", "豐濱鄉", "瑞穗鄉", "萬榮鄉", "玉里鎮", "卓溪鄉", "富里鄉"],
  "台東縣": ["臺東市", "成功鎮", "關山鎮", "卑南鄉", "鹿野鄉", "池上鄉", "東河鄉", "長濱鄉", "太麻里鄉", "大武鄉", "綠島鄉", "蘭嶼鄉", "延平鄉", "達仁鄉"],
  "澎湖縣": ["馬公市", "西嶼鄉", "望安鄉", "七美鄉", "白沙鄉", "湖西鄉"],
  "金門縣": ["金沙鎮", "金湖鎮", "金寧鄉", "金城鎮", "烈嶼鄉"],
  "連江縣": ["南竿鄉", "北竿鄉", "莒光鄉", "東引鄉"],
},

      equipList: [],
      form: {
        hostId: "8D7CC70F-6369-4A41-9A58-05F97ABFB688",
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
  methods: {
    updateDistricts() {
      this.availableDistricts = this.cityData[this.selectedCity] || [];
      this.selectedDistrict = '';
    },

    async fetchEquipments() {
      try {
        const res = await axios.get("http://localhost:8080/api/equipment/all",{withCredentials:true})
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

    async submitForm() {
      const formData = new FormData();
      formData.append("host_id", this.form.hostId);
      formData.append("houseName", this.form.houseName);

      
      const fullAddress = `${this.selectedCity}${this.selectedDistrict}${this.form.detailAddress}`;
      formData.append("ads", fullAddress);

      formData.append("room", this.form.room);
      formData.append("bed", `${this.form.bedCount} 張 ${this.form.bedType}`);
      formData.append("describe", this.form.describe);
      formData.append("tel", this.form.tel);
      formData.append("ppl", this.form.ppl);
      formData.append("price", this.form.price);

      this.form.photos.forEach(file => {
        formData.append("photos", file);
      });

      this.form.equipments.forEach(id => {
        formData.append("equipments", id);
      });

      try {
        await axios.post("http://localhost:8080/listings/add", formData, {
          headers: { "Content-Type": "multipart/form-data" }
        });
        Swal.fire("成功", "房源新增成功", "success");
        this.$router.push('/');
        this.resetForm();
      } catch (error) {
        Swal.fire("錯誤", "新增失敗，請檢查輸入", "error");
      }
    },

    resetForm() {
      this.form = {
        hostId: "6d4d8eb1-4e79-4c3e-9a1c-820ebcb8a8ee",
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
    },
  },
  mounted() {
    this.fetchEquipments();
  }
};
</script>

<style scoped>
@import "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css";
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css";
@import '/src/assets/listing/list3.css';
@import '/src/assets/listing/euqipment.css';
</style>

