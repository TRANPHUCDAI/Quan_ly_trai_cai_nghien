import React from 'react';

function StatsGrid({ statsData, selectedCaxId, onSelectCax }) {
  return (
    <div className="grid grid-cols-4 gap-4">
      {statsData.map((item, index) => {
        const isSelected = selectedCaxId === item.id;
        return (
          <div
            key={index}
            onClick={() => onSelectCax(item.id, item.name)}
            className={`p-4 border rounded-lg cursor-pointer transition-all flex flex-col justify-center min-h-[85px] ${
              isSelected
                ? 'border-blue-500 bg-blue-50 shadow-md ring-1 ring-blue-500'
                : 'bg-white border-gray-200 hover:border-blue-300 hover:bg-gray-50/50'
            }`}
          >
            {/* Tên Xã */}
            <div className="text-xs font-semibold text-gray-500 truncate mb-1">
              {item.name || 'Chưa xác định'}
            </div>

            {/* Số người */}
            <div className="text-base font-bold text-gray-800">
              {item.count ?? 0} <span className="text-sm font-normal text-gray-500">người</span>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default StatsGrid;