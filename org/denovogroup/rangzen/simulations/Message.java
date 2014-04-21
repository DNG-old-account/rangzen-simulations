package org.denovogroup.rangzen.simulations;

public class Message implements Comparable<Message> {
  private static final long serialVersionUID = 1;

  private static final int I_AM_GREATER_THAN = 1;
  private static final int I_AM_EQUAL_TO = 0;
  private static final int I_AM_LESS_THAN = -1;


  private int priority;
  private String content;

  public Message(String content, int priority) {
    this.content = content;
    this.priority = priority;
  }

  public int compareTo(Message other) {
    // All non-null messages have greater priority than all null messages.
    if (other == null) {
      throw new NullPointerException();
    }
    else if (this.priority == other.priority) {
      return I_AM_EQUAL_TO;
    }
    else if (this.priority < other.priority) {
      return I_AM_LESS_THAN;
    }
    else { // (this.priority > other.priority)
      return I_AM_GREATER_THAN;
    }
  }

}
