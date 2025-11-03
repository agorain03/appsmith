import React from "react";
import capitalize from "lodash/capitalize";
import { Slide, toast as toastifyToast } from "react-toastify";

import type { ToastProps } from "./Toast.types";
import { UnauthorizedToastContainer, UnauthorizedToastBody, UnauthorizedToastButton } from "./UnauthorizedToast.styles";
import { getIconByKind } from "../Icon/getIconByKind";

export const UNAUTHORIZED_TOAST_DEFAULT_MESSAGE = "Unauthorized API access";

function UnauthorizedToast(props: ToastProps) {
  return (
    <UnauthorizedToastContainer
      autoClose={70000}
      closeButton={false}
      draggable={false}
      hideProgressBar
      pauseOnHover
      position={toastifyToast.POSITION.TOP_CENTER}
      rtl={false}
      transition={Slide}
      {...props}
    />
  );
}

const unauthToast = {
  show: (content?: string, options?: ToastProps) => {
    const finalContent = content || UNAUTHORIZED_TOAST_DEFAULT_MESSAGE;
    const actionText = capitalize(options?.action?.text);
    const icon = getIconByKind(options?.kind || "error");
    const toastId = JSON.stringify({
      unauthorized: true,
      content: finalContent,
      ...options,
    });

    return toastifyToast(
      <UnauthorizedToastBody>
        {finalContent}
        {actionText && (
          <UnauthorizedToastButton
            kind="tertiary"
            onClick={() => {
              options?.action?.effect && options?.action?.effect();
              toastifyToast.dismiss(toastId);
            }}
            {...options?.action}
          >
            {actionText}
          </UnauthorizedToastButton>
        )}
      </UnauthorizedToastBody>,
      {
        icon,
        toastId,
        type: options?.kind || "error",
        closeOnClick: !actionText,
        ...options,
      },
    );
  },
  dismiss: () => toastifyToast.dismiss(),
};

UnauthorizedToast.displayName = "UnauthorizedToast";

export { UnauthorizedToast, unauthToast };