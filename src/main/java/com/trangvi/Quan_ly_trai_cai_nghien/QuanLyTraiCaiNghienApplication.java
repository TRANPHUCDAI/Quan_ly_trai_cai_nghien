package com.trangvi.Quan_ly_trai_cai_nghien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.trangvi.Quan_ly_trai_cai_nghien.repository") // <-- Bổ sung dòng này
public class QuanLyTraiCaiNghienApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuanLyTraiCaiNghienApplication.class, args);
    }
}