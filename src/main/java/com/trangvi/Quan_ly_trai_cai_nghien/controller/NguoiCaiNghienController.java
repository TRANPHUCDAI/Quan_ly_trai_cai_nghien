package com.trangvi.Quan_ly_trai_cai_nghien.controller;

import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienRequestDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienResponseDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.entity.NguoiCaiNghien;
import com.trangvi.Quan_ly_trai_cai_nghien.service.NguoiCaiNghienService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nguoi-cai-nghien")
@RequiredArgsConstructor
public class NguoiCaiNghienController {
    private final NguoiCaiNghienService nguoiCaiNghienService;

    // 1. Lấy danh sách kết hợp bộ lọc phân trang (Trả về danh sách Response DTO siêu mượt)
    @GetMapping
    public ResponseEntity<Page<NguoiCaiNghienResponseDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tenCax,
            @RequestParam(required = false) Integer idDanToc,
            Pageable pageable) {

        // Đổi kiểu khai báo ở đây sang DTO luôn
        Page<NguoiCaiNghienResponseDTO> result = nguoiCaiNghienService.getAllAndSearch(keyword, tenCax, idDanToc, pageable);
        return new ResponseEntity<>(result, org.springframework.http.HttpStatus.OK);
    }

    // 2. Lấy chi tiết một người cai nghiện theo ID
    @GetMapping("/{id}")
    public ResponseEntity<NguoiCaiNghienResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(nguoiCaiNghienService.getById(id));
    }

    // 3. Đón nhận thông tin từ form gửi lên (Request DTO) để Thêm mới/Cập nhật dữ liệu
    @PostMapping
    public ResponseEntity<NguoiCaiNghienResponseDTO> createOrUpdate(@RequestBody NguoiCaiNghienRequestDTO dto) {
        return ResponseEntity.ok(nguoiCaiNghienService.save(dto));
    }

    // 4. Xóa một hồ sơ
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        nguoiCaiNghienService.delete(id);
        return ResponseEntity.ok("Đã xóa thành công hồ sơ có ID: " + id);
    }

    // 5. Thống kê số lượng (giữ nguyên để vẽ Chart)
    @GetMapping("/thong-ke/theo-xa")
    public ResponseEntity<List<Map<String, Object>>> getThongKe() {
        return ResponseEntity.ok(nguoiCaiNghienService.getThongKeTheoXa());
    }
}
