const EmptyState = ({ title = 'Nothing here yet', description, action }) => (
  <div className="flex flex-col items-center justify-center py-20 text-center">
    <div className="text-4xl mb-4">📭</div>
    <h3 className="text-white font-medium text-lg mb-1">{title}</h3>
    {description && <p className="text-zinc-400 text-sm mb-6 max-w-xs">{description}</p>}
    {action && (
      <button
        onClick={action.onClick}
        className="px-4 py-2 rounded-xl bg-teal-600 hover:bg-teal-500 text-white text-sm transition-colors"
      >
        {action.label}
      </button>
    )}
  </div>
);

export default EmptyState;