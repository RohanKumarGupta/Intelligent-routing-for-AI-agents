import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatScore(score: number | undefined | null): string {
  if (score === undefined || score === null) return "0%";
  return `${Math.round(score * 100)}%`;
}

export function getScoreColor(score: number): string {
  if (score >= 0.75) return "text-emerald-500 bg-emerald-500/10 border-emerald-500/30";
  if (score >= 0.50) return "text-amber-500 bg-amber-500/10 border-amber-500/30";
  return "text-rose-500 bg-rose-500/10 border-rose-500/30";
}
