package com.demokiller.host;

import android.annotation.SuppressLint;

import com.demokiller.host.utils.PatchProxy;
import com.demokiller.robustpatch.ChangeQuickRedirect;

import java.util.ArrayList;
import java.util.List;

public class MoneyBean {
	public static ChangeQuickRedirect changeQuickRedirect;

	public static String desc() {
		if(changeQuickRedirect != null){
			if(PatchProxy.isSupport(new Object[0], null, changeQuickRedirect, true)){
				return (String) PatchProxy.accessDispatch(new Object[0], null, changeQuickRedirect, true);
			}
		}
		return "MoneyBean";
	}

	@SuppressLint("UseValueOf")
	@SuppressWarnings("unchecked")
	public List<String> getInfo(String str, float f, int i, List<String> list) {
		if (changeQuickRedirect != null) {
			if (PatchProxy.isSupport(new Object[]{str, new Float(f), new Integer(i), list}, this, changeQuickRedirect, false)) {
				return (List<String>) PatchProxy.accessDispatch(new Object[]{str, new Float(f), new Integer(i), list}, this, changeQuickRedirect, false);
			}
		}
		return new ArrayList<String>();
	}

	public int getMoneyValue() {
		if(changeQuickRedirect != null){
			if(PatchProxy.isSupport(new Object[0], this, changeQuickRedirect, false)){
				return ((Integer) PatchProxy.accessDispatch(new Object[0], this, changeQuickRedirect, false)).intValue();
			}
		}
		return 10;
	}
}