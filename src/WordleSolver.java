import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Solves a Wordle puzzle using only the green/yellow/gray feedback a real player sees.
 * After each guess it keeps only the words that would have produced the same feedback.
 */
class WordleSolver {
  static final int LENGTH = 5;
  static final int MAX_GUESSES = 6;

  /** Outcome of one solve attempt. */
  static final class Result {
    boolean solved;
    final List<String> guesses = new ArrayList<>();
    final List<String> feedback = new ArrayList<>();
  }

  private final Words words;
  private final Random rng;
  private final boolean verbose;

  WordleSolver(Words words, Random rng, boolean verbose) {
    this.words = words;
    this.rng = rng;
    this.verbose = verbose;
  }

  /**
   * Wordle feedback for a guess: 'G' right letter right spot, 'Y' letter elsewhere in the
   * word, '-' not in the word (or all copies already accounted for).
   */
  static String feedback(String guess, String answer) {
    char[] fb = new char[LENGTH];
    int[] unmatched = new int[26]; // answer letters not used up by a green
    for (int i = 0; i < LENGTH; i++) {
      if (guess.charAt(i) == answer.charAt(i)) fb[i] = 'G';
      else unmatched[answer.charAt(i) - 'a']++;
    }
    for (int i = 0; i < LENGTH; i++) {
      if (fb[i] == 'G') continue;
      int c = guess.charAt(i) - 'a';
      if (unmatched[c] > 0) { fb[i] = 'Y'; unmatched[c]--; }
      else fb[i] = '-';
    }
    return new String(fb);
  }

  static String emoji(String fb) {
    StringBuilder sb = new StringBuilder();
    for (char c : fb.toCharArray()) sb.append(c == 'G' ? "🟩" : c == 'Y' ? "🟨" : "⬛");
    return sb.toString();
  }

  /** Plays one game against the answer. The answer is only ever passed to feedback(). */
  Result solve(String answer) {
    Result result = new Result();
    List<String> candidates = new ArrayList<>(words.valid);
    String allGreen = "G".repeat(LENGTH);

    for (int turn = 1; turn <= MAX_GUESSES && !candidates.isEmpty(); turn++) {
      String guess = pick(candidates);
      String fb = feedback(guess, answer);
      result.guesses.add(guess);
      result.feedback.add(fb);
      if (verbose) System.out.println("Guess " + turn + ": " + guess + "  " + emoji(fb) + "  " + fb
          + "  (" + candidates.size() + " candidates)");
      if (fb.equals(allGreen)) {
        result.solved = true;
        if (verbose) System.out.println("Solved in " + turn + "!");
        return result;
      }
      final String g = guess;
      candidates.removeIf(c -> !feedback(g, c).equals(fb));
    }
    if (verbose) System.out.println("Not solved in " + MAX_GUESSES + " guesses.");
    return result;
  }

  /** Random pick, preferring words from the official answer list when any remain. */
  private String pick(List<String> candidates) {
    List<String> likely = new ArrayList<>();
    for (String c : candidates) if (words.answerSet.contains(c)) likely.add(c);
    List<String> pool = likely.isEmpty() ? candidates : likely;
    return pool.get(rng.nextInt(pool.size()));
  }

  /** Returns an error message if the word cannot be used as a puzzle, else null. */
  static String validate(String word, Words words) {
    if (!word.matches("[a-z]{5}")) return "Enter exactly five letters (a-z).";
    if (!words.validSet.contains(word)) return "'" + word + "' is not in the word list.";
    return null;
  }

  private static void usage() {
    System.out.println("Usage: java -cp out WordleSolver [--word WORD] [--seed N]\n"
        + "  --word WORD  solve WORD non-interactively (exit 0 if solved, 1 if not)\n"
        + "  --seed N     make random choices deterministic\n"
        + "With no --word, an interactive menu is shown. Run from the repo root.");
  }

  public static void main(String[] args) throws IOException {
    String word = null;
    Long seed = null;
    try {
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--word": word = args[++i].toLowerCase(); break;
          case "--seed": seed = Long.parseLong(args[++i]); break;
          case "-h": case "--help": usage(); return;
          default: System.err.println("Unknown argument: " + args[i]); usage(); System.exit(2);
        }
      }
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
      System.err.println("Bad arguments."); usage(); System.exit(2);
    }

    Words words = new Words();
    Random rng = seed == null ? new Random() : new Random(seed);
    WordleSolver solver = new WordleSolver(words, rng, true);

    if (word != null) {
      String err = validate(word, words);
      if (err != null) { System.err.println(err); System.exit(2); }
      System.exit(solver.solve(word).solved ? 0 : 1);
    }
    interactive(solver, words, rng);
  }

  private static void interactive(WordleSolver solver, Words words, Random rng) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Welcome to Wordle solver! Choose an option:");
    while (true) {
      System.out.println("1: Solve a random word from the official Wordle list\n"
          + "2: Enter a custom word to be solved");
      if (!sc.hasNextLine()) break;
      String input = sc.nextLine().trim();
      if (input.equals("1")) {
        solver.solve(words.answers.get(rng.nextInt(words.answers.size())));
      } else if (input.equals("2")) {
        System.out.println("Enter a five letter word");
        if (!sc.hasNextLine()) break;
        String w = sc.nextLine().trim().toLowerCase();
        String err = validate(w, words);
        if (err != null) { System.out.println(err + " Try again."); continue; }
        solver.solve(w);
      } else {
        System.out.println("Invalid input, try again");
        continue;
      }
      System.out.println("Type 'again' to solve another word");
      if (!sc.hasNextLine() || !sc.nextLine().trim().equals("again")) break;
    }
    System.out.println("Thanks for playing!");
  }
}
