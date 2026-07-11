package com.trangvi.Quan_ly_trai_cai_nghien.repository;

import com.trangvi.Quan_ly_trai_cai_nghien.entity.CaxLapHs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaxLapHsRepository extends JpaRepository<CaxLapHs, Integer> {
    // Tìm kiếm nhanh Xã/Phường theo tên
    Optional<CaxLapHs> findByTenCax(String tenCax);
}
