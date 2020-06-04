package net.androidwing.hotxposed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.util.Log;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

/**
 * Created  on 2018/3/30.
 */
public class HotXposed {

  private static final String ACTIVITY_THREAD_CLASSNAME = "android.app.ActivityThread";
  private static final String CURRENT_ACTIVITY_THREAD = "currentActivityThread";
  private static final String GET_SYSTEM_CONTEXT = "getSystemContext";

  public static void hook(Class clazz, String packageName, XC_LoadPackage.LoadPackageParam lpparam)
      throws Exception {
    File apkFile = getApkFile(packageName);

    if (apkFile == null) {
      Log.e("error", "apk file not found");
      return;
    }

    filterNotify(lpparam);

    PathClassLoader classLoader =
        new PathClassLoader(apkFile.getAbsolutePath(), XposedBridge.BOOTCLASSLOADER);

    XposedHelpers.callMethod(classLoader.loadClass(clazz.getName()).newInstance(), "dispatch",lpparam);
  }

  private static void filterNotify(XC_LoadPackage.LoadPackageParam lpparam)
      throws ClassNotFoundException {
    String[] xpPackageNames = {
            "de.robv.android.xposed.installer",
            "com.solohsu.android.edxp.manager",
            "org.meowcat.edxposed.manager"
    };
    for (String xpPackageName : xpPackageNames) {
      if(xpPackageName.equals(lpparam.packageName)){
        XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass(xpPackageName + ".util.NotificationUtil"),
                "showModulesUpdatedNotification", new XC_MethodHook() {
                  @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(null);
                  }

                  @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                  }
                });
        break;
      }
    }
  }


  private static File getApkFile(String packageName) {
    File apkFile;
    try {
      @SuppressLint("PrivateApi")
      Object activityThread = XposedHelpers.callStaticMethod(
              Class.forName(ACTIVITY_THREAD_CLASSNAME),
              CURRENT_ACTIVITY_THREAD
      );
      Context context = (Context) XposedHelpers.callMethod(activityThread, GET_SYSTEM_CONTEXT);
      ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
      apkFile = new File(info.sourceDir);
      XposedBridge.log(apkFile.getAbsolutePath());
    } catch (Throwable th) {
      th.printStackTrace();
      XposedBridge.log(th);
      apkFile = null;
    }

    return apkFile;
  }
}
