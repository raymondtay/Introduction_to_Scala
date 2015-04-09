// This class takes a String of text as input generates a histogram of word counts.
// For example, for the input string: "java is great great fun" the data would look like:
// {"great": 2}, {"java": 1}, {"is": 1}, {"fun": 1}
// For simplicity, assume this is single-threaded and don't worry about punctuation.
import java.util.*;
public class MapReduce {
	public enum Sort { WORD, COUNT }
	private String input;

    private HashMap<String, Integer> hm = new HashMap<String, Integer>();

    public MapReduce(String input) { this.input = input; }

	public void process() {
        String [] xs = input.split(" ");
        for(int i = 0; i < xs.length; ++i) {
           Integer x = hm.get(xs[i]);
           if (x == null) x = 0;
           hm.put(xs[i], x+1);
        }
                        
    }

	/**
	* Returns the word count histogram. The sort parameter determines the order of Map entries:
	* - if Sort.WORD: the entries should be in alphabetical order
	* - if Sort.COUNT: the entries should be in order from most to least
	*
	* Does NOT have to run in constant time.
	*/
	public void printWordCounts(Sort sort) {
       if (sort == Sort.WORD) {
            TreeSet<String> t = new TreeSet<String>(hm.keySet());
            Iterator iter = t.iterator(); 
            while(iter.hasNext()) System.out.println(iter.next());
        } else
        if (sort == Sort.COUNT) {
            // created another map different from the map
            // i am using by reversing the key,value order
            // i.e. "hm" maps word to frequency
            // and now "tm" map frequency to word in descending order
            ArrayList<WordFreq> list = new ArrayList<WordFreq>();
            Iterator<String> iter = hm.keySet().iterator(); 
            while(iter.hasNext()) {
                String key = iter.next();
                Integer freq = hm.get(key);
                list.add(new WordFreq(key, freq));
            }

            Collections.sort(list);
            for(int i = 0; i < list.size(); i++) 
                System.out.println(list.get(i));
        } 
 
    }

} 

class WordFreq implements Comparable<WordFreq>{
    private String word;
    private Integer freq;
    WordFreq(String word, Integer freq) {
        this.word = word;
        this.freq = freq;
    }
    public int compareTo(WordFreq other) {
        if (this == other) return 0;
        if (this.freq < other.freq) return -1;
        else return 1;
    } 
    @Override
    public String toString() {
        return freq + "=" + word;
    }
}

class TestMapReduce {
    public static void main(String[] args) {
        MapReduce mr = new MapReduce("java great great fun, but scala scala scala is more fun");
        mr.process();
        System.out.println(".....sorted by frequency of occurrence each word...");
        mr.printWordCounts(MapReduce.Sort.COUNT);
        System.out.println(".....sorted by first letter of each word...");
        mr.printWordCounts(MapReduce.Sort.WORD);
    }
}

