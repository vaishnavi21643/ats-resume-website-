import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCandidates, createCandidate, deleteCandidate } from '../api/client';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import Spinner from '../components/Spinner';
import EmptyState from '../components/EmptyState';
import Toast from '../components/Toast';
import useToast from '../hooks/useToast';

const Candidates = () => {
  const navigate = useNavigate();
  const { toast, showToast, clearToast } = useToast();

  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [confirmId, setConfirmId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const [form, setForm] = useState({ name: '', email: '', resume: null });

  const load = async () => {
    try {
      const data = await getCandidates();
      setCandidates(data);
    } catch {
      showToast('Failed to load candidates', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async () => {
    if (!form.name || !form.email || !form.resume) return;
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('name', form.name);
      fd.append('email', form.email);
      fd.append('resume', form.resume);
      await createCandidate(fd);
      showToast('Candidate added successfully');
      setShowModal(false);
      setForm({ name: '', email: '', resume: null });
      load();
    } catch {
      showToast('Failed to add candidate', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    try {
      await deleteCandidate(confirmId);
      showToast('Candidate deleted');
      setConfirmId(null);
      load();
    } catch {
      showToast('Cannot delete — match results may exist', 'error');
      setConfirmId(null);
    }
  };

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold text-white">Candidates</h1>
        <button
          onClick={() => setShowModal(true)}
          className="px-4 py-2 bg-teal-600 hover:bg-teal-500 text-white text-sm rounded-xl transition-colors"
        >
          + Add Candidate
        </button>
      </div>

      {loading ? (
        <Spinner label="Loading candidates..." />
      ) : candidates.length === 0 ? (
        <EmptyState
          title="No candidates yet"
          description="Upload a resume to get started."
          action={{ label: 'Add Candidate', onClick: () => setShowModal(true) }}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {candidates.map((c) => (
            <div key={c.id} className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 flex flex-col gap-3">
              <div>
                <p className="text-white font-medium">{c.name}</p>
                <p className="text-zinc-400 text-sm">{c.email}</p>
                <p className="text-zinc-600 text-xs mt-1">{new Date(c.createdAt).toLocaleDateString()}</p>
              </div>
              <div className="flex gap-2 mt-auto">
                <button
                  onClick={() => navigate(`/candidates/${c.id}`)}
                  className="flex-1 py-1.5 text-sm rounded-lg border border-zinc-700 text-zinc-300 hover:border-teal-500 hover:text-teal-400 transition-colors"
                >
                  View
                </button>
                <button
                  onClick={() => setConfirmId(c.id)}
                  className="flex-1 py-1.5 text-sm rounded-lg border border-zinc-700 text-zinc-300 hover:border-red-500 hover:text-red-400 transition-colors"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <Modal title="Add Candidate" onClose={() => setShowModal(false)}>
          <div className="flex flex-col gap-4">
            <input
              type="text" placeholder="Name"
              value={form.name}
              onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
              className="bg-zinc-800 border border-zinc-700 rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-teal-500"
            />
            <input
              type="email" placeholder="Email"
              value={form.email}
              onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
              className="bg-zinc-800 border border-zinc-700 rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-teal-500"
            />
            <input
              type="file" accept=".pdf"
              onChange={e => setForm(f => ({ ...f, resume: e.target.files[0] }))}
              className="text-sm text-zinc-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:bg-zinc-700 file:text-white file:text-sm"
            />
            <button
              onClick={handleSubmit}
              disabled={submitting || !form.name || !form.email || !form.resume}
              className="py-2.5 bg-teal-600 hover:bg-teal-500 disabled:opacity-40 text-white text-sm rounded-xl transition-colors"
            >
              {submitting ? 'Uploading...' : 'Add Candidate'}
            </button>
          </div>
        </Modal>
      )}

      {confirmId && (
        <ConfirmDialog
          message="Delete this candidate? This cannot be undone."
          onConfirm={handleDelete}
          onCancel={() => setConfirmId(null)}
        />
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </div>
  );
};

export default Candidates;