<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8">
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"> 
  <title>房源清單</title>
  <style>
  
    .come {
      font-family: "Segoe UI", sans-serif;
      margin: 0;
      background-color:#ffffff;
    }

   
    .container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 25px;
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


    
    .navbar {
      background-color: #ffffff;
      padding: 30px 50px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
      border-radius: 0 0 10px 10px;
    }

    .navbar-logo {
      font-size: 20px;
      font-weight: bold;
    }

    .navbar-icons span {
      margin-left: 15px;
      font-size: 18px;
      cursor: pointer;
    }

  
    .listing {
      display: flex;
      align-items: center;
      width: 1000px;
      background-color: #fff;
      padding: 12px 12px;
      margin-top: 15px;
      border-radius: 10px;
      box-shadow: 0 3px 6px rgba(0, 0, 0, 0.4);
    }

    .listing img {
      width: 100px;
      height: 70px;
      object-fit: cover;
      border-radius: 8px;
      margin-right: 20px;
    }

    .listing-info {
      flex: 1;
      font-size: 18px;
      font-weight: 500;
    }

    .listing-actions form {
      display: inline-block;
      margin-left: 10px;
    }

    .listing-actions button {
      background-color:#f07b2d;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 14px;
      transition: background 0.3s;
    }

    .listing-actions button:hover {
      background-color: #e7630b;
    }
    .highlight {
  	  color:  #f07b2d;
	}
 	nav a:hover {
      color:#D35400;
    }
    .btn:hover {
      background-color:#e65d03;
    }
    .dropdown {
      position: relative;
      display: inline-block;
    }

    .dropdown-toggle {
    cursor: pointer;
    }

    .dropdown-menu {
  	  display: none;
 	  position: absolute;
  	  right: 0;
  	  top: 120%;
  	  background-color: white;
  	  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  	  border-radius: 6px;
  	  min-width: 130px;
 	  z-index: 1000;
	  }

	 .dropdown-menu a {
 	  display: block;
  	  padding: 10px 15px;
  	  color: #333;
  	  text-decoration: none;
  	  font-size: 14px;
	  }

	 .dropdown-menu a:hover {
 	  background-color: #f5f5f5;
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
  <div class="container">
    <c:forEach var="house" items="${houseList}">
      <div class="listing">
        <!-- 圖片加上超連結 -->
        <a href="ListingsDetail?Lisid=${house.lisid}">
          <img src="${house.photo1}" alt="房源圖片" class="listing-img">
        </a>

      
        <div class="listing-info">${house.house_name}</div>
        <div class="listing-actions">
          <form method="get" action="EditListings">
            <input type="hidden" name="lisid" value="${house.lisid}">
            <button type="submit">編輯</button>
          </form>
          <form method="post" action="DeleListings" 
      	onsubmit="return confirmDelete('${house.house_name}');">
  		<input type="hidden" name="lisid" value="${house.lisid}">
  		<button type="submit">刪除</button>
		</form>
        </div>
      </div>
    </c:forEach>
  </div>
</div>
<script>
  function confirmDelete(name) {
    return confirm("確定要刪除房源「" + name + "」嗎？");
  }
</script>
</body>
</html>