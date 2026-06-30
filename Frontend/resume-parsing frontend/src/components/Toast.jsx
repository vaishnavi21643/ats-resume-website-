import { useEffect } from 'react';

const Toast = ({ message, type = 'success', onClose }) => {
  useEffect(() => {
    const t = setTimeout(onClose, 3000);
    return () => clearTimeout(t);
  }, [onClose]);

  const bg = type === 'success' ? 'bg-green-500' : 'bg-red-500';

  return (
    <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-4 py-3 rounded-xl text-white text-sm shadow-lg ${bg}`}>
      <span>{message}</span>
      <button onClick={onClose} className="text-white/70 hover:text-white text-lg leading-none">&times;</button>
    </div>
  );
};

export default Toast;