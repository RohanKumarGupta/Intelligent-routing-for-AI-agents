export function AgentCardSkeleton() {
  return (
    <div aria-hidden="true" className="animate-pulse rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
      <div className="flex items-start gap-4">
        <div className="h-12 w-12 shrink-0 rounded-xl bg-slate-800" />
        <div className="min-w-0 flex-1 space-y-2 py-1"><div className="h-4 w-3/4 rounded bg-slate-800" /><div className="h-3 w-1/2 rounded bg-slate-800/80" /></div>
        <div className="h-6 w-20 rounded-full bg-slate-800" />
      </div>
      <div className="mt-5 space-y-2"><div className="h-3 w-full rounded bg-slate-800/80" /><div className="h-3 w-4/5 rounded bg-slate-800/80" /></div>
      <div className="mt-5 flex gap-2"><div className="h-6 w-20 rounded-full bg-slate-800" /><div className="h-6 w-24 rounded-full bg-slate-800" /><div className="h-6 w-16 rounded-full bg-slate-800" /></div>
      <div className="mt-6 border-t border-slate-800 pt-4"><div className="h-3 w-2/3 rounded bg-slate-800/80" /></div>
    </div>
  );
}

export function AgentListSkeleton() {
  return <div role="status" aria-label="Loading agents" className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{Array.from({ length: 6 }, (_, index) => <AgentCardSkeleton key={index} />)}<span className="sr-only">Loading agents…</span></div>;
}
