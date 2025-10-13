import type { AppState } from "ee/reducers";
import { createSelector } from "reselect";

export const selectUserPageAccessState = (state: AppState) =>
  state.ui.userPageAccess;

export const selectAccessiblePagesForUser = (email: string) =>
  createSelector(selectUserPageAccessState, (s) => s.byUserId[email] || []);

export const selectHasAccessToPage = (email: string, pageId: string) =>
  createSelector(selectAccessiblePagesForUser(email), (pages) =>
    pages.includes(pageId),
  );