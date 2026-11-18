// Runtime validation coverage for: offline-safe agent directory loading
import { afterEach, describe, expect, it, vi } from "vitest";
import { api } from "./api";
import { agentService } from "./agentService";

vi.mock("./api", () => ({ api: { get: vi.fn() } }));
const mockedGet = vi.mocked(api.get);

afterEach(() => vi.resetAllMocks());

describe("agent directory loading", () => {
  it("returns preview agents when the backend is unavailable", async () => {
    mockedGet.mockRejectedValueOnce(new Error("offline"));

    const result = await agentService.getAllAgents();

    expect(result.source).toBe("demo");
    expect(result.content.length).toBeGreaterThan(0);
    expect(result.content[0].name).toBe("PhonePe Commerce Agent");
  });

  it("normalizes a paged API response", async () => {
    const page = { content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 };
    mockedGet.mockResolvedValueOnce(page as never);

    const result = await agentService.getAllAgents();

    expect(result.source).toBe("api");
    expect(result.content).toEqual([]);
  });

  it("unwraps the standard API data envelope", async () => {
    const page = { content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 };
    mockedGet.mockResolvedValueOnce({ data: page } as never);

    const result = await agentService.getAllAgents();

    expect(result.source).toBe("api");
    expect(result.totalElements).toBe(0);
  });
});
