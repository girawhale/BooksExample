package chapter15;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;


public class WikiCrawler {
    // 크롤링을 시작하는 URL
    @SuppressWarnings("unused")
    private final String source;

    // 결과를 저장하는 JedisIndex 객체
    private JedisIndex index;

    // 발견했지만 아직 인덱싱하지 않은 URL을 추적
    private Queue<String> queue = new LinkedList<String>();

    // 웹페이지를 읽고 파싱하는 객체
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Constructor.
     *
     * @param source
     * @param index
     */
    public WikiCrawler(String source, JedisIndex index) {
        this.source = source;
        this.index = index;
        queue.offer(source);
    }

    /**
     * Returns the number of URLs in the queue.
     *
     * @return
     */
    public int queueSize() {
        return queue.size();
    }

    /**
     * WikiCrawlerTest 클래스에서 호출하면 true고 나머지는 false여야 한다
     *
     * @param testing
     * @return URL of page indexed.
     * @throws IOException
     */
    public String crawl(boolean testing) throws IOException {
        // IF (testing)
        // FIFO 순서로 큐에서 URL을 선택하고 제거
        // WikiFetcher.readWikipedia 메소드를 호출하여 페이지의 내용 읽음
        // 각 페이지의 인덱싱 여부와 관계없이 인덱싱
        // 페이지에 있는 모든 내부 링크를 찾아 등장한 순서대로 큐에 추가
        // 인덱싱한 페이지의 URL을 반환

        // ELSE
        // FIFO 순서로 큐에서 URL을 선택하고 제거
        // URL이 이미 인덱싱 되어 있다면 인덱싱하지 않고 null 반환
        // 인덱싱 되어있지 않다면 WikiFetcher.fetchWikipedia 메소드를 호출하여 페이지 내용 읽기
        // 페이지를 인덱싱하고 큐에 링크를 추가한 후 인덱싱한 URL을 반환
        String url = queue.poll();

        Elements elements;
        if (testing) {
            elements = wf.readWikipedia(url);
        } else {
            if (index.isIndexed(url))
                return null;

            elements = wf.fetchWikipedia(url);
        }

        index.indexPage(url, elements);
        queueInternalLinks(elements);

        return url;
    }

    /**
     * Parses paragraphs and adds internal links to the queue.
     *
     * @param paragraphs
     */
    // NOTE: absence of access level modifier means package-level
    void queueInternalLinks(Elements paragraphs) {
        for (Element paragraph : paragraphs) {
            Elements elems = paragraphs.select("a[href]");

            for (Element elem : elems) {
                String url = elem.attr("href");

                if (url.startsWith("/wiki/")) {
                    String absUrl = "https://en.wikipedia.org" + url;
                    queue.add(absUrl);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // make a WikiCrawler
        Jedis jedis = JedisMaker.make();
        JedisIndex index = new JedisIndex(jedis);
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        WikiCrawler wc = new WikiCrawler(source, index);

        // for testing purposes, load up the queue
        Elements paragraphs = wf.fetchWikipedia(source);
        wc.queueInternalLinks(paragraphs);

        // loop until we index a new page
        String res;
        do {
            res = wc.crawl(false);

            // REMOVE THIS BREAK STATEMENT WHEN crawl() IS WORKING
            break;
        } while (res == null);

        Map<String, Integer> map = index.getCounts("the");
        for (Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry);
        }
    }
}
