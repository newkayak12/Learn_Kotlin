package ch05;

import java.util.function.Consumer;

public class HighOrder {
    static void forEach(int[] array, Consumer<Integer> action ) {
        for(int element : array) action.accept(element);
    }

    public static void main(String[] args) {
        forEach(new int[]{1, 2, 3, 4}, new Consumer<Integer>() {
            @Override
            public void accept(Integer it) {
                    if( it < 2 || it > 3) return;
                    System.out.println();
            }
        });
    }
}
