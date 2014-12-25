package gsajith.daily;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class DSLVFragment extends ListFragment {
  private static final SecureRandom random = new SecureRandom();
  public int dragStartMode = DragSortController.ON_DOWN;
  public boolean removeEnabled = true;
  public int removeMode = DragSortController.FLING_REMOVE;
  public boolean sortEnabled = true;
  public boolean dragEnabled = true;
  DragSortListAdapter adapter;
  private ArrayList<ListItemModel> list;
  private Stack<Pair<ListItemModel, Integer>> undoStack;
  private List<Boolean> daysEnabled;
  private HashMap<String, String> alarmMap;
  private DragSortListView.DropListener onDrop =
    new DragSortListView.DropListener() {
      @Override
      public void drop(int from, int to) {
        if (from != to) {
          ListItemModel item = adapter.getItem(from);
          list.remove(item);
          item.setID(to);
          list.add(to, item);
          adapter.notifyDataSetChanged();
          updateStorage();
        }
      }
    };
  private DragSortListView.RemoveListener onRemove =
    new DragSortListView.RemoveListener() {
      @Override
      public void remove(int which, boolean shouldUndo) {
        if (shouldUndo) {
          undoStack.push(new Pair(adapter.getItem(which), which));
        }
        list.remove(adapter.getItem(which));
        if (list.isEmpty()) {
          adapter.showEmptyPage(getActivity());
        }
        adapter.notifyDataSetChanged();
        updateStorage();
      }
    };
  private DragSortListView.UndoListener onUndo = new DragSortListView.UndoListener() {
    @Override
    public void undo() {
      if (canUndo()) {
        if (list.isEmpty()) {
          adapter.hideEmptyPage(getActivity());
        }
        Pair<ListItemModel, Integer> top = undoStack.pop();
        ListItemModel item = top.first;
        int to = top.second;
        item.setID(to);
        list.add(to, item);
        adapter.notifyDataSetChanged();
        updateStorage();
      }
    }
  };
  private DragSortListView mDslv;
  private DragSortController mController;

  public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
    int x = random.nextInt(clazz.getEnumConstants().length);
    return clazz.getEnumConstants()[x];
  }

  public void updateStorage() {
    GsonBuilder gsonb = new GsonBuilder();
    Gson gson = gsonb.create();
    String value = gson.toJson(list);
    SharedPreferences prefs = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
    SharedPreferences.Editor e = prefs.edit();
    e.putString("list", value);
    e.commit();
  }

  public void updateAlarms() {
    GsonBuilder gsonb = new GsonBuilder();
    Gson gson = gsonb.create();
    String value = gson.toJson(alarmMap);
    SharedPreferences prefs = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
    SharedPreferences.Editor e = prefs.edit();
    e.putString("alarms", value);
    e.commit();
  }

  public boolean canUndo() {
    return !undoStack.isEmpty();
  }

  public boolean permShowUndo() {
    return list.isEmpty() && !undoStack.isEmpty();
  }

  protected int getLayout() {
    // this DSLV xml declaration does not call for the use
    // of the default DragSortController; therefore,
    // DSLVFragment has a buildController() method.
    return R.layout.dslv_fragment_main;
  }

  public void newTask() {
    if (list.isEmpty()) {
      adapter.hideEmptyPage(getActivity());
    }
    ListItemModel item = new ListItemModel("New Daily - tap to edit", false, false, randomEnum(ListItemModel.Color.class), daysEnabled, 0);
    list.add(0, item);
    adapter.notifyDataSetChanged();
    updateStorage();
  }

  public void clearNew() {
    for (int i = list.size() - 1; i >= 0; i--) {
      if (list.get(i).getName().equals("New Daily - tap to edit")) {
        onRemove.remove(i, false);
      }
    }
  }

  /**
   * Return list item layout resource passed to the ArrayAdapter.
   */
  protected int getItemLayout() {
        /*if (removeMode == DragSortController.FLING_LEFT_REMOVE || removeMode == DragSortController.SLIDE_LEFT_REMOVE) {
            return R.layout.list_item_handle_right;
        } else */
    return R.layout.checklist_item;
  }

  public DragSortController getController() {
    return mController;
  }

  /**
   * Called from DSLVFragment.onActivityCreated(). Override to
   * set a different adapter.
   */
  public void setListAdapter() {
    list = new ArrayList<ListItemModel>();
    undoStack = new Stack<Pair<ListItemModel, Integer>>();
    daysEnabled = new ArrayList<Boolean>();
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);

    //***********************
    // load list from DB
    // ***********************
    SharedPreferences prefs = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
    String value = prefs.getString("list", null);
    if (value != null) {
      GsonBuilder gsonb = new GsonBuilder();
      Gson gson = gsonb.create();
      list.addAll(Arrays.asList(gson.fromJson(value, ListItemModel[].class)));
    }
    value = prefs.getString("alarms", null);
    if (value != null) {
      GsonBuilder gsonb = new GsonBuilder();
      Gson gson = gsonb.create();
      alarmMap = gson.fromJson(value, new TypeToken<Map<String, String>>(){}.getType());
    }
    for (int i = 0; i < list.size(); i++) {
      list.get(i).setVisible(true);
    }
    adapter = new DragSortListAdapter(getActivity(), list, DSLVFragment.this);

    if (list.isEmpty()) {
      adapter.showEmptyPage(getActivity());
    } else {
      adapter.hideEmptyPage(getActivity());
    }
    setListAdapter(adapter);
    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
    PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
    AlarmManager alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    alarmMgr.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
      AlarmManager.INTERVAL_DAY, alarmIntent);
  }

  /**
   * Called in onCreateView. Override this to provide a custom
   * DragSortController.
   */
  public DragSortController buildController(DragSortListView dslv) {
    // defaults are
    //   dragStartMode = onDown
    //   removeMode = flingRight
    DragSortController controller = new DragSortController(dslv);
    controller.setDragHandleId(R.id.drag_handle);
    controller.setFlingHandleId(R.id.drag_handle);
    controller.setClickRemoveId(R.id.click_remove);
    controller.setRemoveEnabled(removeEnabled);
    controller.setSortEnabled(sortEnabled);
    controller.setDragInitMode(dragStartMode);
    controller.setRemoveMode(removeMode);
    return controller;
  }


  /**
   * Called when the activity is first created.
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mDslv = (DragSortListView) inflater.inflate(getLayout(), container, false);

    mController = buildController(mDslv);
    mDslv.setFloatViewManager(mController);
    mDslv.setOnTouchListener(mController);
    mDslv.setDragEnabled(dragEnabled);
    mDslv.setRemoveEnabled(removeEnabled);

    return mDslv;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mDslv = (DragSortListView) getListView();

    mDslv.setDropListener(onDrop);
    mDslv.setRemoveListener(onRemove);
    mDslv.setUndoListener(onUndo);

    setListAdapter();
  }

  public void undo() {
    onUndo.undo();
  }


}
