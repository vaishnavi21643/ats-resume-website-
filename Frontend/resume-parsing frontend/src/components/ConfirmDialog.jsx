const ConfirmDialog = ({ message = 'Are you sure?', onConfirm, onCancel }) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70">
    <div className="bg-zinc-900 border border-zinc-800 rounded-2xl w-full max-w-sm mx-4 p-6">
      <p className="text-white text-base mb-6">{message}</p>
      <div className="flex justify-end gap-3">
        <button
          onClick={onCancel}
          className="px-4 py-2 rounded-xl text-sm text-zinc-400 border border-zinc-700 hover:border-zinc-500 hover:text-white transition-colors"
        >
          Cancel
        </button>
        <button
          onClick={onConfirm}
          className="px-4 py-2 rounded-xl text-sm text-white bg-red-600 hover:bg-red-500 transition-colors"
        >
          Delete
        </button>
      </div>
    </div>
  </div>
);

export default ConfirmDialog;