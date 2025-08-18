<script setup lang="ts">
const showSidebar = ref(false);
import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import {onMounted, ref, computed, watch, onBeforeUnmount} from "vue";
import {useRoute, useRouter} from "vue-router";
import api from "@/api";

type Damage = {
  damageId: number;
  reservationId: number;
  imageUrl?: string | null;
  damageNote?: string | null;
  reportDate?: string | null;
};

const route = useRoute();
const router = useRouter();
const reservation = ref<any>({
  license: "",
  driverName: "",
  driverPhone: "",
  driverEmail: "",
  driverAge: "",
  pickupDate: "",
  returnDate: "",
  totalAmount: "",
  vehicleId: "",
  location: "",
  status: ""
});

const damages = ref<Damage[]>([]);
const damageLoading = ref(false);

const newDamageNote = ref("");
const newDamageFiles = ref<File[]>([]);
const previewUrls = ref<string[]>([]);
const uploadingDamage = ref(false);

const editingDamageId = ref<number | null>(null);
const editingNote = ref("");

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await api.get(`reservations/${id}`);
    reservation.value = res.data;
  } catch (err) {
    console.error("載入預約資料失敗：", err);
  }
  await loadDamages();
});

// 讀取車輛損壞
async function loadDamages() {
  try {
    damageLoading.value = true;
    const id = route.params.id;
    const {data} = await api.get<Damage[]>(`reservations/${id}/damages`);
    damages.value = data ?? [];
  } catch (e) {
    console.error("載入損壞紀錄失敗：", e);
  } finally {
    damageLoading.value = false;
  }
}

// 新增損壞紀錄
function onChooseFiles(files: File[] | undefined) {
  clearPreviews();
  newDamageFiles.value = Array.from(files ?? []);
  previewUrls.value = newDamageFiles.value.map(f => URL.createObjectURL(f));
}

function clearNewDamageForm() {
  newDamageNote.value = "";
  newDamageFiles.value = [];
  clearPreviews();
}

function clearPreviews() {
  previewUrls.value.forEach(u => URL.revokeObjectURL(u));
  previewUrls.value = [];
}

onBeforeUnmount(clearPreviews);

async function createDamage() {
  const rid = reservation.value?.reservationId ?? route.params.id;
  if (!rid) return;

  try {
    uploadingDamage.value = true;
    const fd = new FormData();
    fd.append("note", newDamageNote.value ?? "");
    for (const f of newDamageFiles.value) fd.append("images", f);

    const {data} = await api.post<Damage[]>(`/reservations/${rid}/damages`, fd, {
      headers: {"Content-Type": "multipart/form-data"}
    });

    damages.value = [...(data ?? []), ...damages.value];
    clearNewDamageForm();
  } catch (e: any) {
    alert(e?.response?.data?.message || "新增損壞紀錄失敗");
  } finally {
    uploadingDamage.value = false;
  }
}

function startEdit(row: Damage) {
  editingDamageId.value = row.damageId;
  editingNote.value = row.damageNote ?? "";
}

function cancelEdit() {
  editingDamageId.value = null;
  editingNote.value = "";
}

async function saveEdit(damageId: number) {
  try {
    const {data} = await api.put<Damage>(`/damages/${damageId}/note`, {note: editingNote.value});
    const idx = damages.value.findIndex(d => d.damageId === damageId);
    if (idx >= 0) damages.value[idx] = {...damages.value[idx], ...data};
    cancelEdit();
  } catch (e: any) {
    alert(e?.response?.data?.message || "更新描述失敗");
  }
}

// 刪除損壞
async function deleteDamage(damageId: number) {
  if (!confirm("確定刪除這筆損壞紀錄？")) return;
  try {
    await api.delete(`/damages/${damageId}`);
    damages.value = damages.value.filter(d => d.damageId !== damageId);
  } catch (e: any) {
    alert(e?.response?.data?.message || "刪除失敗");
  }
}

// 刪除預約
const isEditing = ref(false);
const insertReservationMode = ref(false);

const deleteReservation = async () => {
  if (!confirm("確定要刪除此筆預約嗎？")) return;
  try {
    const id = reservation.value.reservationId;
    await api.delete(`/reservations/delete/${id}`);
    alert("刪除完成!");
    await router.push("/car-rent/back-homepage");
  } catch (err) {
    console.log("刪除失敗", err);
    alert("刪除失敗，請稍後再試");
  }
};

// 修改預約
const saveEditing = async () => {
  try {
    await api.put(`/reservations/update`, reservation.value);
    alert("修改成功");
    isEditing.value = false;
  } catch (err: any) {
    const msg = err?.response?.data?.message || "修改失敗，請稍後再試";
    if (msg.includes("該車牌已被登入")) alert("該車牌已被登入，請再次檢查車牌資訊");
    else alert(msg);
  }
};

const startInsertReservation = () => {
  reservation.value = {
    license: "",
    driverName: "",
    driverPhone: "",
    driverEmail: "",
    driverAge: "",
    pickupDate: "",
    returnDate: "",
    totalAmount: "",
    vehicleId: "",
    location: "",
    status: ""
  };
  insertReservationMode.value = true;
  isEditing.value = true;
};

// 新增預約
const insertReservation = async () => {
  try {
    const response = await api.post(`/reservations/insert`, reservation.value);
    const newId = response.data.reservationId;
    alert("新增成功");
    insertReservationMode.value = false;
    isEditing.value = false;
    await router.push(`/car-rent/reservations/${newId}`);
  } catch (err: any) {
    const msg = err?.response?.data?.message || "更新失敗，請稍後再試";
    if (msg.includes("該車牌已被登入")) alert("該車牌已被登入，請再次檢查車牌資訊");
    else alert(msg);
  }
};

// 完成預約
function finishedReservation() {
  reservation.value.status = "完成預約";
  saveEditing();
  alert("預約已標記為完成");
}

const currentTab = ref<'summary' | 'damages'>('summary');
const activeField = ref<string | null>(null);
const isInlineEditable = computed(() => isEditing.value || insertReservationMode.value);

watch([isEditing, insertReservationMode], () => {
  activeField.value = null;
});

// 時間顯示
function formatDateTime(dt?: string | null) {
  if (!dt) return "—";
  return dt.replace("T", " ").slice(0, 16);
}
</script>

<template>
  <div v-if="reservation">
    <v-container fluid class="px-6 py-4 main-content">
      <!-- 功能選單 -->
      <v-btn variant="outlined" class="ma-2" prepend-icon="mdi-menu" @click="showSidebar = true">功能選單</v-btn>
      <Sidebar :visible="showSidebar" :close="() => (showSidebar = false)"/>

      <v-card class="pa-4 bg-white" elevation="1">
        <div class="d-flex align-center justify-space-between mb-3">
          <div class="text-h5 font-weight-bold">預約一覽</div>
          <div id="action-buttons" class="d-flex flex-wrap" style="gap:.5rem;">
            <v-btn v-if="!insertReservationMode && !isEditing" variant="outlined" size="small" prepend-icon="mdi-check"
                   @click="finishedReservation">完成預約
            </v-btn>
            <v-btn v-if="isEditing && !insertReservationMode" color="primary" size="small"
                   prepend-icon="mdi-content-save" @click="saveEditing">確定儲存
            </v-btn>
            <v-btn v-if="!insertReservationMode && !isEditing" variant="outlined" color="success" size="small"
                   prepend-icon="mdi-plus" @click="startInsertReservation">新增預約
            </v-btn>
            <v-btn v-if="insertReservationMode" color="primary" size="small" prepend-icon="mdi-check-bold"
                   @click="insertReservation">確定新增
            </v-btn>
            <v-btn v-if="insertReservationMode" variant="outlined" size="small" prepend-icon="mdi-close"
                   @click="insertReservationMode = false; isEditing = false">取消
            </v-btn>
            <v-btn variant="outlined" size="small" prepend-icon="mdi-pencil" @click="isEditing = !isEditing">
              {{ isEditing ? "取消" : "編輯" }}
            </v-btn>
            <v-btn color="error" size="small" prepend-icon="mdi-delete" @click="deleteReservation">刪除預約</v-btn>
          </div>
        </div>

        <!-- 分頁 -->
        <v-tabs v-model="currentTab" density="comfortable" color="primary">
          <v-tab value="summary">摘要</v-tab>
          <v-tab value="damages">損壞紀錄</v-tab>
        </v-tabs>

        <v-window v-model="currentTab" class="mt-3">
          <!-- 摘要 -->
          <v-window-item value="summary">
            <v-row dense align="stretch">
              <!-- 個人資料 -->
              <v-col cols="12" md="6" class="d-flex">
                <v-card class="bg-white flex-grow-1" elevation="1">
                  <v-card-title class="text-subtitle-1">個人資料</v-card-title>
                  <v-card-text>
                    <v-table density="compact" class="text-no-wrap">
                      <colgroup>
                        <col class="label-col"/>
                        <col/>
                      </colgroup>
                      <tbody>
                      <tr>
                        <td class="label-cell">駕照號碼</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'license')"
                                 @click="isInlineEditable && (activeField = 'license')">{{ reservation.license || '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'license'"
                                          v-model="reservation.license" density="comfortable" variant="outlined"
                                          hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">姓名</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'driverName')"
                                 @click="isInlineEditable && (activeField = 'driverName')">
                              {{ reservation.driverName || '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'driverName'"
                                          v-model="reservation.driverName" density="comfortable" variant="outlined"
                                          hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">電話</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'driverPhone')"
                                 @click="isInlineEditable && (activeField = 'driverPhone')">
                              {{ reservation.driverPhone || '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'driverPhone'"
                                          v-model="reservation.driverPhone" density="comfortable" variant="outlined"
                                          hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">電子信箱</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'driverEmail')"
                                 @click="isInlineEditable && (activeField = 'driverEmail')">
                              {{ reservation.driverEmail || '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'driverEmail'"
                                          v-model="reservation.driverEmail" type="email" density="comfortable"
                                          variant="outlined" hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">年齡</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'driverAge')"
                                 @click="isInlineEditable && (activeField = 'driverAge')">
                              {{ reservation.driverAge ?? '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'driverAge'"
                                          v-model.number="reservation.driverAge" type="number" density="comfortable"
                                          variant="outlined" hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td class="label-cell">租屋訂單編號</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'bookingId')"
                                 @click="isInlineEditable && (activeField = 'bookingId')">
                              {{ reservation.bookingId || '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'bookingId'"
                                          v-model="reservation.bookingId" density="comfortable" variant="outlined"
                                          hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>
                      </tbody>
                    </v-table>
                  </v-card-text>
                </v-card>
              </v-col>

              <!-- 本次預約摘要 -->
              <v-col cols="12" md="6" class="d-flex">
                <v-card class="bg-white flex-grow-1" elevation="1">
                  <v-card-title class="text-subtitle-1">本次預約摘要</v-card-title>
                  <v-card-text>
                    <v-table density="compact" class="text-no-wrap">
                      <colgroup>
                        <col class="label-col"/>
                        <col/>
                      </colgroup>
                      <tbody>
                      <tr>
                        <td class="label-cell">預約編號</td>
                        <td>
                          <div class="fe-wrapper disabled">
                            <div class="fe-display">{{ reservation.reservationId ?? '—' }}</div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-cell">預約建立時間</td>
                        <td>
                          <div class="fe-wrapper disabled">
                            <div class="fe-display">{{ formatDateTime(reservation.createdAt) }}</div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-cell">預約總金額</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'totalAmount')"
                                 @click="isInlineEditable && (activeField = 'totalAmount')">
                              {{ reservation.totalAmount ?? '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'totalAmount'"
                                          v-model.number="reservation.totalAmount" type="number" density="comfortable"
                                          variant="outlined" hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-cell">預約時間</td>
                        <td>
                          <div class="fe-wrapper">
                            <div class="fe-display"
                                 v-show="!(isInlineEditable && (activeField === 'pickupDate' || activeField === 'returnDate'))"
                                 @click="isInlineEditable && (activeField = 'pickupDate')">
                              {{ formatDateTime(reservation.pickupDate) }} ～ {{
                                formatDateTime(reservation.returnDate)
                              }}
                            </div>
                            <div class="datetime-edit"
                                 v-show="isInlineEditable && (activeField === 'pickupDate' || activeField === 'returnDate')">
                              <v-text-field v-model="reservation.pickupDate" type="datetime-local" density="comfortable"
                                            variant="outlined" hide-details class="fe-input"
                                            @focus="activeField = 'pickupDate'" @blur="activeField = null"/>
                              <span class="mx-2">～</span>
                              <v-text-field v-model="reservation.returnDate" type="datetime-local" density="comfortable"
                                            variant="outlined" hide-details class="fe-input"
                                            @focus="activeField = 'returnDate'" @blur="activeField = null"/>
                            </div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-cell">租用車輛</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'vehicleId')"
                                 @click="isInlineEditable && (activeField = 'vehicleId')">
                              {{ reservation.vehicleId ?? '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'vehicleId'"
                                          v-model="reservation.vehicleId" density="comfortable" variant="outlined"
                                          hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-cell">租車地點</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'locationId')"
                                 @click="isInlineEditable && (activeField = 'locationId')">
                              {{ reservation.locationId ?? '—' }}
                            </div>
                            <v-text-field v-show="isInlineEditable && activeField === 'locationId'"
                                          v-model="reservation.locationId" density="comfortable" variant="outlined"
                                          hide-details class="fe-input" @blur="activeField = null"/>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-cell">付款狀態</td>
                        <td>
                          <div class="fe-wrapper" :class="{'is-editable': isInlineEditable}">
                            <div class="fe-display" v-show="!(isInlineEditable && activeField === 'status')"
                                 @click="isInlineEditable && (activeField = 'status')">{{ reservation.status || '—' }}
                            </div>
                            <v-select v-show="isInlineEditable && activeField === 'status'"
                                      v-model="reservation.status"
                                      :items="[{ title: '未付款', value: '未付款' },{ title: '已付款', value: '已付款' },{ title: '取消預約', value: '取消預約' }]"
                                      density="comfortable" variant="outlined" hide-details class="fe-input"
                                      @blur="activeField = null"/>
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

          <!-- 損壞紀錄 -->
          <v-window-item value="damages">
            <v-row dense>
              <!-- 新增區 -->
              <v-col cols="12">
                <v-card elevation="1" class="bg-white">
                  <v-card-title class="text-subtitle-1">新增損壞紀錄</v-card-title>
                  <v-card-text>
                    <v-row>
                      <v-col cols="12" md="6">
                        <v-textarea v-model="newDamageNote" label="文字描述（可留空）" auto-grow rows="3"
                                    variant="outlined" density="comfortable"/>
                      </v-col>
                      <v-col cols="12" md="6">
                        <v-file-input
                            label="選擇圖片（可多選）"
                            variant="outlined"
                            density="comfortable"
                            chips
                            multiple
                            show-size
                            accept="image/*"
                            prepend-icon="mdi-image-multiple"
                            @update:model-value="onChooseFiles"
                        />
                        <div v-if="previewUrls.length" class="preview-grid mt-2">
                          <div v-for="src in previewUrls" :key="src" class="thumb">
                            <img :src="src" alt="preview"/>
                          </div>
                        </div>
                      </v-col>
                    </v-row>
                    <div class="d-flex justify-end">
                      <v-btn :loading="uploadingDamage" color="primary" prepend-icon="mdi-upload" @click="createDamage">
                        上傳
                      </v-btn>
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>

              <!-- 清單 -->
              <v-col cols="12">
                <v-card elevation="1" class="bg-white">
                  <v-card-title class="text-subtitle-1">損壞清單</v-card-title>
                  <v-card-text>
                    <v-table density="compact">
                      <thead>
                      <tr>
                        <th style="width:140px;">照片</th>
                        <th>描述</th>
                        <th style="width:180px;">回報時間</th>
                        <th style="width:160px;" class="text-right">操作</th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr v-if="damageLoading">
                        <td colspan="4">
                          <div class="d-flex align-center">
                            <v-progress-circular indeterminate size="20" class="mr-2"/>
                            載入中…
                          </div>
                        </td>
                      </tr>
                      <tr v-else-if="!damages.length">
                        <td colspan="4" class="text-medium-emphasis">尚無紀錄</td>
                      </tr>
                      <tr v-for="row in damages" :key="row.damageId">
                        <td>
                          <div v-if="row.imageUrl" class="thumb">
                            <img :src="row.imageUrl" alt="damage"/>
                          </div>
                          <span v-else>—</span>
                        </td>
                        <td>
                          <div v-if="editingDamageId !== row.damageId">{{ row.damageNote || '—' }}</div>
                          <v-text-field
                              v-else
                              v-model="editingNote"
                              density="comfortable"
                              variant="outlined"
                              hide-details
                          />
                        </td>
                        <td>{{ formatDateTime(row.reportDate) }}</td>
                        <td class="text-right">
                          <template v-if="editingDamageId === row.damageId">
                            <v-btn size="small" color="primary" variant="tonal" class="mr-2"
                                   prepend-icon="mdi-content-save" @click="saveEdit(row.damageId)">儲存
                            </v-btn>
                            <v-btn size="small" variant="text" prepend-icon="mdi-cancel" @click="cancelEdit">取消
                            </v-btn>
                          </template>
                          <template v-else>
                            <v-btn size="small" variant="text" prepend-icon="mdi-pencil" @click="startEdit(row)">編輯
                            </v-btn>
                            <v-btn size="small" color="error" variant="text" prepend-icon="mdi-delete"
                                   @click="deleteDamage(row.damageId)">刪除
                            </v-btn>
                          </template>
                        </td>
                      </tr>
                      </tbody>
                    </v-table>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
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

.datetime-edit {
  display: flex;
  align-items: center;
}

.datetime-edit .fe-input {
  flex: 1 1 0;
}

/* 損壞預覽 / 縮圖 */
.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(96px, 1fr));
  gap: 8px;
}

.thumb {
  width: 96px;
  height: 72px;
  border: 1px solid rgba(0, 0, 0, .08);
  border-radius: 8px;
  overflow: hidden;
  background: #f6f6f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
