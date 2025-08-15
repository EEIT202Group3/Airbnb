<script setup lang="ts">
import { reactive, computed, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const formRef = ref<any>(null);

const formData = reactive({
  location: "",
  pickupDate: "",
  pickupTime: "",
  returnDate: "",
  returnTime: "",
  ageChecked: true,
});

const required = (v: any) => (!!v ? true : "必填");

const todayStr = computed(() => {
  const d = new Date();
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
});

function onSubmit() {
  // Vuetify 3 的 validate() 會回傳 { valid: boolean }
  formRef.value?.validate().then((res: { valid: boolean }) => {
    if (!res?.valid) return;

    const pickupDateTime =
        formData.pickupDate && formData.pickupTime
            ? formData.pickupDate + "T" + formData.pickupTime
            : null;
    const returnDateTime =
        formData.returnDate && formData.returnTime
            ? formData.returnDate + "T" + formData.returnTime
            : null;

    if (!pickupDateTime || !returnDateTime) return;

    router.push({
      path: "/car-select",
      query: {
        location: formData.location,
        pickupDateTime,
        returnDateTime,
        ageChecked: formData.ageChecked ? "true" : "false",
      },
    });
  });
}

const timeOptions = computed(() => {
  const times: string[] = [];
  for (let hour = 9; hour <= 18; hour++) {
    times.push(`${hour.toString().padStart(2, "0")}:00`);
    if (hour < 18) times.push(`${hour.toString().padStart(2, "0")}:30`);
  }
  return times;
});
</script>

<template>
  <v-container class="py-6">
    <v-sheet
        color="grey-darken-4"
        theme="dark"
        class="pa-4 rounded-lg"
        elevation="2"
    >
      <v-form ref="formRef" @submit.prevent="onSubmit">
        <v-row align="end" no-gutters>
          <v-col cols="12" md="4" class="pr-md-2 pb-2 pb-md-0">
            <v-text-field
                v-model="formData.location"
                label="取車地點"
                placeholder="輸入城市或地區"
                clearable
                prepend-inner-icon="mdi-map-marker"
                variant="outlined"
                density="comfortable"
            />
          </v-col>

          <v-col cols="12" md="2" class="px-md-1 pb-2 pb-md-0">
            <v-text-field
                v-model="formData.pickupDate"
                label="取車日期"
                type="date"
                :min="todayStr"
                :rules="[required]"
                variant="outlined"
                density="comfortable"
            />
          </v-col>

          <v-col cols="12" md="2" class="px-md-1 pb-2 pb-md-0">
            <v-select
                v-model="formData.pickupTime"
                :items="timeOptions"
                label="取車時間"
                :rules="[required]"
                clearable
                variant="outlined"
                density="comfortable"
            />
          </v-col>

          <v-col cols="12" md="2" class="px-md-1 pb-2 pb-md-0">
            <v-text-field
                v-model="formData.returnDate"
                label="還車日期"
                type="date"
                :min="formData.pickupDate || todayStr"
                :rules="[required]"
                variant="outlined"
                density="comfortable"
            />
          </v-col>

          <v-col cols="12" md="2" class="pl-md-1 pb-2 pb-md-0">
            <v-select
                v-model="formData.returnTime"
                :items="timeOptions"
                label="還車時間"
                :rules="[required]"
                clearable
                variant="outlined"
                density="comfortable"
            />
          </v-col>

          <v-col cols="12" md="3" class="pt-2 pt-md-0">
            <v-checkbox
                v-model="formData.ageChecked"
                label="駕駛年齡介於 25 - 70"
                color="primary"
                hide-details
                class="mb-2"
            />
            <v-btn type="submit" color="primary" size="large" block>搜尋</v-btn>
          </v-col>
        </v-row>
      </v-form>
    </v-sheet>
  </v-container>
</template>

<style scoped>
</style>
