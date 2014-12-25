package gsajith.daily;

import java.util.Date;
import java.util.List;

/**
 * Created by gsajith on 12/23/14.
 */
public class ClearListAlarm extends AlarmModel {
  public ClearListAlarm(Date date, AlarmType type) {
    super(date, type);
  }

  public void clear(List<ListItemModel> list) {
    for (int i = 0; i < list.size(); i++) {
      list.clear();
    }
  }
}
