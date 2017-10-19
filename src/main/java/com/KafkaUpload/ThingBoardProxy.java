package com.KafkaUpload;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

import java.util.HashMap;

/**
 * Created by tangjialiang on 2017/10/18.
 *
 * thingsboard 的操作代理
 */
public class ThingBoardProxy {

    private String host ;
    private int port ;
    private ThingsBoardApi api ;
    private String username  ;
    private String password  ;

    private HashMap<String, Device> deviceMap = new HashMap<String, Device>() ;

    /**
     *  todo
     *  two ways to guarantee
     *  1. 在启动时，使用重发机制确保一定能获取到token。（从实际使用场景上，该方法性能较优）
     *  2. 在每条发送到thingsboard的消息前进行请求装饰，优先确保token信息成功。
     */
    private String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZW5hbnRAdGhpbmdzYm9hcmQub3JnIiwic2NvcGVzIjpbIlRFTkFOVF9BRE1JTiJdLCJ1c2VySWQiOiI4ODdiMmVhMC1hMWJiLTExZTctOTBjNS0zYmVkYjlkZTMyZWEiLCJlbmFibGVkIjp0cnVlLCJpc1B1YmxpYyI6ZmFsc2UsInRlbmFudElkIjoiODg3OWY2MjAtYTFiYi0xMWU3LTkwYzUtM2JlZGI5ZGUzMmVhIiwiY3VzdG9tZXJJZCI6IjEzODE0MDAwLTFkZDItMTFiMi04MDgwLTgwODA4MDgwODA4MCIsImlzcyI6InRoaW5nc2JvYXJkLmlvIiwiaWF0IjoxNTA4NDIyNjU4LCJleHAiOjE1MTc0MjI2NTh9.n9_VBeoQqzUknxbtZVqNwNWl-NbF-3PUsja0DEQ-jLH47DmzvkllO17g4b9vK5vbPxFGldD1Tik8_ECQPpyGIQ" ; // todo token失效时间

    public ThingBoardProxy(String host, int port) throws Exception {
        this.host = host ;
        this.port = port ;
        this.api = new ThingsBoardApi(this.host, this.port) ;
    }

    public ThingBoardProxy(String host, int port, String username, String password) throws Exception {
        this.host = host ;
        this.port = port ;
        this.api = new ThingsBoardApi(this.host, this.port) ;
        this.username = username ;
        this.password = password ;
//        getToken(username, password);
    }

    // ----------------------- 相关业务 -----------------------
    // ---> 获取token
    public void getToken(String name, String password) throws Exception{
        // {"name": "tjl", "password":"pass"}
        String token = api.api_token(name, password);
        this.token = token ;
    }

    // ---> 创建一个设备
    public void createDevice(String deviceName, String deviceType) throws Exception{
        // "{\"name\":\"test_name_tjl\", \"type\":\"default\"}" ;
        ThingsBoardApi api = new ThingsBoardApi(this.host, this.port) ;
        String deviceId = api.api_device(token, deviceName, deviceType);

    }

    // --> 发送设备的attributions
    public void sendAttributions(String deviceToken, String msg) throws Exception{
        ThingsBoardApi api = new ThingsBoardApi(this.host, this.port) ;
        api.api_attributes(token, deviceToken, msg);
    }

    // --> 发送设备的telemetry
    public void sendTelelmetry(String deviceToken, String msg) throws Exception{
        ThingsBoardApi api = new ThingsBoardApi(this.host, this.port) ;
        api.api_telemetry(token, deviceToken, msg);
    }

    public String get_accessToken(String deviceId) {
        ThingsBoardApi api = new ThingsBoardApi(this.host, this.port) ;
        String access_token = api.api_accessToken(token, deviceId);

        return access_token ;
    }

    // ----------------------- test -----------------------

    public static void main(String[] args) {
        /**
         * usage and test
         */
        try {
            String host = "10.108.218.58";
            int port = 8080;
//            String host = "localhost" ;
//            int port = 1567 ;
            String username = "tenant@thingsboard.org" ;
            String password = "tenant" ;

            ThingBoardProxy tp = new ThingBoardProxy(host, port, username, password);
//            tp.get_accessToken("2bebd5d0-b4d1-11e7-b5a9-39a0b348caf5");
//            tp.createDevice("hello world", "default");

            // telemetry
            String msg = "{\"temperature\":\"31\"}" ;
            String deviecToken = "3HLZRun4LjT8PLSOGhvf" ;
            tp.sendAttributions(deviecToken, msg);

        } catch (Exception e) {
            System.out.println(e) ;
        }
    }
}
