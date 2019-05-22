package com.demokiller.host;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.demokiller.robustpatch.ChangeQuickRedirect;
import com.demokiller.robustpatch.PatchedClassInfo;
import com.demokiller.robustpatch.PatchesInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.DexClassLoader;

public class MainApplication extends Application{
	
	private DexClassLoader mLoader;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void attachBaseContext(Context base) {
		boolean isSucc = loadDex(base);
		if(isSucc){
			patch();
		}
		super.attachBaseContext(base);
	}
	
	@SuppressLint({ "SdCardPath"})
	private boolean loadDex(Context ctx){
		File dexFile = new File("/sdcard/patch.dex");
		if(!dexFile.exists()){
			return false;
		}
		try{
			File odexDir = new File(ctx.getFilesDir()+File.separator+"odex"+File.separator);
			if(!odexDir.exists()){
				odexDir.mkdirs();
			}
			mLoader = new DexClassLoader(dexFile.getAbsolutePath(), odexDir.getAbsolutePath(), null, ctx.getClassLoader());
			return true;
		}catch(Throwable e){
		}
		return false;
	}
	
	@SuppressLint("NewApi")
	private void patch(){
		try{
			Class<?> patchInfoClazz = mLoader.loadClass("cn.wjdiankong.patchimpl.PatchesInfoImpl");
			PatchesInfo patchInfo = (PatchesInfo)patchInfoClazz.newInstance();
			List<PatchedClassInfo> infoList = patchInfo.getPatchedClassesInfo();
			for(PatchedClassInfo info : infoList){
				ChangeQuickRedirect redirectObj = (ChangeQuickRedirect)mLoader.loadClass(
						info.getPatchClassName()).newInstance();
				Class<?> fixClass = mLoader.loadClass(info.getFixClassName());
				Field redirectF = fixClass.getField("changeQuickRedirect");
				redirectF.set(null, redirectObj);
			}
		}catch(Throwable e){

		}
	}
	
}
