<template>
  <div style="height: 130px;"></div>
  <v-footer app class="airbnb-footer">
    <v-container>
      <v-row class="py-6" align="start" justify="space-between">
        <!-- 公司資訊 -->
        <v-col cols="12" sm="6" md="3">
          <h6 class="footer-title">Ctwify</h6>
          <p class="footer-text">輕鬆上下架房源，打造屬於你的旅行體驗。</p>
        </v-col>

        <!-- 探索連結 -->
        <v-col cols="12" sm="6" md="3">
          <h6 class="footer-title">探索</h6>
          <ul class="footer-list">
            <li><a href="#" class="footer-link">關於我們</a></li>
            <li><a href="#" class="footer-link">常見問題</a></li>
            <li><a href="#" class="footer-link">最新消息</a></li>
            <li><a href="#" class="footer-link">隱私權政策</a></li>
          </ul>
        </v-col>

        <!-- 聯絡資訊 -->
        <v-col cols="12" sm="6" md="3">
          <h6 class="footer-title">聯絡我們</h6>
          <p class="footer-text">客服：0800-123-456</p>
          <p class="footer-text">Email：Ctwify@hotel.com</p>
          <div class="footer-icons mt-3">
            <v-icon size="20" class="footer-icon">mdi-facebook</v-icon>
            <v-icon size="20" class="footer-icon">mdi-instagram</v-icon>
            <v-icon size="20" class="footer-icon">mdi-twitter</v-icon>
          </div>
        </v-col>

        <!-- 品牌敘述 -->
        <v-col cols="12" sm="6" md="3">
          <h6 class="footer-title orange-text">我們的使命</h6>
          <p class="footer-text">
            Ctwify 致力於打造安全、可靠、彈性的旅宿平台，無論是短期旅行還是長期居住，都能輕鬆找到理想房源。
          </p>
        </v-col>
      </v-row>

      <v-divider class="my-4 orange-divider"></v-divider>

      <v-row justify="space-between" class="pb-4 text-caption">
        <v-col cols="12" sm="6">
          <span>© 2025 Ctwify, Inc. 保留所有權利</span>
        </v-col>
        <v-col cols="12" sm="6" class="text-sm-end">
          <span>網站條款．隱私政策．Cookie 政策</span>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>
  <br>
</template>

<style scoped>
.airbnb-footer {
  background-color:#555555; /* 深灰背景 */
  color: #ffffff;
  font-size: 14px;
  margin-top: 100px; /* 增加這行 */
}

.footer-title {
  font-weight: bold;
  margin-bottom: 12px;
  font-size: 16px;
  color: #ffffff;
}

.footer-text {
  margin-bottom: 6px;
  color: #e0e0e0; /* 淺白，讓文字有層次 */
}

.footer-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-link {
  color: #cccccc;
  text-decoration: none;
  display: block;
  margin-bottom: 6px;
  transition: color 0.3s;
}


.footer-icons {
  display: flex;
  gap: 12px;
}

.footer-icon {
  color: #cccccc;
  cursor: pointer;
  transition: color 0.3s;
}

.orange-divider {
  border-color:white;
  border-width: 0.1px;
}


</style>
