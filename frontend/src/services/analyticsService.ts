import { api } from "./api";
import { DashboardStats, AgentAnalytics } from "@/types/analytics";

const fallbackDashboardStats: DashboardStats = {
  totalAgents: 24,
  verifiedAgents: 18,
  totalPosts: 412,
  totalComments: 186,
  totalNotifications: 968,
  deliveryRate: 0.92,
  avgRelevanceScore: 0.84,
  engagementRate: 0.39,
  avgRoutingLatencyMs: 132,
  topTopics: [
    { topic: "Food Delivery", count: 126 },
    { topic: "Payments", count: 94 },
    { topic: "Commerce", count: 81 },
    { topic: "Festivals", count: 65 },
  ],
  notificationTrend: [
    { date: "Mon", count: 88 },
    { date: "Tue", count: 92 },
    { date: "Wed", count: 110 },
    { date: "Thu", count: 124 },
    { date: "Fri", count: 119 },
  ],
  engagementTrend: [
    { date: "Mon", count: 24 },
    { date: "Tue", count: 31 },
    { date: "Wed", count: 38 },
    { date: "Thu", count: 42 },
    { date: "Fri", count: 46 },
  ],
};

const fallbackAgentAnalytics: AgentAnalytics = {
  agentId: "demo-agent",
  agentName: "PhonePe Commerce Agent",
  companyName: "PhonePe",
  totalNotifications: 134,
  engagedCount: 58,
  ignoredCount: 14,
  engagementRate: 0.74,
  avgRelevanceScore: 0.86,
  topInterests: ["food_delivery", "discounts", "digital_payments", "cashback"],
  trustScore: 0.92,
};

export const analyticsService = {
  getDashboardStats: async (): Promise<DashboardStats> => {
    try {
      const res: any = await api.get("/analytics/dashboard");
      return res.data ?? fallbackDashboardStats;
    } catch (error) {
      return fallbackDashboardStats;
    }
  },

  getAgentAnalytics: async (agentId: string): Promise<AgentAnalytics> => {
    try {
      const res: any = await api.get(`/analytics/agent/${agentId}`);
      return res.data ?? fallbackAgentAnalytics;
    } catch (error) {
      return { ...fallbackAgentAnalytics, agentId, agentName: "Fallback Agent" };
    }
  },
};
