<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <!-- Footer -->
    <footer>
      <div class="footer-content">
        <div class="footer-section">
          <div class="footer-logo">
            <div class="logo-icon">
              <i class="bx bxs-hospital"></i>
            </div>
            <span>Bệnh viện Tâm Đức</span>
          </div>
          <p>
            Chăm sóc sức khỏe toàn diện với đội ngũ bác sĩ giàu kinh nghiệm và
            trang thiết bị hiện đại.
          </p>
          <div class="social-links">
            <a href="#" aria-label="Facebook"
              ><i class="bx bxl-facebook-circle"></i
            ></a>
            <a href="#" aria-label="Instagram"
              ><i class="bx bxl-instagram-alt"></i
            ></a>
            <a href="#" aria-label="Twitter"><i class="bx bxl-twitter"></i></a>
            <a href="#" aria-label="YouTube"><i class="bx bxl-youtube"></i></a>
          </div>
        </div>

        <div class="footer-section">
          <h3>Liên kết nhanh</h3>
          <ul>
            <li>
              <a href="index.html"
                ><i class="bx bx-chevron-right"></i> Trang chủ</a
              >
            </li>
            <li>
              <a href="departments.html"
                ><i class="bx bx-chevron-right"></i> Chuyên khoa</a
              >
            </li>
            <li>
              <a href="doctors.html"
                ><i class="bx bx-chevron-right"></i> Bác sĩ</a
              >
            </li>
            <li>
              <a href="blog.html"
                ><i class="bx bx-chevron-right"></i> Tin tức</a
              >
            </li>
            <li>
              <a href="appointments.html"
                ><i class="bx bx-chevron-right"></i> Lịch hẹn</a
              >
            </li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>Chuyên khoa</h3>
          <ul>
            <li>
              <a href="doctors.html?dept=cardiology"
                ><i class="bx bx-chevron-right"></i> Tim mạch</a
              >
            </li>
            <li>
              <a href="doctors.html?dept=internal"
                ><i class="bx bx-chevron-right"></i> Nội khoa</a
              >
            </li>
            <li>
              <a href="doctors.html?dept=pediatrics"
                ><i class="bx bx-chevron-right"></i> Nhi khoa</a
              >
            </li>
            <li>
              <a href="doctors.html?dept=surgery"
                ><i class="bx bx-chevron-right"></i> Ngoại khoa</a
              >
            </li>
            <li>
              <a href="doctors.html?dept=obstetrics"
                ><i class="bx bx-chevron-right"></i> Sản phụ khoa</a
              >
            </li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>Liên hệ</h3>
          <ul class="contact-info">
            <li>
              <i class="bx bxs-map"></i>
              <span>123 Đường ABC, Quận 1, TP.HCM</span>
            </li>
            <li>
              <i class="bx bxs-phone"></i>
              <span>1900 123 456</span>
            </li>
            <li>
              <i class="bx bxs-envelope"></i>
              <span>info@benhvientamduc.vn</span>
            </li>
            <li>
              <i class="bx bxs-time"></i>
              <span>24/7 - Cả tuần</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; 2025 Bệnh viện Tâm Đức. Tất cả quyền được bảo lưu.</p>
      </div>
    </footer>

    <script src="js/main.js"></script>
<script>
        function toggleDropdown() {
            const dropdown = document.getElementById('userDropdown');
            dropdown.classList.toggle('opacity-100');
            dropdown.classList.toggle('invisible');
            dropdown.classList.toggle('scale-100');
        }

        document.getElementById('menuToggle').addEventListener('click', function() {
            const mobileNav = document.getElementById('mobileNav');
            mobileNav.classList.toggle('hidden');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(event) {
            const userMenu = document.querySelector('.relative');
            if (userMenu && !userMenu.contains(event.target)) {
                const dropdown = document.getElementById('userDropdown');
                dropdown.classList.add('opacity-0', 'invisible', 'scale-95');
            }
        });
    </script>
    <script>
  const { tw, setup } = twind;
  setup({});
</script>
<script>
document.addEventListener("DOMContentLoaded", function() {
  const fallback = "https://www.fivebranches.edu/wp-content/uploads/2021/08/default-image.jpg";
  
  document.querySelectorAll("img").forEach(img => {
    img.onerror = function() {
      this.onerror = null; 
      this.src = fallback;
    };
  });
});
</script>
  </body>
</html>
