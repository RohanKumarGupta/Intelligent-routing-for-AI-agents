import { Card, CardContent, CardHeader } from "@/components/ui/Card";

function AgentCardSkeleton() {
  return (
    <div className="animate-pulse rounded-xl border border-slate-800 bg-slate-900/60 p-4">
      <div className="mb-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 rounded-full bg-slate-700" />
          <div>
            <div className="h-4 w-28 rounded bg-slate-700" />
            <div className="mt-2 h-3 w-20 rounded bg-slate-800" />
          </div>
        </div>
        <div className="h-6 w-20 rounded-full bg-slate-700" />
      </div>
      <div className="space-y-2">
        <div className="h-3 w-full rounded bg-slate-800" />
        <div className="h-3 w-5/6 rounded bg-slate-800" />
      </div>
      <div className="mt-4 flex gap-2">
        <div className="h-6 w-16 rounded-full bg-slate-800" />
        <div className="h-6 w-20 rounded-full bg-slate-800" />
        <div className="h-6 w-14 rounded-full bg-slate-800" />
      </div>
    </div>
  );
}

export default function AgentsLoading() {
  return (
    <main className="min-h-screen bg-slate-950 p-6 text-slate-100">
      <div className="mx-auto max-w-7xl">
        <div className="mb-6 flex items-center justify-between">
          <div>
            <div className="h-4 w-24 rounded bg-slate-800" />
            <div className="mt-3 h-9 w-56 rounded bg-slate-700" />
          </div>
          <div className="h-11 w-32 rounded-lg bg-slate-700" />
        </div>

        <Card className="border-slate-800 bg-slate-900/70">
          <CardHeader>
            <div className="h-5 w-48 rounded bg-slate-700" />
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            <div className="grid gap-4 md:grid-cols-3">
              <div className="h-12 rounded-lg bg-slate-800" />
              <div className="h-12 rounded-lg bg-slate-800" />
              <div className="h-12 rounded-lg bg-slate-800" />
            </div>
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              <AgentCardSkeleton />
              <AgentCardSkeleton />
              <AgentCardSkeleton />
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  );
}
