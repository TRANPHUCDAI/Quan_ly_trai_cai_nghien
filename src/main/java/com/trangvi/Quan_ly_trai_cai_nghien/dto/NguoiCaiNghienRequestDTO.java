package com.trangvi.Quan_ly_trai_cai_nghien.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class NguoiCaiNghienRequestDTO {
    private Integer tt; // null nếu là thêm mới, có giá trị nếu là cập nhật
    private String hoVaTen;
    private String cccdCmnd;
    private LocalDate ngayCap;
    private Integer caiNghienLanThu;
    private LocalDate thoidiemSdMaTuyDau;
    private Integer tienAn;
    private Integer tienSu;
    private LocalDate ngaySinh;
    private Integer tuoi;

    private Integer idCaxLap; // Chỉ cần truyền ID xã từ dropdown select xuống

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

    private Integer idDanToc; // Chỉ cần truyền ID dân tộc từ dropdown select xuống

    private String trinhDo;
    private String ghiChu;
}
