package com.mdzz.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceScreen
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.mdzz.BuildConfig
import com.mdzz.R
import com.mdzz.toast.ToastUtil
import com.mdzz.util.ClipBoardUtil
import com.mdzz.util.PackageUtil.hasInstalledIt
import com.mdzz.util.UriUtil
import java.lang.StringBuilder
import java.net.URLEncoder

@SuppressLint("ValidFragment")
class AboutFragment : BasePreferenceFragment() {

    private var updateInfoDialog: AlertDialog? = null

    private var agreementDialog: AlertDialog? = null

    override fun getXmlId() = R.xml.about_frag_preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPreference()
    }

    fun initPreference() {
        findPreference("about_version").apply {
            title = getString(R.string.version, BuildConfig.VERSION_NAME)
            onPreferenceClickListener = Preference.OnPreferenceClickListener{ _ ->
                ToastUtil.makeToast(title)
                true
            }
        }
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        when (preference.key) {
            "argument" -> {
                showAgreementDialog()
                return true
            }
            "feedback" -> {
                ClipBoardUtil.setClipBoardContent(activity,
                        ClipData.newPlainText("QQ", "2071769694"))
                ToastUtil.makeToast("QQ号已复制到剪贴板")
                return true
            }
            "lanzou" -> {
                UriUtil.openUri(activity, Uri.parse(getString(R.string.url_lanzou)))
                return true
            }
            "pay" -> {
                if (hasInstalledIt(activity, "com.eg.android.AlipayGphone")) {
                    UriUtil.openUri(activity, getAliPayUri())
                } else {
                    ToastUtil.makeToast("请先安装支付宝后重试")
                }
                return true
            }
            "pay_code" -> {
                ClipBoardUtil.setClipBoardContent(activity,
                        ClipData.newPlainText("payCode", "559532805"))
                ToastUtil.makeToast("推广码已复制到剪贴板，请到支付宝应用首页搜索即可领取红包",
                        Toast.LENGTH_LONG)
                return true
            }
            "update_info" -> {
                showUpdateInfoDialog()
                return true
            }
            "run_github" -> {
                UriUtil.openUri(activity, Uri.parse(getString(R.string.url_github)))
                return true
            }
            "thank_QQPurify" -> {
                UriUtil.openUri(activity, Uri.parse(getString(R.string.url_qqpurify_github)))
                return true
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    private fun showUpdateInfoDialog() {
        val dialog = updateInfoDialog ?: AlertDialog.Builder(activity)
                .setTitle("更新日志")
                .setMessage(getString(R.string.update_info_data))
                .setPositiveButton("OK", null)
                .create()
        updateInfoDialog = dialog
        dialog.show()
    }

    private fun showAgreementDialog() {
        val dialog = agreementDialog ?: AlertDialog.Builder(activity)
                .setTitle("免责声明")
                .setMessage(getString(R.string.agrement_info))
                .setPositiveButton("OK", null)
                .create()
        agreementDialog = dialog
        dialog.show()
    }

    private fun getAliPayUri() = Uri.parse(StringBuilder().apply {
        append("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7." +
                "0.0718&qrcode=")
        append(URLEncoder.encode("https://qr.alipay.com/fkx01115ah2xqiekouwoh" +
                "cb?t=1542454581386", "utf-8"))
        append("%3F_s%3Dweb-other&_t=")
        append(System.currentTimeMillis())
    }.toString())

    companion object {
        private const val TAG = "AboutFragment"

        fun newInstance(args: Bundle? = null): AboutFragment {
            return AboutFragment().apply {
                args?.let {
                    this.arguments = it
                }
            }
        }
    }
}