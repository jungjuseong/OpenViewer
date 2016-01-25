package net.bookinaction.utils;

/**
 * Created by NAVER on 2015-10-15.
 */
public class tif2png {

    public static void main(String[] args) {

        tif2png tp = new tif2png();
        tp.listing(1, 512);
    }

    public void listing(int start, int end) {

        for (int i=start; i<=end; i++) {
            System.out.println(String.format("convert %04d_pbh.tif %04d_pbh.png", i, i));
        }
    }
}
