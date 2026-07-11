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
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NguoiCaiNghienServiceImpl implements NguoiCaiNghienService, CommandLineRunner{
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
    public NguoiCaiNghienResponseDTO save(NguoiCaiNghienRequestDTO dto) {
        NguoiCaiNghien entity = convertToEntity(dto);
        NguoiCaiNghien savedEntity = nguoiCaiNghienRepository.save(entity);
        return convertToResponseDTO(savedEntity);
    }

    @Override
    public void delete(Integer id) {
        nguoiCaiNghienRepository.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getThongKeTheoXa() {
        List<Object[]> rawData = nguoiCaiNghienRepository.countByCax();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rawData) {
            Map<String, Object> map = new HashMap<>();
            map.put("tenXa", row[0]);
            map.put("soLuong", row[1]);
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

            String[] headers = lines.get(0);
            Map<String, Integer> headMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headMap.put(headers[i].trim(), i);
            }

            for (int i = 1; i < lines.size(); i++) {
                String[] row = lines.get(i);

                NguoiCaiNghien n = new NguoiCaiNghien();
                n.setHoVaTen(row[headMap.get("Họ và tên")]);
                n.setCccdCmnd(row[headMap.get("CCCD/CMND")]);
                n.setNgayCap(parseDate(row[headMap.get("Ngày cấp")]));
                n.setCaiNghienLanThu(parseInt(row[headMap.get("cai nghiện lần thứ")]));
                n.setThoidiemSdMaTuyDau(parseDate(row[headMap.get("thời điểm sử dụng ma túy lần đầu")]));
                n.setTienAn(parseInt(row[headMap.get("tiền án")]));
                n.setTienSu(parseInt(row[headMap.get("tiền sự")]));
                n.setNgaySinh(parseDate(row[headMap.get("ngày sinh")]));
                n.setTuoi(parseInt(headMap.containsKey("tuổi") ? row[headMap.get("tuổi")] : row[headMap.get("tuôi")]));
                n.setQueQuan(row[headMap.get("Quê quán")]);
                n.setHkThuongTru(row[headMap.get("HK thường trú")]);
                n.setDiaChiSauSatNhap(row[headMap.get("Địa chỉ sau sát nhập")]);
                n.setNgayVaoCs(parseDate(row[headMap.get("ngày vào CS")]));
                n.setDuKienNgayVe(parseDate(row[headMap.get("Dự kiến ngày về")]));
                n.setDuKienVe2026(parseInt(row[headMap.get("dự kiến về 2026")]));
                n.setDuKienVe2027(parseInt(row[headMap.get("dự kiến về 2027")]));
                n.setHinhThucCaiNghien(row[headMap.get("Hình thức cai nghiện")]);
                n.setSoThangDaCh(parseInt(row[headMap.get("Số tháng đã c/h")]));
                n.setSoNgayDaCh(parseInt(row[headMap.get("số ngày đã c/h")]));
                n.setQdToaAn(row[headMap.get("QĐ TÒA ÁN")]);
                n.setThoiGianCh(parseInt(row[headMap.get("THỜI GIAN CH")]));
                n.setTandKhuVuc(row[headMap.get("TAND khu vực")]);
                n.setQdXetGiam(row[headMap.get("QĐ XÉT GIẢM")]);
                n.setDaiDienGiaDinh(row[headMap.get("ĐAI DIỆN GIA ĐÌNH")]);
                n.setTrinhDo(row[headMap.get("TRÌNH ĐỘ")]);
                n.setGhiChu(row[headMap.get("GHI CHÚ")]);

                String tenDanToc = row[headMap.get("DÂN TỘC")].trim();
                if (!tenDanToc.isEmpty()) {
                    DanToc dt = danTocRepository.findByTenDanToc(tenDanToc)
                            .orElseGet(() -> danTocRepository.save(new DanToc(null, tenDanToc)));
                    n.setDanToc(dt);
                }

                String tenCax = row[headMap.get("CAX lập HS")].trim();
                if (!tenCax.isEmpty()) {
                    CaxLapHs cax = caxLapHsRepository.findByTenCax(tenCax)
                            .orElseGet(() -> caxLapHsRepository.save(new CaxLapHs(null, tenCax)));
                    n.setCaxLapHs(cax);
                }

                nguoiCaiNghienRepository.save(n);
            }
            log.info(">>> ĐÃ TỰ ĐỘNG IMPORT THÀNH CÔNG HỒ SƠ TỪ FILE CSV VÀO POSTGRESQL! <<<");
        } catch (Exception e) {
            log.error("Lỗi khi tự động import dữ liệu CSV: ", e);
        }
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

    // Các hàm Mapper hỗ trợ chuyển đổi
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

    private NguoiCaiNghien convertToEntity(NguoiCaiNghienRequestDTO dto) {
        NguoiCaiNghien n = new NguoiCaiNghien();
        n.setTt(dto.getTt());
        n.setHoVaTen(dto.getHoVaTen());
        n.setCccdCmnd(dto.getCccdCmnd());
        n.setNgayCap(dto.getNgayCap());
        n.setCaiNghienLanThu(dto.getCaiNghienLanThu());
        n.setThoidiemSdMaTuyDau(dto.getThoidiemSdMaTuyDau());
        n.setTienAn(dto.getTienAn());
        n.setTienSu(dto.getTienSu());
        n.setNgaySinh(dto.getNgaySinh());
        n.setTuoi(dto.getTuoi());
        n.setQueQuan(dto.getQueQuan());
        n.setHkThuongTru(dto.getHkThuongTru());
        n.setDiaChiSauSatNhap(dto.getDiaChiSauSatNhap());
        n.setNgayVaoCs(dto.getNgayVaoCs());
        n.setDuKienNgayVe(dto.getDuKienNgayVe());
        n.setDuKienVe2026(dto.getDuKienVe2026());
        n.setDuKienVe2027(dto.getDuKienVe2027());
        n.setHinhThucCaiNghien(dto.getHinhThucCaiNghien());
        n.setSoThangDaCh(dto.getSoThangDaCh());
        n.setSoNgayDaCh(dto.getSoNgayDaCh());
        n.setQdToaAn(dto.getQdToaAn());
        n.setThoiGianCh(dto.getThoiGianCh());
        n.setTandKhuVuc(dto.getTandKhuVuc());
        n.setQdXetGiam(dto.getQdXetGiam());
        n.setDaiDienGiaDinh(dto.getDaiDienGiaDinh());
        n.setTrinhDo(dto.getTrinhDo());
        n.setGhiChu(dto.getGhiChu());

        if (dto.getIdCaxLap() != null) {
            caxLapHsRepository.findById(dto.getIdCaxLap()).ifPresent(n::setCaxLapHs);
        }
        if (dto.getIdDanToc() != null) {
            danTocRepository.findById(dto.getIdDanToc()).ifPresent(n::setDanToc);
        }
        return n;
    }
}
