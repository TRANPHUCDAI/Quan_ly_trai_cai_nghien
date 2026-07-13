package com.trangvi.Quan_ly_trai_cai_nghien.service;

import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienRequestDTO;
import com.trangvi.Quan_ly_trai_cai_nghien.dto.NguoiCaiNghienResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface NguoiCaiNghienService {
    Page<NguoiCaiNghienResponseDTO> getAllAndSearch(String keyword, String tenCax, Integer idDanToc, Pageable pageable);

    NguoiCaiNghienResponseDTO getById(Integer id);

    // Tách rõ ràng để đồng bộ với các phương thức HTTP Client
    NguoiCaiNghienResponseDTO create(NguoiCaiNghienRequestDTO dto);

    NguoiCaiNghienResponseDTO update(Integer id, NguoiCaiNghienRequestDTO dto);

    void delete(Integer id);

    List<Map<String, Object>> getThongKeTheoXa();

    void importCsvData();
}