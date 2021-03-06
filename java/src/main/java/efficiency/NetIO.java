package efficiency;

import java.sql.*;

/**
 * created by zbs on 2018/2/24
 */
public class NetIO {
    private Connection connection;

    public NetIO() {
        try {
            new com.mysql.cj.jdbc.Driver();
            this.connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/aquatic?characterEncoding=UTF-8&useSSL=true", "root", "root");
            // 先执行一次消除初始化影响
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM test WHERE id=1";
            stmt.executeQuery(sql);
        } catch (Exception e) {
            System.exit(0);
        }
    }

    public long calMultiIOCost(int n) {
        long start = System.currentTimeMillis();
        int sum = 0;
        try {
            Statement stmt = connection.createStatement();
            for (int i = 0; i < n; i++) {
                String id = String.valueOf(i * 10);
                String sql = "SELECT * FROM test WHERE id=" + id;
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    sum += rs.getInt("id");
                }
                rs.close();
            }
            stmt.close();

        } catch (Exception e) {
            System.out.println("error execute sql");
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public long calOnceIOCost(int x) {
        long start = System.currentTimeMillis();
        int sum = 0;
        try {
            Statement stmt = connection.createStatement();
            String idMax = String.valueOf(x * 10);
            String sql = "SELECT * FROM test WHERE id <=" + idMax;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                sum += rs.getInt("id");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("error execute sql");
        }
        long end = System.currentTimeMillis();
        return end - start;
    }
}
