package efficiency;

import service.cocurrent.Query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * created by zbs on 2018/2/24
 */
public class Concurrent {

    public static long calSingleThreadCost(int n) {
        try {
            new com.mysql.cj.jdbc.Driver();
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/aquatic?characterEncoding=UTF-8&useSSL=true", "root", "123456");
            Statement stmt = connection.createStatement();
            long start = System.currentTimeMillis();

            int sum = 0;
            for (int i = 0; i < n; i++) {
                String id = String.valueOf(i);
                String sql = "SELECT * FROM test WHERE id=" + id;
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    sum += rs.getInt("id");
                }
            }
            System.out.println(sum);
            long end = System.currentTimeMillis();
            return end - start;
        } catch (Exception e) {
            System.exit(0);
        }

        return 0L;
    }

    public static long calMultiThreadCost(int n) {
        long start = System.currentTimeMillis();
        int sum = 0;
        List<Future<Integer>> futureList = new ArrayList<>(8);
        ExecutorService executorService = Executors.newFixedThreadPool(Query.CPU_CORE_COUNT);
        for (int i = 0; i < Query.CPU_CORE_COUNT; i++) {
            futureList.add(executorService.submit(new Query(i, n)));
        }

        try {
            executorService.shutdown();
            if (!executorService.isTerminated()) {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            }
            for (Future<Integer> future : futureList) {
                sum += future.get();
            }
        } catch (Exception e) {
            System.out.println("interrupt");
        }
        System.out.println(sum);

        long end = System.currentTimeMillis();
        return end - start;
    }
}
