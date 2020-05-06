package com.yu.lib.common.aspect;

import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.yu.lib.annotations.aspect.DynaPermission;
import com.yu.lib.common.bundles.monitor.MAEMonitorFragment;
import com.yu.lib.common.bundles.monitor.MAEPermissionCallback;
import com.yu.lib.common.bundles.monitor.MAEPermissionRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
public class DynaPermissionAspect {

    @Around("execution(@com.yu.lib.annotations.aspect.DynaPermission * *(..)) && @annotation(permission)")
    public void dynaPermission(ProceedingJoinPoint joinPoint, DynaPermission permission) {
        Object object = joinPoint.getThis();
        MAEPermissionRequest request;
        FragmentActivity activity;
        if (object instanceof View) {
            activity = (FragmentActivity) ((View) object).getContext();
            request = MAEMonitorFragment.getInstance(activity);
        } else if (object instanceof FragmentActivity) {
            activity = (FragmentActivity) object;
            request = MAEMonitorFragment.getInstance(activity);
        } else if (object instanceof Fragment) {
            activity = ((Fragment) object).getActivity();
            request = MAEMonitorFragment.getInstance(((Fragment) object));
        } else if(object instanceof View.OnClickListener || object instanceof View.OnLongClickListener) {
            View view = (View) (joinPoint.getArgs()[0]);
            activity = (FragmentActivity) view.getContext();
            request = MAEMonitorFragment.getInstance(activity);
        } else {
            throw new RuntimeException("You must use @DynaPermission in Fragment/FragmentActivity/View");
        }

        List<String> permissionList = new ArrayList<>();
        if(permission.values().length > 0) {
            permissionList.addAll(Arrays.asList(permission.values()));
        } else {
            permissionList.add(permission.value());
        }

        if(permissionList.size() == 0) {
            return;
        }

        if(TextUtils.isEmpty(permission.failInfo())) {
            request.requestPermission((String[]) permissionList.toArray(), new MAEPermissionCallback() {
                @Override
                public void onPermissionApplySuccess() {
                    try {
                        joinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                @Override
                public void onPermissionApplyFailure(List<String> notGrantedPermissions, List<Boolean> shouldShowRequestPermissions) {

                }
            });
        } else {
            request.requestPermissionWithFailDialog((String[]) permissionList.toArray(), permission.failInfo(), (dialog, which) -> {
                if(permission.isFailDialogClickFinishActivity() && activity != null) {
                    activity.finish();
                } else {
                    dialog.dismiss();
                }
            }, new MAEPermissionCallback() {
                @Override
                public void onPermissionApplySuccess() {
                    try {
                        joinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                @Override
                public void onPermissionApplyFailure(List<String> notGrantedPermissions, List<Boolean> shouldShowRequestPermissions) {

                }
            });
        }
    }

}
