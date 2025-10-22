import React from "react";
import type { RouteComponentProps } from "react-router";
import PageLoadingBar from "pages/common/PageLoadingBar";
import { retryPromise } from "utils/AppsmithUtils";
import type { InitAppViewerPayload } from "actions/initActions";
import { initAppViewerAction } from "actions/initActions";
import { APP_MODE } from "entities/App";
import { connect } from "react-redux";
import { getSearchQuery } from "utils/helpers";
import { GIT_BRANCH_QUERY_KEY } from "constants/routes";
import { ReduxActionTypes } from "ee/constants/ReduxActionConstants";

type Props = {
  initAppViewer: (payload: InitAppViewerPayload) => void;
  clearCache: () => void;
} & RouteComponentProps<{ basePageId: string; baseApplicationId?: string }>;

// TODO: Fix this the next time the file is edited
// eslint-disable-next-line @typescript-eslint/no-explicit-any
class AppViewerLoader extends React.PureComponent<Props, { Page: any }> {
  // TODO: Fix this the next time the file is edited
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  constructor(props: any) {
    super(props);

    this.state = {
      Page: null,
    };
  }

  componentDidMount() {
    this.initialize();  // Starts data fetching via Redux
    retryPromise(
      async () => import(/* webpackChunkName: "AppViewer" */ "./index"),
    ).then((module) => {
      this.setState({ Page: module.default });
    });
  }

  render() {
    const { Page } = this.state;

    return Page ? <Page {...this.props} /> : <PageLoadingBar />;
  }

  /**
   * The connect wrapper is smart. When it receives the props from withRouter, it merges them with the props it created itself (initAppViewer, clearCache) and passes the combined "mega" object down to your actual AppViewerLoader component.
   * So, when your AppViewerLoader finally renders, the this.props object it receives is a combination of everything provided by all the wrappers.
   * // The final `this.props` object inside AppViewerLoader
      {
        // From react-router-dom's wrapper
        location: { pathname: '/app/...', search: '?branch=main', ... },
        match: { params: { basePageId: '123' }, ... },
        history: { ... },

        // From react-redux's wrapper
        initAppViewer: function(...) { ... },
        clearCache: function(...) { ... }
      }
   */
  private initialize() {
    const {
      initAppViewer,
      location: { search },  // both location and match these are provided by react-redux-dom
      match: { params },   // both these are provided by react-redux-dom
    } = this.props;
    const { baseApplicationId, basePageId } = params;
    const branch = getSearchQuery(search, GIT_BRANCH_QUERY_KEY);

    // onMount initPage
    if (baseApplicationId || basePageId) {
      initAppViewer({     // Now we are actually running the dispatch function here by calling the prop function created by connect
        baseApplicationId,
        branch,
        basePageId,
        mode: APP_MODE.PUBLISHED,
      });
    }
  }
  componentWillUnmount() {
    const { clearCache } = this.props;

    clearCache();
  }
}

// TODO: Fix this the next time the file is edited
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const mapDispatchToProps = (dispatch: any) => {
  return {
    initAppViewer: (payload: InitAppViewerPayload) =>
      dispatch(initAppViewerAction(payload)),   //The dispatch Function (The Cashier): You take your order slip (the Action) and hand it to the cashier. The cashier is the dispatch function. Their only job is to take your order and put it in the kitchen (the Redux store). They don't care what the order is, they just make sure it gets to the kitchen.
    clearCache: () => {
      dispatch({ type: ReduxActionTypes.CLEAR_CACHE });    // dispatch function belongs to central redux store. It is the only way to send an action to the store to trigger a state change.
    },
  };
};

/*
Now, think of your AppViewerLoader component as a single room in the building. By itself, it has no power. It can't access the building's main generator.

The connect function is what creates a special wall socket in that room, wired directly to the main power supply (<Provider>).
*/
// The `connect` function takes our component and "connects" it to Redux.
// - The first argument (`null`) is for mapping state from the Redux store to props. We don't need to read any state here, so it's `null`.
// - The second argument (`mapDispatchToProps`) is our function from above. `connect` uses it to add `initAppViewer` and `clearCache` to our component's props.
// The result is a new "wrapper" component that has these dispatching functions available in its props.
// connect(null, mapDispatchToProps) -> runs first and returns a function that takes AppViewerLoader as an argument
// When is mapDispatchToProps called and how does dispatch get there?
// This is the key part. When connect is creating that "wrapper" component, it does the following:

// It reaches out to the <Provider>: The connect function is smart. It knows how to find the store that the <Provider> is holding.
// It gets the dispatch function: It asks the store for its dispatch method. store.dispatch.
// It calls mapDispatchToProps: Now that connect has the dispatch function, it calls the mapDispatchToProps function that you gave it, passing dispatch as the argument.
// So, the flow is: <Provider> holds store -> connect asks <Provider> for store -> connect gets store.dispatch -> connect calls mapDispatchToProps(dispatch)
export default connect(null, mapDispatchToProps)(AppViewerLoader);
