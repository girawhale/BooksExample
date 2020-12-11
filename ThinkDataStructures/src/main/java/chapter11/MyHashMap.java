/**
 *
 */
package chapter11;


import java.util.List;
import java.util.Map;

/**
 * Implementation of a HashMap using a collection of MyLinearMap and
 * resizing when there are too many entries.
 *
 * @author downey
 * @param <K>
 * @param <V>
 *
 */
public class MyHashMap<K, V> extends MyBetterMap<K, V> implements Map<K, V> {

    // 재해시하기 전 하위 맵당 평균 엔트리 개수
    protected static final double FACTOR = 1.0;

    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);

        //System.out.println("Put " + key + " in " + map + " size now " + map.size());

        // 하위 맵당 엔트리의 개수가 임계치를 넘지 않는지 확인
        if (size() > maps.size() * FACTOR) {
            rehash();
        }
        return oldValue;
    }

    /**
     * Doubles the number of maps and rehashes the existing entries.
     */
    /**
     *
     */
    protected void rehash() {
        List<MyLinearMap<K, V>> oldMaps = maps;

        makeMaps(maps.size() * 2);

        for (MyLinearMap<K, V> map : oldMaps)
            for (Entry<K, V> entry : map.getEntries())
                put(entry.getKey(), entry.getValue());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Map<String, Integer> map = new MyHashMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            map.put(new Integer(i).toString(), i);
        }
        Integer value = map.get("3");
        System.out.println(value);
    }
}
