package efficiency;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * created by zbs on 2018/2/26
 */
public class FileIO {
    private static final String FILENAME = "/tmp/test.html";

    public static long calReadOnceCost() {
        long start = System.currentTimeMillis();
        try {
            byte[] tempBytes = new byte[10000];
            InputStream in = new FileInputStream(FILENAME);
            in.read(tempBytes, 0, 10000);
            in.close();
            int offset = 0;
            for (int i = 0; i < 10000; i++) {
                offset++;
            }
        } catch (Exception e) {
            System.out.println("error occurs");
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static long calReadMultiCost() {
        long start = System.currentTimeMillis();
        try {
            byte[] tempBytes = new byte[10000];
            InputStream in = new FileInputStream(FILENAME);
            int offset = 0;
            for (int i = 0; i < 10000; i++) {
                in.read(tempBytes, offset, 1);
                offset++;
            }
            in.close();
        } catch (Exception e) {
            System.out.println("error occurs");
        }
        long end = System.currentTimeMillis();
        return end - start;
    }
}
