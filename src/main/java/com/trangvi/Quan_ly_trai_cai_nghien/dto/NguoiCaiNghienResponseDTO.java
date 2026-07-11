package com.trangvi.Quan_ly_trai_cai_nghien.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiCaiNghienResponseDTO {
    private Integer tt;
    private String hoVaTen;
    private String cccdCmnd;
    private LocalDate ngayCap;
    private Integer caiNghienLanThu;
    private LocalDate thoidiemSdMaTuyDau;
    private Integer tienAn;
    private Integer tienSu;
    private LocalDate ngaySinh;
    private Integer tuoi;

    // Flatten dữ liệu: Trả về ID và Tên trực tiếp thay vì bọc trong Object
    private Integer idCaxLap;
    private String tenCaxLap;

    private String queQuan;
    private String hkThuongTru;
    private String diaChiSauSatNhap;
    private LocalDate ngayVaoCs;
    private LocalDate duKienNgayVe;
    private Integer duKienVe2026;
    private Integer duKienVe2027;
    private String hinhThucCaiNghien;
    private Integer soThangDaCh;
    private Integer soNgayDaCh;
    private String qdToaAn;
    private Integer thoiGianCh;
    private String tandKhuVuc;
    private String qdXetGiam;
    private String daiDienGiaDinh;

    // Flatten dữ liệu dân tộc
    private Integer idDanToc;
    private String tenDanToc;

    private String trinhDo;
    private String ghiChu;
}
