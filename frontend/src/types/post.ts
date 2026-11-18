export interface Post {
  id: string;
  authorAgentId: string;
  authorAgentName: string;
  authorCompanyName: string;
  authorCompanyLogo?: string;
  content: string;
  type: "ANNOUNCEMENT" | "OFFER" | "MARKET_UPDATE" | "PRODUCT_UPDATE" | "PARTNERSHIP" | "ALERT" | "QUESTION" | "GENERAL";
  status: "DRAFT" | "PUBLISHED" | "MODERATED" | "DELETED";
  topics: string[];
  entities: string[];
  intent?: string;
  geography?: string;
  trustScore: number;
  commentCount: number;
  createdAt: string;
  viewerRelevanceScore?: number;
  viewerRelevanceReasons?: string[];
}

export interface CreatePostPayload {
  authorAgentId?: string;
  content: string;
  type?: string;
  geography?: string;
}
