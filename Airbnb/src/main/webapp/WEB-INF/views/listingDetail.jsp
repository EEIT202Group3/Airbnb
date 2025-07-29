<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="com.listing.bean.EquipmentBean" %>
<%@ page import="com.listing.dao.EquipmentDao" %>
<%
    EquipmentDao dao = new EquipmentDao();
    List<EquipmentBean> equipList = dao.getAllEquipments();
%>

<!DOCTYPE html>
<html lang="zh-Hant">
<head>
    <meta charset="UTF-8">
    <title>新增房源</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
     <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"> 
    <style>
       body {
            background-color: #f8f9fa;
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
      font-weight: 600;
      transition: color 0.3s;
    }
     .highlight {
 	 color:  #f07b2d;
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
        .form-container {
            background: #ffffff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            max-width: 800px;
            margin: 40px auto;
        }
        .form-title {
            font-weight: bold;
            font-size: 28px;
            color: #343a40;
            margin-bottom: 30px;
            text-align: center;
        }
        .form-label {
            font-weight: 500;
        }
         	.btn-orange {
 		 background-color: #f07b2d;
  		color: white;
  		border: none;
  		transition: background-color 0.3s;
		}

		.btn-orange:hover {
 	 	background-color: #e7630b;
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
        <a href="listingDetail.jsp">刊登旅宿</a>
        <a href="#">規劃行程</a>
        <a href="#">登入/註冊</a>
        <a class="fa-solid fa-earth-americas" href="#"></a>
        
  	<div class="menu">
    <input type="checkbox" id="menu-toggle">

    <label for="menu-toggle" class="menu-icon">
      <i class="fa-solid fa-bars"></i>
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






<div class="container">
    <div class="form-container">
        <div class="form-title">新增房源</div>
       <form action="${pageContext.request.contextPath}/AddListings" method="post" enctype="multipart/form-data">
            <div class="mb-3">
                <label class="form-label">房屋名稱</label>
                <input type="text" name="house_name" class="form-control" required>
            </div>
           
            <div class="mb-3">
                <label class="form-label">地址</label>
                <input type="text" name="ads" class="form-control" required>
            </div>
            <div class="mb-3 row">
                <div class="col">
                    <label class="form-label">房型</label>
                    <input type="text" name="room" class="form-control">
                </div>
                <div class="col">
                    <label class="form-label">床位</label>
                    <input type="text" name="bed" class="form-control">
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">描述</label>
                <textarea name="describe" class="form-control" rows="3"></textarea>
            </div>
            <div class="mb-3 row">
                <div class="col">
                    <label class="form-label">電話</label>
                    <input type="text" name="tel" class="form-control">
                </div>
                <div class="col">
                    <label class="form-label">人數</label>
                    <input type="number" name="ppl" class="form-control">
                </div>
                <div class="col">
                    <label class="form-label">價格</label>
                    <input type="number" name="price" class="form-control">
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">照片上傳（最多十張）</label>
                <input type="file" name="photos" multiple accept="image/*" class="form-control">
            </div>

            <div class="mb-3">
                <label class="form-label">請勾選設備：</label><br>
                <div class="row">
                    <%
                        for (EquipmentBean eq : equipList) {
                    %>
                    <div class="col-6 col-md-4 mb-2">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="equipments" value="<%= eq.getEquip_id() %>">
                            <label class="form-check-label">
                                <%= eq.getEquip_name() %>
                            </label>
                        </div>
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>

           <div class="text-center">
    	<button type="submit" class="btn btn-orange btn-lg px-5">新增房源</button>
		</div>
        </form>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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