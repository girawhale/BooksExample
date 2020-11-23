package chapter2;

public class SelectionSort {
    // i와 j의 위치에 있는 값을 바꿈
    public static void swapElements(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // start로부터 시작하는 최솟값의 위치를 찾아 배열의 마지막 위치까지 간다
    public static int indexLowest(int[] array, int start) {
        int lowIndex = start;
        for (int i = start; i < array.length; i++) {
            if (array[i] < array[lowIndex])
                lowIndex = i;
        }
        return lowIndex;
    }

    // 선택정렬을 사용해 요소 정렬
    public static void selectionSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int j = indexLowest(array, i);
            swapElements(array, i, j);
        }
    }
}
