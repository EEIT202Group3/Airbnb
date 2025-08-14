<script setup lang="ts">
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";
import { useRoute } from "vue-router";

const route = useRoute();

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
  { q: "請問哪裡可以查到各據點的營業時間？", a: "您可以在「服務據點」內，尋找您欲租車的據點位置及營業時間。" },
  { q: "現場租車有什麼折扣？", a: "問就是沒有。" },
  { q: "請問如何知道自己想要的日期是否仍有車可租呢？", a: "您可致電於想要取車的據點（請參考「服務據點」）或透過「意見信箱」詢問。" },
  { q: "電話/網路預約後，如何進行付費交易？", a: "無論方式皆接受信用卡及現金付款。" },
  { q: "我是會員但不會開車，可以請別人取車嗎？", a: "很抱歉，需具備駕照者本人到場取車。" },
  { q: "取車時需要帶什麼證件？", a: "須攜帶身分證、駕照，網路預約則需加上付款憑證或信用卡。" }
];
</script>

<template>
  <!-- 頂部導覽 -->
  <NavigationBar />

  <!-- 搜尋列 -->
  <SearchBar />

  <!-- 熱門地點 -->
  <v-container class="mt-6">
    <div class="d-flex align-center mb-4">
      <v-icon class="mr-2" color="primary">mdi-map-marker</v-icon>
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
          <v-card-title class="text-primary font-weight-bold">
            {{ loc.name }}
          </v-card-title>
          <v-card-text class="text-body-2">
            {{ loc.desc }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>

  <!-- 租車說明 -->
  <v-container class="mt-6">
    <v-card elevation="2">
      <v-card-item>
        <v-card-title class="text-white" style="background: var(--v-theme-primary); border-radius: 8px;">
          如何找到最超值的租車服務
        </v-card-title>
      </v-card-item>

      <v-divider />

      <v-card-text>
        <div class="mb-4">
          <h6 class="font-weight-bold">💡 快速比價，一站完成預訂</h6>
          <p class="mb-0">
            本平台整合上百家租車供應商，<strong>免費比價</strong>、<strong>直接預訂</strong>，讓你省時省力、輕鬆找到心儀車款。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">📅 彈性預訂與免費取消</h6>
          <p class="mb-0">
            多數車行提供《<strong>彈性預訂政策</strong>》與<strong>免費取消</strong>選項，讓你就算臨時變動行程也不擔心。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">📈 長期租車更划算</h6>
          <p class="mb-0">
            若有長時間需求（如一週以上），建議選擇《<strong>月租方案</strong>》查看是否比短租更優惠，特別適合中長期出差或旅遊。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">⛽ 留意燃油政策</h6>
          <p class="mb-0">
            建議選擇《<strong>滿油取車、滿油還車</strong>》政策，避免被收取額外油費，亦可自行掌握加油成本。
          </p>
        </div>

        <div class="mb-4">
          <h6 class="font-weight-bold">🔑 自助取車更方便</h6>
          <p class="mb-0">
            可選擇《<strong>免鑰匙或自助取車</strong>》服務，免排隊、不需填表，直接到場取車開走，<strong>大幅節省時間</strong>。
          </p>
        </div>

        <div>
          <h6 class="font-weight-bold">🌱 電動車環保又省油</h6>
          <p class="mb-0">
            若支持永續旅遊，可篩選《<strong>電動車</strong>》選項，不但已充電完畢、免加油費，也更環保減碳。
          </p>
        </div>

        <v-divider class="my-4" />

        <div>
          <h6 class="font-weight-bold">📍 取車地點靈活選</h6>
          <p class="mb-0">
            若預算有限，可考慮到市區外圍地點取車，通常《<strong>租金會更便宜</strong>》，搭乘大眾運輸前往即可。
          </p>
        </div>
      </v-card-text>
    </v-card>
  </v-container>

  <!-- 常見問題 -->
  <v-container class="mt-6 mb-10">
    <div class="d-flex align-center mb-4">
      <v-icon class="mr-2" color="warning">mdi-help-circle</v-icon>
      <h4 class="mb-0 font-weight-bold">常見問題</h4>
    </div>

    <v-expansion-panels variant="accordion">
      <v-expansion-panel
          v-for="(faq, idx) in faqs"
          :key="idx"
      >
        <v-expansion-panel-title class="font-weight-bold">
          <v-icon class="mr-2" color="secondary">mdi-help-circle-outline</v-icon>
          {{ faq.q }}
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          {{ faq.a }}
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>

  <router-view />
</template>

<style scoped>
.font-weight-bold { font-weight: 700; }
</style>
