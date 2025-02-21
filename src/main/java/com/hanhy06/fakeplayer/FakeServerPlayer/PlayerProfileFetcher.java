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

    public static String getPlayerUuid(String playerName) {
        try {
            HttpRequest uuidRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + playerName))
                    .GET()
                    .build();

            HttpResponse<String> uuidResponse = client.send(uuidRequest, HttpResponse.BodyHandlers.ofString());

            // 플레이어가 존재하지 않으면 상태 코드가 200이 아니거나 빈 본문일 수 있음
            if (uuidResponse.statusCode() != 200 || uuidResponse.body().isEmpty()) {
                System.out.println("존재하지 않는 플레이어 이름입니다: " + playerName);
                return null; // 또는 사용자 정의 예외를 발생시킬 수 있음
            }

            JsonObject uuidJson = JsonParser.parseString(uuidResponse.body()).getAsJsonObject();
            return uuidJson.get("id").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject getPlayerSkin(String playerName) {
        String uuid = getPlayerUuid(playerName);
        if (uuid == null) {
            // UUID를 가져오지 못한 경우 스킨 정보를 요청하지 않음
            return null;
        }
        try {
            HttpRequest skinRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"))
                    .GET()
                    .build();

            HttpResponse<String> skinResponse = client.send(skinRequest, HttpResponse.BodyHandlers.ofString());

            // 스킨 정보를 가져오지 못한 경우 확인
            if (skinResponse.statusCode() != 200 || skinResponse.body().isEmpty()) {
                System.out.println("플레이어 스킨 정보를 가져오지 못했습니다: " + playerName);
                return null;
            }

            JsonObject skinJson = JsonParser.parseString(skinResponse.body()).getAsJsonObject();
            return skinJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void applySkinFromGameProfile(GameProfile profile) {
        JsonObject object = getPlayerSkin(profile.getName());
        if (object == null) {
            System.out.println("스킨을 적용할 수 없습니다. 플레이어 이름을 확인하세요: " + profile.getName());
            return;
        }

        JsonArray array = object.getAsJsonArray("properties");

        String value = null;
        String signature = null;

        for (JsonElement element : array) {
            JsonObject temp = element.getAsJsonObject();

            if ("textures".equals(temp.get("name").getAsString())) {
                value = temp.get("value").getAsString();
                signature = temp.get("signature").getAsString();
            }
        }

        if (value != null && signature != null) {
            Property property = new Property("textures", value, signature);
            profile.getProperties().put("textures", property);
        } else {
            System.out.println("스킨 정보가 올바르지 않습니다: " + profile.getName());
        }
    }
}
