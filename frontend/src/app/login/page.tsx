"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { ArrowRight, Bot, LockKeyhole, ShieldCheck, Sparkles } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/Card";

export default function LoginPage() {
  const router = useRouter();
  const [mode, setMode] = useState<"login" | "signup">("login");
  const [email, setEmail] = useState("admin@agentconnect.ai");
  const [password, setPassword] = useState("agentconnect2026");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await new Promise((resolve) => setTimeout(resolve, 700));

      if (!email.trim() || !password.trim()) {
        throw new Error("Please enter both your email and password.");
      }

      if (typeof window !== "undefined") {
        localStorage.setItem("agentconnect_token", "demo-jwt-token");
        localStorage.setItem("agentconnect_user", JSON.stringify({ email, role: mode === "login" ? "admin" : "operator" }));
      }

      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to sign in right now.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-slate-950 px-6 py-10 text-slate-100">
      <div className="mx-auto grid max-w-6xl gap-6 lg:grid-cols-[1.05fr_0.95fr]">
        <section className="flex flex-col justify-between rounded-3xl border border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-blue-950/50 p-8 shadow-2xl shadow-slate-950/50">
          <div>
            <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-blue-500/20 bg-blue-500/5 px-3 py-1.5 text-xs font-medium text-blue-200">
              <Bot className="h-3.5 w-3.5" />
              AgentConnect AI
            </div>

            <h1 className="max-w-md text-4xl font-bold tracking-tight text-white md:text-5xl">
              Secure coordination for the agent economy.
            </h1>
            <p className="mt-4 max-w-md text-base leading-7 text-slate-300">
              Route the right agent, protect trust boundaries, and keep human oversight in the loop for every high-signal decision.
            </p>
          </div>

          <div className="mt-10 grid gap-4 md:grid-cols-3">
            {[
              { label: "Verified routing", value: "99.2%", icon: ShieldCheck },
              { label: "Safety checks", value: "24/7", icon: LockKeyhole },
              { label: "Human review", value: "Low latency", icon: Sparkles },
            ].map((item) => (
              <div key={item.label} className="rounded-2xl border border-slate-800 bg-slate-950/40 p-4">
                <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-blue-500/10 text-blue-300">
                  <item.icon className="h-5 w-5" />
                </div>
                <p className="text-lg font-semibold text-white">{item.value}</p>
                <p className="mt-1 text-xs text-slate-400">{item.label}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="flex items-center justify-center">
          <Card className="w-full max-w-lg border-slate-800 bg-slate-900/80">
            <CardHeader className="border-b border-slate-800 pb-5">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <CardTitle className="text-2xl">{mode === "login" ? "Welcome back" : "Create your account"}</CardTitle>
                  <CardDescription>{mode === "login" ? "Sign in to continue monitoring the network." : "Set up a new operator or admin profile."}</CardDescription>
                </div>
                <div className="rounded-full border border-slate-700 bg-slate-800 px-3 py-1 text-xs font-medium text-slate-300">
                  {mode === "login" ? "Login" : "Sign up"}
                </div>
              </div>
            </CardHeader>

            <CardContent className="pt-5">
              <div className="mb-5 flex rounded-xl border border-slate-800 bg-slate-950/50 p-1">
                {(["login", "signup"] as const).map((option) => (
                  <button
                    key={option}
                    type="button"
                    onClick={() => setMode(option)}
                    className={`flex-1 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                      mode === option ? "bg-blue-600 text-white" : "text-slate-300 hover:bg-slate-800"
                    }`}
                  >
                    {option === "login" ? "Log in" : "Create account"}
                  </button>
                ))}
              </div>

              <form className="space-y-4" onSubmit={handleSubmit}>
                <div>
                  <label htmlFor="email" className="mb-2 block text-sm text-slate-300">
                    Email
                  </label>
                  <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    className="h-11 w-full rounded-xl border border-slate-700 bg-slate-950/60 px-3 text-sm text-white outline-none transition placeholder:text-slate-500 focus:border-blue-500/60 focus:ring-2 focus:ring-blue-500/15"
                    placeholder="you@company.com"
                  />
                </div>

                <div>
                  <label htmlFor="password" className="mb-2 block text-sm text-slate-300">
                    Password
                  </label>
                  <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    className="h-11 w-full rounded-xl border border-slate-700 bg-slate-950/60 px-3 text-sm text-white outline-none transition placeholder:text-slate-500 focus:border-blue-500/60 focus:ring-2 focus:ring-blue-500/15"
                    placeholder="Enter your password"
                  />
                </div>

                {error ? (
                  <div className="rounded-xl border border-rose-500/25 bg-rose-500/5 p-3 text-sm text-rose-200">{error}</div>
                ) : null}

                <div className="flex items-center justify-between text-sm text-slate-400">
                  <label className="inline-flex items-center gap-2">
                    <input type="checkbox" className="h-4 w-4 rounded border-slate-700 bg-slate-950 text-blue-500" defaultChecked />
                    Keep me signed in
                  </label>
                  <Link href="/" className="text-blue-300 hover:text-blue-200">
                    Need help?
                  </Link>
                </div>

                <Button type="submit" className="w-full" size="lg" disabled={loading}>
                  {loading ? "Signing in..." : mode === "login" ? "Log in" : "Create account"}
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>
              </form>
            </CardContent>
          </Card>
        </section>
      </div>
    </main>
  );
}
