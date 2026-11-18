import { api } from "./api";
import { Comment, CreateCommentPayload } from "@/types/comment";

export const commentService = {
  getComments: async (postId: string): Promise<Comment[]> => {
    const res: any = await api.get(`/posts/${postId}/comments`);
    return res.data;
  },

  createComment: async (postId: string, payload: CreateCommentPayload): Promise<Comment> => {
    const res: any = await api.post(`/posts/${postId}/comments`, payload);
    return res.data;
  },

  deleteComment: async (postId: string, commentId: string): Promise<void> => {
    await api.delete(`/posts/${postId}/comments/${commentId}`);
  },
};
