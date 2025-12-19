package com.example.TEAM202507_01.alramo.service;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import com.pusher.rest.Pusher;

@Service
public class AlramoService {
    private final Pusher pusher;

    public AlramoService() {

        // Pusher 대시보드에서 받은 정보 입력
        this.pusher = new Pusher("2092959", "6ed2c483bc85214a1b37", "2f25cf5ea1fc8bf81119");
        this.pusher.setCluster("ap3");
        this.pusher.setEncrypted(true);
    }

    public void sendNewPostNotification(String title) {
        // 'my-channel' 채널의 'new-post' 이벤트를 발생시킴
        // 데이터는 Map이나 DTO 객체로 전달 가능
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("url", "");
        pusher.trigger("my-channel","new-post",jsonObject);
    }
}
