CREATE TABLE dan_toc (
    id_dan_toc SERIAL PRIMARY KEY,
    ten_dan_toc VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE cax_lap_hs (
    id_cax SERIAL PRIMARY KEY,
    ten_cax VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE nguoi_cai_nghien (
    tt SERIAL PRIMARY KEY,
    ho_va_ten VARCHAR(100) NOT NULL,
    cccd_cmnd VARCHAR(20),
    ngay_cap DATE,
    cai_nghien_lan_thu INT DEFAULT 0,
    thoidiem_sd_ma_tuy_dau DATE,
    tien_an INT DEFAULT 0,
    tien_su INT DEFAULT 0,
    ngay_sinh DATE,
    tuoi INT,
    id_cax_lap INT,
    que_quan VARCHAR(255),
    hk_thuong_tru VARCHAR(255),
    dia_chi_sau_sat_nhap VARCHAR(255),
    ngay_vao_cs DATE,
    du_kien_ngay_ve DATE,
    du_kien_ve_2026 INT,
    du_kien_ve_2027 INT,
    hinh_thuc_cai_nghien VARCHAR(50),
    so_thang_da_ch INT,
    so_ngay_da_ch INT,
    qd_toa_an VARCHAR(100),
    thoi_gian_ch INT,
    tand_khu_vuc VARCHAR(100),
    qd_xet_giam VARCHAR(100),
    dai_dien_gia_dinh VARCHAR(150),
    id_dan_toc INT,
    trinh_do VARCHAR(20),
    ghi_chu TEXT,

    CONSTRAINT fk_nguoi_cai_nghien_cax FOREIGN KEY (id_cax_lap) REFERENCES cax_lap_hs(id_cax) ON DELETE SET NULL,
    CONSTRAINT fk_nguoi_cai_nghien_dantoc FOREIGN KEY (id_dan_toc) REFERENCES dan_toc(id_dan_toc) ON DELETE SET NULL
);