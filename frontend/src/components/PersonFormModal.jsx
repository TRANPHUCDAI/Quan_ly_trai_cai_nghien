import React, { useState, useEffect } from 'react';
import axios from 'axios';

// KHAI BÁO BIẾN ĐỊA CHỈ API RENDER CHUNG
const API_BASE = 'https://quan-ly-trai-cai-nghien.onrender.com';

function PersonFormModal({ isOpen, onClose, onSave, editData }) {
  const initialFormState = {
    hoVaTen: '',
    cccdCmnd: '',
    ngayCap: '',
    caiNghienLanThu: 0,
    thoidiemSdMaTuyDau: '',
    tienAn: 0,
    tienSu: 0,
    ngaySinh: '',
    idCaxLap: '',
    queQuan: '',
    hkThuongTru: '',
    diaChiSauSatNhap: '',
    ngayVaoCs: '',
    hinhThucCaiNghien: 'Bắt buộc',
    soThangDaCh: 0,
    soNgayDaCh: 0,
    qdToaAn: '',
    thoiGianCh: '',
    tandKhuVuc: '',
    qdXetGiam: '',
    daiDienGiaDinh: '',
    idDanToc: '',
    trinhDo: '',
    ghiChu: ''
  };

  const [formData, setFormData] = useState(initialFormState);
  const [listXa, setListXa] = useState([]);
  const [listDanToc, setListDanToc] = useState([]);

  // Tải danh mục dropdown từ Server Render thực tế
  useEffect(() => {
    if (isOpen) {
      const loadReferenceData = async () => {
        try {
          // 1. Lấy danh sách Xã (Không dùng localhost nữa)
          const resXa = await axios.get(`${API_BASE}/api/nguoi-cai-nghien/theo-xa`);
          setListXa(resXa.data || []);

          // 2. Tự động mồi danh sách Dân tộc mẫu hoặc gọi API dân tộc (nếu Backend có hỗ trợ)
          // Để an toàn, chúng ta vẫn mồi danh sách chuẩn đồng bộ theo dữ liệu DB hiện tại
          setListDanToc([
            { id: 1, name: 'Kinh' },
            { id: 2, name: 'Thái' },
            { id: 3, name: 'Mường' },
            { id: 4, name: 'Khác' }
          ]);
        } catch (err) {
          console.error("Lỗi lấy danh mục:", err);
        }
      };
      loadReferenceData();
    }
  }, [isOpen]);

  // Đồng bộ dữ liệu khi bấm Sửa (Edit) hoặc Thêm mới (Create)
  useEffect(() => {
    if (editData) {
      setFormData({
        ...editData,
        hoVaTen: editData.hoVaTen || '',
        cccdCmnd: editData.cccdCmnd || '',
        ngayCap: editData.ngayCap || '',
        caiNghienLanThu: editData.caiNghienLanThu ?? 0,
        thoidiemSdMaTuyDau: editData.thoidiemSdMaTuyDau || '',
        tienAn: editData.tienAn ?? 0,
        tienSu: editData.tienSu ?? 0,
        ngaySinh: editData.ngaySinh || '',
        idCaxLap: editData.idCaxLap || '',
        queQuan: editData.queQuan || '',
        hkThuongTru: editData.hkThuongTru || '',
        diaChiSauSatNhap: editData.diaChiSauSatNhap || '',
        ngayVaoCs: editData.ngayVaoCs || '',
        hinhThucCaiNghien: editData.hinhThucCaiNghien || 'Bắt buộc',
        soThangDaCh: editData.soThangDaCh ?? 0,
        soNgayDaCh: editData.soNgayDaCh ?? 0,
        qdToaAn: editData.qdToaAn || '',
        thoiGianCh: editData.thoiGianCh || '',
        tandKhuVuc: editData.tandKhuVuc || '',
        qdXetGiam: editData.qdXetGiam || '',
        daiDienGiaDinh: editData.daiDienGiaDinh || '',
        idDanToc: editData.idDanToc || '',
        trinhDo: editData.trinhDo || '',
        ghiChu: editData.ghiChu || ''
      });
    } else {
      setFormData(initialFormState);
    }
  }, [editData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      tt: editData ? editData.tt : null,
      idCaxLap: formData.idCaxLap ? parseInt(formData.idCaxLap) : null,
      idDanToc: formData.idDanToc ? parseInt(formData.idDanToc) : null,
      caiNghienLanThu: parseInt(formData.caiNghienLanThu) || 0,
      tienAn: parseInt(formData.tienAn) || 0,
      tienSu: parseInt(formData.tienSu) || 0,
      thoiGianCh: formData.thoiGianCh ? parseInt(formData.thoiGianCh) : 0,
      soThangDaCh: parseInt(formData.soThangDaCh) || 0,
      soNgayDaCh: parseInt(formData.soNgayDaCh) || 0,
    };
    onSave(payload);
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-100">

        {/* Header */}
        <div className="p-5 border-b bg-slate-800 text-white flex justify-between items-center rounded-t-2xl">
          <div>
            <h3 className="text-lg font-bold tracking-wide">{editData ? 'CẬP NHẬT TOÀN DIỆN HỒ SƠ' : 'THÊM HỒ SƠ MỚI ĐẦY ĐỦ'}</h3>
            <p className="text-xs text-slate-300 mt-1">Vui lòng điền chính xác thông tin theo yêu cầu hành chính</p>
          </div>
          <button onClick={onClose} className="text-white text-2xl hover:bg-white/10 px-3 py-1 rounded-xl transition-all">&times;</button>
        </div>

        <form onSubmit={handleSubmit} className="p-8 space-y-6 text-sm">

          {/* PHẦN 1: THÔNG TIN NHÂN THÂN */}
          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-blue-600 block mb-3 border-b pb-1">1. Thông tin nhân thân</span>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-gray-600 font-medium mb-1">Họ và tên *</label>
                <input type="text" required value={formData.hoVaTen} onChange={e => setFormData({...formData, hoVaTen: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Số CCCD/CMND *</label>
                <input type="text" required value={formData.cccdCmnd} onChange={e => setFormData({...formData, cccdCmnd: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Ngày cấp CCCD</label>
                <input type="date" value={formData.ngayCap} onChange={e => setFormData({...formData, ngayCap: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Ngày sinh *</label>
                <input type="date" required value={formData.ngaySinh} onChange={e => setFormData({...formData, ngaySinh: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Dân tộc</label>
                <select value={formData.idDanToc} onChange={e => setFormData({...formData, idDanToc: e.target.value})} className="w-full p-2.5 border rounded-lg focus:border-blue-500 outline-none">
                  <option value="">-- Chọn Dân tộc --</option>
                  {listDanToc.map(dt => <option key={dt.id} value={dt.id}>{dt.name}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Trình độ học vấn</label>
                <input type="text" value={formData.trinhDo} onChange={e => setFormData({...formData, trinhDo: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" placeholder="Ví dụ: 12/12" />
              </div>
              <div className="md:col-span-3">
                <label className="block text-gray-600 font-medium mb-1">Quê quán</label>
                <input type="text" value={formData.queQuan} onChange={e => setFormData({...formData, queQuan: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div className="md:col-span-3">
                <label className="block text-gray-600 font-medium mb-1">Hộ khẩu thường trú</label>
                <input type="text" value={formData.hkThuongTru} onChange={e => setFormData({...formData, hkThuongTru: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div className="md:col-span-3">
                <label className="block text-gray-600 font-medium mb-1">Địa chỉ sau sáp nhập</label>
                <input type="text" value={formData.diaChiSauSatNhap} onChange={e => setFormData({...formData, diaChiSauSatNhap: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
            </div>
          </div>

          {/* PHẦN 2: TIỀN ÁN TIỀN SỰ & QUẢN LÝ */}
          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-indigo-600 block mb-3 border-b pb-1">2. Tiền án, tiền sự & quản lý địa bàn</span>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-gray-600 font-medium mb-1">Đơn vị lập hồ sơ (Xã) *</label>
                <select required value={formData.idCaxLap} onChange={e => setFormData({...formData, idCaxLap: e.target.value})} className="w-full p-2.5 border rounded-lg focus:border-blue-500 outline-none">
                  <option value="">-- Chọn Xã lập hồ sơ --</option>
                  {listXa.map((xa, idx) => <option key={idx} value={xa.idCaxLap}>{xa.tenXa}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Số lần cai nghiện</label>
                <input type="number" min="0" value={formData.caiNghienLanThu} onChange={e => setFormData({...formData, caiNghienLanThu: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Thời điểm sử dụng MT đầu</label>
                <input type="date" value={formData.thoidiemSdMaTuyDau} onChange={e => setFormData({...formData, thoidiemSdMaTuyDau: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Số tiền án</label>
                <input type="number" min="0" value={formData.tienAn} onChange={e => setFormData({...formData, tienAn: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Số tiền sự</label>
                <input type="number" min="0" value={formData.tienSu} onChange={e => setFormData({...formData, tienSu: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Đại diện gia đình</label>
                <input type="text" value={formData.daiDienGiaDinh} onChange={e => setFormData({...formData, daiDienGiaDinh: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
            </div>
          </div>

          {/* PHẦN 3: QUÁ TRÌNH CHẤP HÀNH */}
          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-emerald-600 block mb-3 border-b pb-1">3. Chấp hành cai nghiện</span>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-gray-600 font-medium mb-1">Hình thức cai nghiện</label>
                <select value={formData.hinhThucCaiNghien} onChange={e => setFormData({...formData, hinhThucCaiNghien: e.target.value})} className="w-full p-2.5 border rounded-lg focus:border-blue-500 outline-none">
                  <option value="Bắt buộc">Bắt buộc</option>
                  <option value="Tự nguyện">Tự nguyện</option>
                  <option value="Lưu trú">Lưu trú</option>
                </select>
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Thời gian chấp hành (tháng)</label>
                <input type="number" min="0" value={formData.thoiGianCh} onChange={e => setFormData({...formData, thoiGianCh: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Ngày vào cơ sở</label>
                <input type="date" value={formData.ngayVaoCs} onChange={e => setFormData({...formData, ngayVaoCs: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Quyết định tòa án</label>
                <input type="text" value={formData.qdToaAn} onChange={e => setFormData({...formData, qdToaAn: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">TAND quyết định</label>
                <input type="text" value={formData.tandKhuVuc} onChange={e => setFormData({...formData, tandKhuVuc: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Quyết định xét giảm</label>
                <input type="text" value={formData.qdXetGiam} onChange={e => setFormData({...formData, qdXetGiam: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Số tháng đã chấp hành</label>
                <input type="number" min="0" value={formData.soThangDaCh} onChange={e => setFormData({...formData, soThangDaCh: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-gray-600 font-medium mb-1">Số ngày đã chấp hành</label>
                <input type="number" min="0" value={formData.soNgayDaCh} onChange={e => setFormData({...formData, soNgayDaCh: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
              </div>
            </div>
          </div>

          {/* GHI CHÚ */}
          <div>
            <label className="block text-gray-600 font-medium mb-1">Ghi chú đặc biệt</label>
            <textarea rows="2" value={formData.ghiChu} onChange={e => setFormData({...formData, ghiChu: e.target.value})} className="w-full p-2 border rounded-lg focus:border-blue-500 outline-none" />
          </div>

          <div className="border-t pt-5 flex justify-end gap-2">
            <button type="button" onClick={onClose} className="bg-gray-400 text-white px-5 py-2.5 rounded-xl hover:bg-gray-500 transition-all font-semibold">Hủy bỏ</button>
            <button type="submit" className="bg-blue-600 text-white px-5 py-2.5 rounded-xl hover:bg-blue-700 transition-all font-semibold">Lưu thông tin</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PersonFormModal;