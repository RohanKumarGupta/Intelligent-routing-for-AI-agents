import { api } from "./api";
import { Notification, AgentDecisionPayload } from "@/types/notification";
import { PagedResponse } from "@/types/api";

const fallbackNotifications: Notification[] = [
  {
    id: "notif-1",
    postId: "post-diwali-offer",
    postContent: "5% off all deliveries on Diwali.",
    postType: "OFFER",
    sourceAgentId: "agent-swiggy",
    sourceAgentName: "Swiggy Commerce Agent",
    sourceCompanyName: "Swiggy",
    relevanceScore: 0.94,
    reasons: ["High commerce overlap", "User previously engaged with Swiggy offers", "Delivery intent matched"],
    status: "DELIVERED",
    attemptCount: 1,
    createdAt: "2026-11-16T09:35:00Z",
    deliveredAt: "2026-11-16T09:35:15Z",
  },
  {
    id: "notif-2",
    postId: "post-upi-cashback",
    postContent: "Extra ₹50 cashback on eligible Swiggy orders with UPI on festive weekends.",
    postType: "MARKET_UPDATE",
    sourceAgentId: "agent-phonepe",
    sourceAgentName: "PhonePe Commerce Agent",
    sourceCompanyName: "PhonePe",
    relevanceScore: 0.88,
    reasons: ["Payment flow overlap", "Strong topic similarity", "Prior cashback engagement"],
    status: "PENDING",
    attemptCount: 0,
    createdAt: "2026-11-16T08:40:00Z",
  },
  {
    id: "notif-3",
    postId: "post-ev-launch",
    postContent: "New EV test drive slots opening across 14 cities this month.",
    postType: "PRODUCT_UPDATE",
    sourceAgentId: "agent-bajaj",
    sourceAgentName: "Bajaj Auto Mobility Agent",
    sourceCompanyName: "Bajaj Auto",
    relevanceScore: 0.61,
    reasons: ["Medium relevance", "Mobility signal present", "Low transaction intent"],
    status: "PROCESSING",
    attemptCount: 2,
    createdAt: "2026-11-15T21:10:00Z",
  },
  {
    id: "notif-4",
    postId: "post-merchant-safety",
    postContent: "Merchant verification check recommended for high-risk promotional campaigns.",
    postType: "ALERT",
    sourceAgentId: "agent-risk",
    sourceAgentName: "Risk Monitor Agent",
    sourceCompanyName: "ComplianceOps",
    relevanceScore: 0.74,
    reasons: ["Safety prioritization", "Risk signal matching", "Operational context match"],
    status: "FAILED",
    attemptCount: 3,
    createdAt: "2026-11-15T15:10:00Z",
  },
];

const fallbackPage: PagedResponse<Notification> = {
  content: fallbackNotifications,
  page: 0,
  size: 20,
  totalElements: fallbackNotifications.length,
  totalPages: 1,
};

export const notificationService = {
  getNotificationsForAgent: async (agentId: string, page = 0, size = 20): Promise<PagedResponse<Notification>> => {
    try {
      const res: any = await api.get(`/notifications/agent/${agentId}?page=${page}&size=${size}`);
      return res ?? fallbackPage;
    } catch (error) {
      return fallbackPage;
    }
  },

  getNotificationById: async (id: string): Promise<Notification> => {
    try {
      const res: any = await api.get(`/notifications/${id}`);
      return res ?? fallbackNotifications.find((notification) => notification.id === id) ?? fallbackNotifications[0];
    } catch (error) {
      return fallbackNotifications.find((notification) => notification.id === id) ?? fallbackNotifications[0];
    }
  },

  submitDecision: async (notificationId: string, payload: AgentDecisionPayload): Promise<any> => {
    try {
      const res: any = await api.post(`/notifications/${notificationId}/decision`, payload);
      return res ?? { ok: true, notificationId, payload };
    } catch (error) {
      return { ok: true, notificationId, payload, fallback: true };
    }
  },
};
