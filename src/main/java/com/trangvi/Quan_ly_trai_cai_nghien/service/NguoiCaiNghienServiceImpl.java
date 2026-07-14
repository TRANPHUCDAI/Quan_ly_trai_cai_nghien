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
    public void importCsvData() {
        if (nguoiCaiNghienRepository.count() > 0) {
            log.info("Dữ liệu đã tồn tại, bỏ qua bước khởi tạo tự động từ CSV.");
            return;
        }
        String csvPath = "/app/data/Du_lieu_nguoi_cai_nghien.csv";
        log.info("Bắt đầu đọc và nạp file dữ liệu CSV từ đường dẫn: {}", csvPath);

        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            List<String[]> lines = reader.readAll();
            if (lines.isEmpty()) return;

            // Đọc Header và chuẩn hóa chữ thường để không sợ lệch hoa/thường, khoảng trắng
            String[] headers = lines.get(0);
            Map<String, Integer> headMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headMap.put(headers[i].trim().toLowerCase(), i);
            }

            for (int i = 1; i < lines.size(); i++) {
                String[] row = lines.get(i);
                NguoiCaiNghien n = new NguoiCaiNghien();

                n.setHoVaTen(getValueByHeader(row, headMap, "họ và tên"));
                n.setCccdCmnd(getValueByHeader(row, headMap, "cccd/cmnd"));
                n.setNgayCap(parseDate(getValueByHeader(row, headMap, "ngày cấp")));
                n.setCaiNghienLanThu(parseInt(getValueByHeader(row, headMap, "cai nghiện lần thứ")));
                n.setThoidiemSdMaTuyDau(parseDate(getValueByHeader(row, headMap, "thời điểm sử dụng ma túy lần đầu")));
                n.setTienAn(parseInt(getValueByHeader(row, headMap, "tiền án")));
                n.setTienSu(parseInt(getValueByHeader(row, headMap, "tiền sự")));
                n.setNgaySinh(parseDate(getValueByHeader(row, headMap, "ngày sinh")));

                // Fallback trường tuổi viết sai dấu
                String tuoiStr = getValueByHeader(row, headMap, "tuổi");
                if (tuoiStr == null) tuoiStr = getValueByHeader(row, headMap, "tuôi");
                n.setTuoi(parseInt(tuoiStr));

                n.setQueQuan(getValueByHeader(row, headMap, "quê quán"));
                n.setHkThuongTru(getValueByHeader(row, headMap, "hk thường trú"));
                n.setDiaChiSauSatNhap(getValueByHeader(row, headMap, "địa chỉ sau sát nhập"));
                n.setNgayVaoCs(parseDate(getValueByHeader(row, headMap, "ngày vào cs")));
                n.setDuKienNgayVe(parseDate(getValueByHeader(row, headMap, "dự kiến ngày về")));
                n.setDuKienVe2026(parseInt(getValueByHeader(row, headMap, "dự kiến về 2026")));
                n.setDuKienVe2027(parseInt(getValueByHeader(row, headMap, "dự kiến về 2027")));
                n.setHinhThucCaiNghien(getValueByHeader(row, headMap, "hình thức cai nghiện"));
                n.setSoThangDaCh(parseInt(getValueByHeader(row, headMap, "số tháng đã c/h")));
                n.setSoNgayDaCh(parseInt(getValueByHeader(row, headMap, "số ngày đã c/h")));
                n.setQdToaAn(getValueByHeader(row, headMap, "qđ tòa án"));
                n.setThoiGianCh(parseInt(getValueByHeader(row, headMap, "thời gian ch")));
                n.setTandKhuVuc(getValueByHeader(row, headMap, "tand khu vực"));
                n.setQdXetGiam(getValueByHeader(row, headMap, "qđ xét giảm"));
                n.setDaiDienGiaDinh(getValueByHeader(row, headMap, "đai diện gia đình"));
                n.setTrinhDo(getValueByHeader(row, headMap, "trình độ"));
                n.setGhiChu(getValueByHeader(row, headMap, "ghi chú"));

                String tenDanToc = cleanString(getValueByHeader(row, headMap, "dân tộc"));
                if (tenDanToc != null && !tenDanToc.isEmpty()) {
                    DanToc dt = danTocRepository.findByTenDanToc(tenDanToc)
                            .orElseGet(() -> danTocRepository.save(new DanToc(null, tenDanToc)));
                    n.setDanToc(dt);
                }

                String tenCax = cleanString(getValueByHeader(row, headMap, "cax lập hs"));
                if (tenCax != null && !tenCax.isEmpty()) {
                    CaxLapHs cax = caxLapHsRepository.findByTenCax(tenCax)
                            .orElseGet(() -> caxLapHsRepository.save(new CaxLapHs(null, tenCax)));
                    n.setCaxLapHs(cax);
                } else {
                    CaxLapHs fallbackCax = caxLapHsRepository.findAll().stream().findFirst()
                            .orElseGet(() -> caxLapHsRepository.save(new CaxLapHs(null, "Chưa xác định")));
                    n.setCaxLapHs(fallbackCax);
                }
                nguoiCaiNghienRepository.save(n);
            }
            log.info(">>> ĐÃ TỰ ĐỘNG IMPORT THÀNH CÔNG HỒ SƠ TỪ FILE CSV VÀO POSTGRESQL! <<<");
        } catch (Exception e) {
            log.error("Lỗi khi tự động import dữ liệu CSV: ", e);
        }
    }

    // HÀM HELPER: Lấy giá trị an toàn từ Header chữ thường đã chuẩn hóa
    private String getValueByHeader(String[] row, Map<String, Integer> headMap, String headerName) {
        Integer index = headMap.get(headerName.toLowerCase());
        if (index != null && index < row.length) {
            return cleanString(row[index]);
        }
        return null;
    }

    // HÀM HELPER: Loại bỏ khoảng trắng thừa đầu cuối và khoảng trắng kép ở giữa
    private String cleanString(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("NaN")) return null;
        return val.trim().replaceAll("\\s+", " ");
    }

    private int parseInt(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("NaN")) return 0;
        try { return (int) Double.parseDouble(val.trim()); } catch (Exception e) { return 0; }
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("NaN")) return null;
        String cleanVal = val.trim();
        List<String> formats = Arrays.asList("yyyy-MM-dd", "d/M/yyyy", "dd/MM/yyyy", "d-M-yyyy");
        for (String format : formats) {
            try { return LocalDate.parse(cleanVal, DateTimeFormatter.ofPattern(format)); } catch (Exception ignored) {}
        }
        return null;
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
        n.setHoVaTen(cleanString(dto.getHoVaTen()));
        n.setCccdCmnd(cleanString(dto.getCccdCmnd()));
        n.setCaiNghienLanThu(dto.getCaiNghienLanThu() != null ? dto.getCaiNghienLanThu() : 0);
        n.setTienAn(dto.getTienAn() != null ? dto.getTienAn() : 0);
        n.setTienSu(dto.getTienSu() != null ? dto.getTienSu() : 0);

        // Làm sạch triệt để thông tin cư trú
        n.setQueQuan(cleanString(dto.getQueQuan()));
        n.setHkThuongTru(cleanString(dto.getHkThuongTru()));
        n.setDiaChiSauSatNhap(cleanString(dto.getDiaChiSauSatNhap()));

        n.setHinhThucCaiNghien(dto.getHinhThucCaiNghien() != null ? dto.getHinhThucCaiNghien() : "Bắt buộc");
        n.setSoThangDaCh(dto.getSoThangDaCh() != null ? dto.getSoThangDaCh() : 0);
        n.setSoNgayDaCh(dto.getSoNgayDaCh() != null ? dto.getSoNgayDaCh() : 0);
        n.setQdToaAn(cleanString(dto.getQdToaAn()));
        n.setThoiGianCh(dto.getThoiGianCh() != null ? dto.getThoiGianCh() : 0);
        n.setTandKhuVuc(cleanString(dto.getTandKhuVuc()));
        n.setQdXetGiam(cleanString(dto.getQdXetGiam()));
        n.setDaiDienGiaDinh(cleanString(dto.getDaiDienGiaDinh()));
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