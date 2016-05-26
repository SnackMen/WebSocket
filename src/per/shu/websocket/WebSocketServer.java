package per.shu.websocket;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.*;
@ServerEndpoint("/chat/{page}")
public class WebSocketServer {

    public static int queryElementNumber=0;
    public static int onlineCount=0;
    private static ArrayList<WebSocketServer> webSocket = new ArrayList<WebSocketServer>();
    private static Map<WebSocketServer,WebSocketServer> webSocketServerMap = new LinkedHashMap<WebSocketServer, WebSocketServer>();
    public static Queue<WebSocketServer> queue = new LinkedList<WebSocketServer>();
    public Session session;
    public static int roomnumber=0;
    @OnOpen
    public void onOpen(Session session, @PathParam("userName")String username,@PathParam("page")String page){
        //判断页面类型beginGame还是index
        if(page.equals("beginGame")){
            System.out.println(page);
            this.session = session;
            //将连接服务器的用户先入队
            queue.add(this);
            System.out.println("客户端"+session.getId()+"加入队列!");
            addOnlineCount();
            System.out.println("服务器当前连接数"+getOnlineCount());
            queryElementNumber++;
            System.out.println("入队数open："+queryElementNumber);
            //凑够两个就出队
            if(queryElementNumber==2){
                WebSocketServer webSocketServer1 = queue.poll();
                WebSocketServer webSocketServer2 = queue.poll();
                webSocket.add(webSocketServer1);
                webSocket.add(webSocketServer2);
                webSocketServerMap.put(webSocketServer1,webSocketServer2);
                webSocketServerMap.put(webSocketServer2,webSocketServer1);

                queryElementNumber-=2;

                //两个人组成一个房间，然后向用户发送不同消息
                try{
                    if(webSocketServer1.session.isOpen()){
                        webSocketServer1.sendMessage("由您先开始游戏，请输入正确诗词开始游戏！");
                    }
                    if (webSocketServer2.session.isOpen())
                    {
                        //向用户发送对方用户名
                        webSocketServer2.sendMessage("游戏开始，请等待对方输入");
                    }
                    //向第一个人发送信息要求其先手游戏
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{
                //人数未满，告知对方等待
                System.out.println(session.getId()+"waiting!");
                try{
                    addRoomNumber();
                    this.sendMessage("正在匹配，请等待……");
                    System.out.println("房间数："+getRoomNumber());
                    //告知第一人游戏正在匹配
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if(page.equals("index")){
            //如果是idex页面，那么不对其进行计数
            System.out.println(page+"在门外没有进入房间");
            this.session = session;
            webSocket.add(this);
            addOnlineCount();
            try{
                this.sendMessage(getOnlineCount()+"");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    @OnClose
    public void onClose(Session session,@PathParam("page")String page){
        WebSocketServer webSocketServer = webSocketServerMap.get(this);
        WebSocketServer thisweb = this;
        webSocket.remove(this);
        subOnlineCount();
        if(page.equals("beginGame")){

            System.out.println("客户端"+session.getId()+"断开连接,当前连接数为"+getOnlineCount());
            if(webSocketServer==null){
                queue.poll();
                queryElementNumber--;
                subRoomNumber();
                System.out.println("客户端"+thisweb.session.getId()+"进入房间什么也没干就退出来了");
            }else{
                try{
                    if(webSocketServer.session.isOpen()){
                        webSocketServer.sendMessage("对方断开连接");
                    }else{
                        //第二个人也退出房间，房间回收
                        subRoomNumber();
                    }
                    System.out.println("-------------------------------------------------");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("index中有人离开门,当前连接数"+getOnlineCount());
        }
    }
    /*
    收到客户端信息后调用的方法
    * */
    @OnMessage
    public void onMessage(String message,Session session){
        System.out.println("来自客户端"+session.getId()+"信息:"+message);
        try{
            if(webSocketServerMap.get(this)!=null){
                WebSocketServer webSocketServer = webSocketServerMap.get(this);
                if(webSocketServer.session.isOpen()) {
                    webSocketServer.sendMessage(message);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //发生错误时调用
    @OnError
    public void onError(Session session,Throwable error){
        System.out.println(session.getId()+"发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized  int getOnlineCount(){
        return onlineCount;
    }

    public static synchronized void addOnlineCount(){
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount(){
        WebSocketServer.onlineCount--;
    }
    public static synchronized int getRoomNumber(){
        return roomnumber;
    }

    public static synchronized void addRoomNumber(){
        WebSocketServer.roomnumber++;
    }

    public static synchronized void subRoomNumber(){
        WebSocketServer.roomnumber--;
    }

}
