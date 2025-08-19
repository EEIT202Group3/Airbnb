<template>
  <!-- 主聊天窗口 -->
  <div class="chat-wrapper" v-show="chatVisible">
    <v-card
      width="400"
      class="elevation-12 chat-card"
      :class="{ 'guest-mode': isGuest }"
    >
      <!-- 聊天室標題欄 -->
      <v-card-title
        class="chat-header d-flex justify-space-between align-center"
      >
        <div class="d-flex align-center">
          <v-icon class="me-2" color="white">mdi-chat</v-icon>
          <div class="header-info">
            <span class="text-h6">{{ isGuest ? "訪客客服" : "客服聊天" }}</span>
            <div class="connection-status">
              <v-chip
                size="x-small"
                :color="connected ? 'success' : 'error'"
                variant="flat"
              >
                {{ connected ? "已連接" : "未連接" }}
              </v-chip>
              <span v-if="isGuest" class="guest-label">訪客模式</span>
            </div>
          </div>
        </div>
        <v-btn icon size="small" variant="text" @click="closeChat">
          <v-icon color="white">mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-divider></v-divider>

      <!-- 訪客資訊輸入區 (首次使用) -->
      <v-card-text
        v-if="isGuest && !guestInfoSubmitted"
        class="guest-info-section"
      >
        <div class="text-center mb-4">
          <v-icon size="48" color="primary">mdi-account-question</v-icon>
          <h3 class="text-h6 mt-2">歡迎使用客服服務</h3>
          <p class="text-body-2 text-grey">
            請留下您的聯絡資訊，我們將為您提供更好的服務
          </p>
        </div>

        <v-form ref="guestForm" v-model="guestFormValid">
          <v-text-field
            v-model="guestInfo.name"
            label="您的稱呼"
            placeholder="請輸入姓名或稱呼"
            prepend-icon="mdi-account"
            variant="outlined"
            density="comfortable"
            :rules="[rules.required]"
            class="mb-3"
          />

          <!-- <v-text-field
            v-model="guestInfo.email"
            label="電子郵件 (可選)"
            placeholder="example@email.com"
            prepend-icon="mdi-email"
            variant="outlined"
            density="comfortable"
            :rules="guestInfo.email ? [rules.email] : []"
            class="mb-3"
          />

          <v-text-field
            v-model="guestInfo.phone"
            label="聯絡電話 (可選)"
            placeholder="0912-345-678"
            prepend-icon="mdi-phone"
            variant="outlined"
            density="comfortable"
            class="mb-3"
          /> -->

          <div class="d-flex gap-2">
            <v-btn
              color="primary"
              :disabled="!guestFormValid"
              @click="submitGuestInfo"
              block
            >
              開始對話
            </v-btn>
          </div>
        </v-form>
      </v-card-text>

      <!-- 聊天內容區域 -->
      <v-card-text
        v-if="!isGuest || guestInfoSubmitted"
        class="chat-content"
        ref="chatContentRef"
      >
        <!-- 歡迎訊息 -->
        <div v-if="privateMessages.length === 0" class="welcome-message">
          <v-icon size="48" color="primary" class="mb-2"
            >mdi-chat-outline</v-icon
          >
          <h4 class="text-h6 mb-2">{{ getWelcomeTitle() }}</h4>
          <p class="text-body-2 text-grey">
            {{ getWelcomeMessage() }}
          </p>
          <!-- 快速問題按鈕 -->
          <div class="quick-questions mt-4">
            <v-chip
              v-for="question in quickQuestions"
              :key="question"
              size="small"
              variant="outlined"
              color="primary"
              class="ma-1"
              @click="sendQuickMessage(question)"
            >
              {{ question }}
            </v-chip>
          </div>
        </div>

        <!-- 訊息列表 -->
        <div
          v-for="(m, index) in privateMessages"
          :key="`msg-${m.ts}-${index}`"
          class="chat-bubble"
          :class="{
            'msg-sent': m.sender === currentUserId,
            'msg-received': m.sender !== currentUserId,
            'system-message': m.type === 'system',
          }"
        >
          <div class="message-wrapper">
            <!-- 接收訊息的標題 -->
            <div
              class="message-header"
              v-if="m.sender !== currentUserId && m.type !== 'system'"
            >
              <div class="sender-info">
                <v-avatar size="20" class="me-1">
                  <v-icon size="16" color="primary">mdi-account-tie</v-icon>
                </v-avatar>
                <strong class="sender-name">{{
                  getSenderDisplayName(m.sender)
                }}</strong>
              </div>
              <span class="timestamp">{{ formatTime(m.ts) }}</span>
            </div>

            <!-- 訊息內容 -->
            <div
              class="message-content"
              :class="{ 'system-content': m.type === 'system' }"
            >
              {{ m.content }}
            </div>

            <!-- 發送訊息的時間戳 -->
            <div class="timestamp-sent" v-if="m.sender === currentUserId">
              <v-icon size="12" class="me-1">mdi-check</v-icon>
              {{ formatTime(m.ts) }}
            </div>
          </div>
        </div>

        <!-- 正在輸入指示器 -->
        <div v-if="adminTyping" class="typing-indicator">
          <v-avatar size="24" class="me-2">
            <v-icon size="16" color="primary">mdi-account-tie</v-icon>
          </v-avatar>
          <div class="typing-content">
            <div class="typing-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <small class="text-grey">客服正在輸入...</small>
          </div>
        </div>
      </v-card-text>

      <v-divider v-if="!isGuest || guestInfoSubmitted"></v-divider>

      <!-- 輸入區域 -->
      <v-card-actions
        v-if="!isGuest || guestInfoSubmitted"
        class="message-input-area pa-4"
      >
        <v-text-field
          v-model="draft"
          :placeholder="getInputPlaceholder()"
          variant="outlined"
          density="comfortable"
          :disabled="!connected"
          @keydown.enter.prevent="sendMessage"
          @input="handleTyping"
          class="flex-grow-1"
          hide-details
          autofocus
        >
          <template #prepend-inner>
            <v-btn
              icon
              size="small"
              variant="text"
              @click="showEmojiPicker = !showEmojiPicker"
            >
              <v-icon size="18">mdi-emoticon-outline</v-icon>
            </v-btn>
          </template>

          <template #append-inner>
            <v-btn
              icon
              size="small"
              :disabled="!connected || !draft.trim()"
              @click="sendMessage"
              color="primary"
            >
              <v-icon>mdi-send</v-icon>
            </v-btn>
          </template>
        </v-text-field>

        <!-- 表情符號選擇器 -->
        <v-menu
          v-model="showEmojiPicker"
          :close-on-content-click="false"
          location="top"
        >
          <template #activator="{ props }">
            <div v-bind="props"></div>
          </template>
          <v-card width="250">
            <v-card-text class="emoji-picker">
              <v-btn
                v-for="emoji in commonEmojis"
                :key="emoji"
                size="small"
                variant="text"
                @click="addEmoji(emoji)"
                class="emoji-btn"
              >
                {{ emoji }}
              </v-btn>
            </v-card-text>
          </v-card>
        </v-menu>
      </v-card-actions>

      <!-- 錯誤訊息 -->
      <v-alert
        v-if="errorMessage"
        type="error"
        variant="tonal"
        density="compact"
        class="ma-2"
        closable
        @click:close="errorMessage = ''"
      >
        {{ errorMessage }}
      </v-alert>

      <!-- 重連按鈕 -->
      <v-card-actions
        v-if="!connected && (!isGuest || guestInfoSubmitted)"
        class="pa-2"
      >
        <v-btn
          variant="outlined"
          color="primary"
          size="small"
          @click="connect"
          :loading="connecting"
          block
        >
          <v-icon start>mdi-refresh</v-icon>
          重新連接
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>

  <!-- 聊天按鈕 -->
  <v-btn
    class="chat-toggle elevation-8"
    color="primary"
    size="x-large"
    icon
    v-if="!chatVisible"
    @click="openChat"
  >
    <v-badge
      v-if="unreadCount > 0"
      :content="unreadCount > 99 ? '99+' : unreadCount"
      color="error"
      floating
    >
      <v-icon size="28">mdi-chat</v-icon>
    </v-badge>
    <v-icon v-else size="28">mdi-chat</v-icon>
  </v-btn>

  <!-- 離線提示 -->
  <v-snackbar
    v-model="offlineSnackbar"
    color="warning"
    timeout="3000"
    location="top"
  >
    您目前處於離線狀態，訊息將在重新連線後發送
    <template #actions>
      <v-btn variant="text" @click="offlineSnackbar = false"> 關閉 </v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
import {
  ref,
  computed,
  onMounted,
  onBeforeUnmount,
  watch,
  nextTick,
} from "vue";
import { Client } from "@stomp/stompjs";

// Props
const props = defineProps({
  userId: {
    type: String,
    default: null,
  },
  userName: {
    type: String,
    default: null,
  },
  userEmail: {
    type: String,
    default: null,
  },
  mode: {
    type: String,
    default: "auto", // 'user', 'guest', 'auto'
    validator: (value) => ["user", "guest", "auto"].includes(value),
  },
});

// 響應式數據
const chatVisible = ref(false);
const connected = ref(false);
const connecting = ref(false);
const draft = ref("");
const privateMessages = ref([]);
const errorMessage = ref("");
const adminTyping = ref(false);
const unreadCount = ref(0);
const offlineSnackbar = ref(false);
const showEmojiPicker = ref(false);
const chatContentRef = ref(null);

// 訪客相關
const guestForm = ref(null);
const guestFormValid = ref(false);
const guestInfoSubmitted = ref(false);
const guestInfo = ref({
  name: "",
  //email: "",
  //phone: "",
});

// 表單驗證規則
const rules = {
  required: (value) => !!value || "此欄位為必填",
  email: (value) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return !value || pattern.test(value) || "請輸入有效的電子郵件地址";
  },
};

// 常用表情符號
const commonEmojis = [
  "😊",
  "😄",
  "😅",
  "😂",
  "🤔",
  "👍",
  "👎",
  "❤️",
  "😢",
  "😮",
  "🙏",
  "👋",
];

// 快速問題
const quickQuestions = [
  "營業時間？",
  "如何下單？",
  "退換貨政策",
  "聯絡客服",
  "產品諮詢",
];

// 計算屬性
const isGuest = computed(() => {
  if (props.mode === "guest") return true;
  if (props.mode === "user") return false;
  return !props.userId; // auto 模式：沒有 userId 就是訪客
});

const currentUserId = computed(() => {
  if (isGuest.value) {
    return guestInfoSubmitted.value ? `guest_${Date.now()}` : "guest";
  }
  return props.userId || `user_${Date.now()}`;
});

const currentUserName = computed(() => {
  if (isGuest.value) {
    return guestInfoSubmitted.value ? guestInfo.value.name : "訪客";
  }
  return props.userName || "用戶";
});

// WebSocket 客戶端
let client = null;
let reconnectAttempts = 0;
let typingTimer = null;
const maxReconnectAttempts = 5;

// 連接到 WebSocket
function connect() {
  console.log("正在連接到客服系統...");

  if (client?.active) {
    console.log("已存在活動連接");
    return;
  }

  connecting.value = true;
  errorMessage.value = "";

  const baseUrl = "ws://localhost:8080/ws-chat";
  const wsUrl = isGuest.value
    ? `${baseUrl}?guest=true&username=${guestInfo.value.name}`
    : baseUrl;

  client = new Client({
    brokerURL: wsUrl,
    reconnectDelay: Math.min(1000 * Math.pow(2, reconnectAttempts), 30000),
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    debug: (str) => {
      if (process.env.NODE_ENV === "development") {
        console.debug("[STOMP]", str);
      }
    },
  });

  // 連接成功
  client.onConnect = (frame) => {
    console.log("✅ 客服聊天連接成功");
    connected.value = true;
    connecting.value = false;
    reconnectAttempts = 0;
    errorMessage.value = "";

    // 訂閱私人訊息 (Spring 會自動路由到當前用戶)
    client.subscribe("/user/queue/messages", (frame) => {
      try {
        const message = JSON.parse(frame.body);
        console.log(message, "收到訊息");

        privateMessages.value.push({
          // 若後端有回 timestamp（ISO字串），就轉為毫秒顯示；否則退回現在時間
          ts: message.timestamp
            ? new Date(message.timestamp).getTime()
            : Date.now(),
          sender: message.sender,
          receiver: message.receiver,
          content: message.content,
          type: message.type || "text",
          timestamp: message.timestamp, // 保留原始 ISO 給需要時用
        });

        if (!chatVisible.value) {
          unreadCount.value++;
        }
        scrollToBottom();
      } catch (error) {
        console.error("解析訊息失敗:", error);
      }
    });

    // 訂閱打字狀態
    client.subscribe("/user/queue/typing", (frame) => {
      try {
        const typingStatus = JSON.parse(frame.body);
        if (typingStatus.sender === "ADMIN") {
          adminTyping.value = typingStatus.typing;
          if (typingStatus.typing) {
            scrollToBottom();
          }
        }
      } catch (error) {
        console.error("解析打字狀態失敗:", error);
      }
    });

    // 發送連接通知
    if (guestInfoSubmitted.value || !isGuest.value) {
      sendConnectedMessage();
    }
  };

  // 連接錯誤
  client.onStompError = (frame) => {
    console.error("❌ STOMP 錯誤:", frame.headers["message"], frame.body);
    errorMessage.value = `連接錯誤: ${frame.headers["message"] || "未知錯誤"}`;
    connected.value = false;
    connecting.value = false;
  };

  // WebSocket 錯誤
  client.onWebSocketError = (error) => {
    console.error("❌ WebSocket 錯誤:", error);
    connected.value = false;
    connecting.value = false;

    if (!navigator.onLine) {
      offlineSnackbar.value = true;
    } else {
      errorMessage.value = "網絡連接失敗，請檢查網絡狀態";
      handleReconnect();
    }
  };

  // WebSocket 關閉
  client.onWebSocketClose = (event) => {
    console.log("WebSocket 連接已關閉:", event);
    connected.value = false;
    connecting.value = false;

    // if (event.code !== 1000 && chatVisible.value) {
    //   handleReconnect();
    // }
  };

  client.activate();
}

// 斷開連接
async function disconnect() {
  if (!client) return;

  try {
    console.log("正在斷開客服聊天連接...");
    await client.deactivate();
  } catch (error) {
    console.error("斷開連接時發生錯誤:", error);
  } finally {
    connected.value = false;
    connecting.value = false;
    reconnectAttempts = 0;
  }
}

// 重連處理
function handleReconnect() {
  if (reconnectAttempts < maxReconnectAttempts && chatVisible.value) {
    reconnectAttempts++;
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts - 1), 30000);

    console.log(`${delay / 1000}秒後嘗試第${reconnectAttempts}次重連...`);

    setTimeout(() => {
      if (!connected.value && chatVisible.value) {
        connect();
      }
    }, delay);
  } else if (reconnectAttempts >= maxReconnectAttempts) {
    errorMessage.value = "連接失敗次數過多，請檢查網絡後重新開啟聊天";
  }
}

// 提交訪客資訊
async function submitGuestInfo() {
  if (!guestForm.value) return;

  const { valid } = await guestForm.value.validate();
  if (!valid) return;

  guestInfoSubmitted.value = true;

  // 儲存訪客資訊到本地（可以發送到後端）
  console.log("訪客資訊:", guestInfo.value);

  // 連接 WebSocket
  connect();

  nextTick(() => {
    scrollToBottom();
  });
}

function sendMessage() {
  if (!draft.value.trim() || !client || !connected.value) return;

  const message = {
    sender: currentUserId.value,
    receiver: "ADMIN",
    content: draft.value.trim(),
    type: "text",
    isGuest: isGuest.value,
    timestamp: new Date().toISOString(),
  };
  console.log(currentUserId.value, "發送訊息:", message);

  try {
    client.publish({
      destination: "/app/privateMessageToAdmin",
      body: JSON.stringify(message),
    });

    // 前端本地也存同結構，顯示用
    privateMessages.value.push({
      ts: Date.now(),
      sender: message.sender,
      receiver: message.receiver,
      content: message.content,
      type: message.type,
      // 可視需要也存 message.timestamp
      timestamp: message.timestamp,
    });

    draft.value = "";
    scrollToBottom();
  } catch (error) {
    console.error("發送訊息失敗:", error);
    errorMessage.value = "發送失敗，請重試";
  }
}

// 發送快速問題
function sendQuickMessage(question) {
  draft.value = question;
  sendMessage();
}

// 發送連接通知
function sendConnectedMessage() {
  if (!client || !connected.value) return;

  const userLabel = isGuest.value
    ? guestInfo.value.name
      ? `訪客 ${guestInfo.value.name}`
      : "訪客"
    : currentUserName.value || "用戶";

  const message = {
    sender: currentUserId.value,
    receiver: "ADMIN",
    content: `🟢 ${userLabel} 加入了聊天室`,
    type: "system",
    isGuest: isGuest.value,
    timestamp: new Date().toISOString(),
  };

  try {
    client.publish({
      destination: "/app/privateMessageToAdmin",
      body: JSON.stringify(message),
    });

    privateMessages.value.push({
      ts: Date.now(),
      sender: message.sender,
      receiver: message.receiver,
      content: message.content,
      type: message.type,
      timestamp: message.timestamp,
    });

    scrollToBottom();
  } catch (error) {
    console.error("發送連接通知失敗:", error);
  }
}

// 處理輸入狀態
function handleTyping() {
  if (!client || !connected.value) return;

  client.publish({
    destination: "/app/queue",
    body: JSON.stringify({
      sender: currentUserId.value,
      receiver: "ADMIN",
      typing: true,
    }),
  });

  clearTimeout(typingTimer);

  typingTimer = setTimeout(() => {
    if (client && connected.value) {
      client.publish({
        destination: "/app/queue",
        body: JSON.stringify({
          sender: currentUserId.value,
          receiver: "ADMIN",
          typing: false,
        }),
      });
    }
  }, 3000);
}

// 添加表情符號
function addEmoji(emoji) {
  draft.value += emoji;
  showEmojiPicker.value = false;
}

// 打開聊天窗口
function openChat() {
  chatVisible.value = true;
  unreadCount.value = 0;

  if (!isGuest.value || guestInfoSubmitted.value) {
    if (!connected.value) {
      connect();
    }
  }
}

// 關閉聊天窗口
function closeChat() {
  chatVisible.value = false;
  showEmojiPicker.value = false;

  // 如果是訪客且還未提交資訊，重置狀態
  if (isGuest.value && !guestInfoSubmitted.value) {
    guestInfo.value = { name: "", email: "", phone: "" };
  }
}

// 滾動到底部
async function scrollToBottom() {
  await nextTick();
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight;
  }
}

// 格式化時間
function formatTime(timestamp) {
  const date = new Date(timestamp);
  return date.toLocaleTimeString("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
  });
}

// 獲取發送者顯示名稱
function getSenderDisplayName(sender) {
  if (sender === "ADMIN") return "客服";
  if (sender === "SYSTEM") return "系統";
  return sender;
}

// 獲取歡迎標題
function getWelcomeTitle() {
  return isGuest.value
    ? "歡迎使用線上客服"
    : `${currentUserName.value}，您好！`;
}

// 獲取歡迎訊息
function getWelcomeMessage() {
  return isGuest.value
    ? "我們很樂意為您提供協助，請選擇常見問題或直接輸入您的問題"
    : "有任何問題都可以在這裡詢問我們的客服團隊";
}

// 獲取輸入提示文字
function getInputPlaceholder() {
  return isGuest.value ? "請輸入您的問題..." : "輸入訊息...";
}

// 監聽聊天窗口可見性
watch(chatVisible, (visible) => {
  if (visible) {
    unreadCount.value = 0;
    nextTick(() => scrollToBottom());
  }
});

// 組件卸載時斷開連接
onBeforeUnmount(() => {
  clearTimeout(typingTimer);
  disconnect();
});

// 暴露方法給父組件
defineExpose({
  openChat,
  closeChat,
  connect,
  disconnect,
  sendMessage: sendMessage,
});
</script>

<style scoped>
.chat-wrapper {
  position: fixed;
  bottom: 10px;
  right: 20px;
  z-index: 1000;
  max-height: calc(100vh - 120px);
}

.chat-toggle {
  position: fixed !important;
  bottom: 20px;
  right: 20px;
  z-index: 1001;
}

.chat-card {
  border-radius: 16px !important;
  overflow: hidden;
  max-height: calc(100vh - 120px);
}

.guest-mode {
  border: 2px solid #ff9800;
}

.chat-header {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white !important;
  padding: 16px 20px;
}

.guest-mode .chat-header {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
}

.header-info {
  line-height: 1.2;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.guest-label {
  font-size: 0.75em;
  opacity: 0.9;
}

.guest-info-section {
  padding: 24px;
  background: linear-gradient(to bottom, #f8f9fa, white);
}

.chat-content {
  height: 450px;
  overflow-y: auto;
  padding: 16px;
  background-color: #fafafa;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.welcome-message {
  text-align: center;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
}

.chat-bubble {
  max-width: 85%;
  word-wrap: break-word;
}

.msg-sent {
  align-self: flex-end;
}

.msg-sent .message-wrapper {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white;
  border-radius: 18px 18px 4px 18px;
  padding: 12px 16px;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.3);
}

.msg-received {
  align-self: flex-start;
}

.msg-received .message-wrapper {
  background: white;
  color: #333;
  border-radius: 18px 18px 18px 4px;
  padding: 12px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
}

.system-message {
  align-self: center;
}

.system-message .message-wrapper {
  background: #e3f2fd;
  color: #1565c0;
  border-radius: 12px;
  padding: 8px 12px;
  font-size: 0.9em;
  border: 1px solid #bbdefb;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  font-size: 0.8em;
}

.sender-info {
  display: flex;
  align-items: center;
}

.sender-name {
  color: #1976d2;
  font-weight: 600;
}

.timestamp {
  color: #666;
  font-size: 0.75em;
}

.timestamp-sent {
  text-align: right;
  font-size: 0.75em;
  opacity: 0.7;
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.message-content {
  line-height: 1.4;
}

.system-content {
  font-style: italic;
  text-align: center;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 8px;
}

.typing-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.typing-dots {
  display: flex;
  gap: 3px;
}

.typing-dots span {
  width: 6px;
  height: 6px;
}
</style>
