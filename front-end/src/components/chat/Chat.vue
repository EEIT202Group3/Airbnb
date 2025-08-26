<template>
  <div class="admin-chat-container">
    <!-- 連接狀態 -->
    <div
      class="connection-status"
      :class="{ connected, disconnected: !connected }"
    >
      <span v-if="connected">✅ 已連接 ({{ myUser }})</span>
      <span v-else>❌ 未連接</span>
      <button
        @click="connected ? disconnect() : connect()"
        class="connection-btn"
      >
        {{ connected ? "斷開連接" : "重新連接" }}
      </button>
    </div>

    <!-- 錯誤訊息 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- 聊天介面 -->
    <div class="chat-interface">
      <!-- 私訊區域 -->
      <div class="private-chat">
        <div class="chat-header">
          <h3>私人訊息</h3>
          <!-- <div class="recipient-selector">
            <label>回覆給:</label>
            <input
              v-model="toUser"
              placeholder="輸入客戶用戶名"
              class="recipient-input"
              :disabled="!connected"
            />
          </div> -->
        </div>

        <!-- 訊息列表 -->
        <div id="privateList" class="message-list" ref="privateListRef">
          <div
            v-for="msg in privateMessages"
            :key="msg.ts"
            :class="[
              'message-item',
              {
                sent: msg.sender === myUser,
                received: msg.sender !== myUser,
              },
            ]"
          >
            <div class="message-header">
              <span class="sender">{{ msg.sender }}</span>
              <span class="timestamp">{{ formatTime(msg.ts) }}</span>
            </div>
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>

        <!-- 發送訊息 -->
        <div class="message-input-container">
          <input
            v-model="draft"
            @keypress.enter="sendPrivate"
            placeholder="輸入訊息..."
            class="message-input"
            :disabled="!connected"
          />
          <button
            @click="sendPrivate"
            :disabled="!connected || !draft.trim()"
            class="send-btn"
          >
            發送
          </button>
        </div>
      </div>

      <!-- 在線用戶列表（可選） -->
      <div class="online-users" v-if="showOnlineUsers">
        <h4>在線客戶</h4>

        <div v-if="onlineUsers.length === 0" class="empty">
          目前沒有其他用戶在線
        </div>

        <div
          v-for="user in onlineUsers"
          :key="user"
          class="user-item"
          :class="{ active: user === selectedUser }"
        >
          <span>{{ user }}</span>
          <button @click="selectUser(user)" class="select-user-btn">
            {{ user === selectedUser ? "已選擇" : "選擇" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { Client } from "@stomp/stompjs";

// 響應式數據
const connected = ref(false);
const privateMessages = ref([]);
const myUser = ref("ADMIN");
const toUser = ref("");
const draft = ref("");
const errorMessage = ref("");
const onlineUsers = ref([]);
const showOnlineUsers = ref(true);
const privateListRef = ref(null);

const selectedUser = ref(""); // 目前選中的 receiver

// const isReadyToSend = computed(
//   () => !!selectedUser.value && !!draft.value.trim()
// );

let client;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;

function selectUser(user) {
  selectedUser.value = user;
  toUser.value = user; // 設定 receiver
}
function clearSelection() {
  selectedUser.value = "";
}

// 連接到 WebSocket
function connect() {
  console.log("嘗試連接到聊天伺服器...");

  if (client?.active) {
    console.log("已存在活動連接");
    return;
  }

  // 清除之前的錯誤訊息
  errorMessage.value = "";

  client = new Client({
    // 移除 URL 參數，依賴後端 Session 驗證
    brokerURL: "ws://localhost:8080/ws-chat",

    // 連接配置
    reconnectDelay: Math.min(1000 * Math.pow(2, reconnectAttempts), 30000), // 指數退避
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,

    // 調試信息
    debug: (str) => {
      if (process.env.NODE_ENV === "development") {
        console.debug("[STOMP]", str);
      }
    },
  });

  // 連接成功處理
  client.onConnect = (frame) => {
    console.log("✅ WebSocket 連接成功");
    console.log("連接資訊:", frame.headers);

    connected.value = true;
    reconnectAttempts = 0;
    errorMessage.value = "";

    // 1) 私訊（收自己 /user/** 的訊息）
    client.subscribe("/user/queue/messages", (frame) => {
      try {
        const m = JSON.parse(frame.body); // 後端回來就是 ChatMessage
        console.log(m);

        // 規範化：ts 供 UI 排序/顯示用；timestamp 保留後端原文
        privateMessages.value.push({
          ts: m.timestamp ? new Date(m.timestamp).getTime() : Date.now(),
          sender: m.sender,
          receiver: m.receiver,
          content: m.content,
          type: m.type || MSG_TYPE.TEXT,
          isGuest: m.isGuest ?? false,
          timestamp: m.timestamp,
        });

        scrollToBottom();
      } catch (e) {
        console.error("解析訊息失敗:", e);
      }
    });

    /*
    // 訂閱系統通知
    client.subscribe("/user/queue/", (frame) => {
      try {
        const notification = JSON.parse(frame.body);
        console.log("收到系統通知:", notification);
        handleSystemNotification(notification);
      } catch (error) {
        console.error("解析通知失敗:", error);
      }
    });
    */

    // 訂閱在線用戶列表更新
    client.subscribe("/topic/users", (frame) => {
      try {
        const users = JSON.parse(frame.body);
        console.log("用戶列表", users);

        onlineUsers.value = users.filter((user) => user !== myUser.value);
        console.log(onlineUsers.value);
      } catch (error) {
        console.error("解析用戶列表失敗:", error);
      }
    });
  };

  // 連接錯誤處理
  client.onStompError = (frame) => {
    console.error("❌ STOMP 錯誤:", frame.headers["message"], frame.body);
    errorMessage.value = `連接錯誤: ${frame.headers["message"] || "未知錯誤"}`;
    connected.value = false;
  };

  // WebSocket 錯誤處理
  client.onWebSocketError = (error) => {
    console.error("❌ WebSocket 錯誤:", error);

    if (error.code === 1006 || error.code === 1002) {
      errorMessage.value = "請先登入系統再使用聊天功能";
    } else {
      errorMessage.value = "網絡連接失敗，請檢查網絡狀態";
    }

    connected.value = false;
    handleReconnect();
  };

  // WebSocket 關閉處理
  client.onWebSocketClose = (event) => {
    console.log("WebSocket 連接已關閉:", event);
    connected.value = false;

    if (event.code !== 1000) {
      // 非正常關閉
      handleReconnect();
    }
  };

  // 激活客戶端
  client.activate();
}

// 斷開連接
async function disconnect() {
  if (!client) return;

  try {
    console.log("正在斷開連接...");
    await client.deactivate();
    console.log("連接已斷開");
  } catch (error) {
    console.error("斷開連接時發生錯誤:", error);
  } finally {
    connected.value = false;
    reconnectAttempts = 0;
  }
}

// 重連處理
function handleReconnect() {
  if (reconnectAttempts < maxReconnectAttempts) {
    reconnectAttempts++;
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts - 1), 30000);

    console.log(`${delay / 1000}秒後嘗試第${reconnectAttempts}次重連...`);

    setTimeout(() => {
      if (!connected.value) {
        connect();
      }
    }, delay);
  } else {
    errorMessage.value = "連接失敗次數過多，請手動重新連接";
  }
}
const MSG_TYPE = {
  TEXT: "text",
  SYSTEM: "system",
  ADMIN_REPLY: "admin_reply",
};

function sendPrivate() {
  if (!client || !connected.value || !draft.value.trim()) return;

  const message = {
    sender: myUser.value, // "ADMIN"（請確保與後端 Principal 名稱一致）
    receiver: toUser.value, // 目標使用者的 Principal 名稱（guest_xxx 或 userId）
    content: draft.value.trim(),
    type: MSG_TYPE.ADMIN_REPLY, // 或用 MSG_TYPE.TEXT，看你的後端邏輯
    isGuest: false,
    timestamp: new Date().toISOString(),
  };

  try {
    client.publish({
      destination: "/app/adminReply", // 對應後端 @MessageMapping("/adminReply")
      body: JSON.stringify(message),
    });
    console.log(message);

    // 本地回顯
    privateMessages.value.push({
      ts: Date.now(),
      ...message,
    });

    draft.value = "";
    scrollToBottom();
  } catch (e) {
    console.error("發送訊息失敗:", e);
    errorMessage.value = "發送失敗，請重試";
  }
}

// 處理系統通知
function handleSystemNotification(notification) {
  switch (notification.type) {
    case "user_online":
      if (!onlineUsers.value.includes(notification.user)) {
        onlineUsers.value.push(notification.user);
      }
      break;
    case "user_offline":
      onlineUsers.value = onlineUsers.value.filter(
        (user) => user !== notification.user
      );
      break;
    case "system_message":
      privateMessages.value.push({
        ts: Date.now(),
        sender: "SYSTEM",
        receiver: myUser.value,
        content: notification.message,
        type: "system",
      });
      scrollToBottom();
      break;
  }
}
// 滾動到底部
async function scrollToBottom() {
  await nextTick();
  if (privateListRef.value) {
    privateListRef.value.scrollTop = privateListRef.value.scrollHeight;
  }
}

// 格式化時間
function formatTime(timestamp) {
  const date = new Date(timestamp);
  return date.toLocaleTimeString("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
}

// 清除訊息
function clearMessages() {
  privateMessages.value = [];
}

// 組件掛載時自動連接
onMounted(() => {
  connect();
});

// 組件卸載時斷開連接
onBeforeUnmount(() => {
  disconnect();
});

// 暴露方法給父組件使用
defineExpose({
  connect,
  disconnect,
  clearMessages,
  sendPrivate,
});
</script>

<style scoped>
.admin-chat-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.connection-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-weight: bold;
}

.connection-status.connected {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.connection-status.disconnected {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.connection-btn {
  background: white;
  border: 1px solid #ddd;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.connection-btn:hover {
  background-color: #f8f9fa;
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 10px 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #f5c6cb;
}

.chat-interface {
  display: flex;
  gap: 20px;
}

.private-chat {
  flex: 2;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.chat-header {
  background-color: #f8f9fa;
  padding: 15px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recipient-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.recipient-input {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 150px;
}

.message-list {
  height: 400px;
  overflow-y: auto;
  padding: 15px;
  background-color: white;
}

.message-item {
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
  max-width: 80%;
}

.message-item.sent {
  background-color: #007bff;
  color: white;
  margin-left: auto;
  text-align: right;
}

.message-item.received {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
}

.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.8em;
  margin-bottom: 5px;
  opacity: 0.8;
}

.message-content {
  word-wrap: break-word;
}

.message-input-container {
  display: flex;
  padding: 15px;
  border-top: 1px solid #ddd;
  background-color: #f8f9fa;
}

.message-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  outline: none;
}

.send-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  margin-left: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.send-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.send-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.online-users {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  max-height: 500px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.select-user-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 3px 8px;
  border-radius: 3px;
  font-size: 0.8em;
  cursor: pointer;
}

.select-user-btn:hover {
  background-color: #218838;
}
</style>
