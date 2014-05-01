package org.denovogroup.rangzen.simulation;

import java.io.Serializable;

public class Message implements Serializable, Comparable<Message> {
  private static final long serialVersionUID = 1;

  private static final int I_AM_GREATER_THAN = 1;
  private static final int I_AM_EQUAL_TO = 0;
  private static final int I_AM_LESS_THAN = -1;

  public double priority;
  public String content;

  public Message(String content, double priority) {
    this.content = content;
    this.priority = priority;
  }

  public Message clone() {
    return new Message(content, priority);
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

  public String toString() {
    return "("+priority+"): "+content;
  }

  public boolean equals(Message other) {
    if (other == null) {
      return false;
    } else if (other.content.equals(this.content)) {
      return true;
    } else {
     return false;
    } 
  }

}
