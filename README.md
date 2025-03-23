# BTL1_MAP (1st Deadline 23h59 24/3/2025)
# Main role:
## Người 1 - Giao diện & Component UI
 - Chuyển thiết kế từ Figma sang React + Tailwind (twin.macro).
 - Tạo các component tái sử dụng: nút bấm, input, modal, v.v.
 - Xây dựng các trang chính:
   - Trang chủ
   - Trang đặt vé
   - Trang giỏ hàng
## Người 2 - Camera & Xử lý video đầu vào
 - Tích hợp API camera (navigator.mediaDevices.getUserMedia).
 - Hiển thị camera trên giao diện.
 - Kiểm tra quyền truy cập camera.
 - Xử lý UI khi bật/tắt camera.
## Người 3 - Routing & Quản lý State
 - Define router cho các trang.
 - Quản lý state với Redux hoặc React Context.
 - Lưu thông tin đơn hàng vào local storage.
 - Điều hướng khi đặt vé xong.
# Contributors
1. *Create new branch for your submission*
   ```
   git branch branchName (a.k.a Role2, demo, ...)
   git checkout branchName
   git add .
   git commit -m "Messages"
   git push origin branchName
   ```
2. *How to merge your branch to main*
   ```
   git branch -a (to make sure you at main, if not use "git checkout main")
   git merge branchName
   git add .
   git commit -m "Messages"
   git push origin main
   ```
3. *How to update your branch*
   ```
   *git branch -a (make sure you not in main, if yes, plz use "git checkout branchName" && "git merge main")
   git add .
   git commit -m "messages"
   git push origin branchName
   ```
