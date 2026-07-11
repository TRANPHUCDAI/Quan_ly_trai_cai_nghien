import React from 'react';

function PersonDetailModal({ isOpen, person, onClose }) {
  if (!isOpen || !person) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">

        {/* Header */}
        <div className="p-4 border-b bg-blue-600 text-white flex justify-between items-center rounded-t-xl">
          <h3 className="text-lg font-bold">THÔNG TIN CHI TIẾT HỒ SƠ</h3>
          <button onClick={onClose} className="text-white hover:bg-blue-700 p-1 px-3 rounded-lg text-xl">&times;</button>
        </div>

        {/* Body */}
        <div className="p-6 grid grid-cols-2 gap-4 text-sm text-gray-700">
          <div className="col-span-2 border-b pb-1 font-semibold text-blue-600 text-base">Thông tin cá nhân</div>
          <div><strong>Họ và tên:</strong> {person.hoVaTen}</div>
          <div><strong>CCCD/CMND:</strong> {person.cccdCmnd}</div>
          <div><strong>Ngày sinh:</strong> {person.ngaySinh || 'Chưa cập nhật'}</div>
          <div><strong>Tuổi:</strong> {person.tuoi} tuổi</div>
          <div><strong>Dân tộc:</strong> {person.danToc?.tenDanToc || 'Chưa cập nhật'}</div>
          <div><strong>Trình độ:</strong> {person.trinhDo || 'Chưa cập nhật'}</div>

          <div className="col-span-2 border-b pb-1 font-semibold text-blue-600 text-base mt-2">Cư trú</div>
          <div><strong>Quê quán:</strong> {person.queQuan || 'Chưa cập nhật'}</div>
          <div className="col-span-2"><strong>Hộ khẩu thường trú:</strong> {person.hkThuongTru || 'Chưa cập nhật'}</div>

          <div className="col-span-2 border-b pb-1 font-semibold text-blue-600 text-base mt-2">Thông tin quản lý & Chấp hành</div>
          <div><strong>Khu vực quản lý:</strong> {person.caxLapHs?.tenCax || 'Chưa cập nhật'}</div>
          <div><strong>Hình thức:</strong> {person.hinhThucCaiNghien}</div>
          <div><strong>Thời gian chấp hành:</strong> {person.thoiGianCh} tháng</div>
          <div><strong>Quyết định tòa án:</strong> {person.qdToaAn || 'Chưa cập nhật'}</div>
          <div><strong>Ngày vào cơ sở:</strong> {person.ngayVaoCs || 'Chưa cập nhật'}</div>
          <div><strong>Dự kiến ngày về:</strong> {person.duKienNgayVe || 'Chưa cập nhật'}</div>
        </div>

        {/* Footer */}
        <div className="p-4 border-t bg-gray-50 flex justify-end rounded-b-xl">
          <button onClick={onClose} className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg">Đóng</button>
        </div>

      </div>
    </div>
  );
}

export default PersonDetailModal;