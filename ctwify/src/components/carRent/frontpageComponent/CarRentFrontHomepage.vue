<script setup lang="ts">
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";

const locationList = [
  {
    name: "台北",
    img: "/carPicture/taipei.jpg",
    desc: "首都繁華，融合歷史古蹟與現代摩天大樓，是臺灣的政治、經濟與文化中心。"
  },
  {
    name: "高雄",
    img: "/carPicture/kaoshon.jpg",
    desc: "港都風情與現代藝術並存，擁有蓬勃海洋文化與熱情陽光，是南台灣的重要城市。"
  },
  {
    name: "宜蘭",
    img: "/carPicture/yilan.jpg",
    desc: "山海環抱、自然純淨，以溫泉、田園與民俗活動聞名，是旅遊與慢活的好去處。"
  }
];

const faqs = [
  {
    q: "如何查詢各據點的營業時間？",
    a: "請至「服務據點」頁面選擇欲租車之據點，即可查看地址與營業時間。"
  },
  {
    q: "現場租車是否提供折扣？",
    a: "折扣與優惠以各據點公告為準，建議優先透過線上預約取得即時方案與價格。"
  },
  {
    q: "如何確認指定日期是否有車？",
    a: "請使用首頁搜尋功能輸入取還車日期時間與地點，系統將即時顯示可租車型。"
  },
  {
    q: "完成預約後，如何付款？",
    a: "可使用信用卡或現金付款；若為線上預約，將依頁面指示完成付款或於門市結帳。"
  },
  {
    q: "可否由他人代為取車？",
    a: "需由實際駕駛者本人，並出示有效身分證件與駕照辦理取車。"
  },
  {
    q: "取車時需攜帶哪些證件？",
    a: "請備妥身分證、駕照；若為線上預約，另請攜帶付款憑證或用以付款之信用卡。"
  }
];
</script>

<template>
  <!-- 搜尋列（置頂） -->
  <div class="page-wrap mt-4">
    <SearchBar />
  </div>

  <!-- 地點推薦 -->
  <div class="page-wrap mt-6">
    <div class="d-flex align-center mb-4">
      <v-icon class="mr-2" color="warning">mdi-map-marker</v-icon>
      <h4 class="mb-0 font-weight-bold">地點推薦</h4>
    </div>

    <v-row align="stretch" dense>
      <v-col
          v-for="loc in locationList"
          :key="loc.name"
          cols="12"
          md="4"
          class="mb-4"
      >
        <v-card elevation="2" class="h-100">
          <v-img :src="loc.img" :alt="loc.name" height="200" cover />
          <v-card-title class="text-orange-darken-2 font-weight-bold">
            {{ loc.name }}
          </v-card-title>
          <v-card-text class="text-body-2">
            {{ loc.desc }}
          </v-card-text>
          <v-card-actions class="pt-0">
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </div>

  <!-- 租車說明 -->
  <div class="page-wrap mt-6">
    <v-card elevation="2">
      <v-card-item class="px-0">
        <v-card-title class="title-strip">
          如何找到最超值的租車服務
        </v-card-title>
      </v-card-item>

      <v-divider />

      <v-card-text>
        <div class="mb-4">
          <h6 class="font-weight-bold">💡 快速比價，一站完成預訂</h6>
          <p class="mb-0">
            本平台整合多家租車供應商，提供<strong>即時價格</strong>與<strong>線上預訂</strong>，協助您快速完成挑選與下單。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">📅 彈性預訂與取消政策</h6>
          <p class="mb-0">
            多數據點提供《<strong>彈性預訂</strong>》與《<strong>免費取消</strong>》方案，旅程調整更安心。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">📈 長租方案更優惠</h6>
          <p class="mb-0">
            若租期較長（如 7 天以上），建議比較《<strong>長租/月租</strong>》方案，通常單日費用更具優勢。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">⛽ 留意燃油與里程政策</h6>
          <p class="mb-0">
            建議選擇《<strong>滿油取還</strong>》並確認里程限制，避免產生額外費用。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">🔑 自助取車更省時</h6>
          <p class="mb-0">
            若支援《<strong>自助取車</strong>》或《<strong>免鑰匙</strong>》服務，可縮短現場等待時間。
          </p>
        </div>

        <div>
          <h6 class="font-weight-bold">🌱 電動車選項</h6>
          <p class="mb-0">
            支援電動車預約，可節省燃油成本並降低碳排，適合都會短程或定點往返。
          </p>
        </div>

        <v-divider class="my-4" />

        <div>
          <h6 class="font-weight-bold">📍 取車地點選擇</h6>
          <p class="mb-0">
            若預算有限，可評估市中心外圍據點，通常《<strong>租金較優惠</strong>》，搭乘大眾運輸即可抵達。
          </p>
        </div>
      </v-card-text>
    </v-card>
  </div>

  <!-- 常見問題 -->
  <div class="page-wrap mt-6 mb-10">
    <div class="d-flex align-center mb-4">
      <v-icon class="mr-2" color="orange-darken-2">mdi-help-circle</v-icon>
      <h4 class="mb-0 font-weight-bold">常見問題</h4>
    </div>

    <v-expansion-panels variant="accordion">
      <v-expansion-panel v-for="(faq, idx) in faqs" :key="idx">
        <v-expansion-panel-title class="font-weight-bold">
          <v-icon class="mr-2" color="orange">mdi-help-circle-outline</v-icon>
          {{ faq.q }}
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          {{ faq.a }}
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>

  <router-view />
</template>

<style scoped>
.page-wrap {
  width: 70%;
  margin-left: auto;
  margin-right: auto;
}

.title-strip {
  color: #fff;
  padding: 12px 16px;
  background: linear-gradient(
      90deg,
      rgb(var(--v-theme-warning)),
      rgba(var(--v-theme-warning), 0.15)
  );
  border-radius: 8px;
}

@media (max-width: 1024px) {
  .page-wrap {
    width: 92%;
  }
}

.font-weight-bold { font-weight: 700; }
</style>
