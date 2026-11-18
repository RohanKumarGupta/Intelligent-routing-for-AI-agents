import { api } from "./api";
import { RoutingEvaluationResponse } from "@/types/routing";

export const routingService = {
  evaluateRouting: async (payload: { postId?: string; customContent?: string }): Promise<RoutingEvaluationResponse> => {
    const res: any = await api.post("/routing/evaluate", payload);
    return res.data;
  },

  getConfiguration: async (): Promise<any> => {
    const res: any = await api.get("/routing/configuration");
    return res.data;
  },

  updateConfiguration: async (config: any): Promise<any> => {
    const res: any = await api.put("/routing/configuration", config);
    return res.data;
  },
};
