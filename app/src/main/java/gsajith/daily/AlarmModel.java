package gsajith.daily;

import java.util.Date;

/**
 * Created by gsajith on 12/23/14.
 */
public abstract class AlarmModel {
  private int ID;
  private Date date;
  public enum AlarmType {
    CLEAR_LIST,
    NOTIFICATION
  }
  private AlarmType type;
  public AlarmModel(Date date, AlarmType type) {
    this.date = date;
    this.type = type;
    ID = (String.valueOf(System.currentTimeMillis()) + String.valueOf(date.getTime())).hashCode();
  }

  public int getID() {
    return ID;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public AlarmType getType() {
    return type;
  }

  public void setType(AlarmType type) {
    this.type = type;
  }
}
