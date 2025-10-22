import { createImmerReducer } from "utils/ReducerUtils";
import type { ReduxAction } from "actions/ReduxActionTypes";
import { UserPageAccessActionTypes } from "ce/constants/ReduxActionConstants";

export interface UserPageAccessState {
  loaded: boolean;
  loading: boolean;
  byUserId: Record<string, string[]>;
  error?: string;
}

export const initialState: UserPageAccessState = {
  loaded: false,
  loading: false,
  byUserId: {},
  error: undefined,
};

export const handlers = {
  [UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_INIT]: (
    draft: UserPageAccessState,
  ) => {
    draft.loading = true;
    draft.error = undefined;
  },

  [UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_SUCCESS]: (
    draft: UserPageAccessState,
    action: ReduxAction<{ byUserId: Record<string, string[]> }>,
  ) => {
    draft.loading = false;
    draft.loaded = true;
    draft.byUserId = action.payload.byUserId;
  },

  [UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_ERROR]: (
    draft: UserPageAccessState,
    action: ReduxAction<{ error: string }>,
  ) => {
    draft.loading = false;
    draft.loaded = false;
    draft.error = action.payload.error;
  },
};

const userPageAccessReducer = createImmerReducer(initialState, handlers);

export default userPageAccessReducer;