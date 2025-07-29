<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"> 
<head>
  <title><c:out value="${listing.house_name}" /></title>
  <style>
   .come{
        font-family: 'Segoe UI', sans-serif;
      margin: 40px auto;
      max-width: 1000px;
      color: #333;
  }
   .container2 {
   position: sticky;
  top: 0;
  z-index: 999; 
  background-color: white; 
  box-shadow: 0 2px 5px rgba(0,0,0,0.1); 
  margin: 0 auto;
  padding: 0px 0px;
  box-sizing: content-box;
  width: 100%;
}
.naver {
   position: sticky;
  top: 0;
  z-index: 999; 
  background-color: white; 
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 23vw; 
    background-color: #ffffff;
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    position: sticky;
	}
	.logo {
    font-size: 1.5rem;
    font-weight: 600;
	}
 	  nav a {
      margin-left: 25px;
      text-decoration: none;
      color: #333;
      font-weight: 400;
      transition: color 0.3s;
    }
     .highlight {
 	 color:  #f07b2d;
	}
    
    .photo-section {
      text-align: center;
    }

    .main-photo {
      width: 100%;
      max-height: 450px;
      object-fit: cover;
      border-radius: 12px;
    }

    .thumbs {
      display: flex;
      justify-content: center;
      gap: 8px;
      margin-top: 10px;
      flex-wrap: wrap;
    }

    .thumbs img {
      width: 100px;
      height: 65px;
      object-fit: cover;
      border-radius: 6px;
      cursor: pointer;
      border: 1px solid #ccc;
    }

    .info-flex {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-top: 30px;
    }

    .info-left {
      width: 60%;
      font-size: 1.1rem;
    }

    .info-right {
      width: 35%;
      border: 1px solid #eee;
      padding: 20px;
      border-radius: 10px;
      background-color: #fafafa;
    }

    h1 {
      margin-bottom: 10px;
    }

    .label {
      font-weight: bold;
      margin-top: 10px;
    }

    .description, .amenities {
      margin-top: 25px;
    }

    .amenity-list {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin-top: 10px;
    }

    .amenity-item {
      background-color: #f1f3f5;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 1.1rem;
    }

    .price {
      font-size: 1.5rem;
      font-weight: bold;
      margin-bottom: 10px;
    }

     .book-btn {
  	background-color: #f07b2d;
  	color: white;
  	border: none;
  	padding: 12px;
  	width: 100%;
  	font-size: 1rem;
  	border-radius: 8px;
  	cursor: pointer;
  	transition: background 0.3s;
	}

	.book-btn:hover {
 	 background-color: #e7630b;
	}
	   .btn:hover {
      background-color:#e65d03;
    }
	
    .listing-actions button:hover {
      background-color: #e7630b;
    }
      nav a:hover {
      color:#D35400;
    }
   	#menu-toggle {
      display: none;
    }
    .menu-icon {
    font-size: 16px;
  	cursor: pointer;
  	display: inline-block;
  	padding: 5px;
  	 margin-left: 15px;
  	 transition: color 0.3s;
    }

  
    .menu {
      position: relative;
      display: inline-block;
    }

    .dropdown {
      display: none;
      position: absolute;
      top: 40px;
      left: 0;
      background-color: white;
      border: 1px solid #ccc;
      border-radius: 5px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.15);
      min-width: 150px;
      z-index: 100;
    }

    .dropdown a {
      display: block;
      padding: 10px;
      color: #333;
      text-decoration: none;
    }

    .dropdown a:hover {
      background-color: #f0f0f0;
    }
   
    #menu-toggle:checked + label + .dropdown {
      display: block;
    }
   
	.menu-icon:hover{
	 color:#D35400;
	}
  </style>

</head>
<body>

<div class="container2">
    <header class="naver">
      <div class="logo" style="font-family:Microsoft JhengHei">
    <span class="highlight">My</span>Booking
    </div>
      <nav>
        <a href="JSP/listingDetail.jsp">刊登旅宿</a>
        <a href="#">規劃行程</a>
        <a href="#">登入/註冊</a>
        <a class="fa-solid fa-earth-americas" href="#"></a>
        
  	<div class="menu">
    <input type="checkbox" id="menu-toggle">

    <label for="menu-toggle" class="menu-icon">
      <i class="fa-solid fa-bars dropdown-toggle"></i>
    </label>
    <!-- 下拉內容 -->
    <div class="dropdown">
      <a href="#">旅遊指南</a>
      <a href="#">租車服務</a>
      <a href="#">帳號設定</a>
      <a href="#">我的最愛</a>
      <a href="#">訂房紀錄</a>
      <a href="http://localhost:8080/Airbnb/AllListings#">我的房源</a>
    </div>
  </div>
  </div>
</div>
      </nav>
    </header>
</div>
<div class="come">
  <h1><c:out value="${listing.house_name}" /></h1>
  <!-- 圖片區 -->
  <div class="photo-section">
    <img id="mainPhoto" class="main-photo" src="<c:out value='${listing.photo1}' />" alt="主圖片" />
    <div class="thumbs">
      <c:forEach var="i" begin="1" end="10">
        <c:choose>
          <c:when test="${i == 1}"><c:set var="photo" value="${listing.photo1}" /></c:when>
          <c:when test="${i == 2}"><c:set var="photo" value="${listing.photo2}" /></c:when>
          <c:when test="${i == 3}"><c:set var="photo" value="${listing.photo3}" /></c:when>
          <c:when test="${i == 4}"><c:set var="photo" value="${listing.photo4}" /></c:when>
          <c:when test="${i == 5}"><c:set var="photo" value="${listing.photo5}" /></c:when>
          <c:when test="${i == 6}"><c:set var="photo" value="${listing.photo6}" /></c:when>
          <c:when test="${i == 7}"><c:set var="photo" value="${listing.photo7}" /></c:when>
          <c:when test="${i == 8}"><c:set var="photo" value="${listing.photo8}" /></c:when>
          <c:when test="${i == 9}"><c:set var="photo" value="${listing.photo9}" /></c:when>
          <c:when test="${i == 10}"><c:set var="photo" value="${listing.photo10}" /></c:when>
        </c:choose>
        <c:if test="${not empty photo}">
          <img src="<c:out value='${photo}' />" onclick="switchMainPhoto(this.src)" />
        </c:if>
      </c:forEach>
    </div>
  </div>

  
  <div class="info-flex">
 
    <div class="info-left">
      <div><span class="label">地址：</span><c:out value="${listing.ads}" /></div>
      <div><span class="label">房型：</span><c:out value="${listing.room}" /></div>
      <div><span class="label">床位：</span><c:out value="${listing.bed}" /></div>
      <div><span class="label">入住人數：</span><c:out value="${listing.ppl}" /></div>
      <div><span class="label">聯絡電話：</span><c:out value="${listing.tel}" /></div>
<hr>
      <div class="description">
        <h3>房源介紹</h3>
        <p><c:out value="${listing.describe}" /></p>
      </div>
	<hr>
      <c:if test="${not empty equipments}">
        <div class="amenities">
          <h3>提供設備</h3>
          <div class="amenity-list">
            <c:forEach var="equip" items="${equipments}">
              <div class="amenity-item"><c:out value="${equip}" /></div>
            </c:forEach>
          </div>
        </div>
      </c:if>
    </div>

    
    <div class="info-right">
      <div class="price">NT$ <c:out value="${listing.price}" /></div>
      <div>2 晚｜入住 2026/6/5｜退房 2026/6/7</div>
      <div style="margin: 12px 0;">人數：<c:out value="${listing.ppl}" /> 人</div>
       <form action="/Airbnb/orderConfirm" method="post">
                    <input type="hidden" name="listId" value="${listing.lisid}">
                    <input type="hidden" name="customerId"
                        value="${sessionScope.customerId}">
      <button class="book-btn">預訂</button>
      </form>
    </div>
  </div>
</div>
 <script>
    function switchMainPhoto(src) {
      document.getElementById("mainPhoto").src = src;
    }
  </script>
  
  
  <%
    String error = request.getParameter("error");
    if (error != null && !error.isEmpty()) {
%>
    <script>
        alert("<%= error %>");
    </script>
<%
    }
%> 

  
</body>
</html>