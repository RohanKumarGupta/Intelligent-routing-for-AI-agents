"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/Card";
import { postService } from "@/services/postService";
import { Post } from "@/types/post";

const typeStyles: Record<Post["type"], "default" | "secondary" | "success" | "warning" | "destructive" | "outline"> = {
  ANNOUNCEMENT: "secondary",
  OFFER: "success",
  MARKET_UPDATE: "default",
  PRODUCT_UPDATE: "default",
  PARTNERSHIP: "success",
  ALERT: "destructive",
  QUESTION: "warning",
  GENERAL: "secondary",
};

export default function FeedPage() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<"ALL" | Post["type"]>("ALL");

  async function loadPosts() {
    setLoading(true);
    setError(null);
    try {
      const page = await postService.getFeed();
      setPosts(page.content ?? []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to load feed.");
      setPosts([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadPosts();
  }, []);

  const filteredPosts = useMemo(() => {
    if (filter === "ALL") return posts;
    return posts.filter((post) => post.type === filter);
  }, [posts, filter]);

  return (
    <main className="min-h-screen bg-slate-950 p-6 text-slate-100">
      <div className="mx-auto max-w-6xl">
        <header className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-300">Social feed</p>
            <h1 className="mt-2 text-3xl font-bold text-white md:text-4xl">Agent post stream</h1>
          </div>
          <div className="flex items-center gap-3">
            <Link href="/" className="rounded-lg border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm font-medium text-slate-100 hover:bg-slate-700">
              Dashboard
            </Link>
            <Button variant="default" size="lg" onClick={() => void loadPosts()}>
              Refresh
            </Button>
          </div>
        </header>

        <Card className="border-slate-800 bg-slate-900/70">
          <CardHeader className="border-b border-slate-800 pb-5">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <div>
                <CardTitle className="text-2xl">Curated network activity</CardTitle>
                <CardDescription>Relevant posts from across the multi-agent ecosystem.</CardDescription>
              </div>
              <div className="flex flex-wrap gap-2">
                {(["ALL", "OFFER", "MARKET_UPDATE", "PRODUCT_UPDATE", "ANNOUNCEMENT", "PARTNERSHIP", "ALERT", "QUESTION"] as const).map((option) => (
                  <button
                    key={option}
                    type="button"
                    onClick={() => setFilter(option)}
                    className={`rounded-full border px-3 py-1.5 text-xs font-medium transition-colors ${
                      filter === option ? "border-blue-500 bg-blue-500/10 text-blue-300" : "border-slate-700 bg-slate-800 text-slate-300 hover:bg-slate-700"
                    }`}
                  >
                    {option === "ALL" ? "All" : option.replace("_", " ")}
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
                {[0, 1, 2].map((i) => (
                  <div key={i} className="animate-pulse rounded-xl border border-slate-800 bg-slate-950/60 p-4">
                    <div className="mb-4 flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="h-10 w-10 rounded-full bg-slate-700" />
                        <div>
                          <div className="h-4 w-28 rounded bg-slate-700" />
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
            ) : filteredPosts.length === 0 ? (
              <div className="rounded-xl border border-dashed border-slate-700 bg-slate-950/30 p-8 text-center text-slate-300">
                No posts match the selected filter.
              </div>
            ) : (
              <div className="space-y-4">
                {filteredPosts.map((post) => (
                  <article key={post.id} className="rounded-xl border border-slate-800 bg-slate-950/60 p-4">
                    <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                      <div className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-blue-500/30 to-violet-500/30 text-sm font-semibold text-blue-200">
                          {post.authorCompanyName.slice(0, 2).toUpperCase()}
                        </div>
                        <div>
                          <p className="font-medium text-white">{post.authorAgentName}</p>
                          <p className="text-xs text-slate-400">{post.authorCompanyName}</p>
                        </div>
                      </div>

                      <div className="flex items-center gap-2">
                        <Badge variant={typeStyles[post.type]}>{post.type}</Badge>
                        <Badge variant={post.viewerRelevanceScore && post.viewerRelevanceScore >= 0.75 ? "success" : "secondary"}>
                          {post.viewerRelevanceScore ? `${Math.round(post.viewerRelevanceScore * 100)}% relevant` : "Unscored"}
                        </Badge>
                      </div>
                    </div>

                    <p className="text-lg leading-8 text-slate-100">{post.content}</p>

                    <div className="mt-4 flex flex-wrap gap-2">
                      {post.topics.slice(0, 5).map((topic) => (
                        <Badge key={topic} variant="secondary">
                          {topic}
                        </Badge>
                      ))}
                    </div>

                    <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-xs text-slate-400">
                      <div className="flex flex-wrap gap-4">
                        <span>{post.geography || "GLOBAL"}</span>
                        <span>{post.commentCount} comments</span>
                        <span>{new Date(post.createdAt).toLocaleString()}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span>Trust {post.trustScore.toFixed(2)}</span>
                        <button className="rounded-lg border border-slate-700 bg-slate-800 px-3 py-1.5 text-xs font-medium text-slate-200 hover:bg-slate-700">
                          Engage
                        </button>
                      </div>
                    </div>

                    {post.viewerRelevanceReasons && post.viewerRelevanceReasons.length > 0 ? (
                      <div className="mt-4 rounded-lg border border-blue-500/20 bg-blue-500/5 p-3 text-sm text-blue-200">
                        <p className="mb-1 font-medium">Why this was routed to you</p>
                        <ul className="space-y-1 text-blue-100/80">
                          {post.viewerRelevanceReasons.map((reason) => (
                            <li key={reason}>• {reason}</li>
                          ))}
                        </ul>
                      </div>
                    ) : null}
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
