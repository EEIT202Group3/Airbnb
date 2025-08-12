<script setup>
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { previewOrder } from "@/api/orderconfirm/preview";
//import ListingCard from "@/components/ListingCard.vue";
import ListingCard from "@/components/payment/ListingCard.vue";
import ListLayout from "@/layouts/ListLayout.vue";
import PreviewConfirm from "./PreviewConfirm.vue";

const router = useRouter();
const route = useRoute();

const listId = ref(route.query.listId ? Number(route.query.listId) : null);

const form = ref({
  listid: listId.value, // 後端 DTO 欄位名就是 listid（小寫）
  checkindate: null, // 'YYYY-MM-DD'
  checkoutdate: null,
  people: 1,
});

const loading = ref(false);
const error = ref("");
const previewData = ref(null); // 若你想在本頁先視覺化 preview，可以先塞起來

async function submitPreview() {
  error.value = "";
  if (
    !form.value.listid ||
    !form.value.checkindate ||
    !form.value.checkoutdate ||
    !form.value.people
  ) {
    error.value = "請完整填寫入住資訊";
    return;
  }
  loading.value = true;
  try {
    const payload = {
      listid: form.value.listid,
      checkindate: form.value.checkindate,
      checkoutdate: form.value.checkoutdate,
      people: Number(form.value.people),
    };
    const data = await PreviewConfirm(payload);
    // 直接把 payload + 後端回傳結果帶去下一頁（也可存在 store）
    router.push({
      name: "PreviewConfirm",
      state: { previewPayload: payload, previewResult: data },
    });
  } catch (e) {
    error.value = e.message || "預覽失敗";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <v-container class="py-6" max-width="900">
    <h2 class="mb-4">我要訂房</h2>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-card>
      <v-card-text>
        <v-row dense>
          <v-col cols="12" md="4">
            <v-text-field
              label="2"
              type="number"
              v-model.number="form.listid"
              hint="2"
              persistent-hint
              required
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              label="入住日"
              v-model="form.checkindate"
              type="date"
              required
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              label="退房日"
              v-model="form.checkoutdate"
              type="date"
              required
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              label="入住人數"
              v-model.number="form.people"
              type="number"
              min="1"
              required
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" :loading="loading" @click="submitPreview">
          預覽訂單
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>
