<template>
  <v-container class="py-6">
    <v-card class="mx-auto px-6 py-4" max-width="600" elevation="2">
      <v-card-title class="text-h6 text-center mb-4"> 評論表單 </v-card-title>

      <v-form v-model="valid">
        <v-row dense>
          <v-col cols="12" sm="6">
            <v-text-field v-model="listId" label="ListID" dense></v-text-field>
          </v-col>
          <v-col cols="12" sm="6">
            <v-text-field
              v-model="bookingId"
              label="BookingID"
              dense
            ></v-text-field>
          </v-col>
          <v-col cols="12" sm="6">
            <v-text-field v-model="custId" label="CustID" dense></v-text-field>
          </v-col>
          <v-col cols="12" sm="6">
            <v-text-field v-model="hostId" label="HostID" dense></v-text-field>
          </v-col>
        </v-row>

        <v-divider class="my-4"></v-divider>

        <v-row dense>
          <v-col cols="12" sm="4">
            <div class="text-subtitle-2 font-weight-medium mb-1">乾淨度</div>
            <v-rating
              v-model="cleanScore"
              hover
              :length="5"
              :size="28"
              active-color="yellow-darken-1"
            />
          </v-col>
          <v-col cols="12" sm="4">
            <div class="text-subtitle-2 font-weight-medium mb-1">溝通</div>
            <v-rating
              v-model="commScore"
              hover
              :length="5"
              :size="28"
              active-color="yellow-darken-1"
            />
          </v-col>
          <v-col cols="12" sm="4">
            <div class="text-subtitle-2 font-weight-medium mb-1">性價比</div>
            <v-rating
              v-model="valueScore"
              hover
              :length="5"
              :size="28"
              active-color="yellow-darken-1"
            />
          </v-col>
        </v-row>

        <v-textarea
          v-model="custComm"
          label="文字評論"
          variant="outlined"
          rows="3"
          auto-grow
          clearable
          :counter="200"
          maxlength="200"
          class="my-4"
        ></v-textarea>

        <v-file-input
          v-model="files"
          multiple
          show-size
          accept="image/*"
          label="上傳評論圖片 (最多三張)"
          counter
          :counter-size="3"
          prepend-icon="mdi-camera"
          class="mb-4"
        ></v-file-input>

        <div class="text-center">
          <v-btn color="#e7630b" @click="submit" class="text-white">
            送出
          </v-btn>
        </div>
      </v-form>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const valid = ref(false);
const custComm = ref("");
const cleanScore = ref(1);
const commScore = ref(1);
const valueScore = ref(1);
const listId = ref("");
const bookingId = ref("");
const custId = ref("");
const hostId = ref("");
// const reviewDate = ref("");
const files = ref([]); // 圖片上傳

const submit = async () => {
  const formData = new FormData();
  formData.append("listId", listId.value);
  formData.append("bookingId", bookingId.value);
  formData.append("custId", custId.value);
  formData.append("hostId", hostId.value);
  formData.append("cleanScore", cleanScore.value);
  formData.append("commScore", commScore.value);
  formData.append("valueScore", valueScore.value);
  formData.append("custComm", custComm.value);

  files.value.forEach((file, index) => {
    formData.append(`images`, file); // 多張圖片用相同名稱
  });
  try {
    const res = await axios.post(
      "http://localhost:8080/api/reviews/insert",
      formData,
      {
        withCredentials: true,
      }
    );
    alert("送出成功");
    console.log(formData);

    // 送出後刷新頁面 -> 跳轉至個人的所有評論 ?
    //window.location.reload();
  } catch (err) {
    console.log(formData);
    console.error(err);
    alert("送出失敗");
    // window.location.reload();
  }
};
</script>
<style scoped>
.v-rating {
  justify-content: center;
}
</style>
