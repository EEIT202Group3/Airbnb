import axios from "axios";

const BASE_URL = "http://localhost:8080";

export async function fetchReviews(keyword, type) {
  try {
    const res = await axios.get(`${BASE_URL}/api/reviews`, {
      params: {
        keyword: keyword,
        type: type,
      },
      withCredentials: true,
    });
    return res.data;
  } catch (error) {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      alert("請先登入");
      return null;
    } else {
      console.error("取得評論資料失敗", error);
      throw error;
    }
  }
}

export async function getReviews(id) {
  try {
    const response = await axios.get(
      `${BASE_URL}/api/admins/reviews/get/${id}`,
      {
        withCredentials: true,
      }
    );
    return response.data;
  } catch (error) {
    console.error("取得評論失敗", error);
    throw error;
  }
}
