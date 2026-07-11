import React from 'react';

function Sidebar({ currentTab, setCurrentTab }) {
  const menuItems = [
    { id: 'dashboard', name: 'Bảng điều khiển', icon: '📊' },
    { id: 'danh-sach', name: 'Quản lý người cai nghiện', icon: '👥' },
    { id: 'thong-ke', name: 'Thống kê khu vực', icon: '📍' },
    { id: 'cấu-hình', name: 'Cấu hình hệ thống', icon: '⚙️' }
  ];

  return (
    <div className="w-64 bg-slate-900 text-gray-100 min-h-screen flex flex-col shadow-xl fixed left-0 top-0">
      {/* Tên hệ thống */}
      <div className="p-5 border-b border-slate-800 flex items-center gap-3">
        <span className="text-2xl">🛡️</span>
        <div>
          <h2 className="font-bold text-sm uppercase tracking-wider text-white">Trại Cai Nghiện</h2>
          <p className="text-xs text-slate-400">Hệ thống quản lý lý lịch</p>
        </div>
      </div>

      {/* Danh sách Menu */}
      <nav className="flex-1 p-4 space-y-1">
        {menuItems.map((item) => (
          <button
            key={item.id}
            onClick={() => setCurrentTab(item.id)}
            className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-all ${
              currentTab === item.id
                ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/30 font-semibold'
                : 'text-slate-400 hover:bg-slate-800 hover:text-white'
            }`}
          >
            <span className="text-lg">{item.icon}</span>
            {item.name}
          </button>
        ))}
      </nav>

      {/* Footer Admin */}
      <div className="p-4 border-t border-slate-800 text-center text-xs text-slate-500">
        Phiên bản hệ thống v1.0
      </div>
    </div>
  );
}

export default Sidebar;