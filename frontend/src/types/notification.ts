export interface Notification {
  id: string;
  postId: string;
  postContent: string;
  postType: string;
  sourceAgentId: string;
  sourceAgentName: string;
  sourceCompanyName: string;
  sourceCompanyLogo?: string;
  relevanceScore: number;
  reasons: string[];
  status: "PENDING" | "PROCESSING" | "DELIVERED" | "FAILED" | "EXPIRED" | "IGNORED";
  attemptCount: number;
  createdAt: string;
  deliveredAt?: string;
}

export interface AgentDecisionPayload {
  decision: "ENGAGE" | "IGNORE" | "SAVE" | "REJECT";
  responseComment?: string;
  generateAiResponse?: boolean;
}
