package com.trangvi.Quan_ly_trai_cai_nghien.repository;

import com.trangvi.Quan_ly_trai_cai_nghien.entity.NguoiCaiNghien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface NguoiCaiNghienRepository extends JpaRepository<NguoiCaiNghien, Integer> {

    // 1. Tìm kiếm chính xác theo CCCD/CMND
    Optional<NguoiCaiNghien> findByCccdCmnd(String cccdCmnd);

    // 2. Tìm kiếm gần đúng theo họ tên (Bỏ qua viết hoa viết thường)
    Page<NguoiCaiNghien> findByHoVaTenContainingIgnoreCase(String hoVaTen, Pageable pageable);

    // 3. Bộ lọc tổng hợp chuẩn hóa cho PostgreSQL (Sửa lỗi lower(bytea))
    @Query("SELECT n FROM NguoiCaiNghien n WHERE " +
            "(:keyword IS NULL OR :keyword = '' " +
            " OR LOWER(n.hoVaTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            " OR n.cccdCmnd LIKE CONCAT('%', :keyword, '%') " +
            " OR LOWER(n.caxLapHs.tenCax) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            " OR CAST(n.tuoi AS string) LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:tenCax IS NULL OR :tenCax = '' OR n.caxLapHs.tenCax = :tenCax) " + // <-- Sửa dòng này
            "AND (:idDanToc IS NULL OR n.danToc.idDanToc = :idDanToc)")
    Page<NguoiCaiNghien> searchNguoiCaiNghien(
            @Param("keyword") String keyword,
            @Param("tenCax") String tenCax, // <-- Đổi kiểu dữ liệu thành String
            @Param("idDanToc") Integer idDanToc,
            Pageable pageable
    );

    // 4. Thống kê số lượng người cai nghiện theo từng Xã lập hồ sơ
    @Query("SELECT n.caxLapHs.tenCax, COUNT(n) FROM NguoiCaiNghien n GROUP BY n.caxLapHs.tenCax")
    List<Object[]> countByCax();
}