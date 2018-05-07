package cc.cuichanghao.infivt

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Example Fragment class that shows an identifier inside a TextView.
 */
class CachedWebFragment : Fragment() {

    private var identifier: Int = 0

    companion object {
        val map = listOf(
                "google" to "https://google.com",
                "yahoo" to "https://yahoo.com",
                "news" to "https://news.google.com",
                "wikipedia" to "https://wikipedia.org",
                "amazon" to "https://amazon.com"

        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        identifier = args!!.getInt("identifier")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<View>(R.id.app_webView) as WebView

        val settings = webView.settings
        settings.builtInZoomControls = false
        settings.useWideViewPort = false
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.setSupportZoom(false)

        webView.webViewClient = object : WebViewClient() {

        }
        webView.loadUrl(map[identifier].second)
    }
}
