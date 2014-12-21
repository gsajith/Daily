package gsajith.daily;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Stack;
  import java.util.concurrent.Executors;
  import java.util.concurrent.ScheduledExecutorService;
  import java.util.concurrent.TimeUnit;

  import android.support.v4.app.ListFragment;
  import android.util.Pair;
  import android.view.View;
  import android.view.ViewGroup;
  import android.view.LayoutInflater;
  import android.os.Bundle;

public class DSLVFragment extends ListFragment {

  DragSortListAdapter adapter;

  private ArrayList<ListItemModel> list;
  private Stack<Pair<ListItemModel, Integer>> undoStack;

  private DragSortListView.DropListener onDrop =
    new DragSortListView.DropListener() {
      @Override
      public void drop(int from, int to) {
        if (from != to) {
          ListItemModel item = adapter.getItem(from);
          list.remove(item);
          list.add(to, item);
          adapter.notifyDataSetChanged();
        }
      }
    };

  private DragSortListView.RemoveListener onRemove =
    new DragSortListView.RemoveListener() {
      @Override
      public void remove(int which) {
        undoStack.push(new Pair(adapter.getItem(which), which));
        list.remove(adapter.getItem(which));
        if (list.isEmpty()) {
          adapter.showEmptyPage(getActivity());
        }
        adapter.notifyDataSetChanged();
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
        list.add(to, item);
        adapter.notifyDataSetChanged();
      }
    }
  };

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

  /**
   * Return list item layout resource passed to the ArrayAdapter.
   */
  protected int getItemLayout() {
        /*if (removeMode == DragSortController.FLING_LEFT_REMOVE || removeMode == DragSortController.SLIDE_LEFT_REMOVE) {
            return R.layout.list_item_handle_right;
        } else */
      return R.layout.checklist_item;
  }

  private DragSortListView mDslv;
  private DragSortController mController;

  public int dragStartMode = DragSortController.ON_DOWN;
  public boolean removeEnabled = true;
  public int removeMode = DragSortController.FLING_REMOVE;
  public boolean sortEnabled = true;
  public boolean dragEnabled = true;

  public static DSLVFragment newInstance(int headers, int footers) {
    DSLVFragment f = new DSLVFragment();

    Bundle args = new Bundle();
    args.putInt("headers", headers);
    args.putInt("footers", footers);
    f.setArguments(args);

    return f;
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
    List<Boolean> daysEnabled = new ArrayList<Boolean>();
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    daysEnabled.add(false);
    list.add(new ListItemModel("Meditate for 15 minutes", false, false, ListItemModel.Color.LIGHTBLUE, daysEnabled));
    list.add(new ListItemModel("Practice Chinese", false, false, ListItemModel.Color.LIGHTGREEN, daysEnabled));
    list.add(new ListItemModel("Practice Spanish", false, false, ListItemModel.Color.LIGHTGREEN, daysEnabled));
    list.add(new ListItemModel("Eat an apple", false, false, ListItemModel.Color.RED, daysEnabled));
    list.add(new ListItemModel("Run a mile", false, false, ListItemModel.Color.YELLOW, daysEnabled));
    list.add(new ListItemModel("Contribute to StackOverflow", true, false, ListItemModel.Color.LIGHTBLUE, daysEnabled));
    adapter = new DragSortListAdapter(getActivity(), list);
    if (list.isEmpty()) {
      adapter.showEmptyPage(getActivity());
    }
    setListAdapter(adapter);
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


  /** Called when the activity is first created. */
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
