package com.trangvi.Quan_ly_trai_cai_nghien.config.validation;

import com.trangvi.Quan_ly_trai_cai_nghien.entity.NguoiCaiNghien;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class LogicThoiGianValidator implements ConstraintValidator<ValidLogicThoiGian, NguoiCaiNghien> {

    @Override
    public boolean isValid(NguoiCaiNghien entity, ConstraintValidatorContext context) {
        if (entity == null) {
            return true;
        }

        LocalDate ngaySinh = entity.getNgaySinh();
        LocalDate ngayCap = entity.getNgayCap();
        LocalDate thoidiemSdMaTuyDau = entity.getThoidiemSdMaTuyDau();
        LocalDate ngayVaoCs = entity.getNgayVaoCs();
        LocalDate duKienNgayVe = entity.getDuKienNgayVe();
        LocalDate ngayHienTai = LocalDate.now();

        // Nếu chưa nhập ngày sinh thì bỏ qua để @NotNull của thuộc tính xử lý trước
        if (ngaySinh == null) {
            return true;
        }

        // 1. Kiểm tra Ngày cấp CCCD
        if (ngayCap != null) {
            if (ngayCap.isBefore(ngaySinh) || ngayCap.isAfter(ngayHienTai)) {
                customMessage(context, "ngayCap", "Ngày cấp CCCD phải sau ngày sinh và không được vượt quá ngày hiện tại.");
                return false;
            }
        }

        // 2. Kiểm tra Ngày vào cơ sở
        if (ngayVaoCs != null) {
            if (ngayVaoCs.isBefore(ngaySinh)) {
                customMessage(context, "ngayVaoCs", "Ngày vào cơ sở phải lớn hơn ngày sinh.");
                return false;
            }
        }

        // 3. Kiểm tra Thời điểm sử dụng ma túy lần đầu
        if (thoidiemSdMaTuyDau != null) {
            if (thoidiemSdMaTuyDau.isBefore(ngaySinh)) {
                customMessage(context, "thoidiemSdMaTuyDau", "Thời điểm sử dụng ma túy lần đầu phải sau ngày sinh.");
                return false;
            }
            if (ngayVaoCs != null && thoidiemSdMaTuyDau.isAfter(ngayVaoCs)) {
                customMessage(context, "thoidiemSdMaTuyDau", "Thời điểm sử dụng ma túy lần đầu phải trước ngày vào cơ sở.");
                return false;
            }
        }

        // 4. Kiểm tra Dự kiến ngày về
        if (duKienNgayVe != null && ngayVaoCs != null) {
            if (!duKienNgayVe.isAfter(ngayVaoCs)) {
                customMessage(context, "duKienNgayVe", "Dự kiến ngày về bắt buộc phải sau ngày vào cơ sở.");
                return false;
            }
        }

        return true;
    }

    // Hàm bổ trợ hiển thị lỗi đúng trường thuộc tính bị sai cho Frontend dễ bắt
    private void customMessage(ConstraintValidatorContext context, String propertyName, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(propertyName)
                .addConstraintViolation();
    }
}