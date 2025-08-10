<template>
  <div class="container2">
    <header class="naver">
      <div class="logo"><span class="highlight">My</span>Booking</div>
      <nav>
        <!-- <router-link to="/listing/addlisting">刊登旅宿</router-link> -->
        <!-- <router-link to="/">規劃行程</router-link> -->
        <router-link to="#"><i class="fa-regular fa-circle-user fa-lg"></i> 登入/註冊</router-link>
        <div class="menu">
          <input type="checkbox" id="menu-toggle" />
          <label for="menu-toggle" class="menu-icon">
            <i class="fa-solid fa-bars fa-lg"></i>
          </label>
          <div class="dropdown">
            <!-- <router-link to="/">旅遊指南</router-link> -->
            <!-- <router-link to="/">租車服務</router-link> -->
            <router-link to="/">帳號設定</router-link>
            <router-link to="/">我的最愛</router-link>
            <router-link to="/">訂房紀錄</router-link>
            <router-link to="/">我的房源</router-link>
          </div>
        </div>
      </nav>
    </header>
  </div>
</template>

<style scoped>
@import '/src/assets/listing/list1.css';

.naver {
   position: sticky;
   top: 0;
   z-index: 999;
   background-color: white;
   display: flex;
   justify-content: space-between;
   align-items: center;
   padding: 1rem 24.5vw;
   background-color: #ffffff;
   box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
   position: sticky;
 }
</style>