import java.util.*;
import java.util.Collections;

class WordleSolver {

  public WordleSolver() {}

  public void solveWord(String word) {
    List<String> possibilities = WordleFileHelper.fileToList("guesses.txt");
    String[] goodLetters = new String[5];
    Random rn = new Random();

    //Choose a random word from the guess list
    for (int i = 0; i < 6; i++) {
      String guess = possibilities.get(rn.nextInt(possibilities.size()));
      System.out.println("Guess #" + (i + 1) + " = " + guess);
      System.out.println("Word: " + word);
      if (guess.equals(word)) {
        System.out.println("Correct!");
        break;
      }

      //Check for letters in the correct location, record correct guesses
      for (int j = 0; j < 5; j++) {
        if (word.contains(guess.substring(j, j + 1))){
          goodLetters[j] = word.substring(j, j + 1);
        }
      }

      //Remove remaining incorrect words from the guess list
      for (Iterator<String> it = possibilities.iterator(); it.hasNext(); ) {
        String p = it.next();
        Boolean valid = true;
        for (int j = 0; j < 5; j++) {
          if (goodLetters[j] == null) continue;
          if (!p.substring(j, j + 1).equals(goodLetters[j])) {
            valid = false;
          }
        }
        if (!valid) it.remove();
      }
      possibilities.remove(guess);
    }
  }

  public String getWord() {
    List<String> dictionary = WordleFileHelper.fileToList("words.txt");
    Random rn = new Random();
    return dictionary.get(rn.nextInt(dictionary.size()));
  }

  public static void main (String[] args) {
    WordleSolver solver = new WordleSolver();

    //Clean up format of word list scraped from Wordle
    //WordleFileHelper.filterWords();

    Scanner sc = new Scanner(System.in);
    System.out.println("Welcome to Wordle solver! Choose an option: ");

    while (true) {
      System.out.println("1: Solve a random word from the official Wordle list \n"
                        + "2: Enter a custom word to be solved");
      String input = sc.nextLine();

      if (input.equals("1")) {
        String word = solver.getWord();
        solver.solveWord(word);
      }
      else if (input.equals("2")) {
        System.out.println("Enter a five letter word");
        String word = sc.nextLine();
        if (word.length() != 5) {
          System.out.println("Invalid word length, try again");
          continue;
        }
        solver.solveWord(word);
      }
      else {
        System.out.println("Invalid input, try again");
        continue;
      }
      System.out.println("Type 'again' to solve another word");
      if (sc.nextLine().equals("again")) continue;
      break;
    }
    sc.close();
    System.out.println("Thanks for playing!");
    System.exit(0);
  }
}
