import styled, { css } from "styled-components";
import { ToastContainer } from "react-toastify";
import { Text } from "../Text";
import type { ToastProps } from "./Toast.types";
import { Button } from "../Button";
import { ToastClassName, ToastbodyClassName } from "./Toast.constants";

/**
 * Larger variant ONLY for unauthorized / ACL error toasts.
 * Keeps original token usage but overrides width, spacing, font size, and min-height.
 * Wrap this container with <UnauthorizedToastContainer /> instead of the normal <StyledToast />.
 */

const LargerVariables = css`
// All the --toastify prefixed variables are changing tokens defined in react-toastify
  // For a complete list, see https://fkhadra.github.io/react-toastify/how-to-style/#override-css-variables

  --toastify-toast-width: 320px;
  --toastify-toast-background: var(--ads-v2-colors-response-surface-default-bg);
  --toastify-toast-min-height: 5rem;
  --toastify-toast-max-height: 800px;
  --toastify-font-family: var(--ads-v2-font-family);
  --toastify-z-index: 9999;

  --toastify-text-color-light: var(--ads-v2-colors-response-label-default-fg);
`;

// Container – same pattern as original, but with larger paddings & width vars.
export const UnauthorizedToastContainer = styled(ToastContainer).attrs<ToastProps>({
  toastClassName: `${ToastClassName}-t--toast-unauthorized`,
  bodyClassName: `${ToastbodyClassName}-t--toast-body-unauthorized`,
})`
  .${ToastClassName} {
    border: solid 1px var(--ads-v2-color-border);
    padding: var(--ads-v2-spaces-3);
  }

  .${ToastbodyClassName} {
      padding: 0;
      gap: var(--ads-v2-spaces-3);
      align-items: center;
    }

  .Toastify__toast {
    // TODO: Move box-shadow to theme once https://www.notion.so/appsmith/Box-shadows-enumerate-name-and-document-29c2d8490b4c4a42b4f381d82e761b87 is complete
    box-shadow:
      0 1.9px 7px 0 rgba(207, 13, 13, 0.01),
      0 15px 56px 0 rgba(192, 15, 15, 0.07);
  }

  .Toastify__toast-icon {
    align-self: center;
    width: fit-content;
    margin-right: 0;
  }

  ${LargerVariables}
`;

// Body text – increase font size & line-height
export const UnauthorizedToastBody = styled(Text)`
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--ads-v2-colors-response-label-default-fg);
  gap: var(--ads-v2-spaces-3);
  word-break: break-word;
`;

export const UnauthorizedToastButton = styled(Button)`
  align-self: center;
`;