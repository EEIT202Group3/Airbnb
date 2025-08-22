<template>
  <!-- 用 .come 包起來，和 A.vue 同名容器，方便視覺對齊 -->
  <div class="" style="font-size: 20px">
    <div class="text-medium-emphasis mb-2">共 {{ reviews.length }} 筆評論</div>
    <v-row>
      <!-- 主內容：與 A.vue 左欄對齊（md=8） -->
      <v-col cols="12" md="12">
        <!-- 沒有評論時 -->
        <div v-if="reviews.length === 0" class="text-center py-10">
          <v-icon size="40" color="grey">mdi-comment-alert-outline</v-icon>
          <div class="text-subtitle-1 mt-2">目前沒有評論</div>
        </div>

        <!-- 有評論時 -->
        <template v-else>
          <v-card
            v-for="item in pagedReviews"
            :key="item.reviewId"
            class="mb-5"
            flat
            dense
            style="font-size: 16px"
          >
            <v-card-text>
              <div class="d-flex">
                <div class="d-flex flex-column">
                  <v-card-title class="pa-0" style="font-size: 20px"
                    >{{ maskEmail(item.email) }} :</v-card-title
                  >
                  <v-card-subtitle class="pa-0" style="font-size: 20px">
                    評論日期：{{
                      new Date(item.reviewDate).toLocaleDateString()
                    }}
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

                <!-- 評分放右側（與 A.vue 文字流保持一致） -->
                <v-spacer></v-spacer>
              </div>

              <div class="mt-2" style="font-size: 20px">
                {{ item.cusComm || "（無評論內容）" }}
              </div>
            </v-card-text>
          </v-card>

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
      </v-col>

      <!-- 右側留白（md=4）— 與 A.vue 的右欄寬度一致，保證左欄對齊 -->
      <v-col cols="12" md="4" class="side-placeholder">
        <!-- 可以放統計或空白，暫時留空以保持對齊 -->
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  reviews: {
    type: Array,
    default: () => [],
  },
});

const page = ref(1);
const itemsPerPage = 5;

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
/*
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
    clean: (total.clean / props.reviews.length).toFixed(2),
    comm: (total.comm / props.reviews.length).toFixed(2),
    value: (total.value / props.reviews.length).toFixed(2),
  };
});
*/
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
