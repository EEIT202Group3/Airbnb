<script setup lang="ts">
import { reactive, computed, ref, onMounted } from "vue";
import api from "@/api";
import { useRouter } from "vue-router";

const router = useRouter();
const formRef = ref<any>(null);

const locations = ref<{ locationId: number; name: string; address: string }[]>([]);

const formData = reactive({
  locationId: null as number | null,
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

onMounted(async () => {
  try {
    const res = await api.get("/locations");
    locations.value = res.data ?? [];
  } catch (e) {
    console.error("無法取得地點資料", e);
  }
});

function onSubmit() {
  formRef.value?.validate().then((res: { valid: boolean }) => {
    if (!res?.valid) return;

    const pickupDateTime =
        formData.pickupDate && formData.pickupTime
            ? `${formData.pickupDate}T${formData.pickupTime}`
            : null;

    const returnDateTime =
        formData.returnDate && formData.returnTime
            ? `${formData.returnDate}T${formData.returnTime}`
            : null;

    if (!pickupDateTime || !returnDateTime) return;

    router.push({
      path: "/car-select",
      query: {
        locationId: formData.locationId != null ? String(formData.locationId) : "",
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
    const hh = hour.toString().padStart(2, "0");
    times.push(`${hh}:00`);
    if (hour < 18) times.push(`${hh}:30`);
  }
  return times;
});
</script>

<template>
  <v-container class="py-6">
    <v-sheet class="search-bar pa-4 rounded-lg" elevation="1">
      <v-form ref="formRef" @submit.prevent="onSubmit">
        <v-row align="end" no-gutters>
          <!-- 取車地點 -->
          <v-col cols="12" md="4" class="pr-md-2 pb-2 pb-md-0">
            <v-select
                v-model="formData.locationId"
                :items="locations"
                item-title="name"
                item-value="locationId"
                label="取車地點"
                :rules="[required]"
                clearable
                prepend-inner-icon="mdi-map-marker"
                variant="outlined"
                density="comfortable"
                color="grey-darken-2"
                class="search-field"
            />
          </v-col>

          <!-- 取車日期 -->
          <v-col cols="12" md="2" class="px-md-1 pb-2 pb-md-0">
            <v-text-field
                v-model="formData.pickupDate"
                label="取車日期"
                type="date"
                :min="todayStr"
                :rules="[required]"
                variant="outlined"
                density="comfortable"
                color="grey-darken-2"
                class="search-field"
            />
          </v-col>

          <!-- 取車時間 -->
          <v-col cols="12" md="2" class="px-md-1 pb-2 pb-md-0">
            <v-select
                v-model="formData.pickupTime"
                :items="timeOptions"
                label="取車時間"
                :rules="[required]"
                clearable
                variant="outlined"
                density="comfortable"
                color="grey-darken-2"
                class="search-field"
            />
          </v-col>

          <!-- 還車日期 -->
          <v-col cols="12" md="2" class="px-md-1 pb-2 pb-md-0">
            <v-text-field
                v-model="formData.returnDate"
                label="還車日期"
                type="date"
                :min="formData.pickupDate || todayStr"
                :rules="[required]"
                variant="outlined"
                density="comfortable"
                color="grey-darken-2"
                class="search-field"
            />
          </v-col>

          <!-- 還車時間 -->
          <v-col cols="12" md="2" class="pl-md-1 pb-2 pb-md-0">
            <v-select
                v-model="formData.returnTime"
                :items="timeOptions"
                label="還車時間"
                :rules="[required]"
                clearable
                variant="outlined"
                density="comfortable"
                color="grey-darken-2"
                class="search-field"
            />
          </v-col>

          <v-col cols="12" md="3" class="pt-2 pt-md-0">
            <v-btn type="submit" color="orange-darken-2" class="text-white" size="large" block>
              搜尋
            </v-btn>
          </v-col>
        </v-row>
      </v-form>
    </v-sheet>
  </v-container>
</template>

<style scoped>
.search-bar {
  background: #ffffff;
  border: 1px solid #e0e0e0;
}

:deep(.search-field .v-icon) {
  color: #616161;
}
</style>
