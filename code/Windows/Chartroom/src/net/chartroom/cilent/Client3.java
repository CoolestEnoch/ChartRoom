package net.chartroom.cilent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client3 extends Frame implements ActionListener {

    private String server_ip = "127.0.0.1";
    private String nickname = "Steve";
    private int server_port = 8888;

    private TextArea textArea = new TextArea(30, 50);
    private Label label1 = new Label("IP地址:");
    private Label label2 = new Label("端口号：");
    private Label label3 = new Label("昵称：");
    private TextField ipField = new TextField(server_ip, 20);//ip地址框
    private TextField portField = new TextField(String.valueOf(server_port), 10);//端口框
    private TextField nickName = new TextField(nickname, 20);//昵称框
    private TextField message = new TextField(40);//输入消息的框
    private Button connButton = new Button("连接");
    private Button sendButton = new Button("发送");
    private Panel p1 = new Panel();//ip地址那一栏
    private Panel p2 = new Panel();//发送那一栏

    private Socket socket;

    private void launchFrame() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int scr_w = d.width;//屏幕尺寸
        int scr_h = d.height;
        int win_w = 400;//窗体尺寸
        int win_h = 400;
        this.setLocation((int) ((double) scr_w / 5 - (double) win_w / 2), (int) ((double) scr_h / 3 - (double) win_h / 2));

        this.setTitle("聊天室客户端");
        //关闭窗体退出程序
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
                    print.println("quit");
                    socket.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                System.exit(0);
            }
        });

        //将控件加入到窗体上
        p1.add(label1);//ip地址那一栏
        p1.add(ipField);
        p1.add(label2);
        p1.add(portField);
        p1.add(label3);
        p1.add(nickName);
        p1.add(connButton);

        p2.add(message);//发送那一栏
        p2.add(sendButton);

        this.add(textArea);//会默认放到中间
        this.add(p1, BorderLayout.NORTH);//加入到北方，就是最上面
        this.add(p2, BorderLayout.SOUTH);//加入到南方，就是最下面

        this.pack();//窗体大小自适应控件

        this.setVisible(true);

        //全局更新ip和端口和昵称
        updateAllData();
        //监听button
        connButton.addActionListener(this);
        sendButton.addActionListener(this);
    }

    private void updateAllData() {
        server_ip = ipField.getText();
        server_port = Integer.parseInt(portField.getText());
        nickname = nickName.getText();
    }

    //给button添加点击事件监听
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connButton) {//连接按钮
            updateAllData();
            connService();
            new Thread(new Receive()).start();
        } else if (e.getSource() == sendButton) {//发送按钮
            updateAllData();
            sendMessage();
        }
    }

    //连接服务器,然后给服务器发送一个验证信息，提示xxx进入聊天室
    private void connService() {
        try {
            if (socket == null) {//开始建立连接
                socket = new Socket(server_ip, server_port);
                if (socket != null) {//连上了
                    textArea.append("连接服务器成功！ip: " + server_ip + ":" + server_port + "\n");
                }
                //给服务器发送消息
                PrintWriter print = new PrintWriter(socket.getOutputStream(), true);//发送完成后自动刷新流
                print.println(nickname + " ");
                System.out.println("server ip = " + server_ip + ", server port = " + server_port + ", nickname = " + nickname + ", self ip = " + socket.getLocalSocketAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //向服务器发送消息
    private void sendMessage() {
        try {
            PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
            print.println(nickname + ": " + message.getText());
            textArea.append("我: " + message.getText() + "\n");//聊天框修改
            message.setText("");//清空文本
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //接收消息：该进程需要一直运行，则需要在新开的一个线程里运行
    class Receive implements Runnable {

        //在这里循环接收服务器的消息
        @Override
        public void run() {
            receive();
        }
    }

    private void receive() {
        try {
            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            BufferedReader read = new BufferedReader(input);
            String msg = "";
            while(true){
                if((msg = read.readLine()) != null);{
                    textArea.append(msg + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client3 c1 = new Client3();
        c1.launchFrame();
        //双开
//        Client3 c2 = new Client3();
//        c2.launchFrame();
    }
}
