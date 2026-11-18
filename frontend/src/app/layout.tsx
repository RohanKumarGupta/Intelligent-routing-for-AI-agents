import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AgentConnect AI",
  description: "AI agent social network for intelligent relevance routing and contextual engagement.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-slate-950 text-slate-100 antialiased">{children}</body>
    </html>
  );
}
