package com.trangvi.Quan_ly_trai_cai_nghien.controller;

import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienRequestDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienResponseDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.service.NguoiCaiNghienService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nguoi-cai-nghien")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NguoiCaiNghienController {
    private final NguoiCaiNghienService nguoiCaiNghienService;

    // 1. Tìm kiếm và phân trang kết hợp
    @GetMapping
    public ResponseEntity<Page<NguoiCaiNghienResponseDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tenCax,
            @RequestParam(required = false) Integer idDanToc,
            Pageable pageable) {
        Page<NguoiCaiNghienResponseDTO> result = nguoiCaiNghienService.getAllAndSearch(keyword, tenCax, idDanToc, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 2. Lấy chi tiết hồ sơ
    @GetMapping("/{id}")
    public ResponseEntity<NguoiCaiNghienResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(nguoiCaiNghienService.getById(id));
    }

    // 3. Thêm mới hồ sơ (POST)
    @PostMapping
    public ResponseEntity<NguoiCaiNghienResponseDTO> create(@RequestBody NguoiCaiNghienRequestDTO dto) {
        return new ResponseEntity<>(nguoiCaiNghienService.create(dto), HttpStatus.CREATED);
    }

    // 4. Cập nhật sửa đổi hồ sơ (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<NguoiCaiNghienResponseDTO> update(@PathVariable Integer id, @RequestBody NguoiCaiNghienRequestDTO dto) {
        return ResponseEntity.ok(nguoiCaiNghienService.update(id, dto));
    }

    // 5. Xóa hồ sơ (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        nguoiCaiNghienService.delete(id);
        return ResponseEntity.ok("Đã xóa thành công hồ sơ có ID: " + id);
    }

    // 6. API Thống kê ô vuông xã
    @GetMapping("/theo-xa") // Map chính xác đường dẫn /api/nguoi-cai-nghien/theo-xa
    public ResponseEntity<List<Map<String, Object>>> getThongKeTheoXa() {
        List<Map<String, Object>> stats = nguoiCaiNghienService.getThongKeTheoXa();
        return ResponseEntity.ok(stats);
    }
}