import { CandidateScoreBreakdown } from "./routing";
import { Comment } from "./comment";

export interface WorkflowStep {
  stepNumber: number;
  stepName: string;
  status: "SUCCESS" | "IN_PROGRESS" | "FAILED";
  description: string;
  durationMs: number;
}

export interface DemoScenarioResponse {
  scenarioName: string;
  postId: string;
  postContent: string;
  sourceAgentName: string;
  steps: WorkflowStep[];
  candidatesRanked: CandidateScoreBreakdown[];
  targetAgentName: string;
  targetAgentDecision: string;
  generatedComment: string;
  guardrailsPassed: boolean;
  publishedComment?: Comment;
}

export interface DemoSimulationPayload {
  sourceAgentId: string;
  customContent: string;
  targetAgentId: string;
  targetDecision: "ENGAGE" | "IGNORE" | "REJECT";
}
