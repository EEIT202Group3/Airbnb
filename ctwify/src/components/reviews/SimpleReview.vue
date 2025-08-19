<template>
  <div>
    <!-- 沒有評論時 -->
    <div v-if="reviews.length === 0" class="text-center py-10">
      <v-icon size="40" color="grey">mdi-comment-alert-outline</v-icon>
      <div class="text-subtitle-1 mt-2">目前沒有評論</div>
    </div>
    <v-row v-else>
      <v-col v-for="item in pagedReviews" :key="item.reviewId" cols="12">
        <v-card class="mb-1" flat dense>
          <v-row>
            <v-col cols="12">
              <v-card-text>
                <v-card-subtitle>
                  評論日期：{{ new Date(item.reviewDate).toLocaleDateString() }}
                </v-card-subtitle>
                <span
                  ><v-rating
                    :length="5"
                    :size="20"
                    :model-value="
                      item.cleanScore + item.commScore + item.valueScore
                    "
                    active-color="yellow"
                  />
                  {{
                    item.cleanScore + item.commScore + item.valueScore >= 9
                      ? "（非常滿意）"
                      : item.cleanScore + item.commScore + item.valueScore >= 6
                      ? "（滿意）"
                      : "（不滿意）"
                  }}</span
                ><br />
                {{ item.cusComm || "（無評論內容）" }}
              </v-card-text>
              <v-card-title class="text-wrap"> </v-card-title>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
      <v-card-title class="mb-2">共 {{ reviews.length }} 筆評論</v-card-title>
    </v-row>

    <!-- 分頁控制列 -->
    <div class="d-flex justify-between align-center pa-4 w-100">
      <span class="text-caption">
        第 {{ page }} 頁／共 {{ totalPages }} 頁
      </span>
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

    <!-- <v-btn class="mt-4" @click="show">顯示評論數據 </v-btn> -->
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

const scrollToTop = () => {
  window.scrollTo({ top: 50, behavior: "smooth" });
};

const goNextPage = () => {
  if (page.value < totalPages.value) {
    page.value++;
    scrollToTop();
  }
};

const goPrevPage = () => {
  if (page.value > 1) {
    page.value--;
    scrollToTop();
  }
};

const page = ref(1);
const itemsPerPage = 5;

const totalPages = computed(() =>
  Math.ceil(props.reviews.length / itemsPerPage)
);

const pagedReviews = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return Array.from(props.reviews).slice(start, end);
});

const show = () => {
  console.log("子組件的 props.reviews", props.reviews);
};
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
