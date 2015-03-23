import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Solution {
  private static class Stream {
    private static InputStream in = System.in;

    public char getNextCharacter() {
      try {
        return (char) in.read();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public boolean isEnd() {
      boolean isEnd;
      try {
        in.mark(0);
        isEnd = (in.read() == -1);
        in.reset();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      return isEnd;
    }
  }

  public static void main(String[] args) {    
    test();    
    Stream stream = new Stream();
    printFirstUniqueCharacter(stream);
  }

  /*
   Assumptions are the following:
   (a) The data given is comprises of ASCII characters only so only 256 ASCII charactersj
   (b) There would not be more than 2^32 occurrences of any characters in the data else 
       the "count" data in `int[] xs` would encounter overflow.
   (c) Assumed that each character in the given data string has the same probability as any other character.
  */
  private static char y = '\0';
  private static int ASCII_CHARSET_SIZE = 256;
  private static int[]xs = new int[ASCII_CHARSET_SIZE];
  private static ArrayList<Character> cs = new ArrayList<Character>();

  /* Algorithm analysis:
     I used two data structures 
     (a) "int[] xs" to store the character-frequency count which has a constant size of 256 elements, each of type `int`.
     (b) "ArrayList<Character> cs" to store the characters reads
     (c) "char y" to detect which is the first-unique character
     In the method "printFirstUniqueCharacter", there are two loops
     (1) there's a general IO loop that reads each character from the input stream and stores the data 
         in the "cs" and increments the frequency count of the character in "xs". 
     (2) there's a "scan"-loop where the method reports whether a first-unique character is seen 
         or "error" if not.
     Together, the runtime complexity for (1) is proportionate to `n` (i.e. number of input characters)
     and the "scan-loop" is similarly proportionate to `n`, combining it should be `2n` or O(n)  
     Space complexity is n + 256 where 256 is the size of the `xs`.
  */
  private static void printFirstUniqueCharacter(Stream stream) {
    while(!stream.isEnd()) {
      char c = stream.getNextCharacter();
      cs.add(c);
      xs[c]++;
    }

    for(int i = 0; i < cs.size(); ++i) {
        char c = cs.get(i);
        if (xs[c] == 1) { y = c; break; }
    }
    if (y == '\0') System.out.println("ERROR");
    else System.out.println(y);
  }

  private static String firstUniqueCharacter(String s) {
    ArrayList<Character> ys = new ArrayList<Character>();
    char[] as = s.toCharArray();
    for(int i = 0; i < as.length; ++i) ys.add(as[i]);

    int[] xs = new int[ASCII_CHARSET_SIZE];
    for(int i = 0; i < ys.size(); ++i) {
      xs[ys.get(i)]++;
    }
    char y = '\0';
    for(int i = 0; i < ys.size(); ++i) {
        char c = ys.get(i);
        if (xs[c] == 1) { y = c; break; }
    }
    if (y == '\0') return "ERROR";
    else return y+"";
  }
  
  private static void test() {
    // add your test cases here
    // ensure that your test cases do not print to stdout when submitting 
    // as this will break the automated testing
    firstUniqueCharacter("ICELANDIC");
    firstUniqueCharacter("BARBARIAN");
    firstUniqueCharacter("AABBCCDDEE");
  }
}


