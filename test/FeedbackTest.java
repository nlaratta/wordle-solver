import java.util.List;
import java.util.Random;

/** Dependency-free tests; exits non-zero on any failure. Run from the repo root. */
public class FeedbackTest {
  static int failures = 0;

  static void check(boolean ok, String msg) {
    if (!ok) { failures++; System.out.println("FAIL: " + msg); }
  }

  static void fb(String guess, String answer, String expected) {
    String got = WordleSolver.feedback(guess, answer);
    check(got.equals(expected), guess + " vs " + answer + " expected " + expected + " got " + got);
  }

  public static void main(String[] args) throws Exception {
    // Duplicate-letter handling
    fb("speed", "abide", "--Y-Y");
    fb("eerie", "there", "Y-Y-G");
    fb("crane", "crane", "GGGGG");
    fb("aaaaa", "abbbb", "G----");
    fb("llama", "hello", "YY---");
    System.out.println(failures == 0 ? "feedback ok" : "feedback FAILED");

    Words words = new Words();

    // Solve runs end in the right word for several seeds
    for (long seed = 1; seed <= 5; seed++) {
      for (String ans : List.of("crane", "there", "abide", "fanny")) {
        WordleSolver.Result r = new WordleSolver(words, new Random(seed), false).solve(ans);
        if (r.solved) check(r.guesses.get(r.guesses.size() - 1).equals(ans), "solved with wrong word");
      }
    }

    // No cheating over 200 fixed-seed random answers
    Random pick = new Random(42);
    int solved = 0, total = 200;
    for (int n = 0; n < total; n++) {
      String ans = words.answers.get(pick.nextInt(words.answers.size()));
      WordleSolver.Result r = new WordleSolver(words, new Random(1000 + n), false).solve(ans);
      List<String> cands = new java.util.ArrayList<>(words.valid);
      for (int i = 0; i < r.guesses.size(); i++) {
        String g = r.guesses.get(i);
        boolean allGreen = r.feedback.get(i).equals("GGGGG");
        check(allGreen == g.equals(ans), "correct reported iff guess equals answer: " + g + "/" + ans);
        check(r.feedback.get(i).equals(WordleSolver.feedback(g, ans)), "feedback mismatch");
        check(cands.contains(ans), "answer filtered out: " + ans);
        String f = r.feedback.get(i);
        cands.removeIf(c -> !WordleSolver.feedback(g, c).equals(f));
        check(cands.contains(ans), "answer filtered out after " + g + ": " + ans);
      }
      if (r.solved) solved++;
    }
    System.out.println("solved " + solved + "/" + total + " (seed 42)");
    check(solved >= total * 9 / 10, "solve rate too low: " + solved + "/" + total);

    System.out.println(failures == 0 ? "ALL TESTS PASSED" : failures + " FAILURES");
    System.exit(failures == 0 ? 0 : 1);
  }
}
