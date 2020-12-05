package vadim.volin.tv.activity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import vadim.volin.tv.databinding.ActivityMainBinding;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
    }

}