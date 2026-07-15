package com.trangvi.Quan_ly_trai_cai_nghien.service;

import com.opencsv.CSVReader;
import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienRequestDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienResponseDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.entity.CaxLapHs;
import com.trangvi.Quan_ly_trai_cai_nghien.entity.DanToc;
import com.trangvi.Quan_ly_trai_cai_nghien.entity.NguoiCaiNghien;
import com.trangvi.Quan_ly_trai_cai_nghien.repository.CaxLapHsRepository;
import com.trangvi.Quan_ly_trai_cai_nghien.repository.DanTocRepository;
import com.trangvi.Quan_ly_trai_cai_nghien.repository.NguoiCaiNghienRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NguoiCaiNghienServiceImpl implements NguoiCaiNghienService, CommandLineRunner {
    private final NguoiCaiNghienRepository nguoiCaiNghienRepository;
    private final DanTocRepository danTocRepository;
    private final CaxLapHsRepository caxLapHsRepository;

    @Override
    public Page<NguoiCaiNghienResponseDTO> getAllAndSearch(String keyword, String tenCax, Integer idDanToc, Pageable pageable) {
        Page<NguoiCaiNghien> entities = nguoiCaiNghienRepository.searchNguoiCaiNghien(keyword, tenCax, idDanToc, pageable);
        return entities.map(this::convertToResponseDTO);
    }

    @Override
    public NguoiCaiNghienResponseDTO getById(Integer id) {
        NguoiCaiNghien entity = nguoiCaiNghienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người cai nghiện có ID: " + id));
        return convertToResponseDTO(entity);
    }

    @Override
    @Transactional
    public NguoiCaiNghienResponseDTO create(NguoiCaiNghienRequestDTO dto) {
        NguoiCaiNghien entity = new NguoiCaiNghien();
        mapDtoToEntity(dto, entity);
        NguoiCaiNghien savedEntity = nguoiCaiNghienRepository.save(entity);
        return convertToResponseDTO(savedEntity);
    }

    @Override
    @Transactional
    public NguoiCaiNghienResponseDTO update(Integer id, NguoiCaiNghienRequestDTO dto) {
        NguoiCaiNghien entity = nguoiCaiNghienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người cai nghiện có ID: " + id));
        mapDtoToEntity(dto, entity);
        NguoiCaiNghien updatedEntity = nguoiCaiNghienRepository.save(entity);
        return convertToResponseDTO(updatedEntity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!nguoiCaiNghienRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy hồ sơ đối tượng cần xóa với ID: " + id);
        }
        nguoiCaiNghienRepository.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getThongKeTheoXa() {
        List<Object[]> rawData = nguoiCaiNghienRepository.countByCax();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Map<String, Object> map = new HashMap<>();
            String tenXa = row[0] != null ? cleanString(row[0].toString()) : "Chưa xác định";
            map.put("tenXa", tenXa);
            map.put("soLuong", row[1]);

            Integer idCaxLap = caxLapHsRepository.findByTenCax(tenXa)
                    .map(CaxLapHs::getIdCax)
                    .orElse(null);
            map.put("idCaxLap", idCaxLap);

            result.add(map);
        }
        return result;
    }

    @Override
    public void run(String... args) throws Exception {
        importCsvData();
    }

    @Override
    @Transactional
    public void importCsvData() {
        try {
            if (nguoiCaiNghienRepository.count() > 0) {
                log.info("Dữ liệu đã tồn tại trong database, bỏ qua bước khởi tạo tự động từ CSV.");
                return;
            }
        } catch (Exception e) {
            log.warn("Chưa khởi tạo dữ liệu hoặc bảng trống. Hệ thống bắt đầu nạp dữ liệu...");
        }

        log.info("Bắt đầu đọc dữ liệu hành chính trực tiếp từ Classpath Resource...");

        try (var inputStream = getClass().getClassLoader().getResourceAsStream("Du_lieu_nguoi_cai_nghien.csv")) {
            if (inputStream == null) {
                log.error("Không tìm thấy file 'Du_lieu_nguoi_cai_nghien.csv' trong tài nguyên hệ thống!");
                return;
            }

            try (CSVReader reader = new CSVReader(new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
                List<String[]> lines = reader.readAll();
                if (lines.isEmpty()) {
                    log.warn("File CSV trong tài nguyên trống rỗng.");
                    return;
                }

                String[] headers = lines.get(0);
                Map<String, Integer> headMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    headMap.put(headers[i].trim().toLowerCase(), i);
                }

                int successCount = 0;
                int failCount = 0;

                for (int i = 1; i < lines.size(); i++) {
                    String[] row = lines.get(i);

                    try {
                        NguoiCaiNghien n = new NguoiCaiNghien();

                        // 1. CHUẨN HÓA HỌ VÀ TÊN (Viết hoa chữ đầu, làm sạch dấu cách thừa)
                        String rawHoTen = getValueByHeader(row, headMap, "họ và tên");
                        n.setHoVaTen(capitalizeWords(rawHoTen));

                        // 2. CHUẨN HÓA CCCD/CMND (Loại bỏ khoảng trắng, dấu chấm)
                        String rawCccd = getValueByHeader(row, headMap, "cccd/cmnd");
                        n.setCccdCmnd(cleanCccd(rawCccd));

                        n.setNgayCap(parseDate(getValueByHeader(row, headMap, "ngày cấp")));
                        n.setCaiNghienLanThu(parseInt(getValueByHeader(row, headMap, "cai nghiện lần thứ")));
                        n.setThoidiemSdMaTuyDau(parseDate(getValueByHeader(row, headMap, "thời điểm sử dụng ma túy lần đầu")));
                        n.setTienAn(parseInt(getValueByHeader(row, headMap, "tiền án")));
                        n.setTienSu(parseInt(getValueByHeader(row, headMap, "tiền sự")));

                        LocalDate ngaySinh = parseDate(getValueByHeader(row, headMap, "ngày sinh"));
                        n.setNgaySinh(ngaySinh);

                        // 3. TỰ ĐỘNG TÍNH TUỔI THEO NGÀY SINH (Nếu ngày sinh hợp lệ)
                        String tuoiStr = getValueByHeader(row, headMap, "tuổi");
                        if (tuoiStr == null) tuoiStr = getValueByHeader(row, headMap, "tuôi");
                        int tuoiGốc = parseInt(tuoiStr);
                        n.setTuoi(calculateActualAge(ngaySinh, tuoiGốc));

                        n.setQueQuan(capitalizeWords(getValueByHeader(row, headMap, "quê quán")));
                        n.setHkThuongTru(getValueByHeader(row, headMap, "hk thường trú"));
                        n.setDiaChiSauSatNhap(getValueByHeader(row, headMap, "địa chỉ sau sát nhập"));
                        n.setNgayVaoCs(parseDate(getValueByHeader(row, headMap, "ngày vào cs")));
                        n.setDuKienNgayVe(parseDate(getValueByHeader(row, headMap, "dự kiến ngày về")));
                        n.setDuKienVe2026(parseInt(getValueByHeader(row, headMap, "dự kiến về 2026")));
                        n.setDuKienVe2027(parseInt(getValueByHeader(row, headMap, "dự kiến về 2027")));

                        // 4. TIỀN XỬ LÝ HÌNH THỨC CAI NGHIỆN (Chỉ nhận Bắt buộc, Tự nguyện, Lưu trú)
                        String rawHinhThuc = getValueByHeader(row, headMap, "hình thức cai nghiện");
                        n.setHinhThucCaiNghien(normalizeHinhThuc(rawHinhThuc));

                        n.setSoThangDaCh(parseInt(getValueByHeader(row, headMap, "số tháng đã c/h")));
                        n.setSoNgayDaCh(parseInt(getValueByHeader(row, headMap, "số ngày đã c/h")));
                        n.setQdToaAn(getValueByHeader(row, headMap, "qđ tòa án"));
                        n.setThoiGianCh(parseInt(getValueByHeader(row, headMap, "thời gian ch")));
                        n.setTandKhuVuc(getValueByHeader(row, headMap, "tand khu vực"));
                        n.setQdXetGiam(getValueByHeader(row, headMap, "qđ xét giảm"));
                        n.setDaiDienGiaDinh(capitalizeWords(getValueByHeader(row, headMap, "đai diện gia đình")));
                        n.setTrinhDo(getValueByHeader(row, headMap, "trình độ"));
                        n.setGhiChu(getValueByHeader(row, headMap, "ghi chú"));

                        String tenDanToc = cleanString(getValueByHeader(row, headMap, "dân tộc"));
                        if (tenDanToc != null && !tenDanToc.isEmpty()) {
                            // Viết hoa chữ cái đầu dân tộc (Kinh, Tày, Nùng...)
                            final String formattedDanToc = capitalizeWords(tenDanToc);
                            DanToc dt = danTocRepository.findByTenDanToc(formattedDanToc)
                                    .orElseGet(() -> danTocRepository.save(new DanToc(null, formattedDanToc)));
                            n.setDanToc(dt);
                        }

                        String tenCax = cleanString(getValueByHeader(row, headMap, "cax lập hs"));
                        if (tenCax != null && !tenCax.isEmpty()) {
                            final String formattedCax = formatStreetName(tenCax);
                            CaxLapHs cax = caxLapHsRepository.findByTenCax(formattedCax)
                                    .orElseGet(() -> caxLapHsRepository.save(new CaxLapHs(null, formattedCax)));
                            n.setCaxLapHs(cax);
                        } else {
                            CaxLapHs fallbackCax = caxLapHsRepository.findAll().stream().findFirst()
                                    .orElseGet(() -> caxLapHsRepository.save(new CaxLapHs(null, "Chưa xác định")));
                            n.setCaxLapHs(fallbackCax);
                        }

                        nguoiCaiNghienRepository.save(n);
                        successCount++;
                    } catch (Exception rowEx) {
                        failCount++;
                        log.warn("Bỏ qua dòng số {} do lỗi phân tích dữ liệu: {}", i + 1, rowEx.getMessage());
                    }
                }
                log.info(">>> HOÀN THÀNH TIẾN TRÌNH: Import thành công {} dòng, thất bại {} dòng. <<<", successCount, failCount);
            }
        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi nạp dữ liệu từ tài nguyên hệ thống: ", e);
        }
    }

    // ==========================================
    // CÁC HÀM TIỀN XỬ LÝ DỮ LIỆU CHUẨN HOÁ
    // ==========================================

    // Viết hoa chữ cái đầu của mỗi từ (Ví dụ: "trần phúc đại" -> "Trần Phúc Đại")
    private String capitalizeWords(String str) {
        String clean = cleanString(str);
        if (clean == null || clean.isEmpty()) return null;

        String[] words = clean.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    // Chuẩn hóa tên Phường/Xã lập hồ sơ
    private String formatStreetName(String str) {
        String clean = capitalizeWords(str);
        if (clean == null) return null;
        if (clean.toLowerCase().startsWith("xã ") || clean.toLowerCase().startsWith("thị trấn ")) {
            return clean;
        }
        return clean; // Giữ nguyên tên đã viết hoa đẹp đẽ
    }

    // Làm sạch số CMND/CCCD (Chỉ giữ lại các chữ số)
    private String cleanCccd(String str) {
        String clean = cleanString(str);
        if (clean == null) return null;
        return clean.replaceAll("[^0-9]", ""); // Xóa toàn bộ ký tự không phải số
    }

    // Tiền xử lý chuẩn hóa Hình thức cai nghiện về 3 nhóm duy nhất
    private String normalizeHinhThuc(String str) {
        String clean = cleanString(str);
        if (clean == null) return "Bắt buộc"; // Nhóm mặc định nếu bị rỗng

        String lower = clean.toLowerCase();
        if (lower.contains("tự nguyện") || lower.contains("tu nguyen")) {
            return "Tự nguyện";
        } else if (lower.contains("lưu trú") || lower.contains("luu tru")) {
            return "Lưu trú";
        }
        return "Bắt buộc"; // Các trường hợp khác quy về bắt buộc
    }

    // Tự động tính toán lại tuổi thực tế dựa trên ngày sinh và năm hiện tại 2026
    private int calculateActualAge(LocalDate ngaySinh, int backupTuoi) {
        if (ngaySinh == null) return backupTuoi > 0 ? backupTuoi : 0;
        try {
            LocalDate today = LocalDate.now(); // Sử dụng thời gian hiện tại
            return Period.between(ngaySinh, today).getYears();
        } catch (Exception e) {
            return backupTuoi;
        }
    }

    private String getValueByHeader(String[] row, Map<String, Integer> headMap, String headerName) {
        Integer index = headMap.get(headerName.toLowerCase());
        if (index != null && index < row.length) {
            return cleanString(row[index]);
        }
        return null;
    }

    private String cleanString(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("NaN")) return null;
        return val.trim().replaceAll("\\s+", " ");
    }

    // 1. CHUẨN HOÁ SỐ NGUYÊN AN TOÀN (Bao dung 100%, không bao giờ quăng lỗi)
    private int parseInt(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("NaN") || val.equalsIgnoreCase("null")) {
            return 0;
        }
        try {
            // Loại bỏ các ký tự không phải số trước khi chuyển đổi
            String cleanVal = val.trim().replaceAll("[^0-9.-]", "");
            if (cleanVal.isEmpty()) return 0;
            return (int) Double.parseDouble(cleanVal);
        } catch (Exception e) {
            return 0; // Trả về 0 nếu dữ liệu là chữ
        }
    }

    // 2. CHUẨN HOÁ NGÀY THÁNG AN TOÀN (Bao dung 100%, trả về null thay vì quăng lỗi làm sập dòng)
    private LocalDate parseDate(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("NaN") || val.equalsIgnoreCase("null")) {
            return null;
        }
        String cleanVal = val.trim();

        // Hỗ trợ trường hợp người dùng chỉ nhập mỗi năm sinh (Ví dụ: "1995" -> tự động chuyển thành "01/01/1995")
        if (cleanVal.matches("^\\d{4}$")) {
            cleanVal = "01/01/" + cleanVal;
        }

        List<String> formats = Arrays.asList(
                "yyyy-MM-dd", "d/M/yyyy", "dd/MM/yyyy", "d-M-yyyy",
                "dd-MM-yyyy", "yyyy/MM/dd", "yyyy.MM.dd", "yyyy"
        );

        for (String format : formats) {
            try {
                return LocalDate.parse(cleanVal, DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {}
        }

        log.warn("Không thể chuyển đổi ngày: '{}', hệ thống tự động bỏ qua ô này và tiếp tục lưu.", val);
        return null; // Trả về null an toàn để dòng dữ liệu đó vẫn được lưu bình thường
    }

    private NguoiCaiNghienResponseDTO convertToResponseDTO(NguoiCaiNghien n) {
        NguoiCaiNghienResponseDTO dto = new NguoiCaiNghienResponseDTO();
        dto.setTt(n.getTt());
        dto.setHoVaTen(n.getHoVaTen());
        dto.setCccdCmnd(n.getCccdCmnd());
        dto.setNgayCap(n.getNgayCap());
        dto.setCaiNghienLanThu(n.getCaiNghienLanThu());
        dto.setThoidiemSdMaTuyDau(n.getThoidiemSdMaTuyDau());
        dto.setTienAn(n.getTienAn());
        dto.setTienSu(n.getTienSu());
        dto.setNgaySinh(n.getNgaySinh());
        dto.setTuoi(n.getTuoi());
        dto.setQueQuan(n.getQueQuan());
        dto.setHkThuongTru(n.getHkThuongTru());
        dto.setDiaChiSauSatNhap(n.getDiaChiSauSatNhap());
        dto.setNgayVaoCs(n.getNgayVaoCs());
        dto.setDuKienNgayVe(n.getDuKienNgayVe());
        dto.setDuKienVe2026(n.getDuKienVe2026());
        dto.setDuKienVe2027(n.getDuKienVe2027());
        dto.setHinhThucCaiNghien(n.getHinhThucCaiNghien());
        dto.setSoThangDaCh(n.getSoThangDaCh());
        dto.setSoNgayDaCh(n.getSoNgayDaCh());
        dto.setQdToaAn(n.getQdToaAn());
        dto.setThoiGianCh(n.getThoiGianCh());
        dto.setTandKhuVuc(n.getTandKhuVuc());
        dto.setQdXetGiam(n.getQdXetGiam());
        dto.setDaiDienGiaDinh(n.getDaiDienGiaDinh());
        dto.setTrinhDo(n.getTrinhDo());
        dto.setGhiChu(n.getGhiChu());

        if (n.getCaxLapHs() != null) {
            dto.setIdCaxLap(n.getCaxLapHs().getIdCax());
            dto.setTenCaxLap(n.getCaxLapHs().getTenCax());
        }
        if (n.getDanToc() != null) {
            dto.setIdDanToc(n.getDanToc().getIdDanToc());
            dto.setTenDanToc(n.getDanToc().getTenDanToc());
        }
        return dto;
    }

    private void mapDtoToEntity(NguoiCaiNghienRequestDTO dto, NguoiCaiNghien n) {
        n.setHoVaTen(capitalizeWords(dto.getHoVaTen()));
        n.setCccdCmnd(cleanCccd(dto.getCccdCmnd()));
        n.setCaiNghienLanThu(dto.getCaiNghienLanThu() != null ? dto.getCaiNghienLanThu() : 0);
        n.setTienAn(dto.getTienAn() != null ? dto.getTienAn() : 0);
        n.setTienSu(dto.getTienSu() != null ? dto.getTienSu() : 0);

        n.setQueQuan(capitalizeWords(dto.getQueQuan()));
        n.setHkThuongTru(cleanString(dto.getHkThuongTru()));
        n.setDiaChiSauSatNhap(cleanString(dto.getDiaChiSauSatNhap()));

        n.setHinhThucCaiNghien(normalizeHinhThuc(dto.getHinhThucCaiNghien()));
        n.setSoThangDaCh(dto.getSoThangDaCh() != null ? dto.getSoThangDaCh() : 0);
        n.setSoNgayDaCh(dto.getSoNgayDaCh() != null ? dto.getSoNgayDaCh() : 0);
        n.setQdToaAn(cleanString(dto.getQdToaAn()));
        n.setThoiGianCh(dto.getThoiGianCh() != null ? dto.getThoiGianCh() : 0);
        n.setTandKhuVuc(cleanString(dto.getTandKhuVuc()));
        n.setQdXetGiam(cleanString(dto.getQdXetGiam()));
        n.setDaiDienGiaDinh(capitalizeWords(dto.getDaiDienGiaDinh()));
        n.setTrinhDo(cleanString(dto.getTrinhDo()));
        n.setGhiChu(cleanString(dto.getGhiChu()));

        n.setNgaySinh(dto.getNgaySinh());
        n.setNgayCap(dto.getNgayCap());
        n.setThoidiemSdMaTuyDau(dto.getThoidiemSdMaTuyDau());
        n.setNgayVaoCs(dto.getNgayVaoCs());
        n.setDuKienNgayVe(dto.getDuKienNgayVe());
        n.setDuKienVe2026(dto.getDuKienVe2026());
        n.setDuKienVe2027(dto.getDuKienVe2027());

        if (dto.getIdCaxLap() != null) {
            caxLapHsRepository.findById(dto.getIdCaxLap()).ifPresent(n::setCaxLapHs);
        } else if (n.getCaxLapHs() == null) {
            CaxLapHs fallbackCax = caxLapHsRepository.findAll().stream().findFirst()
                    .orElseGet(() -> caxLapHsRepository.save(new CaxLapHs(null, "Chưa xác định")));
            n.setCaxLapHs(fallbackCax);
        }

        if (dto.getIdDanToc() != null) {
            danTocRepository.findById(dto.getIdDanToc()).ifPresent(n::setDanToc);
        } else {
            n.setDanToc(null);
        }
    }
}