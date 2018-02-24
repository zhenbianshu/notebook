package efficiency;

/**
 * created by zbs on 2018/2/23
 */
public class AlgComplexity {
    public static long calQuadXSpendTime(long x) {
        long start = System.currentTimeMillis();
        long sum = 0L;
        for (long i = 0L; i < x; i++) {
            for (long j = 0L; j < x; j++) {
                sum += i * j;
            }
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static long calXSpendTime(long x) {
        long start = System.currentTimeMillis();
        long sum = 0L;
        for (long i = 0L; i < x; i++) {
            sum += i * i;
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static long calLogXSpendTime(long x) {
        long start = System.currentTimeMillis();
        long sum = 0L;
        while (x > 0) {
            sum += x * x;
            x = x / 2;
        }
        long end = System.currentTimeMillis();
        return end - start;
    }
}
