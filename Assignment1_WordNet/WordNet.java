import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import java.util.HashMap;

public class WordNet {
    private HashMap<Integer, String> idToSynset;
    private HashMap<String, Bag<Integer>> synsetToId;
    private SAP sap;
    private Digraph d;
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        idToSynset = new HashMap<>();
        synsetToId = new HashMap<>();

        In synset = new In(synsets);
        In hypernym = new In(hypernyms);

        while (synset.hasNextLine()) {
            String[] gloss = synset.readLine().split(",");
            String words = gloss[1];
            int num = Integer.parseInt(gloss[0]);
            String[] word = words.split(" ");
            for (String i : word) {
                if (synsetToId.get(i) == null) {
                    Bag<Integer> id = new Bag<>();
                    id.add(num);
                    synsetToId.put(i, id);
                } else {
                    synsetToId.get(i).add(num);
                }
            }
            idToSynset.put(num, words);
        }

        Digraph d = new Digraph(idToSynset.size());

        while (hypernym.hasNextLine()){
            String v[] = hypernym.readLine().split(",");
            int id = Integer.parseInt(v[0]);
            for (int i = 1; i < v.length; i++) {
                int w = Integer.parseInt(v[i]);
                d.addEdge(id, w);
            }
        }
        this.sap = new SAP(d);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetToId.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("Expected word to be a non-null string");
        return synsetToId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        Bag<Integer> nounAid = synsetToId.get(nounA);
        Bag<Integer> nounBid = synsetToId.get(nounB);
        return this.sap.length(nounAid, nounBid);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if(!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();//("noun is an invalid WordNet noun");
        Bag<Integer> nounAid = synsetToId.get(nounA);
        Bag<Integer> nounBid = synsetToId.get(nounB);
        int ancestorId = this.sap.ancestor(nounAid, nounBid);
        return idToSynset.get(ancestorId);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet test = new WordNet("synsets.txt", "hypernyms.txt");
        //for (String i: test.nouns())
            //System.out.println(i);
        System.out.println("worm is noun? " + test.isNoun("worm"));
        System.out.println("baosui is noun? " + test.isNoun("baosui"));
        System.out.println("distance: " + test.distance("worm", "bird"));
        System.out.println("sap: " + test.sap("worm", "bird"));
    }
}
