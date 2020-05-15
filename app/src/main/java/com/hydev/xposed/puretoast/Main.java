package com.hydev.xposed.puretoast;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    // These variable are independent of every app.
    private static String lastToastText = "";
    private static long lastToastTime = 0;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Toast.class, "show", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                // This code will get text from a Toast object.
                // @see https://stackoverflow.com/questions/9002706/how-to-get-a-text-from-a-toast-object

                // AOSP ROM
                String displayedText = ((TextView) ((LinearLayout) ((Toast) param.thisObject).getView()).getChildAt(0)).getText().toString();

                /* CustoMIUIer, when toast add icon only.
                String displayedText = ((TextView) ((LinearLayout) ((LinearLayout) ((Toast) param.thisObject)
                        .getView()).getChildAt(1)).getChildAt(0)).getText().toString();  */

                // If the toast display gap is 3s, display it.
                if (System.currentTimeMillis() - lastToastTime > 3000) {
                    lastToastText = displayedText;
                    lastToastTime = System.currentTimeMillis();
                    return;
                }

                if (lastToastText.equals(displayedText)) {
                    // Prevents the call to the original method.
                    // @see XC_MethodHook#setResult(Object)
                    param.setResult(null);
                } else {
                    lastToastText = displayedText;
                }
            }
        });
    }
}
