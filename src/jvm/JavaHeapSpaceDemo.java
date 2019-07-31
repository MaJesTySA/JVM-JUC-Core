package jvm;

import java.util.Random;

//-Xms10m -Xmx10m -XX:MaxDirectMemorySize=5m -XX:+PrintGCDetails
public class JavaHeapSpaceDemo {
    public static void main(String[] args) {
        String str = "adf";
        while (true) {
            str += str + new Random().nextInt(1111111) + new Random().nextInt(222222);
            str.intern();
        }
    }
}
