const Modal = ({ title, onClose, children }) => {
  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-black/70">
      <div className="bg-zinc-900 border border-zinc-800 rounded-2xl w-full max-w-md mx-4 p-6">
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-white font-semibold text-lg">{title}</h2>
          <button onClick={onClose} className="text-zinc-400 hover:text-white text-2xl leading-none">&times;</button>
        </div>
        {children}
      </div>
    </div>
  );
};

export default Modal;