public class Outcast {
    private WordNet words;
    public Outcast(WordNet wordnet) {
        words = wordnet;
    }     // constructor takes a WordNet object

    public String outcast(String[] nouns) {
        int d = 0;
        int max = 0;
        int outIndex = -1;
        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                d = d + words.distance(nouns[i], nouns[j]);
            }
            if (d > max) {
                max = d;
                outIndex = i;
            }
        }
        return nouns[outIndex];
    }   // given an array of WordNet nouns, return an outcast

    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast oc = new Outcast(wn);
        String[] nouns = {"horse", "zebra", "cat", "bear", "table"};
        System.out.println("outcast: " + oc.outcast(nouns));
    } // see test client below
}
