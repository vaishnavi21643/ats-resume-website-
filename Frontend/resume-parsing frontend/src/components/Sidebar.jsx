import { NavLink } from 'react-router-dom';

const links = [
  {
    to: '/',
    label: 'Dashboard',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <rect x="3" y="3" width="7" height="7" rx="1" /><rect x="14" y="3" width="7" height="7" rx="1" />
        <rect x="3" y="14" width="7" height="7" rx="1" /><rect x="14" y="14" width="7" height="7" rx="1" />
      </svg>
    ),
  },
  {
    to: '/candidates',
    label: 'Candidates',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <circle cx="12" cy="8" r="4" /><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" strokeLinecap="round" />
      </svg>
    ),
  },
  {
    to: '/jobs',
    label: 'Jobs',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <rect x="2" y="7" width="20" height="14" rx="2" />
        <path d="M16 7V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v2" strokeLinecap="round" />
      </svg>
    ),
  },
  {
    to: '/chat',
    label: 'Chat',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    ),
  },
];

const Sidebar = () => (
  <aside className="hidden md:flex flex-col w-60 min-h-screen bg-zinc-900 border-r border-zinc-800 py-6 px-3">
    <div className="flex items-center gap-2 px-3 mb-8">
      <span className="text-teal-400 text-xl">✦</span>
      <span className="text-white font-semibold text-lg tracking-tight">RecruitAI</span>
    </div>

    <nav className="flex flex-col gap-1">
      {links.map(({ to, label, icon }) => (
        <NavLink
          key={to}
          to={to}
          end={to === '/'}
          className={({ isActive }) =>
            `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm transition-colors
            ${isActive
              ? 'text-teal-400 bg-teal-500/10 border-l-2 border-teal-500 pl-[10px]'
              : 'text-zinc-400 hover:text-white hover:bg-zinc-800'
            }`
          }
        >
          {icon}
          {label}
        </NavLink>
      ))}
    </nav>
  </aside>
);

export default Sidebar;