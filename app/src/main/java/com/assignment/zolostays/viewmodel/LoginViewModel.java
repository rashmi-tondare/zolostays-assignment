
package com.assignment.zolostays.viewmodel;

import javax.inject.Inject;

import com.assignment.zolostays.BR;
import com.assignment.zolostays.R;
import com.assignment.zolostays.constants.AppConstants;
import com.assignment.zolostays.model.User;
import com.assignment.zolostays.utils.ActivityUtils;
import com.assignment.zolostays.utils.PasswordUtils;
import com.assignment.zolostays.view.RegistrationActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import dagger.Module;

/**
 * Created by Rashmi on 12/08/17.
 */

@Module
public class LoginViewModel extends BaseViewModel {
    public static final int PHONE_NUMBER_INPUT = 1;
    public static final int PASSWORD_INPUT = 2;

    private String phoneNumber, password;
    public ObservableBoolean loginEnabled;

    @Inject
    public LoginViewModel() {
        phoneNumber = "";
        password = "";
        loginEnabled = new ObservableBoolean(false);
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
        updateLoginButtonStatus();
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
        updateLoginButtonStatus();
    }

    public void onClickLogin(View view) {
        ActivityUtils.hideKeyboard((Activity) view.getContext());
        if (!phoneNumber.trim().matches(AppConstants.MOBILE_NUMBER_VALIDATION_PATTERN)) {
            if (onInputErrorListener != null) {
                onInputErrorListener.onInputError(PHONE_NUMBER_INPUT);
            }
            return;
        }

        if (password.length() < AppConstants.MINIMUM_PASSWORD_LENGTH) {
            if (onInputErrorListener != null) {
                onInputErrorListener.onInputError(PASSWORD_INPUT);
            }
            return;
        }

        if (onInputErrorListener != null) {
            onInputErrorListener.clearInputErrors();
        }
        User user = dbHelper.getUserByPhoneNumber(phoneNumber);
        if (user != null && PasswordUtils.checkIfEqual(user.getPassword(), password)) {
            Snackbar.make(view.getRootView(), R.string.snackbar_logged_in, Snackbar.LENGTH_SHORT).show();
        }
        else {
            Snackbar.make(view.getRootView(), R.string.error_invalid_credentials, Snackbar.LENGTH_LONG).show();
        }
    }

    public void onClickCreateAccount(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, RegistrationActivity.class);
        context.startActivity(intent);
    }

    private void updateLoginButtonStatus() {
        loginEnabled.set(!(TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)));
        loginEnabled.notifyChange();
    }
}
