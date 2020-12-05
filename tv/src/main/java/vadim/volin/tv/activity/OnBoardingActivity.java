package vadim.volin.tv.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;

import vadim.volin.tv.databinding.ActivityOnBoardingBinding;

public class OnBoardingActivity extends Activity {

    /**
     * field for Handler {@link Handler} postDelayed value
     */
    public static final int DELAY_MILLIS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOnBoardingBinding activityOnBoardingBinding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(activityOnBoardingBinding.getRoot());

        Animatable animatable = (Animatable) activityOnBoardingBinding.splashImg.getDrawable();
        animatable.start();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(OnBoardingActivity.this, MainActivity.class));
            animatable.stop();
            finish();
        }, DELAY_MILLIS);
    }
}