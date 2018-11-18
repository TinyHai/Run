package com.mdzz.activity

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.mdzz.run.BuildConfig
import com.mdzz.run.R
import com.mdzz.toast.ToastUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.net.URLEncoder
import android.content.Intent


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "MainActivity"

    private var agreementDialog: AlertDialog.Builder? = null

    private var updateInfoDialog: AlertDialog.Builder? = null

    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        initView()
        initListener()
    }

    private fun initView() {
        if (isActive()) {
            with(isActiveImage) {
                setBackgroundColor(resources.getColor(R.color.is_active_color))
            }
            with(isActiveText) {
                text = resources.getString(R.string.is_active)
                setTextColor(resources.getColor(R.color.is_active_color))
            }
        }
        with(versionTV) {
            text = getString(R.string.version, BuildConfig.VERSION_NAME)
        }
    }

    private fun initListener() {
        isActiveCV.setOnClickListener(this)
        agreementCV.setOnClickListener(this)
        feedbackCV.setOnClickListener(this)
        clouddiskCV.setOnClickListener(this)
        payCV.setOnClickListener(this)
        payCodeCV.setOnClickListener(this)
        updateInfoCV.setOnClickListener(this)
        githubCV.setOnClickListener(this)
    }

    private fun isActive() = false

    override fun onClick(v: View) {
        when (v.id) {
            R.id.isActiveCV -> openXposedInstaller()

            R.id.agreementCV -> showAgreementDialog()

            R.id.feedbackCV -> {
                clipboardManager.primaryClip = ClipData.newPlainText("QQ", "2071769694")
                ToastUtil.makeToast(this, "QQ号已复制到剪贴板")
            }

            R.id.clouddiskCV -> openUri(Uri.parse(getString(R.string.url_clouddisk)))

            R.id.payCV -> {
                if (hasInstalledIt("com.eg.android.AlipayGphone")) {
                    openUri(getAliPayUri())
                } else {
                    ToastUtil.makeToast(this, "请先安装支付宝后重试")
                }
            }

            R.id.payCodeCV -> {
                clipboardManager.primaryClip = ClipData.newPlainText("payCode", "559532805")
                ToastUtil.makeToast(this, "推广码已复制到剪贴板，请到支付宝应用首页搜索即可领取红包",
                        Toast.LENGTH_LONG)
            }

            R.id.githubCV -> openUri(Uri.parse(getString(R.string.url_github)))

            R.id.updateInfoCV -> showUpdateInfoDialog()
        }
    }

    private fun showAgreementDialog() = with(agreementDialog) {
        if (this == null) {
            agreementDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("免责声明")
                    .setMessage(getString(R.string.agrement_info))
                    .setPositiveButton("OK", null)
            agreementDialog
        } else {
            this
        }
    }?.show()

    private fun getAliPayUri() = Uri.parse(StringBuilder().apply {
        append("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7." +
                "0.0718&qrcode=")
        append(URLEncoder.encode("https://qr.alipay.com/fkx01115ah2xqiekouwoh" +
                "cb?t=1542454581386", "utf-8"))
        append("%3F_s%3Dweb-other&_t=")
        append(System.currentTimeMillis())
    }.toString())

    private fun showUpdateInfoDialog() = with(updateInfoDialog) {
        if (this == null) {
            updateInfoDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("更新日志")
                    .setMessage(getString(R.string.update_info_data))
                    .setPositiveButton("OK", null)
            updateInfoDialog
        } else {
            this
        }
    }?.show()

    private fun openUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }

    private fun openXposedInstaller() {
        if (hasInstalledIt("de.robv.android.xposed.installer")) {
            val intent = Intent()
            intent.let {
                it.setComponent(ComponentName("de.robv.android.xposed.installer",
                        "de.robv.android.xposed.installer.WelcomeActivity"))
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("fragment", 1)
                startActivity(it)
            }
        } else {
            ToastUtil.makeToast(this, "请先安装XposedInstaller后重试")
        }
    }

    private fun hasInstalledIt(pkgName: String) = try {
        packageManager.getPackageInfo(pkgName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    private fun setAliasStatus() {
        if (isAliasHide()) {
            packageManager.setComponentEnabledSetting(getAliasComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
        } else {
            packageManager.setComponentEnabledSetting(getAliasComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP)
        }
    }

    @SuppressLint("SwitchIntDef")
    private fun isAliasHide() = when (packageManager.getComponentEnabledSetting(
            getAliasComponentName())) {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> false
        else -> true
    }

    private fun getAliasComponentName() = ComponentName(this,
            "com.mdzz.activity.MainActivityAlias")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.icon_hide_mene).apply {
            if (isAliasHide()) {
                title = getString(R.string.view_icon)
            } else {
                title = getString(R.string.hide_icon)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.icon_hide_mene -> {
                if (isAliasHide()) {
                    item.title = getString(R.string.hide_icon)
                } else {
                    item.title = getString(R.string.view_icon)
                }
                setAliasStatus()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
