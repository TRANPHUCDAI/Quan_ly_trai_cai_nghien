import React, { useState, useEffect } from 'react';
import axios from 'axios';
import * as XLSX from 'xlsx';
import { Bar, Pie } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';

import Sidebar from './components/Sidebar';
import StatsGrid from './components/StatsGrid';
import SearchBar from './components/SearchBar';
import PersonTable from './components/PersonTable';
import PersonDetailModal from './components/PersonDetailModal';
import PersonFormModal from './components/PersonFormModal';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend);

const API_BASE = 'https://quan-ly-trai-cai-nghien-trangvi.onrender.com';

function App() {
  const [currentTab, setCurrentTab] = useState('danh-sach');
  const [listData, setListData] = useState([]);
  const [statsData, setStatsData] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [selectedCaxName, setSelectedCaxName] = useState('');

  // Các State phục vụ Phân trang chuyên nghiệp
  const [currentPage, setCurrentPage] = useState(0); // Spring Boot bắt đầu từ trang 0
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 15; // Định dạng hiển thị 15 người mỗi trang

  // Modals States
  const [selectedPerson, setSelectedPerson] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [formEditData, setFormEditData] = useState(null);

  // Tải danh sách hiển thị trên bảng có phân trang
  const fetchListData = async () => {
    try {
      const response = await axios.get(API_BASE, {
        params: {
          keyword,
          tenCax: selectedCaxName,
          page: currentPage,
          size: pageSize,
          sort: 'tt,asc' // Sắp xếp theo ID tăng dần
        }
      });
      setListData(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      console.error("Lỗi lấy danh sách hồ sơ:", error);
    }
  };

  // Tải dữ liệu các ô vuông thống kê xã
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

  // Reset về trang 0 khi thay đổi từ khóa tìm kiếm hoặc lọc theo xã
  useEffect(() => {
    setCurrentPage(0);
  }, [keyword, selectedCaxName]);

  // Gọi API lấy dữ liệu khi bộ lọc hoặc số trang thay đổi
  useEffect(() => {
    fetchListData();
  }, [keyword, selectedCaxName, currentPage]);

  useEffect(() => {
    fetchStats();
  }, []);

  // CHỨC NĂNG: XUẤT FILE EXCEL TOÀN BỘ HỆ THỐNG ĐẦY ĐỦ CÁC TRƯỜNG LÝ LỊCH
    const handleExportExcel = async () => {
      try {
        alert("Hệ thống đang chuẩn bị tải dữ liệu toàn bộ các xã, vui lòng đợi trong giây lát...");

        const response = await axios.get(API_BASE, {
          params: { keyword: '', tenCax: '', page: 0, size: 5000 } // Đặt size cực đại để lấy trọn vẹn
        });

        const allData = response.data.content || [];

        if (allData.length === 0) {
          alert("Không có dữ liệu nào trong hệ thống để xuất!");
          return;
        }

        // MAP ĐẦY ĐỦ TẤT CẢ CÁC TRƯỜNG CỦA MỘT HỒ SƠ LÝ LỊCH
        const dataToExport = allData.map((p, index) => ({
          "STT": index + 1,
          "Họ và Tên": p.hoVaTen ? p.hoVaTen.trim().replace(/\s+/g, ' ') : "Chưa cập nhật",
          "Số CCCD/CMND": p.cccdCmnd || "Chưa cập nhật",
          "Ngày cấp CCCD": p.ngayCap || "Chưa cập nhật",
          "Ngày Sinh": p.ngaySinh || "Chưa cập nhật",
          "Tuổi": p.tuoi || "Chưa cập nhật",
          "Dân Tộc": p.tenDanToc || "Chưa cập nhật",
          "Trình độ học vấn": p.trinhDo || "Chưa cập nhật",
          "Quê Quán": p.queQuan || "Chưa cập nhật",
          "Hộ khẩu thường trú": p.hkThuongTru || "Chưa cập nhật",
          "Địa chỉ sau sáp nhập": p.diaChiSauSatNhap || "Chưa cập nhật",
          "Địa bàn lập hồ sơ (Xã)": p.tenCaxLap || "Chưa cập nhật",
          "Số lần cai nghiện": p.caiNghienLanThu ?? 0,
          "Thời điểm SD ma túy đầu": p.thoidiemSdMaTuyDau || "Chưa cập nhật",
          "Số tiền án": p.tienAn ?? 0,
          "Số tiền sự": p.tienSu ?? 0,
          "Người đại diện gia đình": p.daiDienGiaDinh || "Chưa cập nhật",
          "Hình Thức Cai Nghiện": p.hinhThucCaiNghien || "Bắt buộc",
          "Thời Gian Cai (Tháng)": p.thoiGianCh || 0,
          "Ngày Vào Cơ Sở": p.ngayVaoCs || "Chưa cập nhật",
          "Quyết định tòa án": p.qdToaAn || "Chưa cập nhật",
          "TAND quyết định": p.tandKhuVuc || "Chưa cập nhật",
          "Quyết định xét giảm": p.qdXetGiam || "Chưa cập nhật",
          "Số tháng đã chấp hành": p.soThangDaCh ?? 0,
          "Số ngày đã chấp hành": p.soNgayDaCh ?? 0,
          "Dự Kiến Ngày Về": p.duKienNgayVe || "Chưa cập nhật",
          "Dự kiến về năm 2026 (tháng)": p.duKienVe2026 || "",
          "Dự kiến về năm 2027 (tháng)": p.duKienVe2027 || "",
          "Ghi Chú Đặc Biệt": p.ghiChu || ""
        }));

        const worksheet = XLSX.utils.json_to_sheet(dataToExport);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Toan_Bo_Nguoi_Cai_Nghien");

        // Kéo rộng tự động các cột Excel
        const maxKeys = Object.keys(dataToExport[0]);
        worksheet["!cols"] = maxKeys.map(key => ({
          wch: Math.max(...dataToExport.map(row => (row[key] ? row[key].toString().length : 10)), key.length) + 3
        }));

        XLSX.writeFile(workbook, "Danh_sach_toan_bo_nguoi_cai_nghien_day_du.xlsx");
      } catch (error) {
        console.error("Lỗi xuất file Excel:", error);
        alert("Đã xảy ra lỗi khi tải dữ liệu xuất Excel!");
      }
    };

  const handleSavePerson = async (formData) => {
    try {
      if (formData.tt) {
        await axios.put(`${API_BASE}/${formData.tt}`, formData);
        alert("Cập nhật thông tin hồ sơ thành công!");
      } else {
        await axios.post(API_BASE, formData);
        alert("Thêm mới hồ sơ thành công!");
      }
      setIsFormOpen(false);
      await fetchListData();
      await fetchStats();
    } catch (error) {
      console.error("Lỗi khi lưu hồ sơ dữ liệu:", error);
      alert("Đã có lỗi xảy ra khi lưu dữ liệu!");
    }
  };

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

  // Config Chart
  const chartLabels = statsData.map(item => item.name);
  const chartValues = statsData.map(item => item.count);

  const barChartData = {
    labels: chartLabels,
    datasets: [{
      label: 'Số lượng đối tượng (người)',
      data: chartValues,
      backgroundColor: 'rgba(59, 130, 246, 0.75)',
      borderColor: 'rgb(59, 130, 246)',
      borderWidth: 1.5,
      borderRadius: 8
    }]
  };

  const pieChartData = {
    labels: chartLabels,
    datasets: [{
      data: chartValues,
      backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#6366f1'],
      borderWidth: 1
    }]
  };

  return (
    <div className="min-h-screen bg-slate-50 flex">
      <Sidebar currentTab={currentTab} setCurrentTab={setCurrentTab} />

      <div className="flex-1 pl-64 flex flex-col">
        {/* Header */}
        <header className="bg-white border-b border-slate-100 h-16 flex items-center justify-between px-8 sticky top-0 z-10 shadow-sm">
          <div className="flex items-center gap-2">
            <span className="text-gray-400 text-sm">Hệ thống quản lý hành chính</span>
            <span className="text-gray-300">/</span>
            <span className="text-blue-600 font-semibold text-sm capitalize">{currentTab.replace('-', ' ')}</span>
          </div>
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-blue-600 to-indigo-600 text-white flex justify-center items-center font-bold text-sm shadow-md">AD</div>
            <span className="text-sm font-semibold text-gray-700">Cán bộ quản lý</span>
          </div>
        </header>

        {/* Content Container */}
        <main className="p-8 flex-1">

          {/* TAB 1: BẢNG ĐIỀU KHIỂN */}
          {currentTab === 'dashboard' && (
            <div className="space-y-6">
              <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-100">
                <div className="flex justify-between items-center mb-4">
                  <div>
                    <h2 className="text-lg font-bold text-gray-800">Thống kê nhanh khu vực xã lập hồ sơ</h2>
                    <p className="text-xs text-gray-400 mt-0.5">Bấm vào ô vuông của từng xã để lọc nhanh danh sách, hoặc bấm "Tất cả" để quay lại.</p>
                  </div>
                  {selectedCaxName && (
                    <button
                      onClick={() => setSelectedCaxName('')}
                      className="bg-blue-50 text-blue-600 hover:bg-blue-100 px-4 py-2 rounded-xl text-xs font-bold transition-all"
                    >
                      🔄 Hiển thị tất cả địa bàn
                    </button>
                  )}
                </div>
                <StatsGrid
                  statsData={statsData}
                  selectedCaxId={selectedCaxName}
                  onSelectCax={(id, name) => setSelectedCaxName(selectedCaxName === name ? '' : name)}
                />
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2 bg-white p-6 rounded-2xl shadow-sm border border-slate-100">
                  <h3 className="text-sm font-bold text-gray-700 uppercase tracking-wider mb-4">Biểu đồ so sánh số lượng giữa các địa bàn</h3>
                  <div className="h-[320px] flex items-center justify-center">
                    <Bar data={barChartData} options={{ maintainAspectRatio: false, plugins: { legend: { display: false } } }} />
                  </div>
                </div>

                <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-100 flex flex-col justify-between">
                  <h3 className="text-sm font-bold text-gray-700 uppercase tracking-wider mb-4">Tỷ lệ phần trăm phân bổ</h3>
                  <div className="h-[260px] flex items-center justify-center">
                    <Pie data={pieChartData} options={{ maintainAspectRatio: false }} />
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: DANH SÁCH */}
          {currentTab === 'danh-sach' && (
            <div className="space-y-6">
              <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-100">
                <div className="flex justify-between items-center mb-4">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400 block">Lọc nhanh theo địa bàn xã lập hồ sơ</span>
                  {selectedCaxName && (
                    <button
                      onClick={() => setSelectedCaxName('')}
                      className="bg-blue-50 text-blue-600 hover:bg-blue-100 px-4 py-2 rounded-xl text-xs font-bold transition-all"
                    >
                      🔄 Hiển thị tất cả địa bàn
                    </button>
                  )}
                </div>
                <StatsGrid
                  statsData={statsData}
                  selectedCaxId={selectedCaxName}
                  onSelectCax={(id, name) => setSelectedCaxName(selectedCaxName === name ? '' : name)}
                />
              </div>

              <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex justify-between items-center">
                  <div>
                    <h3 className="font-bold text-slate-800 text-base">Hồ sơ đối tượng quản lý</h3>
                    <p className="text-xs text-slate-400 mt-0.5">Tổng số: {totalElements} hồ sơ trong hệ thống</p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={handleExportExcel}
                      className="bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold px-4 py-2.5 rounded-xl shadow-md shadow-emerald-600/15 transition-all flex items-center gap-1.5"
                    >
                      📊 Xuất Toàn Bộ Excel
                    </button>
                    <button
                      onClick={() => { setFormEditData(null); setIsFormOpen(true); }}
                      className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-4 py-2.5 rounded-xl shadow-md shadow-blue-600/15 transition-all"
                    >
                      + Thêm Hồ Sơ Mới
                    </button>
                  </div>
                </div>

                <SearchBar
                  keyword={keyword}
                  onSearchChange={setKeyword}
                  selectedCaxName={selectedCaxName}
                  onClearCaxFilter={() => setSelectedCaxName('')}
                />

                <PersonTable
                  listData={listData}
                  onRowClick={(p) => { setSelectedPerson(p); setIsDetailOpen(true); }}
                  onEdit={(person) => { setFormEditData(person); setIsFormOpen(true); }}
                  onDelete={handleDeletePerson}
                  currentPage={currentPage}
                  pageSize={pageSize}
                />

                {/* BỘ ĐIỀU HƯỚNG PHÂN TRANG GIAO DIỆN (PAGINATION CONTROLS) */}
                {totalPages > 1 && (
                  <div className="p-4 bg-gray-50 border-t border-gray-100 flex items-center justify-between">
                    <div className="text-sm text-gray-500">
                      Hiển thị trang <span className="font-semibold text-gray-700">{currentPage + 1}</span> / {totalPages} (Tổng cộng {totalElements} người)
                    </div>
                    <div className="flex gap-1.5">
                      <button
                        disabled={currentPage === 0}
                        onClick={() => setCurrentPage(prev => prev - 1)}
                        className={`px-3 py-1.5 rounded-lg border text-xs font-medium transition-all ${
                          currentPage === 0
                            ? 'bg-gray-100 text-gray-400 cursor-not-allowed border-gray-200'
                            : 'bg-white text-gray-700 hover:bg-gray-50 hover:text-blue-600 border-gray-300'
                        }`}
                      >
                        ◀ Trước
                      </button>

                      {/* Hiển thị danh sách các số trang */}
                      {Array.from({ length: totalPages }, (_, i) => {
                        // Rút gọn bớt nút hiển thị nếu quá nhiều trang
                        if (totalPages > 6 && Math.abs(currentPage - i) > 2 && i !== 0 && i !== totalPages - 1) {
                          if (i === 1 || i === totalPages - 2) {
                            return <span key={i} className="px-2 self-center text-gray-400">...</span>;
                          }
                          return null;
                        }
                        return (
                          <button
                            key={i}
                            onClick={() => setCurrentPage(i)}
                            className={`px-3 py-1.5 rounded-lg border text-xs font-semibold transition-all ${
                              currentPage === i
                                ? 'bg-blue-600 text-white border-blue-600 shadow-sm shadow-blue-600/10'
                                : 'bg-white text-gray-700 hover:bg-gray-50 border-gray-300'
                            }`}
                          >
                            {i + 1}
                          </button>
                        );
                      })}

                      <button
                        disabled={currentPage === totalPages - 1}
                        onClick={() => setCurrentPage(prev => prev + 1)}
                        className={`px-3 py-1.5 rounded-lg border text-xs font-medium transition-all ${
                          currentPage === totalPages - 1
                            ? 'bg-gray-100 text-gray-400 cursor-not-allowed border-gray-200'
                            : 'bg-white text-gray-700 hover:bg-gray-50 hover:text-blue-600 border-gray-300'
                        }`}
                      >
                        Sau ▶
                      </button>
                    </div>
                  </div>
                )}

              </div>
            </div>
          )}

          {/* CÁC TAB KHÁC */}
          {(currentTab === 'thong-ke' || currentTab === 'cấu-hình') && (
            <div className="bg-white p-12 rounded-2xl border border-slate-100 text-center max-w-lg mx-auto mt-12 shadow-sm">
              <span className="text-4xl block mb-4">⚙️</span>
              <h3 className="font-bold text-gray-800 text-base mb-1">Chức năng đang được phát triển</h3>
              <p className="text-xs text-gray-400">Hệ thống phân quyền nâng cao và cấu hình hành chính sẽ được cập nhật ở phiên bản tiếp theo.</p>
            </div>
          )}
        </main>
      </div>

      <PersonDetailModal isOpen={isDetailOpen} person={selectedPerson} onClose={() => setIsDetailOpen(false)} />

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