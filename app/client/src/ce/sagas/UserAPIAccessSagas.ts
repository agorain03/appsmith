import { all, call, put, takeEvery } from "redux-saga/effects";
import UserAPIAccessApi from "ce/api/UserAPIAccessApi";
import type { UserAPIAccessEntry } from "ce/api/UserAPIAccessApi";
import { validateResponse } from "sagas/ErrorSagas";
import {
  fetchUserApiAccessError,
  fetchUserApiAccessSuccess,
} from "ce/actions/userApiAccessAction";
import { UserAPIAccessActionTypes } from "ce/constants/ReduxActionConstants";
import type { ApiResponse } from "api/ApiResponses";

function* fetchUserApiAccessSaga() {
    try {
        console.log("[UserAPIAccess] fetchUserApiAccessSaga start");
        const envelope: ApiResponse<UserAPIAccessEntry[]> = yield call(
            UserAPIAccessApi.getAll,
        );
        console.log("[UserAPIAccess] response envelope =", envelope);
        const isValid: boolean = yield call(validateResponse, envelope);
        if (!isValid) return;

        const list = envelope.data;
        const byUserId: Record<string, string[]> = {};
        for (const entry of list) {
            byUserId[entry.userId] = entry.accessibleApiIds || [];
        }

        yield put(fetchUserApiAccessSuccess({ byUserId }));
    } catch (e: any) {
        yield put(
            fetchUserApiAccessError(e?.message || "Failed to load API access map"),
        );
    }
}

export default function* userApiAccessRootSaga() {
    yield all([
        takeEvery(
            UserAPIAccessActionTypes.FETCH_USER_API_ACCESS_INIT,
            fetchUserApiAccessSaga,
        ),
    ]);
}