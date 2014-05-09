package org.denovogroup.rangzen.simulation;

import au.com.bytecode.opencsv.CSVReader;

import java.io.Reader;
import java.io.IOException;

public class PeekableCSVReader extends CSVReader {

  private String[] nextLine;

  public PeekableCSVReader(Reader reader, char delimiter, char quote, int skip) {
    super(reader, delimiter, quote, skip);

    try {
      nextLine = super.readNext();
    }
    catch (IOException e) {
      e.printStackTrace();
      nextLine = null;
    }
  }
  public String[] readNext() {
    String[] temp = nextLine;
    try {
      nextLine = super.readNext();
    }
    catch (IOException e) {
      e.printStackTrace();
      nextLine = null;
    }
    return temp;
  }
  public String[] peek() {
    return nextLine;
  }
}
