
/*
  Assumptions:
    (1) ascii characters only
    (2) there cannot be more than 2^32 occurrences of ANY character in the given string
    (3) number of array accesses is N + N/2 which is approximately O(N) where N is length of given string 
        (3.1) First N is for first for-loop
        (3.2) Second term i.e. N/2 is the approximate number of access for scanning first
    (4) space needed is about 256 characters
    (5) each character in the given string has a equal probability of occurrence as any other character
*/ 
public class FirstNonRepeatingCharacter {
    public static void main(String[] args) {
        String s = args[0];
        int len = s.length();
        char y = '\0';
        int[] xs = new int[256]; // array initialized to 0 already
        for(int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            xs[c]++;
        }
        for(int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (xs[c] == 1) { y = c; break; }
        }
        System.out.println("First unique character in *" + s + "* is " + y);
    }
}

