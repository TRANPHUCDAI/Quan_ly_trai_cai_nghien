package com.trangvi.Quan_ly_trai_cai_nghien.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "dan_toc")
@NoArgsConstructor
@AllArgsConstructor
public class DanToc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dan_toc")
    private Integer idDanToc;

    @Column(name = "ten_dan_toc", nullable = false, unique = true, length = 50)
    private String tenDanToc;
}
