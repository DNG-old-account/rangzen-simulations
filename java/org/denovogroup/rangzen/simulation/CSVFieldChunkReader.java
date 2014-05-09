package org.denovogroup.rangzen.simulation;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class CSVFieldChunkReader {

  private PeekableCSVReader csvReader;
  private int field;
  private String currentFieldValue = null;
  private List<String[]> nextChunk;
  
  public CSVFieldChunkReader(String filename, char delimiter, char quote, 
                             int skip, int field) 
          throws FileNotFoundException {

    this.csvReader = new PeekableCSVReader(new FileReader(filename), 
                                           delimiter, quote, skip);
    this.field = field;

    readyNextChunk();
  }

  public List<String[]> nextChunk() {
    List<String[]> temp = nextChunk;
    readyNextChunk();
    return temp;
  }
  
  private void readyNextChunk() {
    String[] firstLineOfChunk = csvReader.peek();
    if (firstLineOfChunk == null) {
      nextChunk = null;
    } else {
      nextChunk = new ArrayList<String[]>();
      currentFieldValue = firstLineOfChunk[field];
      String[] nextLine;
      while (csvReader.peek() != null &&
             csvReader.peek()[field].equals(currentFieldValue)) {
          nextChunk.add(csvReader.readNext());
      }
    }

  }


   

}
