package gsajith.daily;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class EditTaskDialog extends DialogFragment {
  private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
  private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
  private static final int ANIM_DURATION = 200;
  private final String PACKAGE = "DSLA";
  RelativeLayout checkListItem;
  LinearLayout nameForm;
  LinearLayout colorForm;
  LinearLayout dayForm;
  RelativeLayout notifyForm;
  LinearLayout colorList;
  LinearLayout dayList;
  LinearLayout dayTextList;
  LinearLayout submitForm;
  View checkBig;
  View checkSmall;
  View mainBox;
  View checkBigShadow;
  View checkSmallShadow;
  View checkShadow1;
  View checkShadow2;
  View mainShadow1;
  View mainShadow2;
  Button saveButton;
  Button cancelButton;
  EditText nameEnter;
  Bundle mBundle;
  DragSortListAdapter mAdapter;
  private ColorDrawable mBackground = new ColorDrawable(Color.BLACK);
  private int mLeftDelta = 0;
  private int mTopDelta = 0;
  private ArrayList<Boolean> daysChecked;
  private boolean shouldNotify = false;
  private boolean[] isDone;
  private Dialog dialog;
  private int listItemNumber;
  private int selectedColor = 0;
  private int clickedColorID = 0;

  public EditTaskDialog() {

  }

  public EditTaskDialog(Bundle bundle, DragSortListAdapter adapter) {
    mBundle = bundle;
    mAdapter = adapter;
    daysChecked = new ArrayList<Boolean>();
    daysChecked.add(false);
    daysChecked.add(false);
    daysChecked.add(false);
    daysChecked.add(false);
    daysChecked.add(false);
    daysChecked.add(false);
    daysChecked.add(false);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);

  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    dialog = super.onCreateDialog(savedInstanceState);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    dialog.setOnKeyListener(new Dialog.OnKeyListener() {
      @Override
      public boolean onKey(final DialogInterface dialog, int keyCode,
                           KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          runExitAnimation(new Runnable() {
            @Override
            public void run() {
              dialog.cancel();
            }
          });
          return true;
        }
        return false;
      }
    });

    return dialog;
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    mAdapter.dialogClickEnabled = true;
  }

  private int getColorNum(int mainColor) {
    switch (mainColor) {
      case ((int) R.color.blue):
        return 1;
      case ((int) R.color.lightblue):
        return 2;
      case ((int) R.color.lightgreen):
        return 3;
      case ((int) R.color.yellow):
        return 4;
      case ((int) R.color.orange):
        return 5;
      case ((int) R.color.red):
        return 6;
      case ((int) R.color.purple):
        return 7;
      default:
        return 1;
    }
  }

  private int getSaveButtonDrawable(int mainColor) {
    switch (mainColor) {
      case ((int) R.color.blue):
        return R.drawable.save_button1;
      case ((int) R.color.lightblue):
        return R.drawable.save_button2;
      case ((int) R.color.lightgreen):
        return R.drawable.save_button3;
      case ((int) R.color.yellow):
        return R.drawable.save_button4;
      case ((int) R.color.orange):
        return R.drawable.save_button5;
      case ((int) R.color.red):
        return R.drawable.save_button6;
      case ((int) R.color.purple):
        return R.drawable.save_button7;
      default:
        return 1;
    }
  }

  private void initializeDayList(LinearLayout dayList, LinearLayout dayTextList) {
    for (int i = 0; i < 7; i++) {
      final int finalI = i;
      Log.e("CHILDNUM", i + "");
      if (daysChecked.get(i)) {
        dayList.getChildAt(i).findViewById(R.id.selected_bar).
          setBackgroundColor(getResources().getColor(clickedColorID));
        ((TextView) dayTextList.getChildAt(i)).setTextColor(
          getResources().getColor(R.color.white)
        );
      } else {
        dayList.getChildAt(i).findViewById(R.id.selected_bar).
          setBackgroundColor(getResources().getColor(R.color.background));
        ((TextView) dayTextList.getChildAt(i)).setTextColor(
          getResources().getColor(R.color.halfwhite)
        );
      }
      dayList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          clickedDay(finalI + 1);
        }
      });
    }
    ((TextView) dayList.getChildAt(0).findViewById(R.id.selected_box)).setText("S");
    ((TextView) dayList.getChildAt(1).findViewById(R.id.selected_box)).setText("M");
    ((TextView) dayList.getChildAt(2).findViewById(R.id.selected_box)).setText("T");
    ((TextView) dayList.getChildAt(3).findViewById(R.id.selected_box)).setText("W");
    ((TextView) dayList.getChildAt(4).findViewById(R.id.selected_box)).setText("T");
    ((TextView) dayList.getChildAt(5).findViewById(R.id.selected_box)).setText("F");
    ((TextView) dayList.getChildAt(6).findViewById(R.id.selected_box)).setText("S");
  }

  private void updateDayListColor() {
    for (int i = 0; i < 7; i++) {
      if (daysChecked.get(i)) {
        ((TextView) dayList.getChildAt(i).findViewById(R.id.selected_box)).
          setTextColor(getResources().getColor(clickedColorID));
        dayList.getChildAt(i).findViewById(R.id.selected_bar).
          setBackgroundColor(getResources().getColor(clickedColorID));
      } else {
        ((TextView) dayList.getChildAt(i).findViewById(R.id.selected_box)).
          setTextColor(getResources().getColor(R.color.checkboxborder));
        dayList.getChildAt(i).findViewById(R.id.selected_bar).
          setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    }
  }

  private void initializeColorList(LinearLayout colorList) {
    for (int i = 0; i < 7; i++) {
      final int finalI = i;
      colorList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          clickedColor(finalI + 1);
        }
      });
    }
    colorList.getChildAt(0).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.blue)
    );
    colorList.getChildAt(0).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.blueshadow)
    );
    colorList.getChildAt(1).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.lightblue)
    );
    colorList.getChildAt(1).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.lightblueshadow)
    );
    colorList.getChildAt(2).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.lightgreen)
    );
    colorList.getChildAt(2).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.lightgreenshadow)
    );
    colorList.getChildAt(3).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.yellow)
    );
    colorList.getChildAt(3).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.yellowshadow)
    );
    colorList.getChildAt(4).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.orange)
    );
    colorList.getChildAt(4).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.orangeshadow)
    );
    colorList.getChildAt(5).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.red)
    );
    colorList.getChildAt(5).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.redshadow)
    );
    colorList.getChildAt(6).findViewById(R.id.main_box).setBackgroundColor(
      getResources().getColor(R.color.purple)
    );
    colorList.getChildAt(6).findViewById(R.id.shadow_box).setBackgroundColor(
      getResources().getColor(R.color.purpleshadow)
    );
  }

  public void clickedDay(int dayNumber) {
    if (!daysChecked.get(dayNumber - 1)) {
      dayList.getChildAt(dayNumber - 1).findViewById(R.id.selected_bar).
        setBackgroundColor(getResources().getColor(clickedColorID));
      ((TextView) dayTextList.getChildAt(dayNumber - 1)).setTextColor(
        getResources().getColor(R.color.white)
      );
      daysChecked.set(dayNumber - 1, true);
    } else {
      dayList.getChildAt(dayNumber - 1).findViewById(R.id.selected_bar).
        setBackgroundColor(getResources().getColor(R.color.background));
      ((TextView) dayTextList.getChildAt(dayNumber - 1)).setTextColor(
        getResources().getColor(R.color.halfwhite)
      );
      daysChecked.set(dayNumber - 1, false);
    }
    updateDayListColor();
    //mAdapter.getItem(listItemNumber).setDays(daysChecked);
  }

  public void clickedColor(int colorNumber) {
    if (colorNumber != selectedColor) {
      if (selectedColor != 0) {
        colorList.getChildAt(selectedColor - 1).findViewById(R.id.selected_box).
          setBackgroundColor(getResources().getColor(R.color.background));
      }
      colorList.getChildAt(colorNumber - 1).findViewById(R.id.selected_box).
        setBackgroundColor(getResources().getColor(R.color.checkboxbg));
      selectedColor = colorNumber;
    }
    int mainColor;
    int shadowColor;
    switch (ListItemModel.Color.values()[selectedColor - 1]) {
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
    checkShadow1.setBackgroundColor(getResources().getColor(shadowColor));
    checkShadow2.setBackgroundColor(getResources().getColor(shadowColor));
    mainShadow1.setBackgroundColor(getResources().getColor(shadowColor));
    mainShadow2.setBackgroundColor(getResources().getColor(shadowColor));
    checkBigShadow.setBackgroundColor(getResources().getColor(shadowColor));
    checkSmallShadow.setBackgroundColor(getResources().getColor(shadowColor));
    mainBox.setBackgroundColor(getResources().getColor(mainColor));
    checkBig.setBackgroundColor(getResources().getColor(mainColor));
    checkSmall.setBackgroundColor(getResources().getColor(mainColor));
    //mAdapter.getItem(listItemNumber).setColor(ListItemModel.Color.values()[selectedColor - 1]);
    clickedColorID = mainColor;
    int saveDrawableColor = getSaveButtonDrawable(clickedColorID);
    saveButton.setBackground(getResources().getDrawable(saveDrawableColor));
    if (shouldNotify) {
      notifyForm.findViewById(R.id.notify_box_inside).setBackgroundColor(
        getResources().getColor(clickedColorID)
      );
    } else {
      notifyForm.findViewById(R.id.notify_box_inside).setBackgroundColor(
        getResources().getColor(R.color.background)
      );
    }
    updateDayListColor();
  }

  private void changeData() {
    mAdapter.getItem(listItemNumber).setColor(ListItemModel.Color.values()[selectedColor - 1]);
    mAdapter.getItem(listItemNumber).setName(nameEnter.getText().toString());
    mAdapter.getItem(listItemNumber).setDays(daysChecked);
    mAdapter.getItem(listItemNumber).setDone(isDone[0]);
    mAdapter.getItem(listItemNumber).setNotify(shouldNotify);
    mAdapter.parent.updateStorage();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View rootView = inflater.inflate(R.layout.edit_task_dialog, container, false);
    RelativeLayout topLevelLayout = (RelativeLayout) rootView.findViewById(R.id.top_level_layout);
    checkListItem = (RelativeLayout) rootView.findViewById(R.id.checklist_item);
    nameForm = (LinearLayout) rootView.findViewById(R.id.name_item);
    nameEnter = (EditText) nameForm.findViewById(R.id.name_enter);
    colorForm = (LinearLayout) rootView.findViewById(R.id.color_item);
    dayForm = (LinearLayout) rootView.findViewById(R.id.day_item);
    notifyForm = (RelativeLayout) rootView.findViewById(R.id.notify_item);
    final TextView nameView = (TextView) rootView.findViewById(R.id.nameText);
    final View checked = rootView.findViewById(R.id.checked);
    final View checkedOverlay = rootView.findViewById(R.id.checkedoverlay);
    final View notifyIcon = rootView.findViewById(R.id.notify_icon);
    colorList = (LinearLayout) rootView.findViewById(R.id.color_list);
    dayList = (LinearLayout) rootView.findViewById(R.id.day_list);
    dayTextList = (LinearLayout) rootView.findViewById(R.id.days_selected);
    submitForm = (LinearLayout) rootView.findViewById(R.id.buttons_item);
    initializeColorList(colorList);
    checkBig = rootView.findViewById(R.id.checkbig);
    checkBigShadow = rootView.findViewById(R.id.checkbigshadow);
    checkSmall = rootView.findViewById(R.id.checksmall);
    checkSmallShadow = rootView.findViewById(R.id.checksmallshadow);
    topLevelLayout.setBackground(mBackground);
    String name = mBundle.getString(PACKAGE + ".name");
    final int itemTop = mBundle.getInt(PACKAGE + ".top");
    final int itemLeft = mBundle.getInt(PACKAGE + ".left");
    final int mainColor = mBundle.getInt(PACKAGE + ".mainColor");
    final int shadowColor = mBundle.getInt(PACKAGE + ".shadowColor");
    isDone = new boolean[]{mBundle.getBoolean(PACKAGE + ".done")};
    boolean[] temp = mBundle.getBooleanArray(PACKAGE + ".daysChecked");
    shouldNotify = mBundle.getBoolean(PACKAGE + ".notify");
    for (int i = 0; i < temp.length; i++) {
      daysChecked.set(i, temp[i]);
    }
    clickedColorID = mainColor;
    initializeDayList(dayList, dayTextList);
    updateDayListColor();
    listItemNumber = mBundle.getInt(PACKAGE + ".itemNumber");
    int setColor = getColorNum(mainColor);
    int saveDrawableColor = getSaveButtonDrawable(mainColor);
    if (setColor != selectedColor) {
      if (selectedColor != 0) {
        colorList.getChildAt(selectedColor - 1).findViewById(R.id.selected_box).
          setBackgroundColor(getResources().getColor(R.color.background));
      }
      colorList.getChildAt(setColor - 1).findViewById(R.id.selected_box).
        setBackgroundColor(getResources().getColor(R.color.checkboxbg));
      selectedColor = setColor;
    }
    nameView.setText(name);
    nameEnter.setText(name);
    nameEnter.setSelection(name.length());
    nameEnter.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      }

      @Override
      public void afterTextChanged(Editable editable) {
        nameView.setText(nameEnter.getText());
        //mAdapter.getItem(listItemNumber).setName(nameEnter.getText().toString());
      }
    });
    if (shouldNotify) {
      notifyForm.findViewById(R.id.notify_box_inside).setBackgroundColor(
        getResources().getColor(clickedColorID)
      );
      notifyIcon.setAlpha(1);
    } else {
      notifyIcon.setAlpha(0.33f);
    }
    notifyForm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        shouldNotify = !shouldNotify;
        if (shouldNotify) {
          notifyForm.findViewById(R.id.notify_box_inside).setBackgroundColor(
            getResources().getColor(clickedColorID)
          );
          notifyIcon.setAlpha(1);
        } else {
          notifyForm.findViewById(R.id.notify_box_inside).setBackgroundColor(
            getResources().getColor(R.color.background)
          );
          notifyIcon.setAlpha(0.25f);
        }
      }
    });
    checkShadow1 = rootView.findViewById(R.id.checklistbottom);
    checkShadow2 = rootView.findViewById(R.id.checklistright);
    mainShadow1 = rootView.findViewById(R.id.mainviewbottom);
    mainShadow2 = rootView.findViewById(R.id.mainviewright);
    saveButton = (Button) rootView.findViewById(R.id.save_button);
    cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
    checkShadow1.setBackgroundColor(getResources().getColor(shadowColor));
    checkShadow2.setBackgroundColor(getResources().getColor(shadowColor));
    mainShadow1.setBackgroundColor(getResources().getColor(shadowColor));
    mainShadow2.setBackgroundColor(getResources().getColor(shadowColor));
    saveButton.setBackground(getResources().getDrawable(saveDrawableColor));
    cancelButton.setBackground(getResources().getDrawable(R.drawable.cancel_button));
    checkBigShadow.setBackgroundColor(getResources().getColor(shadowColor));
    checkSmallShadow.setBackgroundColor(getResources().getColor(shadowColor));
    mainBox = rootView.findViewById(R.id.mainview);
    mainBox.setBackgroundColor(getResources().getColor(mainColor));
    checkBig.setBackgroundColor(getResources().getColor(mainColor));
    checkSmall.setBackgroundColor(getResources().getColor(mainColor));
    if (isDone[0]) {
      checked.setVisibility(View.VISIBLE);
      checkedOverlay.setVisibility(View.VISIBLE);
    } else {
      checked.setVisibility(View.GONE);
      checkedOverlay.setVisibility(View.GONE);
    }
    View checkBox = rootView.findViewById(R.id.checkboxview);
    checkBox.setTag(listItemNumber);
    checkBox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isDone[0] = !isDone[0];
        if (isDone[0]) {
          checked.setVisibility(View.VISIBLE);
          checkedOverlay.setVisibility(View.VISIBLE);
        } else {
          checked.setVisibility(View.GONE);
          checkedOverlay.setVisibility(View.GONE);
        }
        //mAdapter.getItem(listItemNumber).setDone(isDone[0]);
      }
    });
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        runExitAnimation(new Runnable() {
          @Override
          public void run() {
            dialog.cancel();
          }
        });
      }
    });
    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        changeData();
        mAdapter.notifyDataSetChanged();
        runExitAnimation(new Runnable() {
          @Override
          public void run() {
            dialog.cancel();
          }
        });
      }
    });
    if (savedInstanceState == null) {
      ViewTreeObserver observer = checkListItem.getViewTreeObserver();
      observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

        @Override
        public boolean onPreDraw() {
          checkListItem.getViewTreeObserver().removeOnPreDrawListener(this);
          int[] screenLocation = new int[2];
          checkListItem.getLocationOnScreen(screenLocation);
          mLeftDelta = itemLeft - screenLocation[0];
          mTopDelta = itemTop - screenLocation[1];
          runEnterAnimation();
          return true;
        }
      });
    }
    return rootView;
  }

  public void runEnterAnimation() {
    final long duration = (long) (ANIM_DURATION);

    checkListItem.setPivotX(0);
    checkListItem.setPivotY(0);
    checkListItem.setScaleX(1);
    checkListItem.setScaleY(1);
    checkListItem.setTranslationX(mLeftDelta);
    checkListItem.setTranslationY(mTopDelta);

    nameForm.setAlpha(0);
    nameForm.setScaleY(.25f);
    colorForm.setAlpha(0);
    colorForm.setScaleY(.25f);
    dayForm.setAlpha(0);
    dayForm.setScaleY(.25f);
    notifyForm.setAlpha(0);
    notifyForm.setScaleY(.25f);
    submitForm.setAlpha(0);
    submitForm.setScaleY(.25f);
    mAdapter.hideItem(listItemNumber);
    checkListItem.animate().setDuration(duration).
      translationX(0).translationY(0).
      setInterpolator(sDecelerator).
      withEndAction(new Runnable() {
        public void run() {
          nameForm.setTranslationY(-nameForm.getHeight());
          nameForm.animate().
            setDuration(duration / 2).
            scaleY(1f).
            translationY(0).
            alpha(1).
            setInterpolator(sDecelerator).
            withEndAction(new Runnable() {
              @Override
              public void run() {
                colorForm.setTranslationY(-colorForm.getHeight());
                colorForm.animate().
                  setDuration(duration / 2).
                  scaleY(1f).
                  translationY(0).
                  alpha(1).
                  setInterpolator(sDecelerator).
                  withEndAction(new Runnable() {
                    @Override
                    public void run() {
                      dayForm.setTranslationY(-dayForm.getHeight());
                      dayForm.animate().
                        setDuration(duration / 2).
                        scaleY(1f).
                        translationY(0).
                        alpha(1).
                        setInterpolator(sDecelerator).
                        withEndAction(new Runnable() {
                          @Override
                          public void run() {
                            notifyForm.setTranslationY(-notifyForm.getHeight());
                            notifyForm.animate().
                              setDuration(duration / 2).
                              scaleY(1f).
                              translationY(0).
                              alpha(1).
                              setInterpolator(sDecelerator).
                              withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                  submitForm.setTranslationY(-submitForm.getHeight());
                                  submitForm.animate().
                                    setDuration(duration / 2).
                                    scaleY(1f).
                                    translationY(0).
                                    alpha(1).
                                    setInterpolator(sDecelerator);
                                }
                              });
                          }
                        });
                    }
                  });
              }
            });
        }
      });

    ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 155);
    bgAnim.setDuration(duration);
    bgAnim.start();
  }

  public void runExitAnimation(final Runnable endAction) {
    final long duration = (long) (ANIM_DURATION);
    submitForm.animate().translationY(-submitForm.getHeight()).scaleY(.25f).alpha(0).
      setDuration(duration / 2).setInterpolator(sAccelerator).withEndAction(new Runnable() {
      @Override
      public void run() {
        notifyForm.animate().translationY(-notifyForm.getHeight()).scaleY(.25f).alpha(0).
          setDuration(duration / 2).setInterpolator(sAccelerator).withEndAction(new Runnable() {
          @Override
          public void run() {
            dayForm.animate().translationY(-dayForm.getHeight()).scaleY(.25f).alpha(0).
              setDuration(duration / 2).setInterpolator(sAccelerator).withEndAction(new Runnable() {
              @Override
              public void run() {
                colorForm.animate().translationY(-colorForm.getHeight()).scaleY(.25f).alpha(0).
                  setDuration(duration / 2).setInterpolator(sAccelerator).withEndAction(new Runnable() {
                  @Override
                  public void run() {
                    nameForm.animate().translationY(-nameForm.getHeight()).scaleY(.25f).
                      alpha(0).setDuration(duration / 2).setInterpolator(sAccelerator).
                      withEndAction(new Runnable() {
                        @Override
                        public void run() {
                          checkListItem.animate().setDuration(duration).
                            translationY(mLeftDelta).translationY(mTopDelta).
                            withEndAction(new Runnable() {
                              @Override
                              public void run() {
                                mAdapter.revealItem(listItemNumber);
                                nameForm.animate().setDuration(10).alpha(0).setInterpolator(sAccelerator).withEndAction(endAction);
                              }
                            });

                          ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
                          bgAnim.setDuration(duration);
                          bgAnim.start();

                        }
                      });
                  }
                });
              }
            });
          }
        });
      }
    });

  }
}
