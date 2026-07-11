package com.trangvi.Quan_ly_trai_cai_nghien.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cax_lap_hs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaxLapHs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cax")
    private Integer idCax;

    @Column(name = "ten_cax", nullable = false, unique = true, length = 100)
    private String tenCax;
}
