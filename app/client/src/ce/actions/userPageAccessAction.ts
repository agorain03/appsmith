import { UserPageAccessActionTypes } from "ce/constants/ReduxActionConstants";

export const fetchUserPageAccess = () => ({
  type: UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_INIT,
});

export const fetchUserPageAccessSuccess = (payload: {
  byUserId: Record<string, string[]>;
}) => ({
  type: UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_SUCCESS,
  payload,
});

export const fetchUserPageAccessError = (error: string) => ({
  type: UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_ERROR,
  payload: { error },
});