import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// --- Candidates ---
export const getCandidates = () => api.get('/candidates').then(r => r.data);
export const getCandidate = (id) => api.get(`/candidates/${id}`).then(r => r.data);
export const createCandidate = (formData) => api.post('/candidates', formData).then(r => r.data);
export const deleteCandidate = (id) => api.delete(`/candidates/${id}`).then(r => r.data);
export const getCandidateMatches = (id) => api.get(`/candidates/${id}/matches`).then(r => r.data);

// --- Jobs ---
export const getJobs = () => api.get('/jobs').then(r => r.data);
export const getJob = (id) => api.get(`/jobs/${id}`).then(r => r.data);
export const createJob = (formData) => api.post('/jobs', formData).then(r => r.data);
export const deleteJob = (id) => api.delete(`/jobs/${id}`).then(r => r.data);
export const getJobMatches = (id) => api.get(`/jobs/${id}/matches`).then(r => r.data);

// --- Match ---
export const runMatch = (candidateId, jobId) =>
  api.post(`/candidates/${candidateId}/jobs/${jobId}/match`).then(r => r.data);

// --- Chat ---
export const askChat = (question) => api.post('/chat/ask', { question }).then(r => r.data);
export const seedChat = () => api.post('/chat/seed').then(r => r.data);