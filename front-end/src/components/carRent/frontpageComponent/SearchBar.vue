<script setup lang="ts">
import {reactive, computed} from "vue";
import {useRouter} from "vue-router";

const router = useRouter()
const formData = reactive({
  location: '',
  pickupDate: '',
  pickupTime: '',
  returnDate: '',
  returnTime: '',
  ageChecked: true
})

function onSubmit() {
  router.push({
    path: '/car-rent/car-Select',
    query: {
      location: formData.location,
      pickupDate: formData.pickupDate,
      pickupTime: formData.pickupTime,
      returnDate: formData.returnDate,
      returnTime: formData.returnTime,
      ageChecked: formData.ageChecked ? 'true' : 'false'
    }
  })
}

const timeOptions = computed(function () {
  const times: string[] = []
  for (let hour = 9; hour <= 18; hour++) {
    times.push(`${hour.toString().padStart(2, '0')}:00`);
    if (hour < 18) {
      times.push(`${hour.toString().padStart(2, '0')}:30`);
    }
  }
  return times;
})

</script>

<template>
  <!--  搜尋欄-->
  <section class="bg-cover text-white py-5">
    <div class="container bg-dark rounded p-4">
      <form class="row g-2 align-items-end" @submit.prevent="onSubmit">
        <div class="col-md-4">
          <label for="location" class="form-label text-white">取車地點</label>
          <input type="text" class="form-control" id="location" name="location" placeholder="輸入城市或地區"
                 v-model="formData.location">
        </div>
        <div class="col-md-2">
          <label for="pickupDate" class="form-label text-white">取車日期</label>
          <input type="date" class="form-control" id="pickupDate" name="pickupDate" v-model="formData.pickupDate">
        </div>
        <div class="col-md-2">
          <label for="pickupTime" class="form-label text-white">取車時間</label>
          <select class="form-select" id="pickupTime" name="pickupTime" v-model="formData.pickupTime">
            <option selected value="" disabled>請選擇時間</option>
            <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
          </select>
        </div>
        <div class="col-md-2">
          <label for="returnDate" class="form-label text-white">還車日期</label>
          <input type="date" class="form-control" id="returnDate" name="returnDate" v-model="formData.returnDate">
        </div>
        <div class="col-md-2">
          <label for="returnTime" class="form-label text-white">還車時間</label>
          <select class="form-select" id="returnTime" name="returnTime" v-model="formData.returnTime">
            <option selected value="" disabled>請選擇時間</option>
            <option v-for="time in timeOptions" :key="time" :value="time">{{ time }}</option>
          </select>
        </div>
        <div class="col-md-3 d-flex flex-column">
          <div class="form-check text-white mb-2">
            <input class="form-check-input" type="checkbox" id="ageCheck" checked v-model="formData.ageChecked">
            <label class="form-check-label" for="ageCheck">駕駛年齡介於 25 - 70</label>
          </div>
          <button type="submit" class="btn btn-primary">搜尋</button>
        </div>
      </form>
    </div>
  </section>
</template>

<style scoped>

</style>