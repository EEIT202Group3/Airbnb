<template>
  <v-container>
    <v-form v-model="valid">
      <div>
        <v-text-field v-model="listId" label="ListID"></v-text-field>
      </div>
      <div>
        <v-text-field v-model="bookingId" label="BookingID"></v-text-field>
      </div>
      <div>
        <v-text-field v-model="custId" label="CustID"></v-text-field>
      </div>
      <div>
        <v-text-field v-model="hostId" label="HostID"></v-text-field>
      </div>
      <div class="mb-4">
        <div class="text-subtitle-1 font-weight-medium mb-1">乾淨度</div>
        <v-rating
          v-model="cleanScore"
          hover
          :length="5"
          :size="36"
          active-color="light-green-darken-4"
          label="乾淨度"
        />
      </div>
      <div class="mb-4">
        <div class="text-subtitle-1 font-weight-medium mb-1">溝通</div>
        <v-rating
          v-model="commScore"
          hover
          :length="5"
          :size="36"
          active-color="light-green-darken-4"
          label="溝通"
        />
      </div>
      <div class="mb-4">
        <div class="text-subtitle-1 font-weight-medium mb-1">性價比</div>
        <v-rating
          v-model="valueScore"
          hover
          :length="5"
          :size="36"
          active-color="light-green-darken-4"
          label="性價比"
        />
      </div>
      <v-textarea
        v-model="custComm"
        label="文字評論"
        variant="outlined"
        row-height="15"
        rows="1"
        clearable
        no-resize
      ></v-textarea>

      <div class="text-subtitle-1 font-weight-medium mb-1">
        上傳評論圖片 (最多三張)
      </div>
      <v-file-input
        v-model="files"
        multiple
        show-size
        accept="image/*"
        label="上傳評論圖片 (最多三張)"
        counter
        :counter-size="3"
        prepend-icon="mdi-camera"
      ></v-file-input>

      <v-btn @click="submit">送出</v-btn>
    </v-form>
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

<style></style>
