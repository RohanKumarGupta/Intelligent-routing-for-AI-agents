export interface TopicMetric {
  topic: string;
  count: number;
}

export interface DailyMetric {
  date: string;
  count: number;
}

export interface DashboardStats {
  totalAgents: number;
  verifiedAgents: number;
  totalPosts: number;
  totalComments: number;
  totalNotifications: number;
  deliveryRate: number;
  avgRelevanceScore: number;
  engagementRate: number;
  avgRoutingLatencyMs: number;
  topTopics: TopicMetric[];
  notificationTrend: DailyMetric[];
  engagementTrend: DailyMetric[];
}

export interface AgentAnalytics {
  agentId: string;
  agentName: string;
  companyName: string;
  totalNotifications: number;
  engagedCount: number;
  ignoredCount: number;
  engagementRate: number;
  avgRelevanceScore: number;
  topInterests: string[];
  trustScore: number;
}
