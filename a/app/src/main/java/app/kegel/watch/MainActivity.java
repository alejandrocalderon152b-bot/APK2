package app.kegel.watch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;
    private Vibrator vibrator;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 31) {
            VibratorManager manager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = manager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }

        web = new WebView(this);
        web.setBackgroundColor(0xFF000000);
        web.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        web.setLongClickable(false);
        web.setHapticFeedbackEnabled(false);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);

        web.addJavascriptInterface(new Bridge(), "AndroidWatch");
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());

        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web.destroy();
        super.onDestroy();
    }

    /** Puente que usa la página para vibrar y mantener la pantalla encendida. */
    private class Bridge {

        @JavascriptInterface
        public void vibrate(String pattern) {
            if (vibrator == null || !vibrator.hasVibrator() || pattern == null) {
                return;
            }
            String[] parts = pattern.split(",");
            long[] timings = new long[parts.length + 1];
            timings[0] = 0;
            try {
                for (int i = 0; i < parts.length; i++) {
                    long value = Long.parseLong(parts[i].trim());
                    timings[i + 1] = Math.max(0, Math.min(value, 2000));
                }
            } catch (NumberFormatException e) {
                return;
            }
            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1));
        }

        @JavascriptInterface
        public void keepAwake(final boolean on) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (on) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
            });
        }
    }
}
