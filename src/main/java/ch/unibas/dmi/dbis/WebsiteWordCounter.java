package ch.unibas.dmi.dbis;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * @author Ralph Gasser
 * @version 1.0.0
 */
public class WebsiteWordCounter {

    /** Global logger instance. */
    public static final Logger LOGGER = LogManager.getLogger(WebsiteWordCounter.class.getName());

    /**
     * Counts the top-k words on a given website.
     *
     * @param url The URL of the website.
     * @return List of top-k words (string and occurrence count).
     */
    public List<Pair<String,Integer>> topKWordsOnWebsite(String url, int k) {
        final String website = this.loadWebsite(url);
        //LOGGER.info("Website downloaded successfully!");

        final String[] tokens = this.tokenize(website);
        //LOGGER.info("Entries tokenized successfully!");

        final Map<String,Integer> counts = this.count(tokens);
        //LOGGER.info("Count completed! Number of words found: {}", counts.size());

        /* Prepare list of entries. */
        final List<Pair<String,Integer>> topK = new LinkedList<>();
        for (Map.Entry<String,Integer> entry : counts.entrySet()) {
            topK.add(Pair.of(entry.getKey(), entry.getValue()));
        }

        /* Sort list. */
        topK.sort((o1, o2) -> o2.getRight().compareTo(o1.getRight()));

        /* Remove items from list. */
        for (int i = k; i < topK.size() ; i++) {
            topK.remove(i);
        }
        return topK;
    }

    /**
     * Loads the content of a website and returns it as a string.
     *
     * @param url URL of the website to load.
     * @return The text content of the website as a string.
     */
    public String loadWebsite(String url) {
        try {
            // Fetch the HTML content from the website
            Document document = Jsoup.connect(url).get();

            // Extract text from the HTML document
            String extractedText = document.body().text();

            // Clean the extracted text (remove HTML tags) and return it
            return Jsoup.clean(extractedText, Safelist.none());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Splits the given text into tokens (i.e., individual words).
     *
     * @param text The text to split.
     * @return The split text.
     */
    public String[] tokenize(String text) {
        return text.split("\\s+");
    }

    /**
     * Takes a list of tokens and counts the number of occurrences of each token.
     *
     * @param tokens The array of tokens.
     * @return The map of count words.
     */
    public Map<String,Integer> count(String[] tokens) {
        final Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < tokens.length; i++) {
            if (map.containsKey(tokens[i])) {
                map.put(tokens[i], map.get(tokens[i]) + 1);
            } else {
                map.put(tokens[i], 1);
            }
        }
        return map;
    }
}
