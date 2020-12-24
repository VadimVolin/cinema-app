package vadim.volin.mobile;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import vadim.volin.mobile.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    /**
     * field for Handler {@link Handler} postDelayed value
     */
    public static final int DELAY_MILLIS = 2000;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FullscreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActivitySplashBinding activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

        Animatable animatable = (Animatable) activitySplashBinding.fullscreenContent.getDrawable();
        animatable.start();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            animatable.stop();
            finish();
        }, DELAY_MILLIS);
    }

}