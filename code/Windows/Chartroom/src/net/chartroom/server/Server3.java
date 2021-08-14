/*
使用GUI编写聊天器
 */

package net.chartroom.server;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class Server3 extends Frame {

    int server_port = 8888;

    TextArea textArea = new TextArea(20, 40);
    private Socket socket;

    private Hashtable table = new Hashtable();

    private void launchFrame() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int scr_w = d.width;//屏幕尺寸
        int scr_h = d.height;
        int win_w = 400;//窗体尺寸
        int win_h = 400;

        this.setTitle("服务器端");
        this.setLocation((int) ((double) scr_w / 5 - (double) win_w / 2), (int) ((double) scr_h / 3 - (double) win_h / 2));
        this.addWindowListener(new WindowAdapter() {//关闭窗口退出程序
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.add(textArea);
        this.pack();//自动匹配窗体和界面的大小

        this.setVisible(true);

        startService();
    }

    private void startService() {
        try {
            ServerSocket server = new ServerSocket(server_port);
            while (true) {
                //需要用多线程来处理多客户端连接，要不然会在这里卡住
                socket = server.accept();
                ConnService thread = new ConnService(socket);
                new Thread(thread).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage() {
        try {
            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            BufferedReader read = new BufferedReader(input);
            String msg;
            while ((msg = read.readLine()) != null) {
                textArea.append(msg + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ConnService implements Runnable {

        private Socket socket;
        private String name;

        private ConnService(Socket socket) {
            this.socket = socket;
            try {
                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader read = new BufferedReader(input);
                name = read.readLine();
                System.out.println(name + "(" + socket.getRemoteSocketAddress() + ") 进入聊天室" + "\n");
                broadcast(name + "进入聊天室\n", socket);
                table.put(name, socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //让所有客户端收到服务器发的数据，那就需要保存所有在线用户信息
            //receiveMessage();//仅服务器单方面接收

            //广播数据。当服务器收到消息时，开始转发
            try {
                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader read = new BufferedReader(input);
                String content = "";
                while (true) {
                    if ((content = read.readLine()) != null){
                        broadcast(content + "\n", socket);//广播消息
                        if(content.equals("quit")){
                            broadcast(name + "已退出聊天室\n", null);
                            table.remove(name);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //转发客户端发送的消息。参数：转发的内容， 消息发送者
    private void broadcast(String content, Socket socket) {
        Enumeration en = table.keys();
        textArea.append(content);
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            Socket s = (Socket) table.get(name);
            try {
                if (s != socket) {
                    PrintWriter print = new PrintWriter(s.getOutputStream(), true);
                    print.println(content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server3().launchFrame();
    }
}
