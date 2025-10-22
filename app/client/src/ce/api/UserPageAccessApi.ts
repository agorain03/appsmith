import Api from "api/Api";
import type { ApiResponse } from "api/ApiResponses";

export interface UserPageAccessEntry {
  userId: string;
  accessiblePageIds: string[];
}

function normalizeEnvelope(
  raw: any,
): ApiResponse<UserPageAccessEntry[]> {
  console.log("normalizeEnvelope: raw =", raw);
  // Case A: interceptor already returned the envelope
  if (raw && raw.responseMeta && Array.isArray(raw.data)) {
    return raw as ApiResponse<UserPageAccessEntry[]>;
  }

  // Case B: raw is AxiosResponse< ApiResponse<T> >
  if (raw && raw.data && raw.data.responseMeta && Array.isArray(raw.data.data)) {
    return raw.data as ApiResponse<UserPageAccessEntry[]>;
  }

  throw new Error("Unexpected response shape from /v1/user-page-access/all");
}

class UserPageAccessApi {
  static url = "v1/user-page-access/all";

  static getAll(): Promise<ApiResponse<UserPageAccessEntry[]>> {
    return Api.get(UserPageAccessApi.url).then(normalizeEnvelope);
  }
}

export default UserPageAccessApi;