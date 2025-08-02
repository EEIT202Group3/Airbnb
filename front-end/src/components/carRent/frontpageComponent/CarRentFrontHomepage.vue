<script setup lang="ts">
import "@/assets/carRent/frontHomepage.css"
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";

const locationList = [{
  name: '台北',
  img: '../../../../public/carPicture/taipei.jpg',
  desc: '首都繁華，融合歷史古蹟與現代摩天大樓，是臺灣的政治、經濟與文化中心。'
},
  {
    name: '高雄',
    img: '../../../../public/carPicture/kaoshon.jpg',
    desc: '港都風情與現代藝術並存，擁有蓬勃海洋文化與熱情陽光，是南台灣的重要城市。'
  },
  {
    name: '宜蘭',
    img: '../../../../public/carPicture/yilan.jpg',
    desc: '山海環抱、自然純淨，以溫泉、田園與民俗活動聞名，是旅遊與慢活的好去處。'
  }]
</script>

<template>
  <NavigationBar></NavigationBar>
  <SearchBar></SearchBar>


  <!-- 熱門地點 -->
  <div class="container mt-5">
    <p>地點推薦</p>
    <div class="row g-4">
      <div class="col-md-4" v-for="loc in locationList" :key="loc.name">
        <div class="card h-100">
          <a href="#">
            <img :src="loc.img" class="card-img-top" :alt="loc.name">
          </a>
          <div class="card-body">
            <h5 class="card-title">{{ loc.name }}</h5>
            <p class="card-text">{{ loc.desc }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 租車步驟 -->
  <div class="container  mt-5">
    <div class="card">
      <div class="card-header">如何找到最超值的租車服務</div>
      <div class="card-body">
        <figure>
          <blockquote class="blockquote">
            <p>使用本服務</p>
          </blockquote>
          <figcaption class="blockquote-footer">

            我們經常被評為「最值得信賴的旅遊搜尋網站」。
            網站服務完全免費，而我們會搜尋上百家租車供應商服務，讓你能迅速比較價格並預訂心儀的服務。歡迎查看以下詳情，了解如何善用租車搜尋功能。
            立即預訂，日後還能取消
            市面上有很多汽車和廂型車租賃服務都提供《彈性預訂政策》和免費取消選項。因此，你能以優惠價格租車，同時享受充分彈性，即便計畫臨時有變也能安心。

            在中壢市租車一整個月
            想要租車長達近一個月嗎？一般來說，租車業者往往會提供長期租車服務，以減少兩筆預訂之間昂貴的管理費用。不妨在找車時選擇
            30 天，查看月租費用是否比自己需要的三週還便宜。

            比較《燃油政策》
            為節省你在啟程前加滿油的費用，尋找租車服務時，記得選擇滿取滿還的《燃油政策》。

            省去排隊時間
            當你在我們的網站上搜尋租車服務時，會看到顯眼的免鑰匙或自助取車選項。不用鑰匙或填寫文件就能取車，就等於不用排隊了。只要前往最方便的取車地點，即可輕鬆開車上路。

            更環保的選擇
            探索美麗地球，減少環境衝擊，不妨篩選電動車作為代步工具。電動車會在你取車前就充滿電，代表你也無須費心去比較不同的《燃油政策》。

            看看更遠的地方
            如果預算有限，搭乘大眾運輸交通工具到更遠的汽車租賃取車地點，通常會更便宜。<cite
              title="Source Title">資料來源</cite>
          </figcaption>
        </figure>
      </div>
    </div>
  </div>

  <!-- 常見問題 -->
  <div class="container mt-4">
    <div class="mb-2">
      <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse1"
              aria-expanded="false" aria-controls="collapse1">
        請問哪裡可以查到各據點的營業時間？
      </button>
      <div class="collapse" id="collapse1">
        <div class="card card-body">
          答：您可以在「服務據點」內，尋找您欲租車的據點位置及營業時間。
        </div>
      </div>
    </div>
    <div class="mb-2">
      <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse2"
              aria-expanded="false" aria-controls="collapse2">
        現場租車有什麼折扣？
      </button>
      <div class="collapse" id="collapse2">
        <div class="card card-body">
          答：問就是沒有。
        </div>
      </div>
    </div>
    <div class="mb-2">
      <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse3"
              aria-expanded="false" aria-controls="collapse3">
        請問如何知道自己想要的日期是否仍有車可租呢？
      </button>
      <div class="collapse" id="collapse3">
        <div class="card card-body">
          答：您可致電於想要取車的據點（請參考「服務據點」）或是在我們的「意見信箱」詢問，請您屆時提供
        </div>
      </div>
    </div>
    <div class="mb-2">
      <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse4"
              aria-expanded="false" aria-controls="collapse4">
        當我電話/網路預約租車，如何進行付費交易？
      </button>
      <div class="collapse" id="collapse4">
        <div class="card card-body">
          答：無論網路預約租車，或是電話預約租車並現場取車付費時，接受信用卡及現金。
        </div>
      </div>
    </div>
    <div class="mb-2">
      <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse5"
              aria-expanded="false" aria-controls="collapse5">
        我是網路會員，但我本身不會開車，可先付款再請別人取車嗎？
      </button>
      <div class="collapse" id="collapse5">
        <div class="card card-body">
          答：很抱歉，不會開還敢租車。
        </div>
      </div>
    </div>
    <div class="mb-2">
      <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse6"
              aria-expanded="false" aria-controls="collapse6">
        我需要攜帶什麼憑證取車呢？
      </button>
      <div class="collapse" id="collapse6">
        <div class="card card-body">
          答：若您已於網路預約租車並線上信用卡付費成功，僅需攜帶您的「身分證正本」及「中華民國駕照正本」至取車據點即可取車。
          若您為電話預約租車，需攜帶您的「信用卡」、「身分證正本」及「中華民國駕照正本」至取車據點方可取車。
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
</style>