package com.trangvi.Quan_ly_trai_cai_nghien.entity;

import com.trangvi.Quan_ly_trai_cai_nghien.config.validation.ValidLogicThoiGian;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "nguoi_cai_nghien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidLogicThoiGian
public class NguoiCaiNghien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tt;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được quá 100 ký tự")
    @Column(name = "ho_va_ten", nullable = false, length = 100)
    private String hoVaTen;

    @Pattern(regexp = "^([0-9]{9}|[0-9]{12})?$", message = "CCCD/CMND không hợp lệ")
    @Column(name = "cccd_cmnd", unique = true, length = 20)
    private String cccdCmnd;

    @Column(name = "ngay_cap")
    private LocalDate ngayCap;

    @Min(value = 0, message = "Số lần cai nghiện không được âm")
    @Column(name = "cai_nghien_lan_thu")
    private Integer caiNghienLanThu = 0;

    @Column(name = "thoidiem_sd_ma_tuy_dau")
    private LocalDate thoidiemSdMaTuyDau;

    @Min(value = 0, message = "Số tiền án không được âm")
    @Column(name = "tien_an")
    private Integer tienAn = 0;

    @Min(value = 0, message = "Số tiền sự không được âm")
    @Column(name = "tien_su")
    private Integer tienSu = 0;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    private Integer tuoi;

    // LOẠI BỎ SIDE-EFFECT CỦA LOMBOK TRÊN LAZY LOADING
    @NotNull(message = "Phải chọn đơn vị lập hồ sơ")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cax_lap", referencedColumnName = "id_cax")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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

    @Min(value = 0, message = "Số tháng đã cai nghiện không được âm")
    @Column(name = "so_thang_da_ch")
    private Integer soThangDaCh;

    @Min(value = 0, message = "Số ngày đã cai nghiện không được âm")
    @Column(name = "so_ngay_da_ch")
    private Integer soNgayDaCh;

    @Column(name = "qd_toa_an", length = 100)
    private String qdToaAn;

    @Min(value = 0, message = "Thời gian cai nghiện không được âm")
    @Column(name = "thoi_gian_ch")
    private Integer thoiGianCh;

    @Column(name = "tand_khu_vuc", length = 100)
    private String tandKhuVuc;

    @Column(name = "qd_xet_giam", length = 100)
    private String qdXetGiam;

    @Column(name = "dai_dien_gia_dinh", length = 150)
    private String daiDienGiaDinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dan_toc", referencedColumnName = "id_dan_toc")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DanToc danToc;

    @Column(length = 20)
    private String trinhDo;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    // TỰ ĐỘNG TÍNH TOÁN TUỔI VÀ NGÀY VỀ TRƯỚC KHI LƯU VÀO DATABASE
    @PrePersist
    @PreUpdate
    public void handleAutoFields() {
        // 1. Tự động tính tuổi dựa trên ngày sinh
        if (this.ngaySinh != null) {
            this.tuoi = Period.between(this.ngaySinh, LocalDate.now()).getYears();
        }

        // 2. Tự động tính Dự kiến ngày về dựa trên Ngày vào + Thời gian cai nghiện (tháng)
        if (this.ngayVaoCs != null && this.thoiGianCh != null) {
            this.duKienNgayVe = this.ngayVaoCs.plusMonths(this.thoiGianCh);

            // Tách bộ lọc dự kiến về theo năm cho báo cáo (nếu cần dùng tới 2 trường này)
            int namVe = this.duKienNgayVe.getYear();
            if (namVe == 2026) {
                this.duKienVe2026 = this.thoiGianCh;
                this.duKienVe2027 = null;
            } else if (namVe == 2027) {
                this.duKienVe2027 = this.thoiGianCh;
                this.duKienVe2026 = null;
            }
        }
    }
}