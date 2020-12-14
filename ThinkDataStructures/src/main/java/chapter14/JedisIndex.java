package chapter14;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Represents a Redis-backed web search index.
 */
public class JedisIndex {

    private Jedis jedis;

    /**
     * Constructor.
     * Jedis 객체를 인자로 받는 생성자
     *
     * @param jedis
     */
    public JedisIndex(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * Returns the Redis key for a given search term.
     *
     * @return Redis key.
     */
    private String urlSetKey(String term) {
        return "URLSet:" + term;
    }

    /**
     * Returns the Redis key for a URL's TermCounter.
     *
     * @return Redis key.
     */
    private String termCounterKey(String url) {
        return "TermCounter:" + url;
    }

    /**
     * Checks whether we have a TermCounter for a given URL.
     *
     * @param url
     * @return
     */
    public boolean isIndexed(String url) {
        String redisKey = termCounterKey(url);
        return jedis.exists(redisKey);
    }

    /**
     * Adds a URL to the set associated with `term`.
     * 검색어에 연관된 set에 URL을 추가
     *
     * @param term
     * @param tc
     */
    public void add(String term, TermCounter tc) {
        jedis.sadd(urlSetKey(term), tc.getLabel());
    }

    /**
     * Looks up a search term and returns a set of URLs.
     * 검색어를 조회하여 URL 집합을 반환
     *
     * @param term
     * @return Set of URLs.
     */
    public Set<String> getURLs(String term) {
        return jedis.smembers(urlSetKey(term));
    }

    /**
     * Looks up a term and returns a map from URL to count.
     * URL에 있는 검색어가 등장하는 횟수를 반환
     *
     * @param term
     * @return Map from URL to count.
     */
    public Map<String, Integer> getCounts(String term) {
        Map<String, Integer> map = new HashMap<>();
        Set<String> urlSet = getURLs(term);

        for (String url : urlSet) {
            map.put(url, getCount(url, term));
        }

        return map;
    }

    /**
     * Returns the number of times the given term appears at the given URL.
     * 검색어를 인자로 받아 Map<String,String> 객체를 반환하는 메소드
     * 검색어를 포함한 각 URL에서 페이지에 검색어가 등장한 횟수를 매핑
     *
     * @param url
     * @param term
     * @return
     */
    public Integer getCount(String url, String term) {
        return new Integer(jedis.hget(termCounterKey(url), term));
    }

    /**
     * Adds a page to the index.
     * 웹 페이지에 인덱스를 추가하는 메소드
     * String 타입의 URL과 인덱싱해야 하는 페이지의 요소들을 포함한
     * jsoup 라이브러리의 Elements 객체를 인자로 받는다
     *
     * @param url        URL of the page.
     * @param paragraphs Collection of elements that should be indexed.
     */
    public void indexPage(String url, Elements paragraphs) {
        TermCounter tc = new TermCounter(url);
        tc.processElements(paragraphs);

        pushTermCounterToRedis(tc);
    }

    /**
     * Pushes the contents of the TermCounter to Redis.
     * TermCounter에 있는 내용을 레디스로 푸시
     *
     * @param tc
     * @return List of return values from Redis.
     */
    public List<Object> pushTermCounterToRedis(TermCounter tc) {
        Transaction t = jedis.multi();

        String url = tc.getLabel();
        String termCounterKey = termCounterKey(url);

        t.del(termCounterKey);

        for (String term : tc.keySet()) {
            t.hset(termCounterKey, term, tc.get(term).toString());
            t.sadd(urlSetKey(term), url);
        }

        return t.exec();
    }

    /**
     * Prints the contents of the index.
     * <p>
     * Should be used for development and testing, not production.
     */
    public void printIndex() {
        // loop through the search terms
        for (String term : termSet()) {
            System.out.println(term);

            // for each term, print the pages where it appears
            Set<String> urls = getURLs(term);
            for (String url : urls) {
                Integer count = getCount(url, term);
                System.out.println("    " + url + " " + count);
            }
        }
    }

    /**
     * Returns the set of terms that have been indexed.
     * <p>
     * Should be used for development and testing, not production.
     *
     * @return
     */
    public Set<String> termSet() {
        Set<String> keys = urlSetKeys();
        Set<String> terms = new HashSet<String>();
        for (String key : keys) {
            String[] array = key.split(":");
            if (array.length < 2) {
                terms.add("");
            } else {
                terms.add(array[1]);
            }
        }
        return terms;
    }

    /**
     * Returns URLSet keys for the terms that have been indexed.
     * <p>
     * Should be used for development and testing, not production.
     *
     * @return
     */
    public Set<String> urlSetKeys() {
        return jedis.keys("URLSet:*");
    }

    /**
     * Returns TermCounter keys for the URLS that have been indexed.
     * <p>
     * Should be used for development and testing, not production.
     *
     * @return
     */
    public Set<String> termCounterKeys() {
        return jedis.keys("TermCounter:*");
    }

    /**
     * Deletes all URLSet objects from the database.
     * <p>
     * Should be used for development and testing, not production.
     *
     * @return
     */
    public void deleteURLSets() {
        Set<String> keys = urlSetKeys();
        Transaction t = jedis.multi();
        for (String key : keys) {
            t.del(key);
        }
        t.exec();
    }

    /**
     * Deletes all URLSet objects from the database.
     * <p>
     * Should be used for development and testing, not production.
     *
     * @return
     */
    public void deleteTermCounters() {
        Set<String> keys = termCounterKeys();
        Transaction t = jedis.multi();
        for (String key : keys) {
            t.del(key);
        }
        t.exec();
    }

    /**
     * Deletes all keys from the database.
     * <p>
     * Should be used for development and testing, not production.
     *
     * @return
     */
    public void deleteAllKeys() {
        Set<String> keys = jedis.keys("*");
        Transaction t = jedis.multi();
        for (String key : keys) {
            t.del(key);
        }
        t.exec();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Jedis jedis = JedisMaker.make();
        JedisIndex index = new JedisIndex(jedis);

        //index.deleteTermCounters();
        //index.deleteURLSets();
        //index.deleteAllKeys();
        loadIndex(index);

        Map<String, Integer> map = index.getCounts("the");
        for (Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry);
        }
    }

    /**
     * Stores two pages in the index for testing purposes.
     *
     * @return
     * @throws IOException
     */
    private static void loadIndex(JedisIndex index) throws IOException {
        WikiFetcher wf = new WikiFetcher();

        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        Elements paragraphs = wf.readWikipedia(url);
        index.indexPage(url, paragraphs);

        url = "https://en.wikipedia.org/wiki/Programming_language";
        paragraphs = wf.readWikipedia(url);
        index.indexPage(url, paragraphs);
    }
}
