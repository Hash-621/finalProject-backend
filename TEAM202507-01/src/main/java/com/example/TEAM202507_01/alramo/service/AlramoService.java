package com.example.TEAM202507_01.alramo.service;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import com.pusher.rest.Pusher;

@Service
//의미: 이 클래스가 비즈니스 로직을 처리하는 서비스(Service)라는것을 Spring에게 알림
public class AlramoService {
    private final Pusher pusher;
//해석: AlramoService 클래스를 만들고, 그 안에서만 쓸 pusher 변수를 선언

    public AlramoService() {
        // Pusher 대시보드에서 받은 정보 입력
        this.pusher = new Pusher("2092959", "6ed2c483bc85214a1b37", "2f25cf5ea1fc8bf81119");
        //new Pusher("앱ID", "Key", "Secret"): Pusher 서비스에 가입하면 주는 고유한 ID 열쇠 이 열쇠가 있어야 내 계정으로 알림을 보낼 수 있음

        this.pusher.setCluster("ap3");
        //.setCluster("ap3"): 서버의 위치 ap3는 보통 아시아 태평양(도쿄 등) 지역을 의미 가까운 서버를 써야 빠름

        this.pusher.setEncrypted(true);
        //.setEncrypted(true): 통신 내용을 암호화(보안 처리)해서 보내겠다는 설정
    }

    public void sendNewPostNotification(String title) {

        JsonObject jsonObject = new JsonObject();
        // 'my-channel' 채널의 'new-post' 이벤트를 발생시킴
        // 데이터는 Map이나 DTO 객체로 전달 가능
        //해석: JsonObject라는 빈 상자를 하나 만듭니다. 여기에 알림 내용을 담음

        jsonObject.addProperty("title", title);
        jsonObject.addProperty("url", "");
        //해석: 상자에 내용을 담음 , "title"이라는 이름표로 입력받은 title 값을 넣음, "url"이라는 이름표로 빈 값("")을 넣음

        pusher.trigger("my-channel","new-post",jsonObject);
        //해석:알림을 발사(trigger)
        //"my-channel": 방송 채널 이름. 프론트엔드(화면)에서는 이 채널을 듣고(구독하고) 있다가 알림이 오면 반응
        //"new-post": 이벤트 이름. "새 글이 올라왔다"는 구체적인 사건 명칭
        //jsonObject: 실제로 보낼 내용물(제목 등)
    }
}
