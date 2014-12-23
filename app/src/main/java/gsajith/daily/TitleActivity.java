package gsajith.daily;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by gsajith on 10/19/2014.
 */
public class TitleActivity extends Activity {

  private ImageView mImageView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);    //Remove title bar
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    //Remove notification bar
    //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    //  WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_title);
  }


  public void startApp(View view) {
    // Do something in response to button
    Intent intent = new Intent(this, MainListActivity.class);
    startActivity(intent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    // Obtain the sharedPreference, default to true if not available
    boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);

    if (isSplashEnabled) {
    } else {
      // if the splash is not enabled, then finish the activity immediately and go to main.
      finish();
      Intent mainIntent = new Intent(TitleActivity.this, MainListActivity.class);
      TitleActivity.this.startActivity(mainIntent);
    }
  }

}
