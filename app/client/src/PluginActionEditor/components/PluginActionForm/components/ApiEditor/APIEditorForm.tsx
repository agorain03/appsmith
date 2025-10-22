import React from "react";
import CommonEditorForm from "../CommonEditorForm";
import { usePluginActionContext } from "../../../../PluginActionContext";
import { EditorTheme } from "components/editorComponents/CodeEditor/EditorConfig";
import { API_EDITOR_FORM_NAME } from "ee/constants/forms";
import { HTTP_METHOD_OPTIONS } from "../../../../constants/CommonApiConstants";
import PostBodyData from "./PostBodyData";
import { useFeatureFlag } from "utils/hooks/useFeatureFlag";
import { FEATURE_FLAG } from "ee/entities/FeatureFlag";
import { getHasManageActionPermission } from "ee/utils/BusinessFeatures/permissionPageHelpers";
import Pagination from "./Pagination";
import { reduxForm } from "redux-form";
import {
  useHandleRunClick,
  useAnalyticsOnRunClick,
} from "PluginActionEditor/hooks";
import UserAccessTab from "./UserAccessTab";
import { useSelector } from "react-redux";
import { getApplicationsState } from "ce/selectors/applicationSelectors";
import styled from "styled-components";

/* ---------- Read-only shared module notice ---------- */

const SharedWrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 24px 32px;
  align-items: center;
  justify-content: center;
  text-align: center;
  gap: 16px;

  .shared-title {
    font-size: 18px;
    font-weight: 600;
  }
  .shared-sub {
    font-size: 13px;
    opacity: 0.75;
    max-width: 480px;
    line-height: 1.4;
  }
  .shared-badge {
    background: var(--ads-v2-color-bg-subtle,#f1f5f9);
    padding: 4px 10px;
    border-radius: 14px;
    font-size: 11px;
    font-weight: 500;
    letter-spacing: .3px;
    color: #475569;
  }
  .shared-action-name {
    font-family: monospace;
    background: #f8fafc;
    padding: 4px 8px;
    border-radius: 4px;
  }
`;

const SharedModuleReadOnly = ({
  actionName,
  moduleName,
}: {
  actionName: string;
  moduleName?: string;
}) => {
  return (
    <SharedWrapper data-testid="t--shared-module-readonly">
      <div className="shared-badge">Shared Resource</div>
      <div className="shared-title">Shared API / JSObjects not editable</div>
      <div className="shared-sub">
        The selected action <span className="shared-action-name">{actionName}</span>{" "}
        is provided by a shared module
        {moduleName ? (
          <>
            {" "}
            (<strong>{moduleName}</strong>)
          </>
        ) : null}
        . Editing is disabled here to ensure integrity across applications.
        <br />
        You can view and run it, but any changes must be made inside the module
        itself.
      </div>
    </SharedWrapper>
  );
};

/* ---------------------------------------------------- */

const APIEditorForm = () => {
  const { action } = usePluginActionContext();
  const { handleRunClick } = useHandleRunClick();
  const { callRunActionAnalytics } = useAnalyticsOnRunClick();
  const theme = EditorTheme.LIGHT;
  const appState = useSelector(getApplicationsState)

  const isFeatureEnabled = useFeatureFlag(FEATURE_FLAG.license_gac_enabled);
  const isChangePermitted = getHasManageActionPermission(
    isFeatureEnabled,
    action.userPermissions,
  );

  const onTestClick = () => {
    callRunActionAnalytics();
    handleRunClick();
  };

  console.log("Rendering APIEditorForm for action: ---------- ", action);

  const currentAppId = appState.currentApplication?.id;
  console.log("Current App ID: ", currentAppId);
  console.log("Action's App ID: ", action.applicationId);
  const isModuleAction = (currentAppId && action.applicationId !== currentAppId)
  console.log("Is Module Action: ", isModuleAction);

  if (isModuleAction) {
    return (
      <SharedModuleReadOnly
        actionName={action.name}
        moduleName="TEST MODULE"
      />
    );
  }

  return (
    <CommonEditorForm
      action={action}
      bodyUIComponent={
        <PostBodyData
          dataTreePath={`${action.name}.config`}
          theme={EditorTheme.LIGHT}
        />
      }
      dataTestId="t--api-editor-form"
      formName={API_EDITOR_FORM_NAME}
      httpMethodOptions={HTTP_METHOD_OPTIONS}
      isChangePermitted={isChangePermitted}
      paginationUiComponent={
        <Pagination
          actionName={action.name}
          onTestClick={onTestClick}
          paginationType={action.actionConfiguration.paginationType}
          theme={theme}
        />
      }
      apiAccessUiComponent={
        <UserAccessTab
          isChangePermitted={isChangePermitted}
          actionId={action.id}
        />
      }
    />
  );
};

export default reduxForm({
  form: API_EDITOR_FORM_NAME,
  enableReinitialize: true,
})(APIEditorForm);
