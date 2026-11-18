import { api } from "./api";
import type { Agent, AgentRegistrationPayload } from "@/types/agent";
import type { ApiResponse, PagedResponse } from "@/types/api";

export type AgentDirectoryPage = PagedResponse<Agent> & { source: "api" | "demo" };

const demoAgents: Agent[] = [
  { id: "agent-phonepe", companyId: "phonepe", companyName: "PhonePe", name: "PhonePe Commerce Agent", description: "Connects shoppers with relevant UPI offers, rewards, and payment experiences.", status: "VERIFIED", notificationThreshold: 0.75, rateLimit: 120, trustScore: 0.94, verified: true, capabilities: ["UPI payments", "Cashback", "Offer discovery"], interests: [{ topic: "Digital payments", weight: 0.96 }, { topic: "Commerce", weight: 0.84 }, { topic: "Food delivery", weight: 0.78 }], subscriptions: ["payments", "commerce", "food-delivery"], createdAt: "2025-03-14T09:30:00.000Z" },
  { id: "agent-swiggy", companyId: "swiggy", companyName: "Swiggy", name: "Swiggy Delivery Agent", description: "Shares timely delivery updates, dining discoveries, and local food promotions.", status: "VERIFIED", notificationThreshold: 0.72, rateLimit: 100, trustScore: 0.91, verified: true, capabilities: ["Order tracking", "Local discovery", "Promotions"], interests: [{ topic: "Food delivery", weight: 0.98 }, { topic: "Restaurants", weight: 0.89 }, { topic: "Offers", weight: 0.72 }], subscriptions: ["food-delivery", "restaurants", "offers"], createdAt: "2025-02-22T11:15:00.000Z" },
  { id: "agent-hdfc", companyId: "hdfc", companyName: "HDFC Bank", name: "HDFC Bank Growth Agent", description: "Matches customers with useful banking insights, card benefits, and secure offers.", status: "VERIFIED", notificationThreshold: 0.8, rateLimit: 80, trustScore: 0.89, verified: true, capabilities: ["Card rewards", "Personal finance", "Secure offers"], interests: [{ topic: "Personal finance", weight: 0.95 }, { topic: "Banking", weight: 0.92 }, { topic: "Shopping", weight: 0.7 }], subscriptions: ["banking", "personal-finance", "shopping"], createdAt: "2025-01-08T08:00:00.000Z" },
  { id: "agent-cred", companyId: "cred", companyName: "CRED", name: "CRED Travel & Rewards Agent", description: "Brings together premium travel experiences and thoughtfully curated rewards.", status: "VERIFIED", notificationThreshold: 0.77, rateLimit: 90, trustScore: 0.87, verified: true, capabilities: ["Travel discovery", "Rewards", "Member experiences"], interests: [{ topic: "Travel", weight: 0.91 }, { topic: "Rewards", weight: 0.89 }, { topic: "Lifestyle", weight: 0.77 }], subscriptions: ["travel", "rewards", "lifestyle"], createdAt: "2024-12-19T13:45:00.000Z" },
  { id: "agent-zepto", companyId: "zepto", companyName: "Zepto", name: "Zepto Local Commerce Agent", description: "Helps local shoppers discover everyday essentials and neighborhood deals.", status: "PENDING_VERIFICATION", notificationThreshold: 0.74, rateLimit: 60, trustScore: 0.71, verified: false, capabilities: ["Local discovery", "Quick commerce", "Catalog search"], interests: [{ topic: "Quick commerce", weight: 0.94 }, { topic: "Groceries", weight: 0.9 }, { topic: "Local offers", weight: 0.76 }], subscriptions: ["quick-commerce", "groceries", "local"], createdAt: "2025-04-04T10:20:00.000Z" },
  { id: "agent-bookmyshow", companyId: "bookmyshow", companyName: "BookMyShow", name: "BookMyShow Events Agent", description: "Surfaces live events and entertainment picks based on audience interests.", status: "PENDING_VERIFICATION", notificationThreshold: 0.8, rateLimit: 55, trustScore: 0.68, verified: false, capabilities: ["Event discovery", "Ticketing", "Recommendations"], interests: [{ topic: "Entertainment", weight: 0.96 }, { topic: "Live events", weight: 0.92 }, { topic: "Movies", weight: 0.86 }], subscriptions: ["entertainment", "events", "movies"], createdAt: "2025-03-29T15:10:00.000Z" },
  { id: "agent-bajaj", companyId: "bajaj", companyName: "Bajaj Auto", name: "Bajaj Mobility Agent", description: "Shares practical mobility insights, ownership guidance, and electric vehicle updates.", status: "SUSPENDED", notificationThreshold: 0.85, rateLimit: 40, trustScore: 0.55, verified: false, capabilities: ["Mobility advice", "EV information", "Ownership support"], interests: [{ topic: "Mobility", weight: 0.93 }, { topic: "Electric vehicles", weight: 0.88 }, { topic: "Automotive", weight: 0.87 }], subscriptions: ["mobility", "electric-vehicles", "automotive"], createdAt: "2024-11-06T07:25:00.000Z" },
  { id: "agent-apollo", companyId: "apollo", companyName: "Apollo 24|7", name: "Apollo Wellness Agent", description: "Makes trusted wellness information and care services easier to discover.", status: "VERIFIED", notificationThreshold: 0.82, rateLimit: 70, trustScore: 0.92, verified: true, capabilities: ["Wellness guidance", "Care discovery", "Appointment support"], interests: [{ topic: "Wellness", weight: 0.97 }, { topic: "Healthcare", weight: 0.94 }, { topic: "Fitness", weight: 0.76 }], subscriptions: ["wellness", "healthcare", "fitness"], createdAt: "2025-02-03T12:05:00.000Z" },
];

function getDemoPage(page: number, size: number): AgentDirectoryPage {
  const start = page * size;
  return { content: demoAgents.slice(start, start + size), page, size, totalElements: demoAgents.length, totalPages: Math.ceil(demoAgents.length / size), source: "demo" };
}

export const agentService = {
  getAllAgents: async (page = 0, size = 20): Promise<AgentDirectoryPage> => {
    try {
      const response = await api.get<PagedResponse<Agent> | ApiResponse<PagedResponse<Agent>>>(`/agents?page=${page}&size=${size}`);
      const body = response as unknown as PagedResponse<Agent> | ApiResponse<PagedResponse<Agent>>;
      const payload = "data" in body ? body.data : body;
      if (!payload || !Array.isArray(payload.content)) throw new Error("The agent directory returned an invalid response.");
      return { ...payload, source: "api" };
    } catch {
      return getDemoPage(page, size);
    }
  },
  getAgentById: async (id: string): Promise<Agent> => {
    const res: any = await api.get(`/agents/${id}`);
    return res.data;
  },
  registerAgent: async (payload: AgentRegistrationPayload): Promise<Agent> => {
    const res: any = await api.post("/agents", payload);
    return res.data;
  },
  verifyAgent: async (id: string): Promise<Agent> => {
    const res: any = await api.post(`/agents/${id}/verify`);
    return res.data;
  },
  suspendAgent: async (id: string): Promise<Agent> => {
    const res: any = await api.post(`/agents/${id}/suspend`);
    return res.data;
  },
};



