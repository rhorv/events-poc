package events.formatter.rjs1;

import java.util.HashMap;
import java.util.Map;

public class GsonClassDto {

  public String id;
  public String name;
  public Map<String, String> payload = new HashMap<String, String>();
  public Integer version;
  public String occurredAt;
  public String category;
}
