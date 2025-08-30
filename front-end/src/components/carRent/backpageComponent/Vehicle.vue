<script setup lang="ts">
declare global {
  interface Window {
    google: any;
    __gmapsInit__?: () => void;
  }
}

import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import { onMounted, onBeforeUnmount, ref, computed, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";
import api from "@/api";


const showSidebar = ref(false);
const route = useRoute();
const router = useRouter();

const vehicle = ref<any>(null);
const isEditing = ref(false);
const insertVehicleMode = ref(false);
const selectedFile = ref<File | null>(null);
const currentTab = ref<'summary' | 'image' | 'geo'>('summary');

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await api.get(`vehicles/${id}`);
    vehicle.value = res.data;
    if (currentTab.value === "geo") {
      await ensureMap();
      await ensureAutocomplete();
    }
  } catch (err) {
    console.error("載入車輛資料失敗：", err);
  }
});


const emptyVehicle = {
  vehicleId: "",
  plateNo: "",
  brand: "",
  model: "",
  color: "",
  fuelType: "",
  transmission: "",
  seatCapacity: null as number | null,
  fuelCapacity: null as number | null,
  vehicleTax: null as number | null,
  dailyRent: null as number | null,
  mileage: null as number | null,
  latitude: null as number | null,
  longitude: null as number | null,
  status: "",
  image: "",
  location: "",
};

// 新增修改刪除
const startInsertVehicle = () => {
  vehicle.value = { ...emptyVehicle };
  insertVehicleMode.value = true;
  isEditing.value = true;
};

const insertVehicle = async () => {
  try {
    const res = await api.post(`/vehicles/insert`, vehicle.value);

    // 成功提示
    Swal.fire({
      toast: true,
      position: "top",
      icon: "success",
      title: "新增成功！",
      showConfirmButton: false,
      timer: 2000,
    });

    const newId = res.data.vehicleId;
    insertVehicleMode.value = false;
    isEditing.value = false;
    await router.push(`/car-rent/vehicles/${newId}`);
  } catch (err: any) {
    const msg =
        "新增失敗：車牌號碼重複";

    // 失敗提示
    Swal.fire({
      toast: true,
      position: "top",
      icon: "error",
      title: `${msg}`,
      showConfirmButton: false,
      timer: 2000,
    });
  }
};

const saveVehicle = async () => {
  try {
    await api.put(`/vehicles/update`, vehicle.value);
    alert("修改成功！");
    isEditing.value = false;
  } catch (err: any) {
    const msg = err?.response?.data?.message || "更新失敗，請稍後再試";
    alert(msg);
  }
};

const deleteVehicle = async () => {
  if (!confirm("確定要刪除此車輛嗎？")) return;
  try {
    await api.delete(`/vehicles/delete/${vehicle.value.vehicleId}`);
    alert("刪除成功！");
    await router.push("/car-rent/back-homepage");
  } catch (err) {
    alert("刪除失敗：" + err);
  }
};


// 圖片上傳
const previewUrl = ref<string | null>(null);
watch(selectedFile, (f, prev) => {
  if (prev && previewUrl.value) URL.revokeObjectURL(previewUrl.value);
  previewUrl.value = f ? URL.createObjectURL(f) : null;
});

const submitForm = async () => {
  if (!selectedFile.value) {
    alert("請先選擇圖片！");
    return;
  }
  const okType = /^image\/(png|jpe?g|webp|gif)$/i.test(selectedFile.value.type);
  const okSize = selectedFile.value.size <= 5 * 1024 * 1024;
  if (!okType) return alert("只允許 png/jpg/jpeg/webp/gif 圖片");
  if (!okSize) return alert("圖片大小需 ≤ 5MB");

  const formData = new FormData();
  formData.append("image", selectedFile.value);

  try {
    const res = await api.post("/vehicles/upload-image", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    vehicle.value.image = res.data.filename;
    selectedFile.value = null;
    alert("圖片上傳成功！");
  } catch (err) {
    alert("圖片上傳失敗：" + err);
  }
};


const activeField = ref<string | null>(null);
const isInlineEditable = computed(() => isEditing.value || insertVehicleMode.value);
watch([isEditing, insertVehicleMode], () => (activeField.value = null));


const mapEl = ref<HTMLDivElement | null>(null);
const addressField = ref<any>(null); // Vuetify v-text-field 元件

let map: any = null;
let marker: any = null;
let geocoder: any = null;
let autocomplete: any = null;

let gmapsLoadingPromise: Promise<void> | null = null;

const GOOGLE_MAPS_API_KEY = "AIzaSyBGVQM-9yFjzutFDREdhglC4Wzm39Tc_x0";
const MAP_ID: string | undefined = undefined;

const canUseAdvancedMarker = () =>
    Boolean(MAP_ID && window.google?.maps?.marker?.AdvancedMarkerElement);

const loadGoogleMaps = (): Promise<void> => {
  if (!GOOGLE_MAPS_API_KEY) {
    const err = new Error("Google Maps API key 未設定（GOOGLE_MAPS_API_KEY 為空）");
    console.error(err);
    return Promise.reject(err);
  }
  if (window.google?.maps?.Map || window.google?.maps?.importLibrary) return Promise.resolve();
  if (gmapsLoadingPromise) return gmapsLoadingPromise;

  gmapsLoadingPromise = new Promise((resolve, reject) => {
    const existed = document.querySelector<HTMLScriptElement>('script[data-gmaps-loader="true"]');
    if (existed) {
      if ((window as any).__gmapsInit__ === undefined && window.google?.maps) return resolve();
      existed.addEventListener("load", () => resolve(), { once: true });
      existed.addEventListener("error", () => reject(new Error("Google Maps 載入失敗")), { once: true });
      return;
    }

    window.__gmapsInit__ = () => resolve();

    const s = document.createElement("script");
    s.src = `https://maps.googleapis.com/maps/api/js?key=${GOOGLE_MAPS_API_KEY}&libraries=places,marker&language=zh-TW&region=TW&v=weekly&callback=__gmapsInit__`;
    s.async = true;
    s.defer = true;
    s.dataset.gmapsLoader = "true";
    s.onerror = () => reject(new Error("Google Maps 載入失敗"));
    document.head.appendChild(s);
  });

  return gmapsLoadingPromise;
};

const ensureLib = async (name: "maps" | "marker" | "places") => {
  const il = window.google?.maps?.importLibrary;
  if (typeof il === "function") {
    try { return await il(name as any); } catch { /* ignore */ }
  }
  return null;
};

const hasAdvancedMarker = () => !!window.google?.maps?.marker?.AdvancedMarkerElement;

const getMarkerPosition = (): { lat: number; lng: number } | null => {
  if (!marker) return null;
  if (hasAdvancedMarker() && "position" in marker) {
    const p = marker.position;
    if (!p) return null;
    if (typeof p.lat === "number") return { lat: p.lat, lng: p.lng };
    if (typeof p.lat === "function") return { lat: p.lat(), lng: p.lng() };
  }
  if (typeof marker.getPosition === "function") {
    const p = marker.getPosition();
    if (!p) return null;
    return { lat: p.lat(), lng: p.lng() };
  }
  return null;
};

const setMarkerPosition = (pos: { lat: number; lng: number }) => {
  if (!marker) return;
  if (hasAdvancedMarker() && "position" in marker) {
    marker.position = pos;
  } else if (typeof marker.setPosition === "function") {
    marker.setPosition(pos);
  }
};

const getCenterFromVehicle = () => {
  const lat = Number(vehicle.value?.latitude);
  const lng = Number(vehicle.value?.longitude);
  if (Number.isFinite(lat) && Number.isFinite(lng)) return { lat, lng };
  return { lat: 25.033964, lng: 121.564468 }; // 台北101
};

const reverseGeocode = (pos: { lat: number; lng: number }): Promise<string> => {
  if (!geocoder) geocoder = new window.google.maps.Geocoder();
  return new Promise((resolve, reject) => {
    geocoder.geocode({ location: pos }, (results: any, status: string) => {
      if (status === "OK" && results && results.length) {
        resolve(results[0].formatted_address as string);
      } else if (status === "ZERO_RESULTS") {
        resolve("找不到對應地址");
      } else {
        reject(new Error(`Geocoder 失敗：${status}`));
      }
    });
  });
};

const geocodeAddressFromInput = async () => {
  const addr = (vehicle.value?.location || "").trim();
  if (!addr) return alert("請先輸入地址");
  await loadGoogleMaps();
  geocoder ||= new window.google.maps.Geocoder();
  geocoder.geocode({ address: addr, componentRestrictions: { country: "TW" } }, (results: any, status: string) => {
    if (status === "OK" && results && results.length) {
      const r = results[0];
      const pos = { lat: r.geometry.location.lat(), lng: r.geometry.location.lng() };
      vehicle.value.location = r.formatted_address;
      vehicle.value.latitude = pos.lat;
      vehicle.value.longitude = pos.lng;
      if (marker && map) {
        setMarkerPosition(pos);
        map.setCenter(pos);
        map.setZoom(16);
      }
    } else if (status === "ZERO_RESULTS") {
      alert("找不到相符地址");
    } else {
      alert(`地址查詢失敗：${status}`);
    }
  });
};

const ensureMap = async () => {
  if (currentTab.value !== "geo") return;
  await nextTick();
  if (!mapEl.value) return;

  await loadGoogleMaps();

  let MapCtor: any = window.google?.maps?.Map;
  if (typeof window.google?.maps?.importLibrary === "function") {
    const lib = await ensureLib("maps");
    if (lib?.Map) MapCtor = lib.Map;
  }
  if (!MapCtor) {
    console.error("Google Maps SDK 未就緒：Map 類別不存在");
    return;
  }

  const center = getCenterFromVehicle();
  const mapOptions: any = { center, zoom: 14 };
  if (MAP_ID) mapOptions.mapId = MAP_ID;

  if (!map) {
    map = new MapCtor(mapEl.value, mapOptions);

    let AdvancedMarkerCtor: any = null;
    if (canUseAdvancedMarker()) {
      AdvancedMarkerCtor = window.google?.maps?.marker?.AdvancedMarkerElement
          || (await ensureLib("marker"))?.AdvancedMarkerElement
          || null;
    }

    if (AdvancedMarkerCtor) {
      marker = new AdvancedMarkerCtor({
        map,
        position: center,
        gmpDraggable: true,
        title: vehicle.value?.plateNo || "車輛位置",
      });

      marker.addListener("dragend", async () => {
        const p = marker.position;
        const pos = typeof p?.lat === "function" ? { lat: p.lat(), lng: p.lng() } : p;
        if (!pos) return;
        vehicle.value.latitude = pos.lat;
        vehicle.value.longitude = pos.lng;
        try { vehicle.value.location = await reverseGeocode(pos); } catch (e) { console.error(e); }
      });
    } else {
      marker = new window.google.maps.Marker({
        map,
        position: center,
        draggable: true,
        title: vehicle.value?.plateNo || "車輛位置",
      });
      marker.addListener("dragend", async (e: any) => {
        if (!e?.latLng) return;
        const pos = { lat: e.latLng.lat(), lng: e.latLng.lng() };
        vehicle.value.latitude = pos.lat;
        vehicle.value.longitude = pos.lng;
        try { vehicle.value.location = await reverseGeocode(pos); } catch (err) { console.error(err); }
      });
    }

    map.addListener("click", async (e: any) => {
      if (!e.latLng) return;
      const pos = { lat: e.latLng.lat(), lng: e.latLng.lng() };
      if (marker?.position !== undefined) marker.position = pos;
      else if (typeof marker?.setPosition === "function") marker.setPosition(pos);
      map.panTo(pos);
      vehicle.value.latitude = pos.lat;
      vehicle.value.longitude = pos.lng;
      try { vehicle.value.location = await reverseGeocode(pos); } catch (err) { console.error(err); }
    });

    setTimeout(() => {
      if (!map) return;
      window.google.maps.event.trigger(map, "resize");
      map.setCenter(center);
    }, 0);
  } else {
    map.setOptions(mapOptions);
    map.setCenter(center);
    if (marker?.position !== undefined) marker.position = center;
    else if (typeof marker?.setPosition === "function") marker.setPosition(center);
  }
};

const ensureAutocomplete = async () => {
  if (currentTab.value !== "geo") return;
  await loadGoogleMaps();
  await nextTick();
  if (!addressField.value) return;

  await ensureLib("places");

  const inputEl = (addressField.value.$el?.querySelector?.("input") || null) as HTMLInputElement | null;
  if (!inputEl) return;

  const anyInput = inputEl as any;
  if (!anyInput._onEnterBound) {
    inputEl.addEventListener("keydown", (e: KeyboardEvent) => {
      if (e.key !== "Enter") return;
      const pac = document.querySelector(".pac-container") as HTMLElement | null;
      const isOpen = !!pac && pac.offsetParent !== null && pac.offsetHeight > 0;
      if (isOpen) {
        return;
      }
      e.preventDefault();
      geocodeAddressFromInput();
    });
    anyInput._onEnterBound = true;
  }

  inputEl.setAttribute("autocomplete", "off");

  if (autocomplete) return;

  if (window.google?.maps?.places?.Autocomplete) {
    autocomplete = new window.google.maps.places.Autocomplete(inputEl, {
      fields: ["formatted_address", "geometry", "place_id", "name"],
      types: ["address"],
      componentRestrictions: { country: ["tw"] },
    });

    const updateBounds = () => {
      if (!map) return;
      const b = map.getBounds?.();
      if (b) autocomplete!.setBounds(b);
    };
    updateBounds();
    map?.addListener("bounds_changed", updateBounds);

    autocomplete.addListener("place_changed", () => {
      const place = autocomplete!.getPlace();
      const addr = place.formatted_address || place.name || "";
      if (place.geometry?.location) {
        const pos = { lat: place.geometry.location.lat(), lng: place.geometry.location.lng() };
        vehicle.value.location = addr;
        vehicle.value.latitude = pos.lat;
        vehicle.value.longitude = pos.lng;

        if (marker?.position !== undefined) marker.position = pos;                 // AdvancedMarker
        else if (typeof marker?.setPosition === "function") marker.setPosition(pos); // 舊 Marker

        map?.setCenter(pos);
        map?.setZoom(16);
      } else if (place.place_id) {
        geocoder ||= new window.google.maps.Geocoder();
        geocoder.geocode({ placeId: place.place_id }, (results: any, status: string) => {
          if (status === "OK" && results && results.length) {
            const r = results[0];
            const pos = { lat: r.geometry.location.lat(), lng: r.geometry.location.lng() };
            vehicle.value.location = r.formatted_address;
            vehicle.value.latitude = pos.lat;
            vehicle.value.longitude = pos.lng;

            if (marker?.position !== undefined) marker.position = pos;
            else if (typeof marker?.setPosition === "function") marker.setPosition(pos);

            map?.setCenter(pos);
            map?.setZoom(16);
          }
        });
      }
    });
  }
};

watch(currentTab, async (tab) => {
  if (tab === "geo") {
    await nextTick();
    await ensureMap();
    await ensureAutocomplete();
    setTimeout(() => {
      if (map && window.google?.maps?.event) {
        window.google.maps.event.trigger(map, "resize");
        map.setCenter(getCenterFromVehicle());
      }
    }, 0);
  }
});

watch(
    () => [vehicle.value?.latitude, vehicle.value?.longitude],
    ([lat, lng]) => {
      if (!map || !marker) return;
      const nlat = Number(lat);
      const nlng = Number(lng);
      if (Number.isFinite(nlat) && Number.isFinite(nlng)) {
        const pos = { lat: nlat, lng: nlng };
        setMarkerPosition(pos);
        map.setCenter(pos);
      }
    }
);

const refreshAddressFromLatLng = async () => {
  const lat = Number(vehicle.value?.latitude);
  const lng = Number(vehicle.value?.longitude);
  if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
    alert("請先輸入有效的緯度與經度");
    return;
  }
  try {
    const addr = await reverseGeocode({ lat, lng });
    vehicle.value.location = addr;
  } catch (e) {
    console.error(e);
    alert("反查地址失敗");
  }
};

onBeforeUnmount(() => {
  map = null;
  marker = null;
  geocoder = null;
  autocomplete = null;
});

// 測試資料
const fillTestDriver = () => {
  vehicle.value = {
    ...vehicle.value,
    plateNo: "ABC-1234",
    brand: "TOYOTA",
    model: "Corolla Altis",
    color: "白",
    fuelType: "汽油",
    transmission: "自排",
    status: "可租用",
    seatCapacity: 5,
    fuelCapacity: 50,
    vehicleTax: 8640,
    dailyRent: 4500,
    mileage: 3500,
    location: "台北市信義區信義路五段7號",
  };
};
</script>



<template>
  <div v-if="vehicle">
    <v-container fluid class="px-1 py-1 main-content">
      <!-- 功能選單按鈕 -->
      <v-btn
          variant="outlined"
          class="ma-0"
          prepend-icon="mdi-menu"
          @click="showSidebar = true"
      >
        功能選單
      </v-btn>
      <Sidebar :visible="showSidebar" :close="() => (showSidebar = false)"/>

      <v-card class="pa-4 bg-white" elevation="1">
        <!-- 標題 + 操作區 -->
        <div class="d-flex align-center justify-space-between mb-3">
          <div class="text-h5 font-weight-bold">車輛資訊</div>

          <div class="d-flex flex-wrap" style="gap:.5rem;">
            <v-btn
                v-if="isEditing && !insertVehicleMode"
                color="primary"
                size="small"
                prepend-icon="mdi-content-save"
                @click="saveVehicle"
            >儲存
            </v-btn>

            <v-btn
                v-if="!insertVehicleMode && !isEditing"
                variant="outlined"
                color="success"
                size="small"
                prepend-icon="mdi-plus"
                @click="startInsertVehicle"
            >新增車輛
            </v-btn>

            <v-btn
                v-if="insertVehicleMode"
                color="primary"
                size="small"
                prepend-icon="mdi-check-bold"
                @click="insertVehicle"
            >確定新增
            </v-btn>

            <v-btn
                v-if="insertVehicleMode"
                variant="outlined"
                size="small"
                prepend-icon="mdi-close"
                @click="insertVehicleMode = false; isEditing = false"
            >取消
            </v-btn>

            <v-btn
                v-if="!insertVehicleMode"
                variant="outlined"
                size="small"
                prepend-icon="mdi-pencil"
                @click="isEditing = !isEditing"
            >{{ isEditing ? "取消編輯" : "編輯" }}
            </v-btn>

            <v-btn
                v-if="!insertVehicleMode"
                color="error"
                size="small"
                prepend-icon="mdi-delete"
                @click="deleteVehicle"
            >刪除車輛
            </v-btn>
          </div>
        </div>

        <!-- 分頁 -->
        <v-tabs v-model="currentTab" density="comfortable" color="primary">
          <v-tab value="summary">摘要</v-tab>
          <v-tab value="image">圖片</v-tab>
          <v-tab value="geo">位置</v-tab>
        </v-tabs>

        <v-window v-model="currentTab" class="mt-3">
          <!-- 摘要 -->
          <v-window-item value="summary">
            <v-row dense align="stretch">
              <!-- 基本資料 -->
              <v-col cols="12" md="6" class="d-flex">
                <v-card class="bg-white flex-grow-1" elevation="1">
                  <v-card-title class="text-subtitle-1">
                    基本資料
                    <v-btn class="ml-5" size="small" color="primary" variant="outlined" @click="fillTestDriver">
                      測試填入
                    </v-btn>
                  </v-card-title>

                  <v-card-text>
                    <v-table density="compact" class="text-no-wrap">
                      <colgroup>
                        <col class="label-col"/>
                        <col/>
                      </colgroup>
                      <tbody>
                      <tr>
                        <td class="label-cell">車輛編號</td>
                        <td>
                          <div class="fe-wrapper disabled">
                            <div class="fe-display">{{ vehicle.vehicleId ?? '—' }}</div>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">車牌</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'plateNo')"
                                @click="isInlineEditable && (activeField = 'plateNo')"
                            >{{ vehicle.plateNo || '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'plateNo'"
                                v-model="vehicle.plateNo"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">品牌</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'brand')"
                                @click="isInlineEditable && (activeField = 'brand')"
                            >{{ vehicle.brand || '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'brand'"
                                v-model="vehicle.brand"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">型號</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'model')"
                                @click="isInlineEditable && (activeField = 'model')"
                            >{{ vehicle.model || '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'model'"
                                v-model="vehicle.model"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">顏色</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'color')"
                                @click="isInlineEditable && (activeField = 'color')"
                            >{{ vehicle.color || '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'color'"
                                v-model="vehicle.color"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">燃料類型</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'fuelType')"
                                @click="isInlineEditable && (activeField = 'fuelType')"
                            >{{ vehicle.fuelType || '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'fuelType'"
                                v-model="vehicle.fuelType"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>


                      </tbody>
                    </v-table>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col cols="12" md="6" class="d-flex">
                <v-card class="bg-white flex-grow-1" elevation="1">
                  <v-card-title class="text-subtitle-1">價格 / 規格</v-card-title>
                  <v-card-text>
                    <v-table density="compact" class="text-no-wrap">
                      <colgroup>
                        <col class="label-col"/>
                        <col/>
                      </colgroup>
                      <tbody>
                      <tr>
                        <td class="label-cell">租金（日）</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'dailyRent')"
                                @click="isInlineEditable && (activeField = 'dailyRent')"
                            >{{ vehicle.dailyRent ?? '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'dailyRent'"
                                v-model.number="vehicle.dailyRent"
                                type="number"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">稅金</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'vehicleTax')"
                                @click="isInlineEditable && (activeField = 'vehicleTax')"
                            >{{ vehicle.vehicleTax ?? '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'vehicleTax'"
                                v-model.number="vehicle.vehicleTax"
                                type="number"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">里程</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'mileage')"
                                @click="isInlineEditable && (activeField = 'mileage')"
                            >{{ vehicle.mileage ?? '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'mileage'"
                                v-model.number="vehicle.mileage"
                                type="number"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">座位數</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'seatCapacity')"
                                @click="isInlineEditable && (activeField = 'seatCapacity')"
                            >{{ vehicle.seatCapacity ?? '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'seatCapacity'"
                                v-model.number="vehicle.seatCapacity"
                                type="number"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">油箱容量</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'fuelCapacity')"
                                @click="isInlineEditable && (activeField = 'fuelCapacity')"
                            >{{ vehicle.fuelCapacity ?? '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'fuelCapacity'"
                                v-model.number="vehicle.fuelCapacity"
                                type="number"
                                step="0.1"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">傳動系統</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'transmission')"
                                @click="isInlineEditable && (activeField = 'transmission')"
                            >{{ vehicle.transmission || '—' }}
                            </div>
                            <v-text-field
                                v-show="isInlineEditable && activeField === 'transmission'"
                                v-model="vehicle.transmission"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">狀態</td>
                        <td>
                          <div class="fe-wrapper" :class="{ 'is-editable': isInlineEditable }">
                            <div
                                class="fe-display"
                                v-show="!(isInlineEditable && activeField === 'status')"
                                @click="isInlineEditable && (activeField = 'status')"
                            >{{ vehicle.status || '—' }}
                            </div>
                            <v-select
                                v-show="isInlineEditable && activeField === 'status'"
                                v-model="vehicle.status"
                                :items="[
                                  { title: '可租用', value: '可租用' },
                                  { title: '維修中', value: '維修中' },
                                  { title: '下架', value: '下架' }
                                ]"
                                density="comfortable"
                                variant="outlined"
                                hide-details
                                class="fe-input"
                                @blur="activeField = null"
                            />
                          </div>
                        </td>
                      </tr>

                      </tbody>
                    </v-table>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-window-item>

          <!-- 圖片 -->
          <v-window-item value="image">
            <v-row dense>
              <v-col cols="12" md="7">
                <v-card elevation="1" class="pa-4 d-flex align-center justify-center">
                  <v-img
                      :src="previewUrl || (vehicle.image ? `/carPicture/${vehicle.image}` : undefined)"
                      width="300"
                      height="300"
                      :cover="false"
                      class="rounded"
                  >
                    <template #placeholder>
                      <div class="d-flex fill-height align-center justify-center text-medium-emphasis">
                        無圖片
                      </div>
                    </template>
                  </v-img>
                </v-card>
              </v-col>

              <v-col cols="12" md="5">
                <v-card elevation="1">
                  <v-card-title class="text-subtitle-1">圖片上傳</v-card-title>
                  <v-card-text>
                    <v-file-input
                        v-model="selectedFile"
                        :multiple="false"
                        accept="image/*"
                        clearable
                        show-size
                        prepend-icon="mdi-image"
                        :disabled="!isInlineEditable"
                        density="comfortable"
                        variant="outlined"
                        label="選擇圖片"
                        hint="支援單一圖片上傳"
                        persistent-hint
                    />
                    <v-btn
                        class="mt-3"
                        color="primary"
                        :disabled="!isInlineEditable"
                        prepend-icon="mdi-upload"
                        @click="submitForm"
                    >上傳圖片
                    </v-btn>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-window-item>

          <!-- 位置 -->
          <v-window-item value="geo">
            <v-card elevation="1">
              <v-card-title class="text-subtitle-1">位置資訊</v-card-title>
              <v-card-text>
                <v-row dense>
                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model.number="vehicle.latitude"
                        :readonly="!isInlineEditable"
                        label="緯度"
                        type="number"
                        variant="outlined"
                        density="comfortable"
                        hide-details
                    />
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model.number="vehicle.longitude"
                        :readonly="!isInlineEditable"
                        label="經度"
                        type="number"
                        variant="outlined"
                        density="comfortable"
                        hide-details
                    />
                  </v-col>
                </v-row>

                <v-row dense class="mt-1">
                  <v-col cols="12">
                    <v-text-field
                        ref="addressField"
                        v-model="vehicle.location"
                        :readonly="!isInlineEditable"
                        label="地址（可輸入並選建議項）"
                        variant="outlined"
                        density="comfortable"
                        hide-details
                        prepend-inner-icon="mdi-map-marker"
                        append-inner-icon="mdi-magnify"
                        @focus="ensureAutocomplete"
                        @click:append-inner="geocodeAddressFromInput"
                        @keydown.enter.prevent="geocodeAddressFromInput"
                    />
                  </v-col>

                  <v-col cols="12" class="d-flex" style="gap:.5rem;">
                    <v-btn
                        variant="outlined"
                        size="small"
                        prepend-icon="mdi-map-search"
                        :disabled="!isInlineEditable"
                        @click="refreshAddressFromLatLng"
                    >
                      以經緯度反查地址
                    </v-btn>
                    <v-btn
                        variant="outlined"
                        size="small"
                        prepend-icon="mdi-magnify"
                        :disabled="!isInlineEditable"
                        @click="geocodeAddressFromInput"
                    >
                      以地址查座標
                    </v-btn>
                  </v-col>
                </v-row>

                <div ref="mapEl" class="gmap"></div>
                <div class="text-medium-emphasis mt-2">
                  提示：切到「編輯」模式後，可輸入地址並選擇建議、或按 Enter 搜尋；亦可拖曳圖標或點地圖更新位置並自動回填地址。
                </div>
              </v-card-text>
            </v-card>
          </v-window-item>
        </v-window>
      </v-card>
    </v-container>
  </div>

  <div v-else class="text-center mt-5">
    <v-progress-circular indeterminate/>
    <div class="mt-3">載入中...</div>
  </div>
</template>

<style scoped>
.bg-white {
  background-color: #fff !important;
}

.label-col {
  width: 140px;
}

.label-cell {
  color: rgba(0, 0, 0, .6);
  font-weight: 500;
  text-align: right;
  padding-right: 12px;
  white-space: nowrap;
  vertical-align: middle;
}

:deep(.v-table .v-table__wrapper table td),
:deep(.v-table .v-table__wrapper table th) {
  border-bottom: 1px solid rgba(0, 0, 0, .06);
}

.fe-wrapper {
  display: block;
  width: 100%;
}

.fe-input {
  width: 100%;
}

.fe-display {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  min-height: 56px;
  background: transparent;
  font-size: 14px;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.is-editable .fe-display {
  cursor: pointer;
}

.fe-wrapper.disabled .fe-display {
  color: rgba(0, 0, 0, .45);
}

.main-content {
  margin: auto;
  padding: 20px;
}

.gmap {
  width: 100%;
  height: 360px;
  border-radius: 8px;
  background: #f5f5f5;
  overflow: hidden;
}

</style>
<style>
.pac-container {
  z-index: 3000 !important;
}
</style>
