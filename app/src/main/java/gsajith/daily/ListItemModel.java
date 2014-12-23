package gsajith.daily;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gsajith on 10/19/2014.
 */
public class ListItemModel {

  private String dailyName;
  private boolean done;
  private boolean isVisible = true;
  private boolean notify = false;
  private Color color;
  private List<Boolean> daysEnabled;
  private int ID;
  public ListItemModel(String name, boolean done, boolean notify, Color c, List<Boolean> days, int id) {
    dailyName = name;
    this.done = done;
    this.notify = notify;
    color = c;
    daysEnabled = days;
    ID = id;
  }

  public String getName() {
    return dailyName;
  }

  public void setName(String name) {
    dailyName = name;
  }

  public String getDaysString() {
    String ret = "";
    for (int i = 0; i < daysEnabled.size(); i++) {
      if (daysEnabled.get(i)) {
        ret += "1";
      } else {
        ret += "0";
      }
    }
    return ret;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public boolean shouldNotify() {
    return notify;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color c) {
    color = c;
  }

  public int getID() {
    return ID;
  }

  public void setID(int id) {
    ID = id;
  }

  public boolean[] getDays() {
    final boolean[] primitives = new boolean[daysEnabled.size()];
    int index = 0;
    for (Boolean object : daysEnabled) {
      primitives[index++] = object;
    }
    return primitives;
  }

  public void setDays(ArrayList<Boolean> days) {
    daysEnabled = days;
  }

  public boolean isEnabledOn(int day) {
    if (day > 6 || day < 0) return false;
    if (daysEnabled.size() <= day) {
      return false;
    }
    return daysEnabled.get(day);
  }

  public void setNotify(boolean notify) {
    this.notify = notify;
  }

  public void setEnabledOn(int day, boolean enabled) {
    if (day > 6 || day < 0) return;
    if (daysEnabled.size() <= day) {
      return;
    }
    daysEnabled.set(day, enabled);
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  public enum Color {
    DARKBLUE,
    LIGHTBLUE,
    LIGHTGREEN,
    YELLOW,
    ORANGE,
    RED,
    PURPLE
  }
}
