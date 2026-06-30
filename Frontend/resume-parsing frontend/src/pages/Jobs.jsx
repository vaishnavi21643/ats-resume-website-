import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getJobs, createJob, deleteJob } from '../api/client';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import Spinner from '../components/Spinner';
import EmptyState from '../components/EmptyState';
import Toast from '../components/Toast';
import useToast from '../hooks/useToast';

const Jobs = () => {
  const navigate = useNavigate();
  const { toast, showToast, clearToast } = useToast();

  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [confirmId, setConfirmId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const [form, setForm] = useState({ title: '', department: '', jd: null, requirements: '' });

  const load = async () => {
    try {
      const data = await getJobs();
      setJobs(data);
    } catch {
      showToast('Failed to load jobs', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async () => {
    if (!form.title || !form.department || !form.jd) return;
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('title', form.title);
      fd.append('department', form.department);
      fd.append('jd', form.jd);
      if (form.requirements) fd.append('requirements', form.requirements);
      await createJob(fd);
      showToast('Job added successfully');
      setShowModal(false);
      setForm({ title: '', department: '', jd: null, requirements: '' });
      load();
    } catch {
      showToast('Failed to add job', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    try {
      await deleteJob(confirmId);
      showToast('Job deleted');
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
        <h1 className="text-2xl font-semibold text-white">Jobs</h1>
        <button
          onClick={() => setShowModal(true)}
          className="px-4 py-2 bg-teal-600 hover:bg-teal-500 text-white text-sm rounded-xl transition-colors"
        >
          + Add Job
        </button>
      </div>

      {loading ? (
        <Spinner label="Loading jobs..." />
      ) : jobs.length === 0 ? (
        <EmptyState
          title="No jobs yet"
          description="Upload a job description to get started."
          action={{ label: 'Add Job', onClick: () => setShowModal(true) }}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {jobs.map((j) => (
            <div key={j.id} className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 flex flex-col gap-3">
              <div>
                <p className="text-white font-medium">{j.title}</p>
                <p className="text-zinc-400 text-sm">{j.department}</p>
                <p className="text-zinc-600 text-xs mt-1">{new Date(j.createdAt).toLocaleDateString()}</p>
              </div>
              <div className="flex gap-2 mt-auto">
                <button
                  onClick={() => navigate(`/jobs/${j.id}`)}
                  className="flex-1 py-1.5 text-sm rounded-lg border border-zinc-700 text-zinc-300 hover:border-teal-500 hover:text-teal-400 transition-colors"
                >
                  View
                </button>
                <button
                  onClick={() => setConfirmId(j.id)}
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
        <Modal title="Add Job" onClose={() => setShowModal(false)}>
          <div className="flex flex-col gap-4">
            <input
              type="text" placeholder="Job Title"
              value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
              className="bg-zinc-800 border border-zinc-700 rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-teal-500"
            />
            <input
              type="text" placeholder="Department"
              value={form.department}
              onChange={e => setForm(f => ({ ...f, department: e.target.value }))}
              className="bg-zinc-800 border border-zinc-700 rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-teal-500"
            />
            <input
              type="file" accept=".pdf"
              onChange={e => setForm(f => ({ ...f, jd: e.target.files[0] }))}
              className="text-sm text-zinc-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:bg-zinc-700 file:text-white file:text-sm"
            />
            <textarea
              placeholder="Requirements (optional)"
              value={form.requirements}
              onChange={e => setForm(f => ({ ...f, requirements: e.target.value }))}
              rows={3}
              className="bg-zinc-800 border border-zinc-700 rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-teal-500 resize-none"
            />
            <button
              onClick={handleSubmit}
              disabled={submitting || !form.title || !form.department || !form.jd}
              className="py-2.5 bg-teal-600 hover:bg-teal-500 disabled:opacity-40 text-white text-sm rounded-xl transition-colors"
            >
              {submitting ? 'Uploading...' : 'Add Job'}
            </button>
          </div>
        </Modal>
      )}

      {confirmId && (
        <ConfirmDialog
          message="Delete this job? This cannot be undone."
          onConfirm={handleDelete}
          onCancel={() => setConfirmId(null)}
        />
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </div>
  );
};

export default Jobs;