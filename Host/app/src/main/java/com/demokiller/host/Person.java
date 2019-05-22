package com.demokiller.host;

import com.demokiller.host.utils.PatchProxy;
import com.demokiller.robustpatch.ChangeQuickRedirect;

public class Person {
    public static ChangeQuickRedirect changeQuickRedirect;

    public static String toStr(String str) {
        if (changeQuickRedirect != null) {
            if (PatchProxy.isSupport(new Object[]{str}, null, changeQuickRedirect, true)) {
                return (String) PatchProxy.accessDispatch(new Object[]{str}, null, changeQuickRedirect, true);
            }
        }
        return "aaa";
    }

}