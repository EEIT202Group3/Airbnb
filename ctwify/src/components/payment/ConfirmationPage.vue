<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>確認訂單</v-card-title>
          <v-card-text>
            <p>
              <strong>房源名稱：</strong>{{ order.houseName || "載入中..." }}
            </p>
            <p><strong>地址：</strong>{{ order.address || "載入中..." }}</p>
            <p>
              <strong>價格：</strong
              >{{ order.price ? order.price + " 元/晚" : "載入中..." }}
            </p>

            <v-text-field
              label="入住日期"
              v-model="checkindate"
              type="datetime-local"
              required
              :disabled="true"
            ></v-text-field>
            <v-text-field
              label="退房日期"
              v-model="checkoutdate"
              type="datetime-local"
              required
              :disabled="true"
            ></v-text-field>
            <v-text-field
              label="人數"
              v-model="people"
              type="number"
              min="1"
              required
              :disabled="true"
            ></v-text-field>

            <p v-if="totalAmount">住宿天數: {{ days }} 天</p>
            <p v-if="totalAmount">總金額: {{ totalAmount }} 元</p>
          </v-card-text>

          <v-select
            v-model="paymentMethod"
            :items="paymentMethods"
            label="選擇付款方式"
            required
          ></v-select>

          <v-btn
            color="primary"
            :disabled="paymentMethod === '請選擇付款方式' || !totalAmount"
            @click="finalizeOrder"
          >
            確認並付款
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      order: {},
      checkindate: this.$route.query.checkindate || "",
      checkoutdate: this.$route.query.checkoutdate || "",
      people: Number(this.$route.query.people) || 1,
      days: 0,
      totalAmount: 0,
      paymentMethod: "請選擇付款方式",
      paymentMethods: ["信用卡", "現金"],
    };
  },
  created() {
    this.fetchPreviewOrder();
  },
  methods: {
    async fetchPreviewOrder() {
      try {
        const requestPayload = {
          listid: Number(this.$route.query.listid),
          checkindate: this.checkindate,
          checkoutdate: this.checkoutdate,
          people: this.people,
        };

        const response = await fetch(
          "http://localhost:8080/api/orderconfirm/preview",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(requestPayload),
          }
        );

        if (!response.ok) {
          const errorMsg = await response.text();
          alert("訂單預覽失敗: " + errorMsg);
          this.$router.push("/"); // 返回首頁或其他頁面
          return;
        }

        const result = await response.json();

        this.order = result.listing;
        this.days = result.days;
        this.totalAmount = result.total;
        this.checkindate = result.checkindate;
        this.checkoutdate = result.checkoutdate;
        this.people = result.people;
      } catch (error) {
        alert("系統錯誤：" + error.message);
      }
    },
    async finalizeOrder() {
      if (this.paymentMethod === "請選擇付款方式") {
        alert("請選擇付款方式");
        return;
      }

      try {
        const orderData = {
          listid: Number(this.$route.query.listid),
          checkindate: this.checkindate,
          checkoutdate: this.checkoutdate,
          people: this.people,
          paymentMethod: this.paymentMethod,
        };

        const response = await fetch("/api/orderconfirm/finalize", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(orderData),
        });

        const text = await response.text();

        if (response.ok) {
          alert(text); // 或跳轉至成功頁面
          this.$router.push({ name: "paymentSuccess" });
        } else {
          alert("付款失敗：" + text);
        }
      } catch (error) {
        alert("系統錯誤：" + error.message);
      }
    },
  },
};
</script>