package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class Crawling {

    public static Connection conn;

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://localhost/lotto";
        Properties props = new Properties();
        props.setProperty("user", "lotto");
        props.setProperty("password", "lotto");
        props.setProperty("ssl", "false");
        conn = DriverManager.getConnection(url, props);
        conn.setAutoCommit(true);

        int drwNo = fetchMaxDrwNo();
        while (true) {
            drwNo++;

            LottoModel model = getLottoNumber(drwNo);

            if (model == null) break;

            addLottoModel(model);

            addLottoArr(model.drwtNo1, model.drwtNo2, model.drwtNo3);
            addLottoArr(model.drwtNo1, model.drwtNo2, model.drwtNo4);
            addLottoArr(model.drwtNo1, model.drwtNo2, model.drwtNo5);
            addLottoArr(model.drwtNo1, model.drwtNo2, model.drwtNo6);
            addLottoArr(model.drwtNo2, model.drwtNo3, model.drwtNo4);
            addLottoArr(model.drwtNo2, model.drwtNo3, model.drwtNo5);
            addLottoArr(model.drwtNo2, model.drwtNo3, model.drwtNo6);
            addLottoArr(model.drwtNo3, model.drwtNo4, model.drwtNo5);
            addLottoArr(model.drwtNo3, model.drwtNo4, model.drwtNo6);
        }

        conn.close();
    }

    // 회차별 당첨정보 조회
    public static LottoModel getLottoNumber(int drwNo) throws Exception {
        String addr = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=%s";
        URL url = new URL(String.format(addr, drwNo));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        StringBuilder builder = new StringBuilder();
        try (InputStream is = connection.getInputStream();
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader br = new BufferedReader(isr);
        ) {
            String buff;
            while ((buff = br.readLine()) != null) builder.append(buff);
        }

        System.out.println(String.format("[FETCH : %s] %s", drwNo, builder.toString()));
        LottoModel model = new ObjectMapper().readValue(builder.toString(), LottoModel.class);
        if ("success".equals(model.returnValue)) {
            String drwtNos = "";
            drwtNos += model.drwtNo1 < 10 ? "0" + model.drwtNo1 : model.drwtNo1;
            drwtNos += model.drwtNo2 < 10 ? "0" + model.drwtNo2 : model.drwtNo2;
            drwtNos += model.drwtNo3 < 10 ? "0" + model.drwtNo3 : model.drwtNo3;
            drwtNos += model.drwtNo4 < 10 ? "0" + model.drwtNo4 : model.drwtNo4;
            drwtNos += model.drwtNo5 < 10 ? "0" + model.drwtNo5 : model.drwtNo5;
            drwtNos += model.drwtNo6 < 10 ? "0" + model.drwtNo6 : model.drwtNo6;
            model.drwtNos = drwtNos;
            return model;
        } else return null;
    }

    // DB : 최근 데이터 회차번호 조회
    public static Integer fetchMaxDrwNo() throws Exception {
        PreparedStatement pstmt = conn.prepareStatement("SELECT COALESCE(MAX(drwNo), 0) AS drwNo FROM lotto_model");
        ResultSet rs = pstmt.executeQuery();
        rs.next();

        Integer result = rs.getInt("drwNo");

        rs.close();
        pstmt.close();
        return result;
    }

    // DB : 당첨정보 등록
    public static void addLottoModel(LottoModel model) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO lotto_model VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        pstmt.setString(1, model.returnValue);
        pstmt.setString(2, model.drwNoDate);
        pstmt.setInt(3, model.drwNo);
        pstmt.setInt(4, model.drwtNo1);
        pstmt.setInt(5, model.drwtNo2);
        pstmt.setInt(6, model.drwtNo3);
        pstmt.setInt(7, model.drwtNo4);
        pstmt.setInt(8, model.drwtNo5);
        pstmt.setInt(9, model.drwtNo6);
        pstmt.setInt(10, model.bnusNo);
        pstmt.setString(11, model.drwtNos);
        pstmt.executeUpdate();
        pstmt.close();
    }

    // DB : 당첨번호 배열 등록
    public static void addLottoArr(Integer no1, Integer no2, Integer no3) {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("INSERT INTO lotto_arr VALUES(?,?,?)");
            pstmt.setInt(1, no1);
            pstmt.setInt(2, no2);
            pstmt.setInt(3, no3);
            pstmt.executeUpdate();
        } catch (Exception e) {
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ee) {
            }
        }
    }


}

class LottoModel {
    public String returnValue;
    public String drwNoDate;
    public Integer drwNo;
    public Integer drwtNo1;
    public Integer drwtNo2;
    public Integer drwtNo3;
    public Integer drwtNo4;
    public Integer drwtNo5;
    public Integer drwtNo6;
    public Integer bnusNo;
    public String drwtNos;

    public Long totSellamnt;
    public Long firstWinamnt;
    public Long firstPrzwnerCo;
    public Long firstAccumamnt;
}

class LottoArr {

    public static LottoArr newInstance(Integer no1, Integer no2, Integer no3) {
        LottoArr arr = new LottoArr();
        arr.no1 = no1;
        arr.no2 = no2;
        arr.no3 = no3;
        return arr;
    }


    public Integer no1;
    public Integer no2;
    public Integer no3;
}
