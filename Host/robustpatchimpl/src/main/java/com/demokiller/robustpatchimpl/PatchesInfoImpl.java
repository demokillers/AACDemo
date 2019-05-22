package com.demokiller.robustpatchimpl;

import com.demokiller.robustpatch.PatchedClassInfo;
import com.demokiller.robustpatch.PatchesInfo;

import java.util.ArrayList;
import java.util.List;

public class PatchesInfoImpl implements PatchesInfo {
    public List<PatchedClassInfo> getPatchedClassesInfo() {
        List<PatchedClassInfo> patchedClassesInfos = new ArrayList<PatchedClassInfo>();
        PatchedClassInfo patchedClass = new PatchedClassInfo(
                "com.demokiller.host.MoneyBean",
                MoneyBeanStatePatch.class.getCanonicalName());
        patchedClassesInfos.add(patchedClass);
        return patchedClassesInfos;
    }
}
