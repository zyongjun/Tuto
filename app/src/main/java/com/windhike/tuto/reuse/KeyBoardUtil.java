package com.windhike.tuto.reuse;

import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.windhike.tuto.TutoApplication;

import java.lang.reflect.InvocationTargetException;

import rx.functions.Action1;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class KeyBoardUtil {

    private static final int NONE_FLAGS = -1;

    final class AnonymousClass1 implements Action1<Object> {
        final EditText editText;

        AnonymousClass1(EditText editText) {
            this.editText = editText;
        }

        public final void call(Object obj) {
            KeyBoardUtil.showKeyBoard(this.editText, 0);
        }
    }

    public static void showKeyBoard(View view, int i, int i2, int i3) {
        InputMethodManager inputMethodManager = (InputMethodManager) TutoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            if (!view.hasFocus()) {
                view.setFocusableInTouchMode(true);
                view.requestFocus();
            }
            inputMethodManager.showSoftInput(view, i);
            return;
        }
        inputMethodManager.toggleSoftInput(i2, i3);
    }

    public static void showKeyBoard(View view, int i) {
        showKeyBoard(view, i, 0, 0);
    }

    public static void showKeyBoard(int i, int i2) {
        showKeyBoard(null, -1, i, i2);
    }

    public static void showSoftInputDelay(EditText editText, long j) {
//        WRSchedulers.main(j).subscribe(new AnonymousClass1(editText));
    }

    public static boolean hasSoftMenu() {
        try {
            return !((Boolean) ViewConfiguration.class.getMethod("hasPermanentMenuKey", new Class[0]).invoke(ViewConfiguration.get(TutoApplication.getInstance()), new Object[0])).booleanValue();
        } catch (NoSuchMethodException e) {
            return false;
        } catch (IllegalArgumentException e2) {
            return false;
        } catch (IllegalAccessException e3) {
            return false;
        } catch (InvocationTargetException e4) {
            return false;
        } catch (Exception e5) {
            return false;
        }
    }

    public static void hideKeyBoard(View view) {
        ((InputMethodManager) TutoApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
