package util;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class implements a simple data structure that represents
 * an iterable container of Documents. Besides the individual documents
 * a corpus consists of the vocabulary index, and, if applicable, the
 * label and type index.
 */
public class Corpus implements Iterable<Document> {
    private final Index wordIndex;
    private final Index labelIndex;
    private final Index typeIndex;
    private final ArrayList<Document> documents;

    public Corpus () {
        this (new Index());
    }

    public Corpus (Index wordIndex) {
        this (wordIndex, new Index());
    }

    public Corpus (Index wordIndex, Index labelIndex) {
        this (wordIndex, labelIndex, new Index());
    }

    public Corpus (Index wordIndex, Index labelIndex, Index typeIndex) {
        this.wordIndex = wordIndex;
        this.labelIndex = labelIndex;
        this.typeIndex = typeIndex;
        this.documents = new ArrayList<Document>();
    }

    public Iterator<Document> iterator () {
        return documents.iterator();
    }

    public Index getWordIndex () { return wordIndex; }
    public Index getLabelIndex () { return labelIndex; }
    public Index getTypeIndex () { return typeIndex; }

    public int size () { return documents.size(); }

    public int getNumTopics () { return labelIndex.size(); }
    public int getNumTypes () { return typeIndex.size(); }
    public int getNumWords () { return wordIndex.size(); }

    /**
     * Read a file into a Corpus object. At this moment all files should
     * be four column, tab separated where the first column gives the id
     * number of the document, the second its document category (or categories)
     * the third should give the labels attached to the document and the fourth
     * is the text itself. Both the document categories and the labels should be
     * comma separated. The text itself will be tokenized on spaces, so make sure
     * that you have already done proper tokenization somewhere else.
     *
     * @param filename: a filename pointing to the file to read.
     * @throws IOException
     */
    public void readFile (String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            if (line.trim().length() == 0) {
                continue;
            }
            String[] fields = line.split("\t");
            String source = fields[0];
            String[] types = fields[1].split(",");
            String[] labels = fields[2].split(",");
            ArrayList<Integer> indexedLabels = new ArrayList<Integer>();
            for (String label : labels) {
                if (labelIndex.getId(label) == null) {
                    labelIndex.put(label);
                }
                indexedLabels.add(labelIndex.getId(label));
            }
            // We expect the document to be nicely tokenized, e.g. by Ucto
            String[] words = fields[3].split("\\s+");
            ArrayList<Integer> tokens = new ArrayList<Integer>();
            for (String word : words) {
                if (wordIndex.getId(word) == null) {
                    wordIndex.put(word);
                }
                tokens.add(wordIndex.getId(word));
            }
            ArrayList<Integer> indexedTypes = new ArrayList<Integer>();
            for (String type : types) {
                if (typeIndex.getId(type) == null) {
                    typeIndex.put(type);
                }
                indexedTypes.add(typeIndex.getId(type));
            }
            documents.add(new Document(tokens, source, indexedTypes, indexedLabels));
        }
        in.close();
    }

}
