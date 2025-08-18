<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { Client } from "@stomp/stompjs";

const connected = ref(false);
// const publicMessages = ref([]);
const privateMessages = ref([]);
const myUser = ref("ADMIN");
const toUser = ref("user1"); // 私訊對象
const draft = ref("");

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
      // privateMessages.value.push({
      //   ts: Date.now(),
      //   sender: body.sender,
      //   receiver: body.receiver,
      //   content: body.content,
      // });
      privateMessages.value.push({ ts: Date.now(), ...body });
      scrollSoon("private");
    });
    /*
    client.subscribe("/topic/public", (frame) => {
      const body = JSON.parse(frame.body);
      publicMessages.value.push({ ts: Date.now(), ...body });
      scrollSoon("public");
    });
    */
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
/*
function sendPublic() {
  if (
    !client ||
    !connected.value ||
    !draft.value.trim() ||
    !toUser.value.trim()
  )
    return;
  client.publish({
    destination: "/app/sendMessage",
    body: JSON.stringify({
      sender: myUser.value,
      receiver: toUser.value,
      content: draft.value.trim(),
    }),
  });
  draft.value = "";
}
*/

function sendPrivate() {
  if (
    !client ||
    !connected.value ||
    !draft.value.trim() ||
    !toUser.value.trim()
  )
    return;
  client.publish({
    destination: "/app/adminReply",
    body: JSON.stringify({
      sender: myUser.value,
      receiver: toUser.value,
      content: draft.value.trim(),
    }),
  });
  privateMessages.value.push({
    ts: Date.now(),
    sender: myUser.value,
    receiver: toUser.value,
    content: draft.value.trim(),
  });
  draft.value = "";
}

function scrollSoon(which) {
  requestAnimationFrame(() => {
    const el = document.querySelector(
      which === "public" ? "#publicList" : "#privateList"
    );
    if (el) el.scrollTop = el.scrollHeight;
  });
}

onMounted(() => {
  connect();
});
onBeforeUnmount(disconnect);
</script>

<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h2 class="text-h5 font-weight-bold">客服聊天室</h2>
      </v-col>

      <!-- 使用者設定 -->
      <v-col cols="6">
        <v-text-field v-model="myUser" label="我的 Username" outlined dense />
      </v-col>
      <v-col cols="6">
        <v-text-field
          v-model="toUser"
          label="對方 Username（私訊用）"
          outlined
          dense
        />
      </v-col>

      <!-- 私訊 -->
      <!-- 用戶圖片路徑 -->
      <v-col cols="12">
        <v-card outlined>
          <v-card-title class="text-subtitle-1">
            <!-- <v-avatar image="../img/default.png" size="80"></v-avatar> -->
            <v-icon size="x-large">mdi-account</v-icon>
            {{ toUser }}用戶圖片路徑</v-card-title
          >

          <v-divider />
          <v-card-text id="privateList" style="height: 250px; overflow-y: auto">
            <v-list density="compact">
              <v-list-item
                v-for="(m, index) in privateMessages"
                :key="index"
                :class="m.sender === myUser ? 'msg-sent' : 'msg-received'"
              >
                <v-list-item-content>
                  <v-list-item-title>
                    <strong v-if="m.sender !== myUser">{{ m.sender }} :</strong>
                    <span
                      ><strong>{{ m.content }}</strong></span
                    >
                  </v-list-item-title>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      <!-- 訊息輸入 -->
      <v-col cols="12" class="d-flex gap-2">
        <v-text-field
          v-model="draft"
          label="輸入訊息..."
          outlined
          dense
          hide-details
          @keydown.enter="sendPrivate"
        />
        <!-- <v-btn @click="sendPublic" :disabled="!connected" color="primary"
          >廣播</v-btn
        > -->
        <v-btn @click="sendPrivate" :disabled="!connected" color="secondary"
          >私訊</v-btn
        >
      </v-col>

      <!-- 廣播訊息 -->

      <!-- <v-col cols="6">
        <v-card outlined>
          <v-card-title class="text-subtitle-1"
            >廣播 /topic/public</v-card-title
          >
          <v-divider />
          <v-card-text id="publicList" style="height: 250px; overflow-y: auto">
            <v-list density="compact">
              <v-list-item v-for="m in publicMessages" :key="m.ts + m.content">
                <v-list-item-title>
                  <strong>{{ m.sender }}:</strong> {{ m.content }}
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col> -->
      <!-- 連線控制 -->
      <v-col cols="12" class="d-flex align-center gap-2">
        <v-btn @click="connect" :disabled="connected" color="success"
          >Connect</v-btn
        >
        <v-btn @click="disconnect" :disabled="!connected" color="error"
          >Disconnect</v-btn
        >
        <span :class="connected ? 'text-success' : 'text-error'">
          {{ connected ? "Connected" : "Disconnected" }}
        </span>
      </v-col>
    </v-row>
  </v-container>
</template>

<style>
.msg-sent {
  justify-content: flex-end;
  text-align: right;
}

.msg-received {
  justify-content: flex-start;
  text-align: left;
}
</style>
