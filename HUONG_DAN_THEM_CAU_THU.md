# 📖 HƯỚNG DẪN THÊM VÀ QUẢN LÝ DỮ LIỆU THẺ CẦU THỦ (LifeOS)

Tài liệu này hướng dẫn bạn cách tùy biến, mở rộng bộ sưu tập thẻ cầu thủ trong LifeOS bằng tính năng **Tự Tạo Thẻ** và **Nhập Hàng Loạt Qua CSV** mà không cần sửa code ứng dụng.

---

## 1. CÁCH 1: TỰ TẠO THẺ CẦU THỦ TRỰC TIẾP TRÊN APP

Bạn có thể tự tạo thẻ cho chính mình, bạn bè hoặc danh thủ yêu thích ngay trong giao diện:
1. Vào tab **Tài chính** -> chọn tab con **Bộ Sưu Tập (FUT Cards)**.
2. Bấm nút **⭐ Tự Tạo Thẻ**.
3. Điền các trường thông tin:
   - **Tên cầu thủ**: Ví dụ: *Nguyễn Quang Hải*
   - **Câu lạc bộ**: Ví dụ: *Công An Hà Nội*
   - **Quốc tịch**: Ví dụ: *Việt Nam*
   - **Giải đấu**: Chọn từ danh sách (*Premier League, La Liga, Serie A, Bundesliga, Ligue 1, V-League, Icons, Heroes*)
   - **Vị trí**: *ST, CF, RW, LW, CAM, CM, CDM, CB, LB, RB, GK*
   - **Bậc thẻ**: *BRONZE, SILVER, GOLD, TOTW, HERO, ICON*
   - **Chỉ số OVR**: Kéo thanh trượt từ 50 đến 99
   - **6 chỉ số thành phần**: PAC, SHO, PAS, DRI, DEF, PHY
   - **Tiểu sử / Thành tích**: Viết ngắn gọn về phong cách thi đấu
4. Xem thẻ hiển thị trực quan (Live Preview) theo tỷ lệ chuẩn 5:7 với hiệu ứng đổi màu theo bậc.
5. Bấm **Lưu Thẻ Vào Bộ Sưu Tập**. Thẻ sẽ ngay lập tức được lưu vào cơ sở dữ liệu Room và sẵn sàng để lật thẻ 3D!

---

## 2. CÁCH 2: NHẬP DỮ LIỆU HÀNG LOẠT QUA CSV (BULK IMPORT)

Để thêm nhanh hàng chục hoặc hàng trăm cầu thủ cùng lúc:
1. Vào mục **Bộ Sưu Tập Thẻ** -> bấm nút **📁 Nhập/Xuất CSV**.
2. Dán nội dung các dòng cầu thủ theo đúng định dạng chuẩn dưới đây.
3. Bấm **Tiến Hành Nhập Thẻ**. App sẽ tự động phân tích và lưu vào cơ sở dữ liệu.

### 📋 Cấu Trúc File CSV (15 Cột)
```csv
Tên,CLB,Giải Đấu,Vị Trí,OVR,Bậc,Quốc Tịch,PAC,SHO,PAS,DRI,DEF,PHY,Chân,Tiểu Sử
```

### 🔍 Giải Thích Chi Tiết Từng Cột
| Cột | Ý nghĩa | Giá trị hợp lệ / Ví dụ |
| :--- | :--- | :--- |
| **Tên** | Tên đầy đủ của cầu thủ | *Nguyễn Quang Hải, Lionel Messi* |
| **CLB** | Tên câu lạc bộ chủ quản | *Arsenal, Real Madrid, Công An Hà Nội* |
| **Giải Đấu** | Giải đấu tham gia | *Premier League, La Liga, Serie A, Bundesliga, Ligue 1, Icons, Heroes, V-League* |
| **Vị Trí** | Vị trí chiến thuật trên sân | *ST, CF, RW, LW, CAM, CM, CDM, CB, LB, RB, GK* |
| **OVR** | Chỉ số tổng (Overall Rating) | Số nguyên từ *50* đến *99* |
| **Bậc** | Phân cấp độ hiếm của thẻ | *BRONZE, SILVER, GOLD, TOTW, HERO, ICON* |
| **Quốc Tịch** | Quốc gia thi đấu | *Việt Nam, Anh, Pháp, Brazil, Argentina, Đức...* |
| **PAC** | Tốc độ chạy (Pace) | *40 - 99* |
| **SHO** | Khả năng dứt điểm (Shooting) | *40 - 99* |
| **PAS** | Khả năng chuyền bóng (Passing) | *40 - 99* |
| **DRI** | Rê dắt bóng (Dribbling) | *40 - 99* |
| **DEF** | Chỉ số phòng ngự (Defending) | *30 - 99* |
| **PHY** | Sức mạnh thể lực (Physical) | *40 - 99* |
| **Chân** | Chân thuận | *Trái* hoặc *Phải* |
| **Tiểu Sử** | Mô tả phong cách thi đấu | Viết ngắn gọn 1-2 câu |

---

## 3. MẪU DỮ LIỆU CSV MẪU (SẴN SÀNG COPY & DÁN)

```csv
Tên,CLB,Giải Đấu,Vị Trí,OVR,Bậc,Quốc Tịch,PAC,SHO,PAS,DRI,DEF,PHY,Chân,Tiểu Sử
Quang Hải,CAHN,V-League,CAM,84,GOLD,Việt Nam,82,85,86,87,55,68,Trái,Nhạc trưởng hoa mỹ của bóng đá Việt Nam với những siêu phẩm cầu vồng tuyết
Tiến Linh,Bình Dương,V-League,ST,82,GOLD,Việt Nam,80,84,72,76,42,82,Phải,Cây săn bàn hàng đầu Đông Nam Á với khả năng chớp thời cơ nhạy bén
Hoàng Đức,Thể Công,V-League,CM,83,GOLD,Việt Nam,78,79,85,84,72,77,Trái,Quả bóng vàng Việt Nam với lối chơi đĩnh đạc và nhãn quan sắc bén
Văn Hậu,CAHN,V-League,LB,81,GOLD,Việt Nam,84,68,76,75,82,86,Trái,Hậu vệ biên dũng mãnh với thể hình lý tưởng và những cú sút xa sấm sét
Đặng Văn Lâm,Bình Định,V-League,GK,82,GOLD,Việt Nam,82,84,70,83,50,85,Phải,Thủ thành phản xạ xuất thần người gác đền tin cậy của đội tuyển
```

---

## 4. CƠ CHẾ GÓI THẺ VÀ BẢO HIỂM (PITY SYSTEM)

Trong Cửa Hàng Gói Thẻ:
1. **Gói Tân Thủ Đồng (50 Xu)**: Tỷ lệ Bronze 80%, Silver 20%.
2. **Gói Tiêu Chuẩn Bạc (100 Xu)**: Tỷ lệ Silver 75%, Gold 24%, TOTW 1%.
3. **Gói Vàng Cao Cấp (250 Xu)**: Tỷ lệ Gold 75%, TOTW 18%, Hero 6%, Icon 1%.
4. **Gói Ngôi Sao Đặc Biệt (500 Xu)**: Tỷ lệ Gold 85+ (45%), TOTW (40%), Hero (12%), Icon (3%).
5. **Gói Huyền Thoại Tối Thượng (1.200 Xu)**: 100% ĐẢM BẢO trúng Hero (60%) hoặc Icon (40%).
6. **Bảo hiểm Huyền thoại (Pity System)**: Sau 10 lần mở gói cao cấp mà không trúng Icon, hệ thống kích hoạt bảo hiểm để lần mở tiếp theo chắc chắn nhận 1 thẻ Icon Huyền Thoại!
