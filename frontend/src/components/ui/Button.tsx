import React from "react";
import { cn } from "@/lib/utils";

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "default" | "secondary" | "outline" | "ghost" | "destructive" | "success";
  size?: "sm" | "md" | "lg" | "icon";
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = "default", size = "md", children, disabled, ...props }, ref) => {
    const base = "inline-flex items-center justify-center font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary disabled:pointer-events-none disabled:opacity-50 rounded-lg cursor-pointer";

    const variants = {
      default: "bg-blue-600 text-white hover:bg-blue-500 shadow-sm",
      secondary: "bg-slate-800 text-slate-100 hover:bg-slate-700 border border-slate-700",
      outline: "border border-slate-700 text-slate-200 hover:bg-slate-800",
      ghost: "text-slate-300 hover:bg-slate-800 hover:text-white",
      destructive: "bg-rose-600 text-white hover:bg-rose-500",
      success: "bg-emerald-600 text-white hover:bg-emerald-500",
    };

    const sizes = {
      sm: "h-8 px-3 text-xs",
      md: "h-9 px-4 text-sm",
      lg: "h-11 px-6 text-base",
      icon: "h-9 w-9 p-0",
    };

    return (
      <button
        ref={ref}
        disabled={disabled}
        className={cn(base, variants[variant], sizes[size], className)}
        {...props}
      >
        {children}
      </button>
    );
  }
);
Button.displayName = "Button";
