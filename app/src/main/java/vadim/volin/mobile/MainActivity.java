package vadim.volin.mobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import vadim.volin.mobile.databinding.ActivityMainBinding;
import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.player.ExoPlayerService;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(activityMainBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Navigation.findNavController(this, R.id.nav_host_fragment);
    }

}