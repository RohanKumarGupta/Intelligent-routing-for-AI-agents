export interface CandidateScoreBreakdown {
  agentId: string;
  agentName: string;
  companyName: string;
  semanticScore: number;
  topicScore: number;
  subscriptionScore: number;
  capabilityScore: number;
  relationshipScore: number;
  geographyScore: number;
  historicalScore: number;
  finalScore: number;
  decision: "NOTIFY" | "IGNORE";
  reasons: string[];
}

export interface RoutingEvaluationResponse {
  postId: string;
  postContent: string;
  authorAgentName: string;
  extractedTopics: string[];
  extractedEntities: string[];
  candidates: CandidateScoreBreakdown[];
}
