package ru.gb.core.test;

import org.junit.*;

public class ArrayTest {

    @Test
    public void after4Positive1(){
        int[] source = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        int[] res = {1, 7};
        Assert.assertArrayEquals(res, ArrayTransform.after4(source));
    }

    @Test
    public void after4Positive2(){
        int[] source = {4, 2, 2, 3, 1, 7};
        int[] res = {2, 2, 3, 1, 7};
        Assert.assertArrayEquals(res, ArrayTransform.after4(source));
    }

    @Test
    public void after4Empty(){
        int[] source = {4, 2, 2, 3, 1, 7, 4};
        int[] res = {};
        Assert.assertArrayEquals(res, ArrayTransform.after4(source));
    }

    @Test(expected = RuntimeException.class)
    public void after4no4(){
        int[] source = {2, 2, 3, 1, 7};
        ArrayTransform.after4(source);
    }

    @Test
    public void check14True(){
        int[] source = {1, 3, 5, 6, 7, 4};
        Assert.assertEquals(true, ArrayTransform.check14(source));
    }

    @Test
    public void check14False(){
        int[] source = {3, 5, 6, 7};
        Assert.assertEquals(false, ArrayTransform.check14(source));
    }
}
