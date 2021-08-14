/*
ʹ��GUI��д������
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
        int scr_w = d.width;//��Ļ�ߴ�
        int scr_h = d.height;
        int win_w = 400;//����ߴ�
        int win_h = 400;

        this.setTitle("��������");
        this.setLocation((int) ((double) scr_w / 5 - (double) win_w / 2), (int) ((double) scr_h / 3 - (double) win_h / 2));
        this.addWindowListener(new WindowAdapter() {//�رմ����˳�����
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.add(textArea);
        this.pack();//�Զ�ƥ�䴰��ͽ���Ĵ�С

        this.setVisible(true);

        startService();
    }

    private void startService() {
        try {
            ServerSocket server = new ServerSocket(server_port);
            while (true) {
                //��Ҫ�ö��߳��������ͻ������ӣ�Ҫ��Ȼ�������￨ס
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
                System.out.println(name + "(" + socket.getRemoteSocketAddress() + ") ����������" + "\n");
                broadcast(name + "����������\n", socket);
                table.put(name, socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //�����пͻ����յ��������������ݣ��Ǿ���Ҫ�������������û���Ϣ
            //receiveMessage();//�����������������

            //�㲥���ݡ����������յ���Ϣʱ����ʼת��
            try {
                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader read = new BufferedReader(input);
                String content = "";
                while (true) {
                    if ((content = read.readLine()) != null){
                        broadcast(content + "\n", socket);//�㲥��Ϣ
                        if(content.equals("quit")){
                            broadcast(name + "���˳�������\n", null);
                            table.remove(name);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //ת���ͻ��˷��͵���Ϣ��������ת�������ݣ� ��Ϣ������
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
