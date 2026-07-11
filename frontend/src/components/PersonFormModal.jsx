import React, { useState, useEffect } from 'react';

function PersonFormModal({ isOpen, onClose, onSave, editData }) {
  const initialFormState = {
    hoVaTen: '',
    cccdCmnd: '',
    tuoi: '',
    tenXa: '',
    hinhThucCaiNghien: 'Bắt buộc',
    thoiGianCh: '',
    queQuan: '',
    hkThuongTru: '',
    ghiChu: ''
  };

  const [formData, setFormData] = useState(initialFormState);

  useEffect(() => {
    if (editData) {
      setFormData(editData); // Nếu là sửa đổi -> điền dữ liệu cũ vào form
    } else {
      setFormData(initialFormState); // Nếu là thêm mới -> reset form trống
    }
  }, [editData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-4 border-b bg-slate-800 text-white flex justify-between items-center rounded-t-xl">
          <h3 className="text-lg font-bold">{editData ? 'CẬP NHẬT HỒ SƠ' : 'THÊM HỒ SƠ MỚI'}</h3>
          <button onClick={onClose} className="text-white text-xl px-2">&times;</button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 grid grid-cols-2 gap-4 text-sm">
          <div>
            <label className="block text-gray-600 font-medium mb-1">Họ và tên *</label>
            <input type="text" required value={formData.hoVaTen} onChange={e => setFormData({...formData, hoVaTen: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>
          <div>
            <label className="block text-gray-600 font-medium mb-1">Số CCCD/CMND *</label>
            <input type="text" required value={formData.cccdCmnd} onChange={e => setFormData({...formData, cccdCmnd: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>
          <div>
            <label className="block text-gray-600 font-medium mb-1">Tuổi</label>
            <input type="number" value={formData.tuoi || ''} onChange={e => setFormData({...formData, tuoi: parseInt(e.target.value) || ''})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>
          <div>
            <label className="block text-gray-600 font-medium mb-1">Xã lập hồ sơ</label>
            <input type="text" value={formData.tenXa || ''} onChange={e => setFormData({...formData, tenXa: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500" placeholder="Ví dụ: xã Nậm Cắn" />
          </div>
          <div>
            <label className="block text-gray-600 font-medium mb-1">Hình thức cai nghiện</label>
            <select value={formData.hinhThucCaiNghien} onChange={e => setFormData({...formData, hinhThucCaiNghien: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500">
              <option value="Bắt buộc">Bắt buộc</option>
              <option value="Tự nguyện">Tự nguyện</option>
              <option value="Tại cộng đồng">Tại cộng đồng</option>
            </select>
          </div>
          <div>
            <label className="block text-gray-600 font-medium mb-1">Thời gian chấp hành (tháng)</label>
            <input type="number" value={formData.thoiGianCh || ''} onChange={e => setFormData({...formData, thoiGianCh: parseInt(e.target.value) || ''})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>
          <div className="col-span-2">
            <label className="block text-gray-600 font-medium mb-1">Quê quán</label>
            <input type="text" value={formData.queQuan || ''} onChange={e => setFormData({...formData, queQuan: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>
          <div className="col-span-2">
            <label className="block text-gray-600 font-medium mb-1">Hộ khẩu thường trú</label>
            <input type="text" value={formData.hkThuongTru || ''} onChange={e => setFormData({...formData, hkThuongTru: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>
          <div className="col-span-2">
            <label className="block text-gray-600 font-medium mb-1">Ghi chú</label>
            <textarea rows="2" value={formData.ghiChu || ''} onChange={e => setFormData({...formData, ghiChu: e.target.value})} className="w-full p-2 border rounded outline-none focus:border-blue-500" />
          </div>

          <div className="col-span-2 border-t pt-4 flex justify-end gap-2 mt-2">
            <button type="button" onClick={onClose} className="bg-gray-400 text-white px-4 py-2 rounded-lg hover:bg-gray-500">Hủy bỏ</button>
            <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">Lưu thông tin</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PersonFormModal;