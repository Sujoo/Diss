package sujoo.nlp.wordnet;

import java.util.List;

import com.google.common.collect.Lists;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class QueryWordNet {
    WordNetDatabase database;

    public QueryWordNet() {
        System.setProperty("wordnet.database.dir", "C:\\Users\\mbcusick\\Documents\\WordNet\\dict\\");
        database = WordNetDatabase.getFileInstance();
    }

    public List<Synset> getNounSynsets(String term) {
        return Lists.newArrayList(database.getSynsets(term, SynsetType.NOUN));
    }

    public List<Synset> getVerbSynsets(String term) {
        return Lists.newArrayList(database.getSynsets(term, SynsetType.VERB));
    }

    public List<Synset> getAllAdjectiveSynsets(String term) {
        List<Synset> resultList = Lists.newArrayList();

        resultList.addAll(getAdjectiveSynsets(term));
        resultList.addAll(getAdjectiveSatelliteSynsets(term));

        return resultList;
    }

    public List<Synset> getAdjectiveSynsets(String term) {
        return Lists.newArrayList(database.getSynsets(term, SynsetType.ADJECTIVE));
    }

    public List<Synset> getAdjectiveSatelliteSynsets(String term) {
        return Lists.newArrayList(database.getSynsets(term, SynsetType.ADJECTIVE_SATELLITE));
    }

    public List<Synset> getAdverbSynsets(String term) {
        return Lists.newArrayList(database.getSynsets(term, SynsetType.ADVERB));
    }
}
