import { useState, useRef, useEffect } from 'react';
import { askChat, seedChat } from '../api/client';

const formatTime = (date) =>
  date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

const SUGGESTIONS = [
  'Which candidate has the most Python experience?',
  'Who is the best fit for the Java Developer role?',
  'Compare all candidates by skills',
];

const Avatar = ({ isUser }) => (
  <div className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 text-sm font-semibold
    ${isUser ? 'bg-teal-600 text-white' : 'bg-teal-900 text-teal-400'}`}>
    {isUser ? 'You' : '✦'}
  </div>
);

const CopyButton = ({ text }) => {
  const [copied, setCopied] = useState(false);
  const handle = () => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };
  return (
    <button onClick={handle} className={`text-xs mt-1 transition-colors ${copied ? 'text-green-400' : 'text-zinc-500 hover:text-teal-400'}`}>
      {copied ? '✓ Copied' : 'Copy'}
    </button>
  );
};

const Sources = ({ sources }) => {
  const [open, setOpen] = useState(false);
  if (!sources?.length) return null;
  return (
    <div className="mt-3">
      <button
        onClick={() => setOpen(o => !o)}
        className="text-xs text-zinc-500 hover:text-zinc-300 flex items-center gap-1 transition-colors"
      >
        <span>{open ? '▲' : '▼'}</span>
        View sources ({sources.length})
      </button>
      {open && (
        <div className="mt-2 flex flex-col gap-2">
          {sources.map((s, i) => (
            <div key={i} className="bg-zinc-800 rounded-lg p-3 max-h-32 overflow-y-auto font-mono text-xs text-zinc-400">
              {s}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const Message = ({ msg }) => {
  const isUser = msg.role === 'user';
  return (
    <div className={`w-full border-b border-zinc-800 px-6 py-5 ${isUser ? 'bg-zinc-900' : 'bg-black'}`}>
      <div className="max-w-3xl mx-auto flex gap-4">
        <Avatar isUser={isUser} />
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <span className={`text-sm font-semibold ${isUser ? 'text-white' : 'text-teal-400'}`}>
              {isUser ? 'You' : 'RecruitAI'}
            </span>
            <span className="text-zinc-500 text-xs">{formatTime(msg.time)}</span>
          </div>
          {msg.thinking ? (
            <div className="flex items-center gap-2 text-zinc-400 text-sm">
              <span className="animate-pulse">●</span>
              <span className="animate-pulse delay-75">●</span>
              <span className="animate-pulse delay-150">●</span>
              <span className="ml-1">Thinking...</span>
            </div>
          ) : (
            <>
              <p className="text-zinc-100 text-sm leading-relaxed whitespace-pre-wrap">{msg.content}</p>
              {!isUser && <Sources sources={msg.sources} />}
            </>
          )}
        </div>
        {!msg.thinking && <CopyButton text={msg.content} />}
      </div>
    </div>
  );
};

const Chat = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [seeded, setSeeded] = useState(false);
  const bottomRef = useRef(null);
  const textareaRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = async (text) => {
    const question = (text ?? input).trim();
    if (!question || loading) return;

    setInput('');
    const userMsg = { id: Date.now(), role: 'user', content: question, time: new Date() };
    const thinkingMsg = { id: Date.now() + 1, role: 'assistant', thinking: true, content: '', time: new Date() };
    setMessages(prev => [...prev, userMsg, thinkingMsg]);
    setLoading(true);

    try {
      const data = await askChat(question);
      setSeeded(true);
      setMessages(prev =>
        prev.map(m =>
          m.id === thinkingMsg.id
            ? { ...m, thinking: false, content: data.answer, sources: data.sources }
            : m
        )
      );
    } catch {
      setMessages(prev =>
        prev.map(m =>
          m.id === thinkingMsg.id
            ? { ...m, thinking: false, content: 'Something went wrong. Please try again.' }
            : m
        )
      );
    } finally {
      setLoading(false);
    }
  };

  const handleReseed = async () => {
    try {
      await seedChat();
      setSeeded(true);
    } catch {
      // silent
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const recentHistory = messages.filter(m => m.role === 'user').slice(-5);

  return (
    <div className="flex h-screen bg-black overflow-hidden">
      {/* Left Panel */}
      <aside className="w-64 bg-zinc-900 border-r border-zinc-800 flex flex-col py-5 px-3 shrink-0">
        <div className="flex items-center gap-2 px-3 mb-6">
          <span className="text-teal-400 text-xl">✦</span>
          <span className="text-white font-semibold text-lg">RecruitAI</span>
        </div>

        <button
          onClick={() => setMessages([])}
          className="mb-5 mx-1 py-2 text-sm rounded-xl border border-teal-600 text-teal-400 hover:bg-teal-600/10 transition-colors"
        >
          + New Chat
        </button>

        <p className="text-zinc-500 text-xs uppercase tracking-widest px-3 mb-2">Recent</p>
        <div className="flex flex-col gap-1 flex-1 overflow-y-auto">
          {recentHistory.length === 0 ? (
            <p className="text-zinc-600 text-xs px-3">No history yet</p>
          ) : (
            recentHistory.map(m => (
              <div key={m.id} className="px-3 py-2 rounded-lg text-zinc-400 text-xs truncate hover:bg-zinc-800 cursor-default">
                {m.content}
              </div>
            ))
          )}
        </div>

        <div className="mt-4 flex flex-col gap-3 px-1">
          <button
            onClick={handleReseed}
            className="py-2 text-xs rounded-xl border border-zinc-700 text-zinc-400 hover:text-white hover:border-zinc-500 transition-colors"
          >
            ↻ Reseed Context
          </button>
          <div className="flex items-center gap-2 px-2">
            <span className={`w-2 h-2 rounded-full ${seeded ? 'bg-green-500' : 'bg-red-500'}`} />
            <span className="text-xs text-zinc-500">{seeded ? 'Context loaded' : 'Not seeded'}</span>
          </div>
        </div>
      </aside>

      {/* Center Panel */}
      <div className="flex flex-col flex-1 min-w-0">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-zinc-800 shrink-0">
          <span className="text-white font-medium text-sm">AI Recruiter Chat</span>
          <button
            onClick={handleReseed}
            title="Refresh context"
            className="text-zinc-400 hover:text-teal-400 transition-colors text-lg"
          >
            ↻
          </button>
        </div>

        {/* Messages */}
        <div className="flex-1 overflow-y-auto">
          {messages.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-center px-6">
              <div className="text-5xl mb-4">✦</div>
              <h2 className="text-white text-2xl font-medium mb-2">Ask me anything</h2>
              <p className="text-zinc-400 text-sm mb-8">
                I have access to all your candidate resumes and job descriptions.
              </p>
              <div className="flex flex-wrap justify-center gap-3">
                {SUGGESTIONS.map(s => (
                  <button
                    key={s}
                    onClick={() => handleSend(s)}
                    className="px-4 py-2 rounded-full bg-zinc-800 border border-zinc-700 hover:border-teal-500 text-zinc-300 text-sm transition-colors"
                  >
                    {s}
                  </button>
                ))}
              </div>
            </div>
          ) : (
            <>
              {messages.map(msg => <Message key={msg.id} msg={msg} />)}
              <div ref={bottomRef} />
            </>
          )}
        </div>

        {/* Input */}
        <div className="border-t border-zinc-800 px-6 py-4 bg-zinc-900 shrink-0">
          <div className="flex gap-3 items-end max-w-3xl mx-auto">
            <textarea
              ref={textareaRef}
              rows={1}
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Ask about your candidates and jobs..."
              className="flex-1 bg-zinc-800 border border-zinc-700 focus:border-teal-500 focus:outline-none rounded-2xl px-4 py-3 text-white text-sm resize-none leading-relaxed"
              style={{ maxHeight: '120px', overflowY: 'auto' }}
            />
            <button
              onClick={() => handleSend()}
              disabled={!input.trim() || loading}
              className="px-4 py-3 bg-teal-600 hover:bg-teal-500 disabled:opacity-40 text-white rounded-xl transition-colors shrink-0"
            >
              ↑
            </button>
          </div>
          <p className="text-zinc-600 text-xs text-center mt-2 max-w-3xl mx-auto">
            RecruitAI can make mistakes. Verify important decisions.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Chat;