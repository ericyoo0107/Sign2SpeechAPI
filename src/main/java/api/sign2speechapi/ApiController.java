package api.sign2speechapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final ApiRepository apiRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/get")
    public void getDataAndSave() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://api.kcisa.kr/openapi/service/rest/meta13/getCTE01701"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=c3347eee-0da5-46bf-ad34-4f95fbc52b32"); /*서비스키*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("20000", "UTF-8")); /*세션당 요청레코드수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지수*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept","application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        } else {

            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {

            sb.append(line);

        }
        rd.close();
        conn.disconnect();
        String jsonString = sb.toString();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // "response" → "body" → "items" → "item" 배열까지 내려간다.
        JsonNode itemsNode = rootNode
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        if (itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                // ======================================
                // 2) 각 JsonNode에서 필요한 필드를 추출
                //    - title          → gloss
                //    - subDescription → sign_video_url
                //    - signDescription → sign_description
                //    - signImages     → sign_images
                // ======================================
                String title            = itemNode.path("title").asText(null);
                String subDescription   = itemNode.path("subDescription").asText(null);
                String signDescription  = itemNode.path("signDescription").asText(null);
                String signImages       = itemNode.path("signImages").asText(null);

                // ======================================
                // 3) SignVideo 엔티티 생성 및 저장
                // ======================================
                SignVideo video = SignVideo.builder()   // 롬복의 @Builder 사용 가정
                        .gloss(title)
                        .videoUrl(subDescription)
                        .description(signDescription)
                        .imgUrl(signImages)
                        .build();

                apiRepository.save(video);
            }
        }
    }
}
