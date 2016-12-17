package homeseek.app.android.capstonehomeseek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by pluckannfeel on 5/28/2016.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        final ImageView iv = (ImageView) findViewById(R.id.imageView);

        //edittext
        final Animation slidebot = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_slide_in_bottom);

        //imageview
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation);
        final Animation fadeout = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        //first execution
        iv.startAnimation(an);

        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(fadeout);
                finish();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
