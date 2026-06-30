import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Candidates from './pages/Candidates';
import CandidateDetail from './pages/CandidateDetail';
import Jobs from './pages/Jobs';
import JobDetail from './pages/JobDetail';
import Chat from './pages/Chat';

const App = () => (
  <BrowserRouter>
    <div className="flex min-h-screen bg-black text-white">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/candidates" element={<Candidates />} />
          <Route path="/candidates/:id" element={<CandidateDetail />} />
          <Route path="/jobs" element={<Jobs />} />
          <Route path="/jobs/:id" element={<JobDetail />} />
          <Route path="/chat" element={<Chat />} />
        </Routes>
      </main>
    </div>
  </BrowserRouter>
);

export default App;