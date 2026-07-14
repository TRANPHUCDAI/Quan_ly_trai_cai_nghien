import React from 'react';

function PersonDetailModal({ isOpen, person, onClose }) {
  if (!isOpen || !person) return null;

  const handleOverlayClick = (e) => {
    if (e.target.id === 'modal-overlay') {
      onClose();
    }
  };

  const renderField = (value, unit = '') => {
    if (value === null || value === undefined || value === '') {
      return <span className="text-gray-400 italic font-normal">Chưa cập nhật</span>;
    }
    return <span>{value}{unit}</span>;
  };

  const renderNumberField = (value, unit = '') => {
    if (value === null || value === undefined || value === 0) {
      return <span className="text-gray-400 italic font-normal">Chưa cập nhật</span>;
    }
    return <span>{value}{unit}</span>;
  };

  return (
    <div
      id="modal-overlay"
      onClick={handleOverlayClick}
      className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50 p-4 transition-all cursor-pointer"
    >
      <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-100 cursor-default">

        {/* Header */}
        <div className="p-5 border-b bg-gradient-to-r from-blue-600 to-indigo-700 text-white flex justify-between items-center rounded-t-2xl">
          <div>
            <h3 className="text-lg font-bold tracking-wide">THÔNG TIN CHI TIẾT HỒ SƠ LÝ LỊCH</h3>
            <p className="text-xs text-blue-100 mt-1">Mã số hồ sơ (TT): #{person.tt}</p>
          </div>
          <button
            onClick={onClose}
            className="text-white hover:bg-white/15 p-2 rounded-xl text-2xl transition-all w-10 h-10 flex items-center justify-center"
          >
            &times;
          </button>
        </div>

        {/* Body */}
        <div className="p-8 space-y-6">

          {/* PHẦN 1: THÔNG TIN CÁ NHÂN */}
          <div>
            <div className="border-l-4 border-blue-600 pl-3 mb-4">
              <h4 className="font-bold text-gray-800 text-base uppercase tracking-wider">Thông tin nhân thân</h4>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 bg-slate-50/50 p-5 rounded-2xl border border-gray-100 text-gray-700">
              <div><strong>Họ và tên:</strong> <span className="text-gray-900 font-semibold">{renderField(person.hoVaTen)}</span></div>
              <div><strong>Số CCCD/CMND:</strong> {renderField(person.cccdCmnd)}</div>
              <div><strong>Ngày cấp CCCD:</strong> {renderField(person.ngayCap)}</div>
              <div><strong>Ngày sinh:</strong> {renderField(person.ngaySinh)}</div>
              <div><strong>Tuổi thực tế:</strong> {renderNumberField(person.tuoi, ' tuổi')}</div>
              <div><strong>Dân tộc:</strong> {renderField(person.tenDanToc)}</div>
              <div><strong>Trình độ học vấn:</strong> {renderField(person.trinhDo)}</div>
              <div><strong>Quê quán:</strong> {renderField(person.queQuan)}</div>
              <div className="md:col-span-3"><strong>Hộ khẩu thường trú:</strong> {renderField(person.hkThuongTru)}</div>
              <div className="md:col-span-3"><strong>Địa chỉ sau sáp nhập:</strong> {renderField(person.diaChiSauSatNhap)}</div>
            </div>
          </div>

          {/* PHẦN 2: THÔNG TIN QUẢN LÝ & TIỀN ÁN TIỀN SỰ */}
          <div>
            <div className="border-l-4 border-indigo-600 pl-3 mb-4">
              <h4 className="font-bold text-gray-800 text-base uppercase tracking-wider">Lý lịch tư pháp & Quản lý</h4>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 bg-slate-50/50 p-5 rounded-2xl border border-gray-100 text-gray-700">
              <div><strong>Đơn vị lập hồ sơ (Xã):</strong> <span className="text-indigo-600 font-semibold">{renderField(person.tenCaxLap)}</span></div>
              <div><strong>Số lần cai nghiện:</strong> {renderNumberField(person.caiNghienLanThu, ' lần')}</div>
              <div><strong>Thời điểm SD ma túy đầu:</strong> {renderField(person.thoidiemSdMaTuyDau)}</div>
              <div><strong>Số tiền án:</strong> {renderNumberField(person.tienAn, ' tiền án')}</div>
              <div><strong>Số tiền sự:</strong> {renderNumberField(person.tienSu, ' tiền sự')}</div>
              <div><strong>Người đại diện gia đình:</strong> {renderField(person.daiDienGiaDinh)}</div>
            </div>
          </div>

          {/* PHẦN 3: QUÁ TRÌNH CHẤP HÀNH CAI NGHIỆN */}
          <div>
            <div className="border-l-4 border-emerald-600 pl-3 mb-4">
              <h4 className="font-bold text-gray-800 text-base uppercase tracking-wider">Quá trình chấp hành cai nghiện</h4>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 bg-slate-50/50 p-5 rounded-2xl border border-gray-100 text-gray-700">

              {/* ĐỒNG BỘ MÀU SẮC TRONG CHI TIẾT MODAL */}
              <div><strong>Hình thức cai nghiện:</strong>
                {(() => {
                  const hinhThuc = (person.hinhThucCaiNghien || '').trim();
                  if (hinhThuc === 'Tự nguyện') {
                    return (
                      <span className="ml-2 px-2.5 py-1 text-xs font-semibold rounded bg-emerald-100 text-emerald-800 border border-emerald-200">
                        Tự nguyện
                      </span>
                    );
                  } else if (hinhThuc === 'Lưu trú') {
                    return (
                      <span className="ml-2 px-2.5 py-1 text-xs font-semibold rounded bg-amber-100 text-amber-800 border border-amber-200">
                        Lưu trú
                      </span>
                    );
                  } else {
                    return (
                      <span className="ml-2 px-2.5 py-1 text-xs font-semibold rounded bg-red-100 text-red-800 border border-red-200">
                        Bắt buộc
                      </span>
                    );
                  }
                })()}
              </div>

              <div><strong>Thời gian cai nghiện:</strong> {renderNumberField(person.thoiGianCh, ' tháng')}</div>
              <div><strong>Quyết định tòa án:</strong> {renderField(person.qdToaAn)}</div>
              <div><strong>TAND quyết định:</strong> {renderField(person.tandKhuVuc)}</div>
              <div><strong>Quyết định xét giảm:</strong> {renderField(person.qdXetGiam)}</div>
              <div><strong>Ngày vào cơ sở:</strong> {renderField(person.ngayVaoCs)}</div>
              <div><strong>Số tháng đã C/H:</strong> {renderNumberField(person.soThangDaCh, ' tháng')}</div>
              <div><strong>Số ngày đã C/H:</strong> {renderNumberField(person.soNgayDaCh, ' ngày')}</div>
              <div className="bg-blue-50/50 border border-blue-100 p-3 rounded-xl md:col-span-3 grid grid-cols-1 md:grid-cols-3 gap-4">
                <div><strong>Dự kiến ngày về:</strong> <span className="text-blue-700 font-semibold">{renderField(person.duKienNgayVe)}</span></div>
                <div><strong>Dự kiến về năm 2026:</strong> {renderNumberField(person.duKienVe2026, ' tháng')}</div>
                <div><strong>Dự kiến về năm 2027:</strong> {renderNumberField(person.duKienVe2027, ' tháng')}</div>
              </div>
            </div>
          </div>

          {/* PHẦN 4: GHI CHÚ */}
          <div>
            <div className="border-l-4 border-amber-500 pl-3 mb-4">
              <h4 className="font-bold text-gray-800 text-base uppercase tracking-wider">Ghi chú đặc biệt</h4>
            </div>
            <div className="bg-amber-50/40 border border-amber-200/60 p-4 rounded-xl text-gray-700 min-h-[60px]">
              {person.ghiChu ? person.ghiChu : <span className="text-gray-400 italic">Không có ghi chú bổ sung nào cho hồ sơ này.</span>}
            </div>
          </div>

        </div>

        {/* Footer */}
        <div className="p-5 border-t bg-slate-50 flex justify-end gap-3 rounded-b-2xl">
          <button
            onClick={onClose}
            className="bg-slate-700 hover:bg-slate-800 text-white font-medium px-6 py-2.5 rounded-xl transition-all shadow-sm"
          >
            Đóng cửa sổ
          </button>
        </div>

      </div>
    </div>
  );
}

export default PersonDetailModal;