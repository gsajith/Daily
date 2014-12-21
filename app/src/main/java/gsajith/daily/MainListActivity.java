package gsajith.daily;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;


public class MainListActivity extends FragmentActivity{

  private int mDragStartMode = DragSortController.ON_DOWN;
  private boolean mRemoveEnabled = true;
  private int mRemoveMode = DragSortController.FLING_REMOVE;
  private boolean mSortEnabled = false;
  private boolean mDragEnabled = true;
  private int mNumHeaders = 0;
  private int mNumFooters = 0;
  private DSLVFragment mDSLV;
  private FrameLayout undo_banner;
  private boolean undo_shown = false;

  private String mTag = "dslvTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main_list);
      if (savedInstanceState == null) {
        mDSLV = (DSLVFragmentClicks) getNewDslvFragment();
        undo_banner = (FrameLayout) findViewById(R.id.undo_banner);
        getSupportFragmentManager().beginTransaction().add(R.id.main_list, mDSLV, mTag).commit();
      }
    }

    private Fragment getNewDslvFragment() {
        DSLVFragmentClicks f = DSLVFragmentClicks.newInstance(mNumHeaders, mNumFooters);
        f.removeMode = mRemoveMode;
        f.removeEnabled = mRemoveEnabled;
        f.dragStartMode = mDragStartMode;
        f.sortEnabled = mSortEnabled;
        f.dragEnabled = mDragEnabled;
        return f;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_add) {
          addTask(null);
          return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTask(View view) {
      Intent intent = new Intent(this, EditTaskDialog.class);
      startActivity(intent);
    }

  public void undo(View v) {
      if (mDSLV != null) {
        mDSLV.undo();
        if (mDSLV.canUndo()) {
          showUndo();
        }
      }
    }
    public boolean isUndoShown() {
      return undo_shown;
    }

    public void showUndo() {
      undo_shown = true;
      undo_banner.setVisibility(View.VISIBLE);
      Animation fadeIn = new AlphaAnimation(0, 1);
      fadeIn.setDuration(300);
      Animation fadeOut = new AlphaAnimation(1, 0);
      fadeOut.setStartOffset(2000);
      fadeOut.setDuration(3000);

      AnimationSet animation = new AnimationSet(true);
      animation.addAnimation(fadeIn);
      if (!mDSLV.permShowUndo()) {
        animation.addAnimation(fadeOut);
        final Handler handler = new Handler ();
        final Timer timer = new Timer();
        timer.schedule (new TimerTask(){
          public void run (){
            handler.post (new Runnable (){
              public void run (){
                killUndoBanner();
              }
            });
          }
        }, 5);
      }
      undo_banner.setAnimation(animation);

    }

    private void killUndoBanner() {
      undo_shown = false;
      undo_banner.setVisibility(View.GONE);
    }
}
