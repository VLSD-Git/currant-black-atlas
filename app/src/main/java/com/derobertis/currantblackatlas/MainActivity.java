package com.derobertis.currantblackatlas;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.webkit.WebViewAssetLoader;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.lang.ref.WeakReference;

public final class MainActivity extends Activity {
    private static final String HOME_URL =
            "https://appassets.androidplatform.net/assets/index.html";
    private static final int CREATE_DOCUMENT_REQUEST = 4021;

    private WebView webView;
    private String pendingFileData;
    private String pendingFileName;
    private String pendingMimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureSystemBars();

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(3, 1, 4));
        setContentView(webView);
        applyInsets();
        configureWebView();

        if (savedInstanceState == null || webView.restoreState(savedInstanceState) == null) {
            webView.loadUrl(HOME_URL);
        }
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);
        }
    }

    private void applyInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            webView.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets bars = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
                );
                view.setPadding(0, bars.top, 0, bars.bottom);
                return insets;
            });
        } else {
            webView.setOnApplyWindowInsetsListener((view, insets) -> {
                view.setPadding(
                        0,
                        insets.getSystemWindowInsetTop(),
                        0,
                        insets.getSystemWindowInsetBottom()
                );
                return insets;
            });
        }
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setTextZoom(100);

        WebView.setWebContentsDebuggingEnabled(false);

        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler(
                        "/assets/",
                        new WebViewAssetLoader.AssetsPathHandler(this)
                )
                .build();

        webView.setWebViewClient(new LocalWebViewClient(assetLoader, this));
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AppBridge(this), "CurrantApp");
    }

    void beginTextExport(String fileName, String mimeType, String data) {
        pendingFileName = sanitizeFileName(fileName);
        pendingMimeType = mimeType == null || mimeType.isBlank()
                ? "text/plain"
                : mimeType;
        pendingFileData = data == null ? "" : data;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType(pendingMimeType)
                .putExtra(Intent.EXTRA_TITLE, pendingFileName);
        try {
            startActivityForResult(intent, CREATE_DOCUMENT_REQUEST);
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "No file picker is available.", Toast.LENGTH_LONG).show();
        }
    }

    private static String sanitizeFileName(String value) {
        if (value == null || value.isBlank()) return "currant-black-atlas-export.txt";
        return value.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    void openExternalUrl(String url) {
        if (url == null || url.isBlank()) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "No app can open this link.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CREATE_DOCUMENT_REQUEST || resultCode != RESULT_OK
                || data == null || data.getData() == null) {
            return;
        }
        Uri target = data.getData();
        try (OutputStream stream = getContentResolver().openOutputStream(target, "w")) {
            if (stream == null) throw new IllegalStateException("Unable to open destination");
            stream.write(pendingFileData.getBytes(StandardCharsets.UTF_8));
            stream.flush();
            Toast.makeText(this, "Saved " + pendingFileName, Toast.LENGTH_SHORT).show();
        } catch (Exception error) {
            Toast.makeText(this, "Could not save the export.", Toast.LENGTH_LONG).show();
        } finally {
            pendingFileData = null;
            pendingFileName = null;
            pendingMimeType = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript(
                "(function(){if(document.body.classList.contains('menu-open')){" +
                        "document.body.classList.remove('menu-open');return 'closed';}" +
                        "return 'open';})()",
                result -> {
                    if ("\"closed\"".equals(result)) return;
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        MainActivity.super.onBackPressed();
                    }
                }
        );
    }

    private static final class LocalWebViewClient extends WebViewClient {
        private final WebViewAssetLoader assetLoader;
        private final WeakReference<MainActivity> activityReference;

        LocalWebViewClient(WebViewAssetLoader loader, MainActivity activity) {
            assetLoader = loader;
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(
                WebView view,
                WebResourceRequest request
        ) {
            return assetLoader.shouldInterceptRequest(request.getUrl());
        }

        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view,
                WebResourceRequest request
        ) {
            return route(request.getUrl());
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return route(Uri.parse(url));
        }

        private boolean route(Uri uri) {
            if ("appassets.androidplatform.net".equals(uri.getHost())) {
                return false;
            }
            MainActivity activity = activityReference.get();
            if (activity != null) activity.openExternalUrl(uri.toString());
            return true;
        }
    }

    private static final class AppBridge {
        private final WeakReference<MainActivity> activityReference;

        AppBridge(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @JavascriptInterface
        public void saveText(String fileName, String mimeType, String data) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.runOnUiThread(
                        () -> activity.beginTextExport(fileName, mimeType, data)
                );
            }
        }

        @JavascriptInterface
        public void openUrl(String url) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.runOnUiThread(() -> activity.openExternalUrl(url));
            }
        }
    }
}
