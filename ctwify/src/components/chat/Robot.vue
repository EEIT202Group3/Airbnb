  <template>
  <!-- 主聊天窗口 -->
  <div class="chat-wrapper" v-show="chatVisible">
    <v-card
      width="400"
      max-height="90vh"
      class="elevation-12 chat-card d-flex flex-column"
    >
      <!-- 標題欄 -->
      <v-card-title
        class="chat-header d-flex justify-space-between align-center bg-black"
      >
        <div class="d-flex align-center">
          <v-icon class="me-2" color="white">mdi-chat</v-icon>
          <div class="header-info">
            <span class="text-h6" style="color: white">客服聊天</span>
            <div class="connection-status">
              <v-chip
                size="x-small"
                :color="connected ? 'success' : 'error'"
                variant="flat"
              >
                {{ connected ? "已連接" : "未連接" }}
              </v-chip>
            </div>
          </div>
        </div>
        <v-btn icon size="small" variant="text" @click="closeChat">
          <v-icon color="white">mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-divider />

      <!-- 未登入提示 -->
      <v-card-text v-if="!isAuthenticated" class="pa-6 text-center">
        <v-icon size="48" color="primary">mdi-lock</v-icon>
        <h3 class="text-h6 mt-2 mb-1">請先登入後再使用客服聊天</h3>
        <p class="text-body-2 text-grey mb-4">您需要登入帳號才能與客服對話。</p>
        <v-btn color="primary" @click="goLogin" block>
          <v-icon start>mdi-login</v-icon> 前往登入
        </v-btn>
      </v-card-text>

      <!-- 聊天內容區（已登入） -->
      <v-card-text v-else class="chat-content" ref="chatContentRef">
        <!-- 歡迎訊息 -->
        <div v-if="privateMessages.length === 0" class="welcome-message">
          <v-icon size="48" color="primary" class="mb-2"
            >mdi-chat-outline</v-icon
          >
          <h4 class="text-h6 mb-2">{{ getWelcomeTitle() }}</h4>
          <p class="text-body-2 text-grey">
            {{ getWelcomeMessage() }}
          </p>
          <!-- <div class="quick-questions mt-4">
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
            </div> -->
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

            <div
              class="message-content"
              :class="{ 'system-content': m.type === 'system' }"
            >
              {{ m.content }}
            </div>

            <div class="timestamp-sent" v-if="m.sender === currentUserId">
              <v-icon size="12" class="me-1">mdi-check</v-icon>
              {{ formatTime(m.ts) }}
            </div>
          </div>
        </div>

        <!-- 正在輸入 -->
        <div v-if="adminTyping" class="typing-indicator">
          <v-avatar size="24" class="me-2">
            <v-icon size="16" color="primary">mdi-account-tie</v-icon>
          </v-avatar>
          <div class="typing-content">
            <div class="typing-dots">
              <span></span><span></span><span></span>
            </div>
            <small class="text-grey">客服正在輸入...</small>
          </div>
        </div>
      </v-card-text>

      <v-divider v-if="isAuthenticated" />

      <!-- 輸入區域（已登入） -->
      <v-card-actions v-if="isAuthenticated" class="message-input-area pa-4">
        <v-text-field
          v-model="draft"
          placeholder="輸入訊息..."
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

        <v-menu
          v-model="showEmojiPicker"
          :close-on-content-click="false"
          location="top"
        >
          <template #activator="{ props }"><div v-bind="props" /></template>
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

      <!-- 重連 -->
      <v-card-actions v-if="isAuthenticated && !connected" class="pa-2">
        <v-btn
          variant="outlined"
          color="primary"
          size="small"
          @click="connect"
          :loading="connecting"
          block
        >
          <v-icon start>mdi-refresh</v-icon> 重新連接
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>

  <!-- 浮動開關 -->
  <v-btn
    class="chat-toggle elevation-8"
    color="orange-lighten-1"
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
      <v-icon size="28" style="color: white">mdi-chat</v-icon>
    </v-badge>
    <v-icon v-else size="28" style="color: white">mdi-chat</v-icon>
  </v-btn>

  <!-- 離線提示 -->
  <v-snackbar
    v-model="offlineSnackbar"
    color="warning"
    timeout="3000"
    location="top"
  >
    您目前處於離線狀態，訊息將在重新連線後發送
    <template #actions
      ><v-btn variant="text" @click="offlineSnackbar = false"
        >關閉</v-btn
      ></template
    >
  </v-snackbar>
</template>

<script setup>
import {
  ref,
  computed,
  onBeforeUnmount,
  watch,
  nextTick,
  onMounted,
} from "vue";
import { Client } from "@stomp/stompjs";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useCustomerStore } from "@/stores/customer";

// Props（以 userId 是否存在來判斷已登入）
const props = defineProps({
  userId: { type: String, default: null },
  userName: { type: String, default: null },
  userEmail: { type: String, default: null },
});

const router = useRouter();

// 狀態
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

const customerStore = useCustomerStore();
const { customer } = storeToRefs(customerStore);
// 計算屬性：是否已登入
const isAuthenticated = computed(() => {
  console.log(customer.value);
  return customer.value;
});

// 當前使用者
const currentUserId = computed(() => customer.value.email);
const currentUserName = computed(() => customer.value.username || "用戶");

// 常用表情與快捷問題
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

/*
const quickQuestions = [
  "營業時間？",
  "如何下單？",
  "退換貨政策",
  "聯絡客服",
  "產品諮詢",
];*/

// STOMP
let client = null;
let reconnectAttempts = 0;
let typingTimer = null;
const maxReconnectAttempts = 5;

// 前往登入
function goLogin() {
  router.push("/login");
}

// 連線
function connect() {
  if (!isAuthenticated.value) {
    errorMessage.value = "請先登入後再使用客服聊天";
    return;
  }
  if (client?.active) return;

  connecting.value = true;
  errorMessage.value = "";

  // 建議用 http(s) + SockJS：/ws-chat 必須與後端 WebSocket 端點一致
  const wsUrl = "ws://localhost:8080/ws-chat";

  client = new Client({
    brokerURL: wsUrl,
    reconnectDelay: Math.min(1000 * Math.pow(2, reconnectAttempts), 30000),
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    debug: (str) => {
      if (import.meta.env.DEV) console.debug("[STOMP]", str);
    },
  });

  client.onConnect = (frame) => {
    console.log("連接資訊:", frame.headers);
    connected.value = true;
    connecting.value = false;
    reconnectAttempts = 0;
    errorMessage.value = "";

    // 私訊訂閱
    client.subscribe("/user/queue/messages", (frame) => {
      try {
        const message = JSON.parse(frame.body);
        privateMessages.value.push({
          ts: message.timestamp
            ? new Date(message.timestamp).getTime()
            : Date.now(),
          sender: message.sender,
          receiver: message.receiver,
          content: message.content,
          type: message.type || "text",
          timestamp: message.timestamp,
        });
        if (!chatVisible.value) unreadCount.value++;
        scrollToBottom();
      } catch (e) {
        console.error("解析訊息失敗:", e);
      }
    });

    // 打字狀態訂閱
    client.subscribe("/user/queue/typing", (frame) => {
      try {
        const typingStatus = JSON.parse(frame.body);
        if (typingStatus.sender === "ADMIN") {
          adminTyping.value = typingStatus.typing;
          if (typingStatus.typing) scrollToBottom();
        }
      } catch (e) {
        console.error("解析打字狀態失敗:", e);
      }
    });

    // 連線通知
    sendConnectedMessage();
  };

  client.onStompError = (frame) => {
    console.error("❌ STOMP 錯誤:", frame.headers["message"], frame.body);
    errorMessage.value = `連接錯誤: ${frame.headers["message"] || "未知錯誤"}`;
    connected.value = false;
    connecting.value = false;
  };

  client.onWebSocketError = (error) => {
    console.error("❌ WebSocket 錯誤:", error);
    connected.value = false;
    connecting.value = false;
    if (!navigator.onLine) {
      offlineSnackbar.value = true;
    } else {
      errorMessage.value = "網路連線失敗，請檢查網路狀態";
      //handleReconnect();
    }
  };

  client.onWebSocketClose = () => {
    connected.value = false;
    connecting.value = false;
    // 可視需要自動重連
  };

  client.activate();
}

// 斷線
async function disconnect() {
  if (!client) return;
  try {
    await client.deactivate();
  } catch (e) {
    console.error("斷開連接時發生錯誤:", e);
  } finally {
    connected.value = false;
    connecting.value = false;
    reconnectAttempts = 0;
  }
}

// 重連
function handleReconnect() {
  if (reconnectAttempts < maxReconnectAttempts && chatVisible.value) {
    reconnectAttempts++;
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts - 1), 30000);
    setTimeout(() => {
      if (!connected.value && chatVisible.value) connect();
    }, delay);
  } else if (reconnectAttempts >= maxReconnectAttempts) {
    errorMessage.value = "連接失敗次數過多，請檢查網路後重新開啟聊天";
  }
}

onMounted(() => {
  console.log("🌟 customer store 初始值:", customer.value);
});
// 發訊息
function sendMessage() {
  if (!draft.value.trim() || !client || !connected.value) return;
  console.log(customer.value);

  const message = {
    sender: customer.value.email,
    receiver: "ADMIN",
    content: draft.value.trim(),
    type: "text",
    timestamp: new Date().toISOString(),
  };

  console.log(message.sender);

  try {
    client.publish({
      destination: "/app/privateMessageToAdmin",
      body: JSON.stringify(message),
    });
    // 本地回顯
    privateMessages.value.push({
      ts: Date.now(),
      sender: message.sender,
      receiver: message.receiver,
      content: message.content,
      type: message.type,
      timestamp: message.timestamp,
    });
    draft.value = "";
    scrollToBottom();
  } catch (e) {
    console.error("發送訊息失敗:", e);
    errorMessage.value = "發送失敗，請重試";
  }
}

// 快速問題
/*function sendQuickMessage(q) {
  messages.value.push({
    sender: customer.value.userId,
    content: text,
    type: "text",
    timestamp: new Date().toISOString(),
  });
}*/

// 連線通知
function sendConnectedMessage() {
  if (!client || !connected.value) return;
  const message = {
    sender: currentUserId.value,
    receiver: "ADMIN",
    content: `🟢 ${currentUserName.value} 加入了聊天室`,
    type: "system",
    timestamp: new Date().toISOString(),
  };
  try {
    client.publish({
      destination: "/app/privateMessageToAdmin",
      body: JSON.stringify(message),
    });
    privateMessages.value.push({ ts: Date.now(), ...message });
    scrollToBottom();
  } catch (e) {
    console.error("發送連接通知失敗:", e);
  }
}

// 輸入狀態
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

// emoji
function addEmoji(emoji) {
  draft.value += emoji;
  showEmojiPicker.value = false;
}

// 開啟/關閉
function openChat() {
  chatVisible.value = true;
  unreadCount.value = 0;
  if (isAuthenticated.value && !connected.value) connect();
}
function closeChat() {
  chatVisible.value = false;
  showEmojiPicker.value = false;
  disconnect();
}

// 滾動到底
async function scrollToBottom() {
  await nextTick();
  if (chatContentRef.value)
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight;
}

// 顯示用函式
function formatTime(ts) {
  const date = new Date(ts);
  return date.toLocaleTimeString("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
  });
}
function getSenderDisplayName(sender) {
  if (sender === "ADMIN") return "客服";
  if (sender === "SYSTEM") return "系統";
  return sender;
}
function getWelcomeTitle() {
  return `${currentUserName.value}，您好！`;
}
function getWelcomeMessage() {
  return "有任何問題都可以在這裡詢問我們的客服團隊";
}

// 監看
watch(chatVisible, (visible) => {
  if (visible) {
    unreadCount.value = 0;
    nextTick(() => scrollToBottom());
  }
});

// 卸載
onBeforeUnmount(() => {
  clearTimeout(typingTimer);
  disconnect();
});

// 對外暴露
defineExpose({ openChat, closeChat, connect, disconnect, sendMessage });
</script>

<style scoped>
.chat-wrapper {
  position: fixed;
  bottom: 10px;
  right: 20px;
  z-index: 1000;
  max-height: calc(100vh - 120px);
  margin-bottom: 100px;
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
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
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
