import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

class WordleFileHelper {
  public static void filterWords() {
    BufferedReader br = null;
    BufferedWriter bw = null;
    //Read an unfiltered set of words scraped from Wordle source code.
    try {
      br = new BufferedReader(new FileReader("../unfilteredwords.txt"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    //Write a new file without commas, apostraphes, or spaces in the wordlist.
    try {
      bw = new BufferedWriter(new FileWriter(new File("../words.txt")));
      String word = br.readLine();
      while (word != null) {
        word = word.replace("'", "");
        word = word.replace(",", "");
        word = word.replace(" ", "");
        bw.write(word);
        bw.newLine();
        word = br.readLine();
      }
      br.close();
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<String> fileToList(String fileName) {
    BufferedReader br = null;
    List<String> dictionary = new ArrayList<>();
    try {
      br = new BufferedReader (new FileReader("../" + fileName));
      String word = br.readLine();
      while (word != null) {
        dictionary.add(word);
        word = br.readLine();
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return dictionary;
  }
}
