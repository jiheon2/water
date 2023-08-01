package kopo.poly.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
@Service
public class KakaoService {

    public String getAccessToken(String authorize_code) {

        log.info(this.getClass().getName() + ".service 엑세스 토큰 얻기 실행");

        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {

            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            /*  POST 요청을 위해 기본값이 false인 setDoOutput을 true로  */
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            /*  POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송  */
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=a9cf415eff39557471ef2d61bda1b541");
            sb.append("&redirect_uri=http://localhost:11000/kakao/login");
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            log.info(sb.toString());
            bw.flush();

            /*  결과 코드가 200이라면 성공  */
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            /*  요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기  */
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            /*  Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성  */
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("access_token : " + access_Token);
            log.info("refresh_token : " + refresh_Token);

            br.close();
            bw.close();

        } catch (IOException e) {

            /* TODO Auto-generated catch block  */
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".service 엑세스 토큰 얻기 종료");

        return access_Token;

    }

    /*  유저 정보 받아오기  */
    public HashMap<String, Object> getUserInfo(String access_Token) {

        log.info(this.getClass().getName() + ".service 유저 정보 받아오기 실행");

        /*  요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap 타입으로 선언  */
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {

            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            /*  요청에 필요한 Headeer에 포함될 내용  */
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            /*  결과 코드가 200이라면 성공  */
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            log.info("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            log.info("1");

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
//            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            log.info("2");

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
//            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            log.info("3");

            userInfo.put("nickname", nickname);
//            userInfo.put("email", email);

            log.info("nickname : " + nickname);
//            log.info("email : " + email);

        } catch (Exception e) {


            /*  TODO Auto-generated catch block  */
            e.printStackTrace();

        }

        log.info(this.getClass().getName() + ".service 유저 정보 받아오기 종료");


        return userInfo;

    }

    /*  카카오 로그아웃  */
    public void kakaoLogout(String access_Token) {

        String reqURL = "http://kapi.kakao.com/v1/user/logout";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            /*  결과 코드가 200이라면 성공  */
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            log.info("response body : " + result);

        } catch (IOException e) {
            /* TODO Auto-generated catch block  */
            e.printStackTrace();
        }


    }

}
