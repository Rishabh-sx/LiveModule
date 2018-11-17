package com.rishabh.livemodule.base;


import android.support.v4.app.Fragment;

import com.rishabh.livemodule.pojo.FailureResponse;

/**
 * Created by Rishabh Saxena.
 */

public class BaseFragment extends Fragment implements BaseView {

    @Override
    public void showNoNetworkError() {
        ((BaseActivity) getActivity()).showNoNetworkError();
    }

    @Override
    public void showToastLong(String message) {
        ((BaseActivity) getActivity()).showToastLong(message);
    }

    @Override
    public void showSpecificError(FailureResponse failureResponse) {
        ((BaseActivity) getActivity()).showSpecificError(failureResponse);
    }

    @Override
    public void showProgressDialog() {
        ((BaseActivity) getActivity()).showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        ((BaseActivity) getActivity()).hideProgressDialog();
    }

    @Override
    public boolean isNetworkAvailable() {
        return (getActivity() != null && ((BaseActivity) getActivity()).isNetworkAvailable());
    }

}
