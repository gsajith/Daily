package gsajith.daily;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by gsajith on 10/19/2014.
 */
public class DragSortListAdapter extends BaseAdapter {
  private final List<ListItemModel> checklist;
  private final Activity context;
  private LayoutInflater inflater;
  private final String PACKAGE = "DSLA";
  public boolean dialogClickEnabled = true;

  FragmentManager fm;

  public DragSortListAdapter(Activity context, List<ListItemModel> list) {
    this.context = context;
    fm = context.getFragmentManager();
    this.checklist = list;
  }

  @Override
  public int getCount() {
    return checklist.size();
  }

  @Override
  public ListItemModel getItem(int position) {
    return checklist.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public void showEmptyPage(Activity activity) {
    RelativeLayout emptyPage = (RelativeLayout) activity.findViewById(R.id.empty);
    if (emptyPage != null) {
      emptyPage.setVisibility(View.VISIBLE);
      Animation fadeIn = new AlphaAnimation(0, 1);
      fadeIn.setDuration(500);

      AnimationSet animation = new AnimationSet(true);
      animation.addAnimation(fadeIn);
      emptyPage.setAnimation(animation);
    }

  }

  public void hideEmptyPage(Activity activity) {
    RelativeLayout emptyPage = (RelativeLayout) activity.findViewById(R.id.empty);
    if (emptyPage != null) {
      emptyPage.setVisibility(View.GONE);
    }
  }
  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    if (inflater == null) {
      inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.checklist_item, null);
    }
    LinearLayout dayList = (LinearLayout) convertView.findViewById(R.id.days_selected);
    TextView nameView = (TextView) convertView.findViewById(R.id.nameText);
    View checked = convertView.findViewById(R.id.checked);
    View checkBox = convertView.findViewById(R.id.checkboxview);
    View checkedOverlay = convertView.findViewById(R.id.checkedoverlay);

    View checkBig = convertView.findViewById(R.id.checkbig);
    View checkBigShadow = convertView.findViewById(R.id.checkbigshadow);
    View checkSmall = convertView.findViewById(R.id.checksmall);
    View checkSmallShadow = convertView.findViewById(R.id.checksmallshadow);

    checkBox.setTag(checklist.get(position));
    checkBox.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view) {
        ListItemModel item = (ListItemModel) view.getTag();
        item.setDone(!item.isDone());
        notifyDataSetChanged();
      }
    });
    final ListItemModel m = checklist.get(position);
    if (m.isVisible()) {
      convertView.setVisibility(View.VISIBLE);
    } else {
      convertView.setVisibility(View.GONE);
    }
    ListItemModel.Color c = m.getColor();
    final int mainColor;
    final int shadowColor;
    switch(c) {
      case LIGHTBLUE:
        mainColor = R.color.lightblue;
        shadowColor = R.color.lightblueshadow;
        break;
      case DARKBLUE:
        mainColor = R.color.blue;
        shadowColor = R.color.blueshadow;
        break;
      case PURPLE:
        mainColor = R.color.purple;
        shadowColor = R.color.purpleshadow;
        break;
      case RED:
        mainColor = R.color.red;
        shadowColor = R.color.redshadow;
        break;
      case YELLOW:
        mainColor = R.color.yellow;
        shadowColor = R.color.yellowshadow;
        break;
      case ORANGE:
        mainColor = R.color.orange;
        shadowColor = R.color.orangeshadow;
        break;
      case LIGHTGREEN:
        mainColor = R.color.lightgreen;
        shadowColor = R.color.lightgreenshadow;
        break;
      default:
        mainColor = R.color.lightgreen;
        shadowColor = R.color.lightblueshadow;
        break;
    }
    View checkShadow1 = convertView.findViewById(R.id.checklistbottom);
    View checkShadow2 = convertView.findViewById(R.id.checklistright);
    View mainShadow1 = convertView.findViewById(R.id.mainviewbottom);
    View mainShadow2 = convertView.findViewById(R.id.mainviewright);
    checkShadow1.setBackgroundColor(context.getResources().getColor(shadowColor));
    checkShadow2.setBackgroundColor(context.getResources().getColor(shadowColor));
    mainShadow1.setBackgroundColor(context.getResources().getColor(shadowColor));
    mainShadow2.setBackgroundColor(context.getResources().getColor(shadowColor));
    checkBigShadow.setBackgroundColor(context.getResources().getColor(shadowColor));
    checkSmallShadow.setBackgroundColor(context.getResources().getColor(shadowColor));
    View mainBox = convertView.findViewById(R.id.mainview);
    View editButton = convertView.findViewById(R.id.editbutton);
    mainBox.setBackgroundColor(context.getResources().getColor(mainColor));
    checkBig.setBackgroundColor(context.getResources().getColor(mainColor));
    checkSmall.setBackgroundColor(context.getResources().getColor(mainColor));
    final View finalConvertView = convertView;
    editButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (dialogClickEnabled) {
          dialogClickEnabled = false;
          int[] screenLocation = new int[2];
          finalConvertView.getLocationOnScreen(screenLocation);
          Bundle bundle = new Bundle();
          bundle.putInt(PACKAGE + ".left", screenLocation[0]);
          bundle.putInt(PACKAGE+".top", screenLocation[1]);
          bundle.putInt(PACKAGE+".mainColor", mainColor);
          bundle.putInt(PACKAGE+".shadowColor", shadowColor);
          bundle.putString(PACKAGE + ".name", m.getName());
          bundle.putBoolean(PACKAGE + ".done", m.isDone());
          bundle.putInt(PACKAGE+".itemNumber", position);
          bundle.putBooleanArray(PACKAGE+".daysChecked", m.getDays());
          bundle.putBoolean(PACKAGE+".notify", m.shouldNotify());
          EditTaskDialog dialog = new EditTaskDialog(bundle, DragSortListAdapter.this);
          dialog.show(fm, "Dialog fragment");
        }
      }
    });
    nameView.setText(m.getName());
    updateDays(dayList, m.getDays());
    if (m.isDone()) {
      checked.setVisibility(View.VISIBLE);
      checkedOverlay.setVisibility(View.VISIBLE);
    } else {
      checked.setVisibility(View.GONE);
      checkedOverlay.setVisibility(View.GONE);
    }

    return convertView;
  }

  private void updateDays(LinearLayout dayList, boolean[] days) {
    for (int i = 0; i < days.length; i++) {
      if (days[i]) {
        ((TextView)dayList.getChildAt(i)).setTextColor(
          context.getResources().getColor(R.color.white)
        );
      } else {
        ((TextView)dayList.getChildAt(i)).setTextColor(
          context.getResources().getColor(R.color.halfwhite)
        );
      }
    }
  }

  public void revealItem(int itemNum) {
    this.getItem(itemNum).setVisible(true);
    notifyDataSetChanged();
  }

  public void hideItem(int itemNum) {
    this.getItem(itemNum).setVisible(false);
    notifyDataSetChanged();
  }
}
