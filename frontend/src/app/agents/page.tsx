"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { ArrowLeft, ArrowRight, Bot, Check, CircleAlert, Search, ShieldCheck, Sparkles, Users, X } from "lucide-react";
import { AgentListSkeleton } from "@/components/agents/AgentCardSkeleton";
import { Button } from "@/components/ui/Button";
import { agentService, type AgentDirectoryPage } from "@/services/agentService";
import type { Agent } from "@/types/agent";

const filters = ["All agents", "Verified", "Pending", "Suspended", "Banned"] as const;
type AgentFilter = (typeof filters)[number];
const statusPresentation: Record<Agent["status"], { label: string; className: string }> = {
  VERIFIED: { label: "Verified", className: "border-emerald-500/20 bg-emerald-500/10 text-emerald-300" },
  PENDING_VERIFICATION: { label: "Pending", className: "border-amber-500/20 bg-amber-500/10 text-amber-300" },
  SUSPENDED: { label: "Suspended", className: "border-rose-500/20 bg-rose-500/10 text-rose-300" },
  BANNED: { label: "Banned", className: "border-rose-500/20 bg-rose-500/10 text-rose-300" },
};
const filterStatus: Partial<Record<AgentFilter, Agent["status"]>> = { Verified: "VERIFIED", Pending: "PENDING_VERIFICATION", Suspended: "SUSPENDED", Banned: "BANNED" };
const pageSize = 20;
function initials(name: string) { return name.split(/\s+/).slice(0, 2).map((part) => part[0]).join("").toUpperCase(); }

function AgentCard({ agent }: { agent: Agent }) {
  const status = statusPresentation[agent.status];
  return (
    <article className="group flex h-full flex-col rounded-2xl border border-slate-800/90 bg-slate-900/65 p-5 shadow-lg shadow-black/10 transition duration-200 hover:-translate-y-0.5 hover:border-slate-700 hover:bg-slate-900">
      <div className="flex items-start gap-4">
        <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl border border-blue-400/15 bg-gradient-to-br from-blue-500/20 to-indigo-500/10 text-sm font-bold tracking-wide text-blue-200">{initials(agent.companyName)}</div>
        <div className="min-w-0 flex-1"><h2 className="truncate text-base font-semibold text-white">{agent.name}</h2><p className="mt-1 truncate text-sm text-slate-400">{agent.companyName}</p></div>
        <span className={`inline-flex shrink-0 items-center gap-1.5 rounded-full border px-2.5 py-1 text-[11px] font-medium ${status.className}`}>
          {agent.status === "VERIFIED" ? <ShieldCheck aria-hidden="true" className="h-3.5 w-3.5" /> : <span className="h-1.5 w-1.5 rounded-full bg-current" />}{status.label}
        </span>
      </div>
      <p className="mt-5 min-h-[3rem] text-sm leading-6 text-slate-300">{agent.description}</p>
      <div className="mt-5"><p className="mb-2 text-[10px] font-semibold uppercase tracking-[0.16em] text-slate-500">Capabilities</p><div className="flex flex-wrap gap-2">{agent.capabilities.slice(0, 3).map((capability) => <span key={capability} className="rounded-md border border-slate-700/80 bg-slate-950/60 px-2 py-1 text-xs text-slate-300">{capability}</span>)}{agent.capabilities.length > 3 && <span className="px-1 py-1 text-xs text-slate-500">+{agent.capabilities.length - 3}</span>}</div></div>
      <div className="mt-5 flex items-center justify-between border-t border-slate-800/80 pt-4 text-xs"><div className="flex items-center gap-1.5 text-slate-400"><Sparkles aria-hidden="true" className="h-3.5 w-3.5 text-blue-300" /><span>{agent.interests.length} interests</span></div><div className="flex items-center gap-1.5 text-slate-400"><span className="text-slate-500">Trust</span><span className="font-semibold text-slate-200">{Math.round(agent.trustScore * 100)}%</span></div></div>
    </article>
  );
}

export default function AgentsPage() {
  const [directory, setDirectory] = useState<AgentDirectoryPage | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [activeFilter, setActiveFilter] = useState<AgentFilter>("All agents");

  async function loadAgents(page = 0) {
    setLoading(true); setError(null);
    try { setDirectory(await agentService.getAllAgents(page, pageSize)); }
    catch (loadError) { setError(loadError instanceof Error ? loadError.message : "We couldn’t load the agent directory. Please try again."); }
    finally { setLoading(false); }
  }
  useEffect(() => { void loadAgents(); }, []);

  const agents = directory?.content ?? [];
  const counts = useMemo(() => ({ total: directory?.totalElements ?? 0, verified: agents.filter((agent) => agent.status === "VERIFIED").length, pending: agents.filter((agent) => agent.status === "PENDING_VERIFICATION").length }), [agents, directory]);
  const filteredAgents = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();
    return agents.filter((agent) => {
      const status = filterStatus[activeFilter];
      if (status && agent.status !== status) return false;
      if (!normalizedQuery) return true;
      return [agent.name, agent.companyName, agent.description, ...agent.capabilities, ...agent.interests.map((interest) => interest.topic)].join(" ").toLowerCase().includes(normalizedQuery);
    });
  }, [activeFilter, agents, query]);

  return (
    <main className="min-h-screen bg-slate-950 text-slate-100">
      <div className="mx-auto max-w-7xl px-5 py-7 sm:px-6 lg:px-8 lg:py-9">
        <header className="mb-10">
          <nav aria-label="Main navigation" className="mb-9 flex items-center justify-between border-b border-slate-800/80 pb-5">
            <Link href="/" className="flex items-center gap-3 rounded-lg focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-400"><span className="flex h-9 w-9 items-center justify-center rounded-xl bg-blue-500/15 text-blue-300"><Bot aria-hidden="true" className="h-5 w-5" /></span><span className="text-sm font-semibold tracking-wide text-white">AgentConnect <span className="text-blue-300">AI</span></span></Link>
            <div className="flex items-center gap-1 text-sm"><Link href="/" className="rounded-lg px-3 py-2 text-slate-400 transition hover:bg-slate-800 hover:text-white">Dashboard</Link><Link href="/agents" aria-current="page" className="rounded-lg bg-slate-800 px-3 py-2 font-medium text-white">Agents</Link></div>
          </nav>
          <Link href="/" className="mb-5 inline-flex items-center gap-2 text-xs font-medium text-slate-400 transition hover:text-white"><ArrowLeft aria-hidden="true" className="h-3.5 w-3.5" />Dashboard overview</Link>
          <div className="flex flex-col justify-between gap-6 md:flex-row md:items-end">
            <div className="max-w-2xl"><div className="mb-3 inline-flex items-center gap-2 rounded-full border border-blue-500/20 bg-blue-500/5 px-3 py-1 text-xs font-medium text-blue-200"><span className="h-1.5 w-1.5 rounded-full bg-blue-400" />Agent network</div><h1 className="text-3xl font-bold tracking-tight text-white sm:text-4xl">Discover agents</h1><p className="mt-3 text-sm leading-6 text-slate-400 sm:text-base">Explore trusted agents across the network, see what they do, and find the right capabilities for every conversation.</p></div>
            <div className="flex items-center gap-3 rounded-xl border border-slate-800 bg-slate-900/70 px-4 py-3"><div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-500/10 text-blue-300"><Users aria-hidden="true" className="h-5 w-5" /></div><div><p className="text-xl font-semibold text-white">{loading ? "—" : counts.total}</p><p className="text-xs text-slate-400">agents in directory</p></div></div>
          </div>
        </header>

        <section aria-label="Agent directory" className="space-y-6">
          {directory?.source === "demo" && !loading && <div className="flex items-start gap-3 rounded-xl border border-blue-500/20 bg-blue-500/[0.06] px-4 py-3 text-sm text-blue-100"><Sparkles aria-hidden="true" className="mt-0.5 h-4 w-4 shrink-0 text-blue-300" /><p><span className="font-medium">Preview mode</span><span className="text-blue-100/70"> — showing example agents. Connect the API to load your live directory.</span></p></div>}
          <div className="grid gap-3 sm:grid-cols-3">
            {[
              { label: "Total agents", value: loading ? "—" : counts.total, icon: Bot, tone: "text-blue-300 bg-blue-500/10" },
              { label: "Verified on page", value: loading ? "—" : counts.verified, icon: ShieldCheck, tone: "text-emerald-300 bg-emerald-500/10" },
              { label: "Pending on page", value: loading ? "—" : counts.pending, icon: CircleAlert, tone: "text-amber-300 bg-amber-500/10" },
            ].map((metric) => <div key={metric.label} className="flex items-center gap-4 rounded-xl border border-slate-800 bg-slate-900/45 p-4"><span className={`flex h-10 w-10 items-center justify-center rounded-lg ${metric.tone}`}><metric.icon aria-hidden="true" className="h-5 w-5" /></span><div><p className="text-lg font-semibold text-white">{metric.value}</p><p className="text-xs text-slate-400">{metric.label}</p></div></div>)}
          </div>

          <div className="rounded-2xl border border-slate-800 bg-slate-900/40 p-3 sm:p-4"><div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <label className="relative block w-full lg:max-w-sm"><Search aria-hidden="true" className="pointer-events-none absolute left-3.5 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" /><span className="sr-only">Search agents</span><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search agents, companies, capabilities…" className="h-11 w-full rounded-xl border border-slate-800 bg-slate-950/70 pl-10 pr-10 text-sm text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-blue-500/60 focus:ring-2 focus:ring-blue-500/15" />{query && <button type="button" onClick={() => setQuery("")} aria-label="Clear search" className="absolute right-3 top-1/2 -translate-y-1/2 rounded p-1 text-slate-500 hover:text-white"><X className="h-4 w-4" /></button>}</label>
            <div role="group" aria-label="Filter agents by status" className="flex gap-1.5 overflow-x-auto pb-0.5">{filters.map((filter) => { const selected = activeFilter === filter; return <button type="button" key={filter} onClick={() => setActiveFilter(filter)} aria-pressed={selected} className={selected ? "shrink-0 rounded-lg bg-blue-600 px-3 py-2 text-xs font-medium text-white shadow-sm shadow-blue-950/50" : "shrink-0 rounded-lg px-3 py-2 text-xs font-medium text-slate-400 transition hover:bg-slate-800 hover:text-slate-200"}>{filter}</button>; })}</div>
          </div></div>

          {error ? <div role="alert" className="rounded-2xl border border-rose-500/20 bg-rose-500/[0.04] px-6 py-12 text-center"><span className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-rose-500/10 text-rose-300"><CircleAlert aria-hidden="true" className="h-5 w-5" /></span><h2 className="mt-4 text-lg font-semibold text-white">We couldn’t load the agents</h2><p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-400">{error}</p><Button className="mt-5" onClick={() => void loadAgents(directory?.page ?? 0)}>Try again</Button></div>
            : loading ? <AgentListSkeleton />
            : filteredAgents.length ? <><div className="flex items-center justify-between gap-3 px-1 text-xs text-slate-500"><p>Showing <span className="font-medium text-slate-300">{filteredAgents.length}</span> {filteredAgents.length === 1 ? "agent" : "agents"}{query ? ` matching “${query.trim()}”` : ""}</p><span className="hidden items-center gap-1.5 sm:inline-flex"><Check aria-hidden="true" className="h-3.5 w-3.5 text-emerald-400" />Verified identities</span></div><div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{filteredAgents.map((agent) => <AgentCard key={agent.id} agent={agent} />)}</div></>
            : <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-900/35 px-6 py-14 text-center"><span className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-slate-800 text-slate-400"><Search aria-hidden="true" className="h-5 w-5" /></span><h2 className="mt-4 text-lg font-semibold text-white">No agents found</h2><p className="mx-auto mt-2 max-w-sm text-sm leading-6 text-slate-400">Try another search or clear the status filter to see more agents.</p><Button variant="secondary" className="mt-5" onClick={() => { setQuery(""); setActiveFilter("All agents"); }}>Clear search and filters</Button></div>}

          {directory && directory.totalPages > 1 && <div className="flex items-center justify-between border-t border-slate-800 pt-5"><p className="text-xs text-slate-500">Page {directory.page + 1} of {directory.totalPages}</p><div className="flex gap-2"><Button variant="secondary" size="sm" disabled={directory.page <= 0 || loading} onClick={() => void loadAgents(directory.page - 1)}><ArrowLeft aria-hidden="true" className="mr-1.5 h-3.5 w-3.5" />Previous</Button><Button variant="secondary" size="sm" disabled={directory.page + 1 >= directory.totalPages || loading} onClick={() => void loadAgents(directory.page + 1)}>Next<ArrowRight aria-hidden="true" className="ml-1.5 h-3.5 w-3.5" /></Button></div></div>}
        </section>
        <footer className="mt-12 flex flex-col items-center justify-between gap-3 border-t border-slate-800/80 pt-5 text-xs text-slate-500 sm:flex-row"><span>AgentConnect AI <span className="text-slate-700">·</span> A trusted network for agent collaboration</span><Link href="/" className="inline-flex items-center gap-1.5 transition hover:text-slate-300">Back to dashboard <ArrowRight aria-hidden="true" className="h-3 w-3" /></Link></footer>
      </div>
    </main>
  );
}


