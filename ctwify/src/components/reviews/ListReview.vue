<!-- 有評論時 -->
<template v-else>
  <v-divider :thickness="1" ></v-divider>
  <v-row class="align-center text-center" style="margin-top : 60px;">
    <!-- 左 -->
    <v-col class="d-flex flex-column align-center mb-5">
      <div class="text-h5 font-weight-bold">乾淨度</div>
      <div class="text-h4 font-weight-bold">{{ avg.clean }}</div>
      <v-icon size="48">mdi-spray</v-icon>
    </v-col>

    <v-divider vertical></v-divider>

    <!-- 中 -->
    <v-col class="d-flex flex-column align-center mb-5">
      <div class="text-h5 font-weight-bold">溝通評分</div>
      <div class="text-h4 font-weight-bold">{{ avg.comm }}</div>
      <v-icon size="48">mdi-comment-outline</v-icon>
    </v-col>

    <v-divider vertical></v-divider>

    <!-- 右 -->
    <v-col class="d-flex flex-column align-center mb-5">
      <div class="text-h5 font-weight-bold">性價比</div>
      <div class="text-h4 font-weight-bold">{{ avg.value }}</div>
      <v-icon size="48">mdi-tag-outline</v-icon>
    </v-col>
  </v-row>
  <v-divider :thickness="1"></v-divider>
  <v-row>
    <v-col v-for="item in pagedReviews" :key="item.reviewId" cols="12" md="6">
      <v-card class="mb-5" flat dense style="font-size: 16px">
        <v-btn
  icon
  variant="text"
  class="position-absolute"
  style="top: 8px; right: 8px; opacity: 0.3"
  @click="handleReport(item.reviewId)"
>
  <v-icon color="red" size="24">mdi-alert-circle-outline</v-icon>
</v-btn>
        <v-card-text>
          <div class="d-flex">
            <div class="d-flex flex-column">
              <v-card-title class="pa-0" style="font-size: 20px">
                {{ maskEmail(item.email) || maskEmail(item.customerEmail) }}:
              </v-card-title>
              <v-card-subtitle class="pa-0" style="font-size: 20px">
                評論日期：{{ new Date(item.reviewDate).toLocaleDateString() }}
              </v-card-subtitle>

              <div class="text-caption" style="font-size: 20px">
                <v-rating
                  :length="5"
                  :size="18"
                  :model-value="
                    item.cleanScore + item.commScore + item.valueScore
                  "
                  active-color="yellow"
                  readonly
                />
              </div>
            </div>
            <v-spacer></v-spacer>
          </div>

          <div class="mt-2" style="font-size: 20px">
            {{ item.cusComm || "（無評論內容）" }}
          </div>
          <div class="mt-2" style="font-size: 20px">
            {{ item.hostComm || "(房東未進行回復)" }}
          </div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>

  <!-- 分頁控制列 -->
  <div class="d-flex justify-space-between align-center py-4">
    <span></span>
    <div>
      <v-btn
        :disabled="page === 1"
        @click="goPrevPage"
        class="me-2"
        color="primary"
        variant="outlined"
      >
        上一頁
      </v-btn>
      <v-btn
        :disabled="page >= totalPages"
        @click="goNextPage"
        color="primary"
        variant="outlined"
      >
        下一頁
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axios from 'axios';

const props = defineProps({
  reviews: {
    type: Array,
    default: () => [],
  },
});

const page = ref(1);
const itemsPerPage = 6;

async function handleReport (reviewId){
  console.log(props.reviews.r);
  
  try {
    await axios.patch(`http://localhost:8080/api/reviews/${reviewId}/report`);
    alert('檢舉成功');
  } catch (err) {
    console.error('檢舉失敗', err);
    alert('檢舉失敗');
  }
}

const totalPages = computed(() =>
  Math.ceil(props.reviews.length / itemsPerPage)
);

const pagedReviews = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return props.reviews.slice(start, end);
});

const goNextPage = () => {
  if (page.value < totalPages.value) {
    page.value++;
  }
};

const goPrevPage = () => {
  if (page.value > 1) {
    page.value--;
  }
};

const scrollToTop = () => {
  window.scrollTo({ top: 50, behavior: "smooth" });
};

// 隱藏email
function maskEmail(email) {
  if (!email) return "";

  const [name, domain] = email.split("@");
  if (!domain) return email;

  // 保留前2與後2，中間補*
  if (name.length > 4) {
    return (
      name.slice(0, 2) +
      "*".repeat(name.length - 4) +
      name.slice(-2) +
      "@" +
      domain
    );
  } else {
    // 太短的名字，至少留頭尾
    return (
      name[0] +
      "*".repeat(Math.max(name.length - 2, 1)) +
      name.slice(-1) +
      "@" +
      domain
    );
  }
}

console.log(props.reviews);

const avg = computed(() => {
  if (props.reviews.length === 0) return { clean: 0, comm: 0, value: 0 };

  const total = props.reviews.reduce(
    (acc, review) => {
      acc.clean += review.cleanScore;
      acc.comm += review.commScore;
      acc.value += review.valueScore;
      return acc;
    },
    { clean: 0, comm: 0, value: 0 }
  );

  return {
    clean: (total.clean / props.reviews.length).toFixed(1),
    comm: (total.comm / props.reviews.length).toFixed(1),
    value: (total.value / props.reviews.length).toFixed(1),
  };
});
console.log(avg.value.clean);
</script>
<style scoped>
/* 與 A.vue 的 .come 命名相同 — 這段只影響 B.vue（scoped） */
.come {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0px;
}

/* 小調：讓卡片間距、標題與 A.vue 更接近 */
.v-card {
  border-radius: 8px;
  margin: 0 auto;
  padding: 0px;
}

.side-placeholder {
  /* 保證右側空白在大螢幕顯示；在小螢幕會自動疊放 */
  min-height: 1px;
}

/* 調整評論文字行距 */
.v-card-text {
  line-height: 1.6;
}
</style>
