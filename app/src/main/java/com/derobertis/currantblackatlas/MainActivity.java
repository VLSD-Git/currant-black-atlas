package com.derobertis.currantblackatlas;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private static final String HOME_URL = "file:///android_asset/index.html";
    private static final int CREATE_DOCUMENT_REQUEST = 4021;

    private LinearLayout root;
    private WebView webView;
    private String pendingFileData;
    private String pendingFileName;
    private String pendingMimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(3, 1, 4));
        getWindow().setNavigationBarColor(Color.rgb(3, 1, 4));

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(3, 1, 4));
        setContentView(root);

        try {
            createAndLoadWebView(savedInstanceState);
        } catch (Throwable error) {
            showDiagnosticScreen("The app could not start.", error);
        }
    }

    private void createAndLoadWebView(Bundle savedInstanceState) {
        if (webView != null) {
            root.removeView(webView);
            webView.destroy();
        }

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(3, 1, 4));
        webView.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false);

        root.removeAllViews();
        root.addView(
                webView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setTextZoom(100);
        settings.setSafeBrowsingEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        WebView.setWebContentsDebuggingEnabled(false);
        webView.setWebViewClient(new AtlasWebViewClient(this));
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AppBridge(this), "CurrantApp");

        boolean restored = savedInstanceState != null
                && webView.restoreState(savedInstanceState) != null;
        if (!restored) {
            webView.loadUrl(HOME_URL);
        }
    }

    private void showDiagnosticScreen(String title, Throwable error) {
        String detail = error == null
                ? "No diagnostic detail was provided."
                : error.getClass().getName() + ": " + String.valueOf(error.getMessage());

        root.removeAllViews();
        root.setPadding(48, 72, 48, 48);

        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextColor(Color.rgb(247, 238, 249));
        heading.setTextSize(24);
        heading.setPadding(0, 0, 0, 24);
        root.addView(heading);

        TextView message = new TextView(this);
        message.setText(
                "Currant [Black] Atlas recovered instead of closing.\n\n"
                        + detail
                        + "\n\nTap Try Again. If it still fails, copy this diagnostic and send it to Ada."
        );
        message.setTextColor(Color.rgb(224, 180, 255));
        message.setTextSize(16);
        message.setPadding(0, 0, 0, 32);
        root.addView(message);

        Button retry = new Button(this);
        retry.setText("Try Again");
        retry.setOnClickListener(view -> {
            try {
                createAndLoadWebView(null);
            } catch (Throwable retryError) {
                showDiagnosticScreen("The app still could not start.", retryError);
            }
        });
        root.addView(retry);

        Button copy = new Button(this);
        copy.setText("Copy Diagnostic");
        copy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Currant Atlas diagnostic", detail));
            Toast.makeText(this, "Diagnostic copied", Toast.LENGTH_SHORT).show();
        });
        root.addView(copy);
    }

    void beginTextExport(String fileName, String mimeType, String data) {
        pendingFileName = sanitizeFileName(fileName);
        pendingMimeType = mimeType == null || mimeType.trim().isEmpty()
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
        if (value == null || value.trim().isEmpty()) {
            return "currant-black-atlas-export.txt";
        }
        return value.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    void openExternalUrl(String url) {
        if (url == null || url.trim().isEmpty()) return;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "No app can open this link.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CREATE_DOCUMENT_REQUEST
                || resultCode != RESULT_OK
                || data == null
                || data.getData() == null) {
            return;
        }

        try (OutputStream stream = getContentResolver().openOutputStream(data.getData(), "w")) {
            if (stream == null) throw new IllegalStateException("Unable to open destination");
            stream.write((pendingFileData == null ? "" : pendingFileData)
                    .getBytes(StandardCharsets.UTF_8));
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
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (webView == null) {
            super.onBackPressed();
            return;
        }

        webView.evaluateJavascript(
                "(function(){if(document.body&&document.body.classList.contains('menu-open')){"
                        + "document.body.classList.remove('menu-open');return 'closed';}"
                        + "return 'open';})()",
                result -> {
                    if ("\"closed\"".equals(result)) return;
                    if (webView != null && webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        MainActivity.super.onBackPressed();
                    }
                }
        );
    }

    private static final class AtlasWebViewClient extends WebViewClient {
        private final WeakReference<MainActivity> activityReference;

        AtlasWebViewClient(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return route(request.getUrl());
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return route(Uri.parse(url));
        }

        private boolean route(Uri uri) {
            String scheme = uri.getScheme();
            if ("file".equalsIgnoreCase(scheme) || "about".equalsIgnoreCase(scheme)) {
                return false;
            }

            MainActivity activity = activityReference.get();
            if (activity != null) activity.openExternalUrl(uri.toString());
            return true;
        }

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            MainActivity activity = activityReference.get();
            if (activity == null) return true;

            activity.webView = null;
            view.destroy();
            activity.showDiagnosticScreen(
                    detail.didCrash()
                            ? "Android WebView stopped unexpectedly."
                            : "Android reclaimed the WebView process.",
                    new IllegalStateException("Renderer priority: " + detail.rendererPriorityAtExit())
            );
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
