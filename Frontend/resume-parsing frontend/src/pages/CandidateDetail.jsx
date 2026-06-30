import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getCandidate, getCandidateMatches, getJobs, runMatch } from '../api/client';
import ScoreRing from '../components/ScoreRing';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import useToast from '../hooks/useToast';

const CandidateDetail = () => {
  const { id } = useParams();
  const { toast, showToast, clearToast } = useToast();

  const [candidate, setCandidate] = useState(null);
  const [matches, setMatches] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [selectedJob, setSelectedJob] = useState('');
  const [matching, setMatching] = useState(false);
  const [expandedId, setExpandedId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [c, m, j] = await Promise.all([
          getCandidate(id),
          getCandidateMatches(id),
          getJobs(),
        ]);
        setCandidate(c);
        setMatches(m.sort((a, b) => b.score - a.score));
        setJobs(j);
      } catch {
        showToast('Failed to load data', 'error');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  const handleMatch = async () => {
    if (!selectedJob) return;
    setMatching(true);
    try {
      const result = await runMatch(id, selectedJob);
      setMatches(prev => [...prev, result].sort((a, b) => b.score - a.score));
      showToast('Match completed!');
      setSelectedJob('');
    } catch {
      showToast('Match failed', 'error');
    } finally {
      setMatching(false);
    }
  };

  if (loading) return <div className="p-8"><Spinner label="Loading..." /></div>;
  if (!candidate) return <div className="p-8 text-zinc-400">Candidate not found.</div>;

  return (
    <div className="p-8 max-w-4xl mx-auto">
      {/* Candidate Info */}
      <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-6 mb-6">
        <h1 className="text-xl font-semibold text-white">{candidate.name}</h1>
        <p className="text-zinc-400 text-sm mb-4">{candidate.email}</p>
        <p className="text-xs text-zinc-500 mb-2 uppercase tracking-wide">Resume</p>
        <div className="bg-zinc-800 rounded-lg p-4 max-h-48 overflow-y-auto text-zinc-300 text-sm whitespace-pre-wrap leading-relaxed">
          {candidate.resumeText || 'No resume text available.'}
        </div>
      </div>

      {/* Run New Match */}
      <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-6 mb-6">
        <h2 className="text-white font-medium mb-4">Run New Match</h2>
        <div className="flex gap-3 items-center">
          <select
            value={selectedJob}
            onChange={e => setSelectedJob(e.target.value)}
            className="flex-1 bg-zinc-800 border border-zinc-700 text-white text-sm rounded-xl px-4 py-2.5 focus:outline-none focus:border-teal-500"
          >
            <option value="">Select a job...</option>
            {jobs.map(j => (
              <option key={j.id} value={j.id}>{j.title} — {j.department}</option>
            ))}
          </select>
          <button
            onClick={handleMatch}
            disabled={!selectedJob || matching}
            className="px-5 py-2.5 bg-teal-600 hover:bg-teal-500 disabled:opacity-40 text-white text-sm rounded-xl transition-colors whitespace-nowrap"
          >
            {matching ? 'Analyzing...' : 'Match'}
          </button>
        </div>
        {matching && (
          <div className="mt-4">
            <Spinner label="Analyzing match, this may take 10–30 seconds..." />
          </div>
        )}
      </div>

      {/* Matches List */}
      <h2 className="text-white font-medium mb-3">Match Results</h2>
      {matches.length === 0 ? (
        <p className="text-zinc-500 text-sm">No matches yet. Run a match above.</p>
      ) : (
        <div className="flex flex-col gap-3">
          {matches.map(m => (
            <div key={m.id} className="bg-zinc-900 border border-zinc-800 rounded-xl p-5">
              <div className="flex items-center gap-4">
                <ScoreRing score={m.score} size={56} />
                <div className="flex-1">
                  <p className="text-white font-medium">{m.jobTitle}</p>
                  <button
                    onClick={() => setExpandedId(expandedId === m.id ? null : m.id)}
                    className="text-teal-400 text-xs mt-1 hover:underline"
                  >
                    {expandedId === m.id ? 'Hide explanation ▲' : 'View explanation ▼'}
                  </button>
                </div>
              </div>
              {expandedId === m.id && (
                <p className="mt-4 text-zinc-300 text-sm leading-relaxed border-t border-zinc-800 pt-4">
                  {m.explanation}
                </p>
              )}
            </div>
          ))}
        </div>
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </div>
  );
};

export default CandidateDetail;