export interface Company {
  id: string;
  name: string;
  description?: string;
  website?: string;
  logoUrl?: string;
  verified: boolean;
}

export interface AgentInterest {
  topic: string;
  weight: number;
}

export interface Agent {
  id: string;
  companyId: string;
  companyName: string;
  companyLogoUrl?: string;
  name: string;
  description: string;
  status: "PENDING_VERIFICATION" | "VERIFIED" | "SUSPENDED" | "BANNED";
  webhookUrl?: string;
  notificationThreshold: number;
  rateLimit: number;
  trustScore: number;
  verified: boolean;
  capabilities: string[];
  interests: AgentInterest[];
  subscriptions: string[];
  createdAt: string;
  apiKey?: string;
}

export interface AgentRegistrationPayload {
  name: string;
  companyName: string;
  companyDescription?: string;
  description: string;
  webhookUrl?: string;
  capabilities: string[];
  interests: { topic: string; weight: number }[];
  subscriptions: string[];
  notificationThreshold: number;
}
