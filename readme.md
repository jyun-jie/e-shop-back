
# 📦電商平台（Spring Boot + Vue 3）
簡介:
本專案為一個 以 Spring Boot + Vue 3 建構的全端電商平台，
實作 多角色（買家 / 賣家 / 管理員） 的完整交易流程，
包含商品管理、購物車快取、訂單拆單、第三方金流物流整合與賣家結算機制。

系統採用 JWT 角色權限控管，並整合 藍新金流（NewebPay）與物流服務，
可支援實際付款、出貨與金流 Callback 流程。

展示網址：
http://43.212.247.58/

前端:
https://github.com/jyun-jie/e-shop-front



## API 模組劃分
| Module        | spec       |
|---------------|------------|
| BuyerShopping | 商品瀏覽       |
| BuyerOrder    | 買家訂單       |
| Cart          | 購物車        |
| Image         | 圖片處理       |
| Logistics     | 物流         |
| Payment       | 金流         |
| SellerOrder   | 賣家訂單       |
| Seller        | 賣家結算       |
| SellerProduct | 賣家商品       |
| User          | 登入/註冊/角色申請 |

## 🧩 系統功能（Features）
### 👤 買家（Buyer）

* 使用者註冊 / 登入（JWT) <br>
* 商品瀏覽、分類、搜尋<br>
* 使用 Redis 儲存購物車（避免頻繁 DB 存取）<br>
* 下訂單與付款流程（藍新金流 / 物流）<br>
* 訂單查詢與付款狀態同步<br>

### 🛒賣家（Seller）

* 商品新增 / 修改 / 上架 / 刪除 管理<br>
* 賣家訂單管理
* 出貨與訂單狀態更新
* 賣家月結金額計算

### 🛠 後台（Admin）
* 使用者角色與權限控管

<br>

## 🔐 系統層級（System Level）
* JWT + Spring Security
  * User / Seller / Admin 權限分流

* Redis 快取
  * 購物車
  * 訂單確認 <br>
* 第三方金流 / 物流：
  * 含 Callback / NotifyURL <br>
  * 狀態更新與資料一致性控制
  
## 🔧 技術架構（Tech Stack）
### Backend
- Spring Boot
- Spring Security + JWT（身分驗證與角色授權）
- MyBatis（資料存取）
- MySQL（核心資料）
- Redis（購物車快取、訂單確認狀態）
- Docker / Docker Compose

### Frontend
- Vue 3
- RESTful API 呼叫

### Cloud / Infra
- AWS EC2
- AWS S3
- Nginx（Reverse Proxy）

### Third-party Integration
- 藍新金流（NewebPay）
   - 金流付款
   - 物流服務


## 資料庫設計
![dataStructure1.png](images%2FdataStructure1.png)

## 系統架構圖
目前系統部署於 單一 AWS EC2 節點，架構如下：

使用 Nginx 作為 Reverse Proxy
負責前端靜態資源服務 API 請求與第三方金流 Callback 轉發

後端服務以 Docker Compose 管理
Spring Boot API Server
MySQL（資料庫）
Redis（快取 / 購物車）
圖片與檔案資源儲存於 AWS S3
![架構.png](images%2F%E6%9E%B6%E6%A7%8B.png)