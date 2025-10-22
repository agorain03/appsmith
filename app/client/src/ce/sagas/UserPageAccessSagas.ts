import { all, call, put, takeEvery } from "redux-saga/effects";
import UserPageAccessApi from "ce/api/UserPageAccessApi";
import type { UserPageAccessEntry } from "ce/api/UserPageAccessApi";
import { validateResponse } from "sagas/ErrorSagas";
import {
  fetchUserPageAccessError,
  fetchUserPageAccessSuccess,
} from "ce/actions/userPageAccessAction";
import { UserPageAccessActionTypes } from "ce/constants/ReduxActionConstants";
import type { ApiResponse } from "api/ApiResponses";

function* fetchUserPageAccessSaga() {
  try {
    console.log("[UserPageAccess] fetchUserPageAccessSaga start");
    const envelope: ApiResponse<UserPageAccessEntry[]> = yield call(
      UserPageAccessApi.getAll,
    );
    console.log("[UserPageAccess] response envelope =", envelope);
    const isValid: boolean = yield call(validateResponse, envelope);
    if (!isValid) return;

    const list = envelope.data;
    const byUserId: Record<string, string[]> = {};
    for (const entry of list) {
      byUserId[entry.userId] = entry.accessiblePageIds || [];
    }

    yield put(fetchUserPageAccessSuccess({ byUserId }));
  } catch (e: any) {
    yield put(
      fetchUserPageAccessError(e?.message || "Failed to load access map"),
    );
  }
}

export default function* userPageAccessRootSaga() {
  yield all([
    takeEvery(
      UserPageAccessActionTypes.FETCH_USER_PAGE_ACCESS_INIT,
      fetchUserPageAccessSaga,
    ),
  ]);
  
}