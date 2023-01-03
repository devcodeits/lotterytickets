package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.*;

public class Raffle {

    public static Connection conn;
    public static Random random = new Random();

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://localhost/lotto";
        Properties props = new Properties();
        props.setProperty("user", "lotto");
        props.setProperty("password", "lotto");
        props.setProperty("ssl", "false");
        conn = DriverManager.getConnection(url, props);
        conn.setAutoCommit(true);

        for (int i = 0; i < 2; i++) {
            // 1게임 예상번호
            HashSet<Integer> raffle = new HashSet<>();
            while (true) {
                // 패턴배열 1회차 뽑기
                List<Integer> arr1 = raffle();
                // 패턴배열 2회차 뽑기
                List<Integer> arr2 = raffle();
                // 패턴배열 모두 합치기 (중복 배제)
                raffle.addAll(arr1);
                raffle.addAll(arr2);


                if (raffle.size() < 6) {
                    // 중복 배제한 패턴배열이 6자리가 안되면 예상번호 다시 뽑기
                    raffle.clear();
                } else {
                    // 중복 배제한 패턴배열이 6자리 충족 !!
                    // 패턴배열 방식의 필요충분조건 확인
//                    ArrayList<Integer> selected = new ArrayList<>(raffle);
//                    Collections.sort(selected);
//
//                    boolean checked = checkRaffle(selected);
//
//                    // 충족하면 break;
//                    if (checked) break;
//                    else raffle.clear();
                    break;
                }
            }

            ArrayList<Integer> selected = new ArrayList<>(raffle);
            Collections.sort(selected);
            System.out.println(String.format("Selected : %s", new ObjectMapper().writeValueAsString(selected)));
        }

        conn.close();
    }

    public static boolean checkRaffle(List<Integer> arr) throws Exception {
        final String query = "SELECT COUNT(*) AS count FROM lotto_arr WHERE no1 = ? AND no2 = ? AND no3 = ?";
        PreparedStatement pstmt;
        ResultSet rs;

        for (int a = 0; a <= 3; a++) {
            for (int b = 1; b <= 4; b++) {
                for (int c = 2; c <= 5; c++) {
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, arr.get(a));
                    pstmt.setInt(2, arr.get(b));
                    pstmt.setInt(3, arr.get(c));
                    rs = pstmt.executeQuery();
                    rs.next();

                    int count = rs.getInt("count");
                    rs.close();
                    pstmt.close();

                    if (count == 0) return false;
                }
            }
        }
        return true;
    }

    public static List<Integer> raffle() throws Exception {
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // random rownum
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(*) as count FROM lotto_arr");
        rs.next();
        int count = rs.getInt("count");

        rs.close();
        stmt.close();

        // select arr
        pstmt = conn.prepareStatement("SELECT * FROM (SELECT * FROM (SELECT (ROW_NUMBER() OVER(ORDER BY random())) AS ROWNUM, * FROM lotto_arr) a ORDER BY random()) a WHERE rownum = ?");
        pstmt.setInt(1, random.nextInt(count) + 1);
        rs = pstmt.executeQuery();
        rs.next();

        int rownum = rs.getInt("rownum");
        int no1 = rs.getInt("no1");
        int no2 = rs.getInt("no2");
        int no3 = rs.getInt("no3");

        rs.close();
        pstmt.close();

        List<Integer> arr = new ArrayList<>();
        arr.add(no1);
        arr.add(no2);
        arr.add(no3);

//        System.out.println(String.format("SELECT [rownum=%s] %s, %s, %s", rownum, no1, no2, no3));

        return arr;
    }

}
