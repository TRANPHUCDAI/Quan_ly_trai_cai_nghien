import React from 'react';

function SearchBar({ keyword, onSearchChange, selectedCaxName, onClearCaxFilter }) {
  return (
    <div className="p-4">
      {/* Ô nhập thông tin tìm kiếm */}
      <div className="relative">
        <input
          type="text"
          value={keyword}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="Tìm kiếm theo tên, số CCCD/CMND, xã hoặc tuổi..."
          className="w-full p-3 pl-10 border rounded-lg focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none"
        />
        <span className="absolute left-3 top-3.5 text-gray-400">🔍</span>
      </div>

      {/* Tag thông báo nếu đang có bộ lọc theo xã */}
      {selectedCaxName && (
        <div className="mt-2 p-2 bg-yellow-50 text-yellow-800 rounded-md flex justify-between items-center text-sm">
          <span>Đang hiển thị khu vực: <strong>{selectedCaxName}</strong></span>
          <button
            onClick={onClearCaxFilter}
            className="underline text-blue-600 hover:text-blue-800"
          >
            Xem tất cả khu vực
          </button>
        </div>
      )}
    </div>
  );
}

export default SearchBar;