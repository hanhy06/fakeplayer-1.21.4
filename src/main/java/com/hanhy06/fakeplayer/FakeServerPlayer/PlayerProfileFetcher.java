package com.hanhy06.fakeplayer.FakeServerPlayer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class PlayerProfileFetcher {
    private static HttpClient client = HttpClient.newHttpClient();

    public static String getPlayerUuid(String playerName){
        try {
            HttpRequest uuidRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + playerName))
                    .GET()  // HTTP GET 방식 요청
                    .build();

            HttpResponse<String> uuidResponse = client.send(uuidRequest, HttpResponse.BodyHandlers.ofString());

            JsonObject uuidJson = JsonParser.parseString(uuidResponse.body()).getAsJsonObject();
            String uuid = uuidJson.get("id").getAsString();

            return uuid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject getPlayerSkin(String playerName) {
        try {
            HttpRequest skinRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + getPlayerUuid(playerName) + "?unsigned=false"))
                    .GET()  // HTTP GET 방식 요청
                    .build();

            // 스킨 정보 요청을 동기적으로 전송하고 응답을 문자열 형태로 받습니다.
            HttpResponse<String> skinResponse = client.send(skinRequest, HttpResponse.BodyHandlers.ofString());

            // 응답으로 받은 JSON 문자열을 JsonObject로 파싱합니다.
            JsonObject skinJson = JsonParser.parseString(skinResponse.body()).getAsJsonObject();

            return skinJson;
        } catch (Exception e) {
            // 예외가 발생하면 에러 메시지와 스택 트레이스를 출력하고 null을 반환합니다.
            e.printStackTrace();
            return null;
        }
    }

    public static void applySkinFromGameProfile(GameProfile profile){
        JsonObject object = getPlayerSkin(profile.getName());
        JsonArray array = object.getAsJsonArray("properties");

        String value =null;
        String signature = null;

        for(JsonElement element :array){
            JsonObject temp = element.getAsJsonObject();

            if ("textures".equals(temp.get("name").getAsString())) {
                value = temp.get("value").getAsString();
                signature = temp.get("signature").getAsString();
            }
        }

        Property property = new Property("textures",value,signature);
        profile.getProperties().put("textures",property);
    }
}
