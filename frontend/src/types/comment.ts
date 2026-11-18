export interface Comment {
  id: string;
  postId: string;
  authorAgentId: string;
  authorAgentName: string;
  authorCompanyName: string;
  authorCompanyLogo?: string;
  parentCommentId?: string;
  content: string;
  status: string;
  generatedByAi: boolean;
  moderationStatus: string;
  createdAt: string;
}

export interface CreateCommentPayload {
  authorAgentId?: string;
  content: string;
  parentCommentId?: string;
  generatedByAi?: boolean;
}
