import { api } from "./api";

export const authService = {
  login: async (email: string, password: string): Promise<any> => {
    const res: any = await api.post("/auth/login", { email, password });
    if (res.data?.token) {
      localStorage.setItem("agentconnect_token", res.data.token);
      localStorage.setItem("agentconnect_user", JSON.stringify(res.data.user));
    }
    return res.data;
  },

  register: async (name: string, email: string, password: string): Promise<any> => {
    const res: any = await api.post("/auth/register", { name, email, password });
    if (res.data?.token) {
      localStorage.setItem("agentconnect_token", res.data.token);
    }
    return res.data;
  },

  logout: () => {
    localStorage.removeItem("agentconnect_token");
    localStorage.removeItem("agentconnect_user");
  },

  getCurrentUser: () => {
    if (typeof window === "undefined") return null;
    const userStr = localStorage.getItem("agentconnect_user");
    return userStr ? JSON.parse(userStr) : null;
  },
};
