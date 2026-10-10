import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Loads the word lists from the data/ directory (paths are relative to the repo root). */
final class Words {
  static final Path DIR = Path.of(System.getProperty("wordle.data", "data"));

  /** Words that can be the secret answer (the official Wordle answer list). */
  final List<String> answers;
  /** Every word accepted as a guess; a superset of answers. */
  final List<String> valid;
  final Set<String> answerSet;
  final Set<String> validSet;

  Words() throws IOException {
    answers = read("answers.txt");
    valid = read("valid-guesses.txt");
    answerSet = new HashSet<>(answers);
    validSet = new HashSet<>(valid);
  }

  private static List<String> read(String name) throws IOException {
    Path p = DIR.resolve(name);
    if (!Files.exists(p)) {
      throw new IOException("Cannot find " + p.toAbsolutePath()
          + " - run from the repo root (or pass -Dwordle.data=<dir>)");
    }
    List<String> out = new ArrayList<>();
    for (String line : Files.readAllLines(p)) {
      line = line.trim();
      if (!line.isEmpty()) out.add(line);
    }
    return out;
  }
}
