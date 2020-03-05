package net.androidwing.hotxposed;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

/**
 * Created  on 2018/3/30.
 */
public class HotXposed {

  private static final String ACTIVITY_THREAD_CLASSNAME = "android.app.ActivityThread";
  private static final String GET_PACKAGE_MANAGE_METHOD_NAME = "getPackageManage";

  public static void hook(Class clazz, XC_LoadPackage.LoadPackageParam lpparam)
      throws Exception {
    String packageName = clazz.getName().replace("."+clazz.getSimpleName(),"");
    File apkFile = getApkFile(packageName);

    if (apkFile == null) {
      Log.e("error", "apk file not found");
      return;
    }

    filterNotify(lpparam);

    PathClassLoader classLoader =
        new PathClassLoader(apkFile.getAbsolutePath(), lpparam.getClass().getClassLoader());

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
                    param.setResult(new Object());
                  }

                  @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                  }
                });
      }
    }
  }


  private static File getApkFile(String packageName) {
    File apkFile;
    try {
      @SuppressLint("PrivateApi")
      PackageManager pm = (PackageManager) XposedHelpers.callStaticMethod(
              Class.forName(ACTIVITY_THREAD_CLASSNAME),
              GET_PACKAGE_MANAGE_METHOD_NAME
      );
      ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
      apkFile = new File(info.sourceDir);
      return apkFile;
    } catch (ClassNotFoundException | PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }
}
