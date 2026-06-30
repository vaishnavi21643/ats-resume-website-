import { useEffect, useState } from 'react';
import { getJobs, getCandidates, getJobMatches } from '../api/client';
import ScoreRing from '../components/ScoreRing';
import Spinner from '../components/Spinner';

const StatCard = ({ label, value, }) => (
  <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-5">
    <p className="text-3xl font-bold text-teal-400">{value ?? '—'}</p>
    <p className="text-zinc-400 text-sm mt-1">{label}</p>
  </div>
);

const Dashboard = () => {
  const [jobs, setJobs] = useState([]);
  const [totalCandidates, setTotalCandidates] = useState(0);
  const [matchMap, setMatchMap] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [jobsData, candidatesData] = await Promise.all([getJobs(), getCandidates()]);
        setJobs(jobsData);
        setTotalCandidates(candidatesData.length);

        const entries = await Promise.all(
          jobsData.map(async (job) => {
            try {
              const matches = await getJobMatches(job.id);
              return [job.id, matches.slice(0, 3)];
            } catch {
              return [job.id, []];
            }
          })
        );
        setMatchMap(Object.fromEntries(entries));
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const allMatches = Object.values(matchMap).flat();
  const totalMatches = allMatches.length;
  const avgScore = totalMatches
    ? Math.round(allMatches.reduce((sum, m) => sum + m.score, 0) / totalMatches)
    : null;

  if (loading) return <div className="p-8"><Spinner label="Loading dashboard..." /></div>;

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <h1 className="text-2xl font-semibold text-white mb-6">Dashboard</h1>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-10">
        <StatCard label="Total Candidates" value={totalCandidates} />
        <StatCard label="Total Jobs" value={jobs.length} />
        <StatCard label="Total Matches Run" value={totalMatches} />
        <StatCard label="Average Match Score" value={avgScore} />
      </div>

      {/* Top Matches per Job */}
      <h2 className="text-lg font-medium text-white mb-4">Top Matches per Job</h2>

      {jobs.length === 0 ? (
        <p className="text-zinc-500 text-sm">No jobs yet. Upload a job description to get started.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {jobs.map((job) => {
            const matches = matchMap[job.id] ?? [];
            return (
              <div key={job.id} className="bg-zinc-900 border border-zinc-800 rounded-xl p-5">
                <p className="text-white font-medium">{job.title}</p>
                <p className="text-zinc-500 text-xs mb-4">{job.department}</p>
                {matches.length === 0 ? (
                  <p className="text-zinc-600 text-xs">No matches yet</p>
                ) : (
                  <ul className="flex flex-col gap-3">
                    {matches.map((m, i) => (
                      <li key={m.id} className="flex items-center gap-3">
                        <span className="text-zinc-500 text-xs w-4">{i + 1}.</span>
                        <ScoreRing score={m.score} size={48} />
                        <span className="text-zinc-200 text-sm">{m.candidateName}</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Dashboard;