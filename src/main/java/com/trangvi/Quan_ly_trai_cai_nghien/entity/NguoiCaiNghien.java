package com.trangvi.Quan_ly_trai_cai_nghien.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "nguoi_cai_nghien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiCaiNghien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tt;

    @Column(name = "ho_va_ten", nullable = false, length = 100)
    private String hoVaTen;

    @Column(name = "cccd_cmnd", unique = true, length = 20)
    private String cccdCmnd;

    @Column(name = "ngay_cap")
    private LocalDate ngayCap;

    @Column(name = "cai_nghien_lan_thu")
    private Integer caiNghienLanThu = 0;

    @Column(name = "thoidiem_sd_ma_tuy_dau")
    private LocalDate thoidiemSdMaTuyDau;

    @Column(name = "tien_an")
    private Integer tienAn = 0;

    @Column(name = "tien_su")
    private Integer tienSu = 0;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    private Integer tuoi;

    // KHÓA NGOẠI: Khớp với id_cax_lap trong DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cax_lap", referencedColumnName = "id_cax")
    private CaxLapHs caxLapHs;

    @Column(name = "que_quan")
    private String queQuan;

    @Column(name = "hk_thuong_tru")
    private String hkThuongTru;

    @Column(name = "dia_chi_sau_sat_nhap")
    private String diaChiSauSatNhap;

    @Column(name = "ngay_vao_cs")
    private LocalDate ngayVaoCs;

    @Column(name = "du_kien_ngay_ve")
    private LocalDate duKienNgayVe;

    @Column(name = "du_kien_ve_2026")
    private Integer duKienVe2026;

    @Column(name = "du_kien_ve_2027")
    private Integer duKienVe2027;

    @Column(name = "hinh_thuc_cai_nghien", length = 50)
    private String hinhThucCaiNghien;

    @Column(name = "so_thang_da_ch")
    private Integer soThangDaCh;

    @Column(name = "so_ngay_da_ch")
    private Integer soNgayDaCh;

    @Column(name = "qd_toa_an", length = 100)
    private String qdToaAn;

    @Column(name = "thoi_gian_ch")
    private Integer thoiGianCh;

    @Column(name = "tand_khu_vuc", length = 100)
    private String tandKhuVuc;

    @Column(name = "qd_xet_giam", length = 100)
    private String qdXetGiam;

    @Column(name = "dai_dien_gia_dinh", length = 150)
    private String daiDienGiaDinh;

    // KHÓA NGOẠI: Khớp với id_dan_toc trong DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dan_toc", referencedColumnName = "id_dan_toc")
    private DanToc danToc;

    @Column(length = 20)
    private String trinhDo;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;
}
