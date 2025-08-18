<template>
  <v-container class="py-6" max-width="1000">
    <div class="header-row">
      <v-icon size="30" class="mr-2" color="deep-orange-darken-1"
        >mdi-clipboard-text-clock</v-icon
      >
      <h2 class="page-title">我的訂單</h2>
    </div>

    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
      border="start"
      color="red-darken-1"
      icon="mdi-alert-octagon-outline"
    >
      {{ error }}
    </v-alert>

    <!-- 訂單卡片 -->
    <v-card
      v-for="order in orders"
      :key="order.bookingId"
      class="mb-4 soft-card hoverable"
      elevation="2"
      rounded="xl"
      @click="openDetail(order.bookingId)"
    >
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3"
          >mdi-receipt-text-outline</v-icon
        >
        <span class="card-title">訂單編號：{{ order.bookingId }}</span>
        <v-spacer />
        <v-chip
          size="small"
          :color="statusColor(order.bookingstatus)"
          variant="elevated"
          class="status-chip"
        >
          <v-icon start size="16">
            {{ statusIcon(order.bookingstatus) }}
          </v-icon>
          {{ order.bookingstatus }}
        </v-chip>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-3">
        <v-row>
          <v-col cols="12" md="6">
            <v-list density="compact" class="flat-list">
              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-home-city</v-icon>
                </template>
                <v-list-item-title class="kv">
                  房名：<span class="value">{{ order.housename }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-map-marker</v-icon>
                </template>
                <v-list-item-title class="kv">
                  地址：<span class="value">{{ order.address }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-phone</v-icon>
                </template>
                <v-list-item-title class="kv">
                  電話：<span class="value">{{ order.tel }}</span>
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="6">
            <v-list density="compact" class="flat-list">
              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-bed-queen-outline</v-icon>
                </template>
                <v-list-item-title class="kv">
                  房型：<span class="value">{{ order.bed }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-account-multiple</v-icon>
                </template>
                <v-list-item-title class="kv">
                  人數：<span class="value">{{ order.people }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-calendar-range</v-icon>
                </template>
                <v-list-item-title class="kv">
                  入住：<span class="value">{{
                    formatDate(order.checkindate)
                  }}</span>
                  <span class="mx-2 sep">•</span>
                  退房：<span class="value">{{
                    formatDate(order.checkoutdate)
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend>
                  <v-icon color="deep-orange">mdi-cash-multiple</v-icon>
                </template>
                <v-list-item-title class="kv">
                  總金額：<span class="value emphasis"
                    >NT$ {{ formatAmount(order.grandtotal) }}</span
                  >
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <div v-if="orders.length === 0 && !error" class="empty-box">
      <v-icon size="34" class="mb-2" color="deep-orange-darken-1"
        >mdi-invoice-list-outline</v-icon
      >
      目前沒有訂單紀錄
    </div>

    <!-- 明細 Dialog -->
    <v-dialog v-model="showDetail" max-width="680">
      <v-card rounded="xl" class="soft-card">
        <v-card-title class="text-h6 d-flex align-center">
          <v-icon class="mr-2" color="deep-orange-accent-3"
            >mdi-file-document-alert-outline</v-icon
          >
          <span class="card-title">訂單明細</span>
          <v-spacer />
          <v-chip
            v-if="selectedOrder"
            size="small"
            :color="statusColor(selectedOrder.bookingStatus)"
            variant="elevated"
          >
            <v-icon start size="16">{{
              statusIcon(selectedOrder.bookingStatus)
            }}</v-icon>
            {{ selectedOrder.bookingStatus }}
          </v-chip>
        </v-card-title>

        <v-divider class="mx-4"></v-divider>

        <v-card-text v-if="detailLoading" class="py-6">
          <v-skeleton-loader type="article, actions" />
        </v-card-text>

        <!-- 真的有資料再渲染內容，避免 undefined -->
        <v-card-text v-else-if="selectedOrder" class="py-4">
          <v-list density="comfortable" class="flat-list">
            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange">mdi-home-city</v-icon></template
              >
              <v-list-item-title class="kv"
                >房名：<span class="value">{{
                  selectedOrder.houseName
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item v-if="selectedOrder.locationId">
              <template #prepend
                ><v-icon color="deep-orange">mdi-car-info</v-icon></template
              >
              <v-list-item-title class="kv"
                >租車明細：<span class="value">{{
                  selectedOrder.reservationId
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange">mdi-map-marker</v-icon></template
              >
              <v-list-item-title class="kv"
                >地址：<span class="value">{{
                  selectedOrder.address
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange">mdi-phone</v-icon></template
              >
              <v-list-item-title class="kv"
                >電話：<span class="value">{{
                  selectedOrder.tel
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-bed-queen-outline</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >房型：<span class="value">{{
                  selectedOrder.bed
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-account-multiple</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >人數：<span class="value">{{
                  selectedOrder.people
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-calendar-range</v-icon
                ></template
              >
              <v-list-item-title class="kv">
                入住：<span class="value">{{
                  formatDate(selectedOrder.checkinDate)
                }}</span>
                <span class="mx-2 sep">•</span>
                退房：<span class="value">{{
                  formatDate(selectedOrder.checkoutDate)
                }}</span>
              </v-list-item-title>
            </v-list-item>

            <v-list-item v-if="selectedOrder.locationId">
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-map-marker-distance</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >取車地點：<span class="value">{{
                  selectedOrder.locationId
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-ticket-confirmation-outline</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >付款訂單：<span class="value">{{
                  selectedOrder.paymentId
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange">mdi-cash</v-icon></template
              >
              <v-list-item-title class="kv"
                >租房金額：<span class="value"
                  >NT$ {{ formatAmount(selectedOrder.roomprice || 0) }}</span
                ></v-list-item-title
              >
            </v-list-item>

            <v-list-item v-if="selectedOrder.locationId">
              <template #prepend
                ><v-icon color="deep-orange">mdi-cash-100</v-icon></template
              >
              <v-list-item-title class="kv"
                >租車金額：<span class="value"
                  >NT$
                  {{
                    formatAmount(
                      selectedOrder.cartotal || selectedOrder.carTotal || 0
                    )
                  }}</span
                ></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-cash-multiple</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >總金額：<span class="value emphasis"
                  >NT$
                  {{
                    formatAmount(
                      selectedOrder.grandtotal || selectedOrder.grandTotal || 0
                    )
                  }}</span
                ></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-credit-card-settings-outline</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >付款方式：<span class="value">{{
                  selectedOrder.bookingMethod
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-check-decagram</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >付款狀態：<span class="value">{{
                  selectedOrder.mentStatus
                }}</span></v-list-item-title
              >
            </v-list-item>

            <v-list-item>
              <template #prepend
                ><v-icon color="deep-orange"
                  >mdi-clock-outline</v-icon
                ></template
              >
              <v-list-item-title class="kv"
                >付款時間：<span class="value">{{
                  formatDateTime(selectedOrder.paidTime)
                }}</span></v-list-item-title
              >
            </v-list-item>
          </v-list>
        </v-card-text>

        <v-card-actions class="px-4 pb-4">
          <v-spacer />
          <v-btn
            color="orange-darken-1"
            variant="text"
            @click="showDetail = false"
          >
            <v-icon start>mdi-close</v-icon> 關閉
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "OrderList",
  data() {
    return {
      orders: [],
      error: "",
      showDetail: false,
      detailLoading: false,
      selectedOrder: null,
    };
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    statusIcon(s) {
      const k = (s || "").toString();
      if (k.includes("已入住")) return "mdi-bed";
      if (k.includes("待入住")) return "mdi-timer-sand";
      if (k.includes("完成")) return "mdi-check-circle";
      if (k.includes("已取消")) return "mdi-close-circle";
      return "mdi-information-outline";
    },
    statusColor(s) {
      const k = (s || "").toString();
      if (k.includes("已入住")) return "green-darken-1";
      if (k.includes("待入住")) return "amber-darken-2";
      if (k.includes("完成")) return "teal-darken-1";
      if (k.includes("已取消")) return "red-darken-1";
      return "grey-darken-1";
    },

    async fetchOrders() {
      try {
        const res = await axios.get("/api/orderconfirm/byCustomer", {
          withCredentials: true,
        });
        const list = Array.isArray(res.data) ? res.data : [];

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const getCheckin = (o) => {
          const v = o.checkindate || o.checkinDate;
          const d = v ? new Date(v) : null;
          return d && !isNaN(d) ? d : null;
        };

        list.sort((a, b) => {
          const da = getCheckin(a);
          const db = getCheckin(b);

          if (!da && !db) return 0;
          if (!da) return 1;
          if (!db) return -1;

          const aIsFutureOrToday = da.getTime() >= today.getTime();
          const bIsFutureOrToday = db.getTime() >= today.getTime();

          if (aIsFutureOrToday !== bIsFutureOrToday) {
            return aIsFutureOrToday ? -1 : 1;
          }

          const diffA = Math.abs(da.getTime() - today.getTime());
          const diffB = Math.abs(db.getTime() - today.getTime());
          return diffA - diffB;
        });

        this.orders = list;
      } catch (err) {
        this.error = err.response?.data?.error || "無法取得訂單資料";
      }
    },

    // 先開 dialog + loading，再打 API；回來後再塞資料
    async openDetail(bookingId) {
      this.showDetail = true;
      this.detailLoading = true;
      this.selectedOrder = null;
      try {
        const res = await axios.get("/api/orderconfirm/detail", {
          params: { bookingId: bookingId },
          withCredentials: true,
        });
        this.selectedOrder = res.data;
      } catch (err) {
        this.showDetail = false;
        alert("取得明細失敗：" + (err.response?.data?.error || "未知錯誤"));
      } finally {
        this.detailLoading = false;
      }
    },

    formatAmount(n) {
      return new Intl.NumberFormat("zh-TW").format(n || 0);
    },
    formatDate(v) {
      if (!v) return "-";
      return new Date(v).toLocaleDateString("zh-TW");
    },
    formatDateTime(v) {
      if (!v) return "-";
      return new Date(v).toLocaleString("zh-TW");
    },
  },
};
</script>

<style scoped>
.soft-card {
  background: #fff7ed;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #c2410c;
  margin: 0;
}
.card-title {
  font-size: 18px;
  font-weight: 700;
}

.flat-list {
  --v-list-padding-start: 0;
  --v-list-padding-end: 0;
}
.kv {
  font-size: 15px;
}
.kv .value {
  font-weight: 700;
  color: #7c2d12;
}
.kv .emphasis {
  color: #b45309;
}

.status-chip {
  font-weight: 600;
}

.hoverable {
  transition: box-shadow 0.2s ease, transform 0.1s ease;
  cursor: pointer;
}
.hoverable:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 22px rgba(0, 0, 0, 0.06);
}

.empty-box {
  text-align: center;
  color: #9a3412;
  background: #fff7ed;
  border: 1px dashed #fdba74;
  padding: 28px 16px;
  border-radius: 16px;
}

.header-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.sep {
  opacity: 0.5;
}
</style>
