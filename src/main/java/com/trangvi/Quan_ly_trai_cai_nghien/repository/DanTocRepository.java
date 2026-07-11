package com.trangvi.Quan_ly_trai_cai_nghien.repository;

import com.trangvi.Quan_ly_trai_cai_nghien.entity.DanToc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DanTocRepository extends JpaRepository<DanToc, Integer> {
    // Tìm kiếm nhanh dân tộc theo tên (Hữu ích khi bạn check trùng lặp lúc import CSV)
    Optional<DanToc> findByTenDanToc(String tenDanToc);
}
