import java.util.*;

public class TestPerfomance {
    final private int SOURCE_ARRAY_SIZE = 10000;
    // массив, который будем использовать как источник значений
    private String sourceArray[] = new String[SOURCE_ARRAY_SIZE];

    // массив, который будет источником для поисковых значений (1/10 от общего количества)
    private String findArray[] = new String[SOURCE_ARRAY_SIZE / 10];

    private TestPerfomance() {
        // заполним массивы тестовыми данными
        for (int i = 0; i < SOURCE_ARRAY_SIZE; i++) {
            UUID uuid = UUID.randomUUID();
            sourceArray[i] = uuid.toString();
            if (i % 10 == 0)
                findArray[i / 10] = uuid.toString();
        }

        long startTime, estimatedTime;

        // ADD
        // тестим LinkedList
        List<String> linkedList = new LinkedList<>();
        startTime = System.nanoTime();
        testAddList(linkedList);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Add " + sourceArray.length + " elements to LinkedList - " + printFormattedNanoSeconds(estimatedTime));

        // ArrayList
        List<String> arrayList = new ArrayList<>();
        startTime = System.nanoTime();
        testAddList(arrayList);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Add " + sourceArray.length + " elements to ArrayList  - " + printFormattedNanoSeconds(estimatedTime));

        // TreeSet
        Set<String> stringTreeSet = new TreeSet<>();
        startTime = System.nanoTime();
        testAddSet(stringTreeSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Add " + sourceArray.length + " elements to TreeSet    - " + printFormattedNanoSeconds(estimatedTime));

        // HashSet
        Set<String> stringHashSet = new HashSet<>();
        startTime = System.nanoTime();
        testAddSet(stringHashSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Add " + sourceArray.length + " elements to HashSet    - " + printFormattedNanoSeconds(estimatedTime));

        // SEARCH
        // тестим LinkedList
        startTime = System.nanoTime();
        testSearchList(linkedList);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Search " + findArray.length + " elements in LinkedList - " + printFormattedNanoSeconds(estimatedTime));

        // ArrayList
        startTime = System.nanoTime();
        testSearchList(arrayList);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Search " + findArray.length + " elements in ArrayList  - " + printFormattedNanoSeconds(estimatedTime));

        // TreeSet
        startTime = System.nanoTime();
        testSearchSet(stringTreeSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Search " + findArray.length + " elements in TreeSet    - " + printFormattedNanoSeconds(estimatedTime));

        // HashSet
        startTime = System.nanoTime();
        testSearchSet(stringHashSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Search " + findArray.length + " elements in HashSet    - " + printFormattedNanoSeconds(estimatedTime));

        // REMOVE
        // тестим LinkedList
        startTime = System.nanoTime();
        testRemoveList(linkedList);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Remove 1000 elements from LinkedList - " + printFormattedNanoSeconds(estimatedTime));

        // ArrayList
        startTime = System.nanoTime();
        testRemoveList(arrayList);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Remove 1000 elements from ArrayList  - " + printFormattedNanoSeconds(estimatedTime));

        // TreeSet
        startTime = System.nanoTime();
        testRemoveSet(stringTreeSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Remove 1000 elements from TreeSet    - " + printFormattedNanoSeconds(estimatedTime));

        // HashSet
        startTime = System.nanoTime();
        testRemoveSet(stringHashSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Remove 1000 elements from HashSet    - " + printFormattedNanoSeconds(estimatedTime));
    }

    public static void main(String[] args) {
        new TestPerfomance();
    }

    private void testAddList(List<String> list) {
        for (String aSourceArray : sourceArray) {
            list.add(aSourceArray);
        }
    }

    private void testAddSet(Set<String> set) {
        for (String aSourceArray : sourceArray) {
            set.add(aSourceArray);
        }
    }

    private void testSearchList(List<String> list) {
        for (String item : findArray) {
            list.contains(item);
        }
    }

    private void testSearchSet(Set<String> set) {
        for (String item : findArray) {
            set.contains(item);
        }
    }

    private void testRemoveList(List<String> list) {
        for (String item : findArray)
            list.remove(item);
//        list.clear();
    }

    private void testRemoveSet(Set<String> set) {
        for (String item : findArray)
            set.remove(item);
//        set.clear();
    }

    private String printFormattedNanoSeconds(long nanoseconds) {
        long nanos = 0;
        long micros = 0;
        long milis = 0;
        long seconds = 0;

        nanos = nanoseconds % 1000;
        nanoseconds /= 1000;
        milis = nanoseconds % 1000;
        nanoseconds /= 1000;
        micros = nanoseconds % 1000;
        nanoseconds /= 1000;
        seconds = nanoseconds;

        return String.format("%3ds %3dms %3dmcs %3dns", seconds, micros, milis, nanos);
    }
}
