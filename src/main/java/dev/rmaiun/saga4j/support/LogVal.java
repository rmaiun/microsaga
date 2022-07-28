package dev.rmaiun.saga4j.support;

public class LogVal {

  private static final String ACTION = "action";
  private static final String COMPENSATION = "compensation";
  private String type;
  private String name;
  private long ms;

  public LogVal(String type, String name, long ms) {
    this.type = type;
    this.name = name;
    this.ms = ms;
  }

  public static LogVal action(String name, long ms) {
    return new LogVal(ACTION, name, ms);
  }

  public static LogVal compensation(String name, long ms) {
    return new LogVal(COMPENSATION, name, ms);
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public long getMs() {
    return ms;
  }
}
