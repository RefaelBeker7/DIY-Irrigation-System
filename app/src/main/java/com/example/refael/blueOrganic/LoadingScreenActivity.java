package com.example.refael.blueOrganic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.refael.blueOrganic.model.ProgressBarAnimation;
import com.github.ybq.android.spinkit.style.FadingCircle;

public class LoadingScreenActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.text_view);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        ProgressBar progressBar2 = (ProgressBar)findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar2.setIndeterminateDrawable(fadingCircle);

        progressAnimation();
    }

    private void progressAnimation() {
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar, textView, 0f, 100f);
        anim.setDuration(5000);
        progressBar.setAnimation(anim);
    }
}
