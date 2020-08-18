package ru.gb.core.test;

import java.util.Arrays;

/*Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
необходимо выбросить RuntimeException.*/
public class ArrayTransform {
    public static int[] after4(int[] arr){
        int n = arr.length - 1;
        if(arr[n] == 4){
            return new int[0];
        }
        for(; n >= 0; n--){
            if(arr[n] == 4){
                return Arrays.copyOfRange(arr, n + 1, arr.length);
            }
        }
        throw new RuntimeException("4 is missed");
    }

    /*Написать метод, который проверяет состав массива из чисел 1 и 4. Если в нем нет хоть
    одной четверки или единицы, то метод вернет false;*/
    public static boolean check14(int[] arr){
        for(int i : arr){
            if(i == 1 || i == 4){
                return true;
            }
        }
        return false;
    }
}
