package chapter7;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     * <p>
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     * <p>
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     * that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        testConjecture(destination, source, 10);
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    public static void testConjecture(String destination, String source, int limit) throws IOException {
        String url = source;
        for (int i = 0; i < limit; i++) {
            if (visited.contains(url)) {
                System.err.println("이미 방문한 페이지 입니다!");
                return;
            }
            visited.add(url);

            Element element = getFirstValidLink(url);
            if (element == null) {
                System.err.println("유효한 페이지 링크가 존재하지 않습니다.");
                return;
            }

            System.out.println("**" + element.text() + "**");
            url = element.absUrl("href");

            if (url.equals(destination)) {
                System.out.println((i + 1) + "번째에 도착했습니다!!");
                return;
            }
        }

    }

    public static Element getFirstValidLink(String url) throws IOException {
        System.out.println("Fetching " + url + "...");
        Elements paragraphs = wf.fetchWikipedia(url);
        WikiParser wp = new WikiParser(paragraphs);

        return wp.findFirstLink();
    }
}
