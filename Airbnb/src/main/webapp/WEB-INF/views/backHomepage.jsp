<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>後台頁面</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/backStyle.css">
</head>

<body>
<div style="max-height: 100vh; overflow-y: auto;">
<!-- 左側選單 -->
<div class="sidebar">
    <a href="${pageContext.request.contextPath}/JSP/backHomepage.jsp"><i class="fa-solid fa-house"></i> 主頁面</a>
    <a href="#"><i class="fa-solid fa-car-side"></i> 車輛管理</a>
    <a href="${pageContext.request.contextPath}/JSP/order1.jsp"><i class="fa-solid fa-user"></i> 訂單資訊</a>
    <a href="http://localhost:8080/Project2/backEnd.html"><i class="fa-solid fa-backward-step"></i> 後臺介面</a>
    
</div>

<!-- 主內容區 -->
<div class="main-content">
    <!-- 頂部狀態卡 -->
    <div class="row row-cols-1 row-cols-md-6 g-4">
        <div class="col">
            <div class="card h-10">
                <div class="card-body">
                    <h5 class="card-title"><i
                            class="fa-solid fa-list-check"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3</h5>
                    <p class="card-text text-center">已預定</p>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card h-10">
                <div class="card-body">
                    <h5 class="card-title"><i
                            class="fa-solid fa-arrow-turn-down"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1</h5>
                    <p class="card-text text-center">已還車</p>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card h-10">
                <div class="card-body">
                    <h5 class="card-title"><i
                            class="fa-solid fa-car-side"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;15</h5>
                    <p class="card-text text-center">出租中</p>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card h-10">
                <div class="card-body">
                    <h5 class="card-title"><i
                            class="fa-solid fa-exclamation-circle"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2</h5>
                    <p class="card-text text-center">逾期</p>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card h-10">
                <div class="card-body">
                    <h5 class="card-title"><i
                            class="fa-solid fa-wrench"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0</h5>
                    <p class="card-text text-center">待維修</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 下方功能區 -->
    <div class="row mt-4 mb-4">
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h6 class="card-title">查詢訂單</h6>
                    <div class="input-group">
                        <form class="input-group" action="${pageContext.request.contextPath}/SearchReservationServlet" method="get">
                            <select class="form-select" name="type">
                                <option value="license">駕照號碼</option>
                                <option value="phone">電話號碼</option>
                            </select>
                            <input type="text" name="query" class="form-control" placeholder="請輸入對應內容">
                            <button type="submit" class="btn btn-success text-white">查詢</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h6 class="card-title">查詢車輛</h6>
                    <div class="input-group">
                        <input type="text" class="form-control" placeholder="車牌號碼">
                        <input type="text" class="form-control" placeholder="訂單編號">
                        <button class="btn btn-success">
                            <a href="back-page-3.html" class="text-white text-decoration-none">查詢</a>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <h6 class="card-title">車輛資訊</h6>
                        <select class="form-select form-select-sm w-auto">
                            <option>All Vehicle Types</option>
                        </select>
                    </div>
                    <div class="my-3 text-center">
                        <div
                                style="width: 160px; height: 160px; margin: 0 auto; background-color: #eee; border-radius: 50%; line-height: 160px;">
                            **串Chart.js
                        </div>
                    </div>
                    <ul class="list-unstyled small">
                        <li><span class="badge bg-success me-2">&nbsp;</span>可租用 - 2</li>
                        <li><span class="badge bg-primary me-2">&nbsp;</span>停用 - 1</li>
                        <li><span class="badge bg-info me-2">&nbsp;</span>交車中 - 1</li>
                        <li><span class="badge bg-warning me-2">&nbsp;</span>維修中 - 1</li>
                        <li><span class="badge bg-danger me-2">&nbsp;</span>已出租 - 10</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h6 class="card-title">車型租金一覽表</h6>

                    <table class="table table-bordered table-sm">
                        <thead class="table-dark">
                        <tr>
                            <th>車型</th>
                            <th>免費里程數</th>
                            <th>日租金</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>大型轎車</td>
                            <td>100</td>
                            <td>80</td>
                        </tr>
                        <tr>
                            <td>中型車</td>
                            <td>100</td>
                            <td>70</td>
                        </tr>
                        <tr>
                            <td>休旅車</td>
                            <td>100</td>
                            <td>150</td>
                        </tr>
                        <tr>
                            <td>豪華車</td>
                            <td>0</td>
                            <td>60</td>
                        </tr>
                        <tr>
                            <td>普通轎車</td>
                            <td>0</td>
                            <td>130</td>
                        </tr>
                        <tr>
                            <td>小型車</td>
                            <td>0</td>
                            <td>10</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>