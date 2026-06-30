const ScoreRing = ({ score, size = 64 }) => {
  const radius = 26;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;

  const color =
    score >= 70 ? '#22c55e' : score >= 40 ? '#eab308' : '#ef4444';

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      <svg width={size} height={size} viewBox="0 0 64 64" className="-rotate-90">
        <circle cx="32" cy="32" r={radius} fill="none" stroke="#27272a" strokeWidth="6" />
        <circle
          cx="32" cy="32" r={radius}
          fill="none"
          stroke={color}
          strokeWidth="6"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
        />
      </svg>
      <span className="absolute text-sm font-semibold text-white">{score}</span>
    </div>
  );
};

export default ScoreRing;