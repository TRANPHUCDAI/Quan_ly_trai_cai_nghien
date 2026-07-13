import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Sidebar from './components/Sidebar';
import StatsGrid from './components/StatsGrid';
import SearchBar from './components/SearchBar';
import PersonTable from './components/PersonTable';
import PersonDetailModal from './components/PersonDetailModal';
import PersonFormModal from './components/PersonFormModal';

const API_BASE = 'http://localhost:8081/api/nguoi-cai-nghien';

function App() {
  const [currentTab, setCurrentTab] = useState('danh-sach');
  const [listData, setListData] = useState([]);
  const [statsData, setStatsData] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [selectedCaxName, setSelectedCaxName] = useState('');

  // Modals States
  const [selectedPerson, setSelectedPerson] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [formEditData, setFormEditData] = useState(null);

  // Hàm tải danh sách hồ sơ
  const fetchListData = async () => {
    try {
      const response = await axios.get(API_BASE, {
        params: { keyword, tenCax: selectedCaxName, page: 0, size: 50 }
      });
      setListData(response.data.content || []);
    } catch (error) {
      console.error("Lỗi lấy danh sách hồ sơ:", error);
    }
  };

  // Hàm tải dữ liệu thống kê ô vuông xã
  const fetchStats = async () => {
    try {
      const response = await axios.get(`${API_BASE}/thong-ke/theo-xa`);
      const formattedStats = response.data.map((item) => {
        const nameXax = item.tenXa || 'Chưa xác định';
        return { id: nameXax, name: nameXax, count: item.soLuong ?? 0 };
      });
      setStatsData(formattedStats);
    } catch (error) {
      console.error("Lỗi lấy dữ liệu thống kê xã:", error);
    }
  };

  useEffect(() => { fetchListData(); }, [keyword, selectedCaxName]);
  useEffect(() => { fetchStats(); }, []);

  // XỬ LÝ: THÊM MỚI HOẶC CẬP NHẬT (SAVE)
    const handleSavePerson = async (formData) => {
      try {
        if (formData.tt) {
          // Thực hiện Cập nhật (PUT) theo ID (trường tt)
          await axios.put(`${API_BASE}/${formData.tt}`, formData);
          alert("Cập nhật thông tin hồ sơ thành công!");
        } else {
          // Thực hiện Tạo mới (POST)
          await axios.post(API_BASE, formData);
          alert("Thêm mới hồ sơ thành công!");
        }

        // 1. Đóng Form Modal lại ngay lập tức
        setIsFormOpen(false);

        // 2. ÉP RE-RENDER: Gọi lại API lấy danh sách mới nhất từ Database đổ vào state
        await fetchListData();

        // 3. Cập nhật lại luôn cả các ô vuông thống kê số lượng xã nếu có thay đổi địa bàn
        await fetchStats();

      } catch (error) {
        console.error("Lỗi khi lưu hồ sơ dữ liệu:", error);
        alert("Đã có lỗi xảy ra khi lưu dữ liệu!");
      }
    };

  // XỬ LÝ: XÓA HỒ SƠ (DELETE)
  const handleDeletePerson = async (ttId) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa vĩnh viễn hồ sơ đối tượng này?")) {
      try {
        await axios.delete(`${API_BASE}/${ttId}`);
        alert("Xóa hồ sơ thành công!");
        fetchListData();
        fetchStats();
      } catch (error) {
        console.error("Lỗi xóa hồ sơ:", error);
        alert("Không thể xóa đối tượng này!");
      }
    }
  };

  const handleOpenCreateForm = () => {
    setFormEditData(null); // Không có dữ liệu cũ
    setIsFormOpen(true);
  };

  const handleOpenEditForm = (person) => {
    setFormEditData(person); // Truyền dữ liệu cũ vào để hiển thị lên form sửa
    setIsFormOpen(true);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex">
      <Sidebar currentTab={currentTab} setCurrentTab={setCurrentTab} />

      <div className="flex-1 pl-64 flex flex-col">
        <header className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-8 sticky top-0 z-10 shadow-sm">
          <div className="flex items-center gap-2">
            <span className="text-gray-400 text-sm">Hệ thống hành chính</span>
            <span className="text-gray-300">/</span>
            <span className="text-gray-800 font-medium text-sm capitalize">{currentTab.replace('-', ' ')}</span>
          </div>
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex justify-center items-center font-bold text-sm">AD</div>
            <span className="text-sm font-semibold text-gray-700">Cán bộ quản lý</span>
          </div>
        </header>

        <main className="p-8 flex-1">
          {currentTab === 'dashboard' && (
            <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
              <h2 className="text-lg font-bold text-gray-800 mb-4">Tổng hợp số liệu dữ liệu</h2>
              <StatsGrid statsData={statsData} selectedCaxId={selectedCaxName} onSelectCax={(id, name) => setSelectedCaxName(selectedCaxName === name ? '' : name)} />
            </div>
          )}

          {currentTab === 'danh-sach' && (
            <div className="space-y-6">
              <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100">
                <span className="text-xs font-bold uppercase tracking-wider text-gray-400 block mb-3">Lọc nhanh theo địa bàn xã lập hồ sơ</span>
                <StatsGrid statsData={statsData} selectedCaxId={selectedCaxName} onSelectCax={(id, name) => setSelectedCaxName(selectedCaxName === name ? '' : name)} />
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="p-5 border-b border-gray-100 bg-gray-50/50 flex justify-between items-center">
                  <h3 className="font-bold text-gray-800">Danh sách đối tượng hồ sơ</h3>
                  <button
                    onClick={handleOpenCreateForm}
                    className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-4 py-2 rounded-lg shadow-md transition-all"
                  >
                    + Thêm Hồ Sơ Mới
                  </button>
                </div>

                <SearchBar keyword={keyword} onSearchChange={setKeyword} selectedCaxName={selectedCaxName} onClearCaxFilter={() => setSelectedCaxName('')} />

                <PersonTable
                  listData={listData}
                  onRowClick={(p) => { setSelectedPerson(p); setIsDetailOpen(true); }}
                  onEdit={handleOpenEditForm}
                  onDelete={handleDeletePerson}
                />
              </div>
            </div>
          )}
        </main>
      </div>

      {/* MODAL XEM CHI TIẾT */}
      <PersonDetailModal isOpen={isDetailOpen} person={selectedPerson} onClose={() => setIsDetailOpen(false)} />

      {/* MODAL FORM: THÊM / SỬA */}
      <PersonFormModal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSave={handleSavePerson}
        editData={formEditData}
      />
    </div>
  );
}

export default App;