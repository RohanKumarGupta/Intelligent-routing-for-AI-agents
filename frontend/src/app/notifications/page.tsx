"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { Bell, CheckCircle2, Clock3, Filter, Inbox, ShieldAlert, Sparkles } from "lucide-react";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/Card";
import { notificationService } from "@/services/notificationService";
import type { Notification } from "@/types/notification";

const statusStyles: Record<Notification["status"], "default" | "secondary" | "success" | "warning" | "destructive" | "outline"> = {
  PENDING: "warning",
  PROCESSING: "secondary",
  DELIVERED: "success",
  FAILED: "destructive",
  EXPIRED: "outline",
  IGNORED: "secondary",
};

const filters = ["ALL", "PENDING", "PROCESSING", "DELIVERED", "FAILED"] as const;
type NotificationFilter = (typeof filters)[number];

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<NotificationFilter>("ALL");

  async function loadNotifications() {
    setLoading(true);
    setError(null);
    try {
      const page = await notificationService.getNotificationsForAgent("agent-demo-user");
      setNotifications(page.content ?? []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to load notifications.");
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadNotifications();
  }, []);

  const filteredNotifications = useMemo(() => {
    if (filter === "ALL") {
      return notifications;
    }
    return notifications.filter((item) => item.status === filter);
  }, [filter, notifications]);

  const summary = useMemo(() => {
    const delivered = notifications.filter((item) => item.status === "DELIVERED").length;
    const pending = notifications.filter((item) => item.status === "PENDING").length;
    const failed = notifications.filter((item) => item.status === "FAILED").length;
    return { delivered, pending, failed };
  }, [notifications]);

  return (
    <main className="min-h-screen bg-slate-950 p-6 text-slate-100">
      <div className="mx-auto max-w-6xl">
        <header className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-violet-300">Routing center</p>
            <h1 className="mt-2 text-3xl font-bold text-white md:text-4xl">Notifications</h1>
          </div>
          <div className="flex items-center gap-3">
            <Link href="/" className="rounded-lg border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm font-medium text-slate-100 hover:bg-slate-700">
              Dashboard
            </Link>
            <Button variant="default" size="lg" onClick={() => void loadNotifications()}>
              Refresh
            </Button>
          </div>
        </header>

        <div className="mb-6 grid gap-4 md:grid-cols-3">
          <Card className="border-slate-800 bg-slate-900/70">
            <CardContent className="flex items-center justify-between gap-4 pt-5">
              <div>
                <p className="text-sm text-slate-400">Delivered</p>
                <p className="mt-2 text-3xl font-bold text-white">{summary.delivered}</p>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-300">
                <CheckCircle2 className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>

          <Card className="border-slate-800 bg-slate-900/70">
            <CardContent className="flex items-center justify-between gap-4 pt-5">
              <div>
                <p className="text-sm text-slate-400">Pending</p>
                <p className="mt-2 text-3xl font-bold text-white">{summary.pending}</p>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-amber-500/10 text-amber-300">
                <Clock3 className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>

          <Card className="border-slate-800 bg-slate-900/70">
            <CardContent className="flex items-center justify-between gap-4 pt-5">
              <div>
                <p className="text-sm text-slate-400">Failures</p>
                <p className="mt-2 text-3xl font-bold text-white">{summary.failed}</p>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-rose-500/10 text-rose-300">
                <ShieldAlert className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>
        </div>

        <Card className="border-slate-800 bg-slate-900/70">
          <CardHeader className="border-b border-slate-800 pb-5">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <div>
                <CardTitle className="text-2xl">Inbox</CardTitle>
                <CardDescription>Relevant agent updates and routing decisions for your network.</CardDescription>
              </div>
              <div className="flex flex-wrap gap-2">
                {filters.map((option) => (
                  <button
                    key={option}
                    type="button"
                    onClick={() => setFilter(option)}
                    className={`rounded-full border px-3 py-1.5 text-xs font-medium transition-colors ${
                      filter === option ? "border-violet-500 bg-violet-500/10 text-violet-300" : "border-slate-700 bg-slate-800 text-slate-300 hover:bg-slate-700"
                    }`}
                  >
                    {option}
                  </button>
                ))}
              </div>
            </div>
          </CardHeader>

          <CardContent className="space-y-4 pt-5">
            {error ? (
              <div className="rounded-xl border border-rose-500/25 bg-rose-500/5 p-4 text-sm text-rose-200">{error}</div>
            ) : null}

            {loading ? (
              <div className="space-y-4">
                {[0, 1, 2].map((item) => (
                  <div key={item} className="animate-pulse rounded-xl border border-slate-800 bg-slate-950/60 p-4">
                    <div className="mb-4 flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="h-10 w-10 rounded-full bg-slate-700" />
                        <div>
                          <div className="h-4 w-32 rounded bg-slate-700" />
                          <div className="mt-2 h-3 w-24 rounded bg-slate-800" />
                        </div>
                      </div>
                      <div className="h-6 w-20 rounded-full bg-slate-700" />
                    </div>
                    <div className="space-y-2">
                      <div className="h-4 w-full rounded bg-slate-800" />
                      <div className="h-4 w-5/6 rounded bg-slate-800" />
                    </div>
                  </div>
                ))}
              </div>
            ) : filteredNotifications.length === 0 ? (
              <div className="rounded-xl border border-dashed border-slate-700 bg-slate-950/30 p-8 text-center text-slate-300">
                <Inbox className="mx-auto mb-3 h-8 w-8 text-slate-500" />
                No notifications match the selected filter.
              </div>
            ) : (
              <div className="space-y-4">
                {filteredNotifications.map((notification) => (
                  <article key={notification.id} className="rounded-xl border border-slate-800 bg-slate-950/60 p-4">
                    <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                      <div className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-violet-500/30 to-indigo-500/30 text-sm font-semibold text-violet-200">
                          {notification.sourceAgentName.slice(0, 2).toUpperCase()}
                        </div>
                        <div>
                          <p className="font-medium text-white">{notification.sourceAgentName}</p>
                          <p className="text-xs text-slate-400">{notification.sourceCompanyName}</p>
                        </div>
                      </div>

                      <div className="flex items-center gap-2">
                        <Badge variant={statusStyles[notification.status]}>{notification.status}</Badge>
                        <Badge variant="secondary">{Math.round(notification.relevanceScore * 100)}% match</Badge>
                      </div>
                    </div>

                    <div className="flex items-center gap-2 text-xs text-slate-400">
                      <Sparkles className="h-3.5 w-3.5 text-violet-300" />
                      <span>{notification.postType}</span>
                      <span>•</span>
                      <span>{new Date(notification.createdAt).toLocaleString()}</span>
                    </div>

                    <p className="mt-4 text-base leading-7 text-slate-100">{notification.postContent}</p>

                    <div className="mt-4 flex flex-wrap gap-2">
                      {notification.reasons.map((reason) => (
                        <Badge key={reason} variant="outline">
                          {reason}
                        </Badge>
                      ))}
                    </div>

                    <div className="mt-5 flex flex-wrap items-center justify-between gap-3 border-t border-slate-800 pt-4 text-sm text-slate-300">
                      <div className="flex items-center gap-2">
                        <Filter className="h-4 w-4 text-slate-500" />
                        <span>Attempts: {notification.attemptCount}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Button variant="secondary" size="sm">
                          View details
                        </Button>
                        {notification.status === "PENDING" ? (
                          <Button variant="default" size="sm">
                            Approve
                          </Button>
                        ) : null}
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </main>
  );
}
