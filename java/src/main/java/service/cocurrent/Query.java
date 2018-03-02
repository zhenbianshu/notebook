package service.cocurrent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;

/**
 * created by zbs on 2018/2/24
 */
public class Query implements Callable<Integer> {
    public static final int CPU_CORE_COUNT = 8;
    private int index;
    private int maxId;
    private Connection connection;

    public Query(int index, int maxId) {
        this.index = index;
        this.maxId = maxId;
        try {
            new com.mysql.cj.jdbc.Driver();
            this.connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/aquatic?characterEncoding=UTF-8&useSSL=true", "root", "root");
        } catch (Exception e) {
            System.exit(0);
        }
    }

    @Override
    public Integer call() throws Exception {
        Integer sum = 0;
        try {
            Statement stmt = connection.createStatement();
            for (int i = 0; i <= maxId; i++) {
                if (i % CPU_CORE_COUNT != index) {
                    continue;
                }
                String id = String.valueOf(i);
                String sql = "SELECT * FROM test WHERE id=" + id;
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    sum += rs.getInt("id");
                }
            }

        } catch (Exception e) {
            System.out.println("error occurs");
        }

        return sum;
    }
}
