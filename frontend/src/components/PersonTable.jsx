import React from 'react';

function PersonTable({ listData, onRowClick, onEdit, onDelete, currentPage = 0, pageSize = 15 }) {
  return (
    <div className="overflow-x-auto p-4">
      <table className="min-w-full bg-white border rounded-lg">
        <thead>
          <tr className="bg-gray-100 text-left text-xs font-semibold uppercase tracking-wider text-gray-600 border-b">
            <th className="p-3">TT</th>
            <th className="p-3">Họ và tên</th>
            <th className="p-3">CCCD/CMND</th>
            <th className="p-3">Tuổi</th>
            <th className="p-3">Khu vực quản lý</th>
            <th className="p-3">Hình thức</th>
            <th className="p-3 text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {listData.length === 0 ? (
            <tr>
              <td colSpan="7" className="p-4 text-center text-gray-500">Không tìm thấy người nào khớp với bộ lọc</td>
            </tr>
          ) : (
            listData.map((person, index) => (
              <tr
                key={person.tt}
                onClick={() => onRowClick(person)}
                className="border-b hover:bg-blue-50 cursor-pointer transition-colors"
              >
                <td className="p-3 font-semibold text-gray-500">
                  {currentPage * pageSize + index + 1}
                </td>

                <td className="p-3 font-medium text-gray-900">
                  {person.hoVaTen ? person.hoVaTen.trim().replace(/\s+/g, ' ') : <span className="text-gray-400 italic">Chưa cập nhật</span>}
                </td>
                <td className="p-3">{person.cccdCmnd}</td>
                <td className="p-3">{person.tuoi}</td>
                <td className="p-3">{person.tenCaxLap || 'Chưa rõ'}</td>

                {/* ĐỒNG BỘ MÀU SẮC TRÊN BẢNG */}
                <td className="p-3">
                  {(() => {
                    const hinhThuc = (person.hinhThucCaiNghien || '').trim();
                    if (hinhThuc === 'Tự nguyện') {
                      return (
                        <span className="px-2.5 py-1 text-xs font-semibold rounded bg-emerald-100 text-emerald-800 border border-emerald-200">
                          Tự nguyện
                        </span>
                      );
                    } else if (hinhThuc === 'Lưu trú') {
                      return (
                        <span className="px-2.5 py-1 text-xs font-semibold rounded bg-amber-100 text-amber-800 border border-amber-200">
                          Lưu trú
                        </span>
                      );
                    } else {
                      // Tất cả các trường hợp khác (bao gồm cả 'Bắt buộc' hoặc rỗng) đều hiển thị màu Đỏ
                      return (
                        <span className="px-2.5 py-1 text-xs font-semibold rounded bg-red-100 text-red-800 border border-red-200">
                          Bắt buộc
                        </span>
                      );
                    }
                  })()}
                </td>

                <td className="p-3 text-center space-x-2">
                  <button
                    onClick={(e) => { e.stopPropagation(); onEdit(person); }}
                    className="text-blue-600 hover:text-blue-900 bg-blue-50 p-1 px-2 rounded text-xs"
                  >
                    ✏️ Sửa
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); onDelete(person.tt); }}
                    className="text-red-600 hover:text-red-900 bg-red-50 p-1 px-2 rounded text-xs"
                  >
                    🗑️ Xóa
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default PersonTable;