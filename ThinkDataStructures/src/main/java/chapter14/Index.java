package chapter14;

import chapter8.WikiFetcher;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates a map from search term to set of TermCounter.
 *
 * @author downey
 */
public class Index {

    private Map<String, Set<TermCounter>> index = new HashMap<String, Set<TermCounter>>();

    /**
     * Adds a TermCounter to the set associated with `term`.
     *
     * @param term
     * @param tc
     */
    public void add(String term, TermCounter tc) {
        Set<TermCounter> set = get(term);

        // 어떤 검색어를 입력한다면? 새로운 Set을 생성!
        if (set == null) {
            set = new HashSet<TermCounter>();
            index.put(term, set);
        }
        // 그렇지 않다면 기존 Set을 변경
        set.add(tc);
    }

    /**
     * Looks up a search term and returns a set of TermCounters.
     *
     * @param term
     * @return
     */
    public Set<TermCounter> get(String term) {
        return index.get(term);
    }

    /**
     * Prints the contents of the index.
     */
    public void printIndex() {
        // 검색어에 반복문 실해
        for (String term : keySet()) {
            System.out.println(term);

            // 단어별 등장하는 페이지와 등장 횟수 표시
            Set<TermCounter> tcs = get(term);
            for (TermCounter tc : tcs) {
                Integer count = tc.get(term);
                System.out.println("    " + tc.getLabel() + " " + count);
            }
        }
    }

    /**
     * Returns the set of terms that have been indexed.
     *
     * @return
     */
    public Set<String> keySet() {
        return index.keySet();
    }

    /**
     * Add a page to the index.
     *
     * @param url        URL of the page.
     * @param paragraphs Collection of elements that should be indexed.
     */
    public void indexPage(String url, Elements paragraphs) {
        // TermCounter 객체를 만들고 단락에 있는 단어를 센다
        TermCounter tc = new TermCounter(url);
        tc.processElements(paragraphs);

        // TermCounter에 있는 각 검색어에 대해 TermCounter 객체를 인덱스에 추가한다.
        for (String key : tc.keySet())
            add(key, tc);
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        chapter8.WikiFetcher wf = new WikiFetcher();
        Index indexer = new Index();

        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        Elements paragraphs = wf.fetchWikipedia(url);
        indexer.indexPage(url, paragraphs);

        url = "https://en.wikipedia.org/wiki/Programming_language";
        paragraphs = wf.fetchWikipedia(url);
        indexer.indexPage(url, paragraphs);

        indexer.printIndex();
    }
}