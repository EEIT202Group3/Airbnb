<template>
  <!-- 主畫面區塊 -->
  <div class="chat-wrapper" v-show="chatVisible">
    <v-card width="350" class="elevation-8">
      <v-card-title class="text-h6 d-flex justify-space-between">
        即時聊天室
        <v-btn icon size="small" @click="chatVisible = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text class="chat-content">
        <div
          v-for="(m, index) in privateMessages"
          :key="index"
          class="chat-bubble"
          :class="m.sender === myUser ? 'msg-sent' : 'msg-received'"
        >
          <strong>{{ m.sender === myUser ? "你" : m.sender }}：</strong>
          <div class="message-content">{{ m.content }}</div>
        </div>
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions>
        <v-text-field
          v-model="draft"
          hide-details
          placeholder="輸入訊息"
          variant="outlined"
          density="compact"
          @keydown.enter="sendToAdmin"
        />
        <v-btn icon @click="sendToAdmin">
          <v-icon>mdi-send</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>

  <!-- 展開按鈕 -->
  <v-btn
    icon
    color="primary"
    class="chat-toggle"
    v-if="!chatVisible"
    @click="chatVisible = true"
  >
    <v-icon>mdi-chat</v-icon>
  </v-btn>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { Client } from "@stomp/stompjs";

const chatVisible = ref(false);

const connected = ref(false);
const draft = ref("");
const privateMessages = ref([]);
const myUser = ref("user1");
const toUser = ref("ADMIN"); // 私訊對象

let client;

function connect() {
  console.log("執行連接");

  if (client?.active) return; // 如果已經有連接則不再建立新的連接

  client = new Client({
    brokerURL: `ws://localhost:8080/ws-chat?username=${myUser.value}`,
    // webSocketFactory: () => new SockJS(url),
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    debug: (str) => console.debug("[STOMP]", str),
  });

  client.onConnect = () => {
    console.log("已連接，用戶名：", myUser.value);
    connected.value = true;

    client.subscribe("/user/queue/messages", (frame) => {
      const body = JSON.parse(frame.body);
      console.log(body);

      privateMessages.value.push({ ts: Date.now(), ...body });
      scrollSoon("private");
    });
  };

  client.onStompError = (frame) => {
    console.error("Broker error:", frame.headers["message"], frame.body);
    console.log("onStompError", frame);
  };

  client.onWebSocketClose = () => {
    connected.value = false;
    console.log("WebSocket closed");
  };

  client.activate();
  console.log(client.active);
}

function disconnect() {
  if (!client) return;
  try {
    client.deactivate(); // ⬅️ deactivate 回傳 Promise
  } finally {
    connected.value = false;
  }
}
function sendToAdmin() {
  if (!draft.value.trim()) return;
  if (!client || !connected.value) return;

  // 傳送訊息
  client.publish({
    destination: "/app/privateMessageToAdmin",
    body: JSON.stringify({
      sender: myUser.value,
      receiver: toUser.value, // 如果對管理員可固定為 "admin"
      content: draft.value.trim(),
    }),
  });

  // 顯示自己剛發出的訊息在聊天室中
  privateMessages.value.push({
    ts: Date.now(),
    sender: myUser.value,
    receiver: toUser.value,
    content: draft.value.trim(),
  });

  // 清空輸入框
  draft.value = "";

  // 模擬管理員回覆
  setTimeout(() => {
    privateMessages.value.push({
      ts: Date.now(),
      sender: "ADMIN",
      receiver: myUser.value,
      content: "我們已收到您的訊息。",
    });
  }, 1000);
}

function scrollSoon(which) {
  requestAnimationFrame(() => {
    const el = document.querySelector(
      which === "public" ? "#publicList" : "#privateList"
    );
    if (el) el.scrollTop = el.scrollHeight;
  });
}

function sendConnectedMessage() {
  const message = {
    sender: myUser.value,
    receiver: "ADMIN",
    content: "🟢 使用者已加入聊天室",
  };

  if (client && connected.value) {
    client.publish({
      destination: "/app/privateMessageToAdmin",
      body: JSON.stringify(message),
    });

    privateMessages.value.push({
      ...message,
      ts: Date.now(),
    });
  }
}

watch(chatVisible, (visable) => {
  if (visable) {
    connect();
    sendConnectedMessage();
  }
});

onBeforeUnmount(disconnect);
</script>

<style scoped>
.chat-content {
  max-height: 300px;
  overflow-y: auto;
  padding: 12px;
  background-color: #f9f9f9;
}

/* 訊息泡泡通用樣式 */
.chat-bubble {
  margin: 8px 0;
  padding: 10px;
  border-radius: 8px;
  word-break: break-word;
  white-space: pre-wrap;
  line-height: 1.4;
  max-width: 100%;
}

/* 自己發送 */
.msg-sent {
  background-color: #e3f2fd;
  align-self: flex-end;
  text-align: right;
}

/* 對方回應 */
.msg-received {
  background-color: #ffe0b2;
  align-self: flex-start;
  text-align: left;
}

.message-content {
  margin-top: 4px;
  font-size: 14px;
}


</style>
