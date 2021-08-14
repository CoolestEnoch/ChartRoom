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
    private Label label1 = new Label("IP��ַ:");
    private Label label2 = new Label("�˿ںţ�");
    private Label label3 = new Label("�ǳƣ�");
    private TextField ipField = new TextField(server_ip, 20);//ip��ַ��
    private TextField portField = new TextField(String.valueOf(server_port), 10);//�˿ڿ�
    private TextField nickName = new TextField(nickname, 20);//�ǳƿ�
    private TextField message = new TextField(40);//������Ϣ�Ŀ�
    private Button connButton = new Button("����");
    private Button sendButton = new Button("����");
    private Panel p1 = new Panel();//ip��ַ��һ��
    private Panel p2 = new Panel();//������һ��

    private Socket socket;

    private void launchFrame() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int scr_w = d.width;//��Ļ�ߴ�
        int scr_h = d.height;
        int win_w = 400;//����ߴ�
        int win_h = 400;
        this.setLocation((int) ((double) scr_w / 5 - (double) win_w / 2), (int) ((double) scr_h / 3 - (double) win_h / 2));

        this.setTitle("�����ҿͻ���");
        //�رմ����˳�����
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

        //���ؼ����뵽������
        p1.add(label1);//ip��ַ��һ��
        p1.add(ipField);
        p1.add(label2);
        p1.add(portField);
        p1.add(label3);
        p1.add(nickName);
        p1.add(connButton);

        p2.add(message);//������һ��
        p2.add(sendButton);

        this.add(textArea);//��Ĭ�Ϸŵ��м�
        this.add(p1, BorderLayout.NORTH);//���뵽����������������
        this.add(p2, BorderLayout.SOUTH);//���뵽�Ϸ�������������

        this.pack();//�����С����Ӧ�ؼ�

        this.setVisible(true);

        //ȫ�ָ���ip�Ͷ˿ں��ǳ�
        updateAllData();
        //����button
        connButton.addActionListener(this);
        sendButton.addActionListener(this);
    }

    private void updateAllData() {
        server_ip = ipField.getText();
        server_port = Integer.parseInt(portField.getText());
        nickname = nickName.getText();
    }

    //��button��ӵ���¼�����
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connButton) {//���Ӱ�ť
            updateAllData();
            connService();
            new Thread(new Receive()).start();
        } else if (e.getSource() == sendButton) {//���Ͱ�ť
            updateAllData();
            sendMessage();
        }
    }

    //���ӷ�����,Ȼ�������������һ����֤��Ϣ����ʾxxx����������
    private void connService() {
        try {
            if (socket == null) {//��ʼ��������
                socket = new Socket(server_ip, server_port);
                if (socket != null) {//������
                    textArea.append("���ӷ������ɹ���ip: " + server_ip + ":" + server_port + "\n");
                }
                //��������������Ϣ
                PrintWriter print = new PrintWriter(socket.getOutputStream(), true);//������ɺ��Զ�ˢ����
                print.println(nickname + " ");
                System.out.println("server ip = " + server_ip + ", server port = " + server_port + ", nickname = " + nickname + ", self ip = " + socket.getLocalSocketAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //�������������Ϣ
    private void sendMessage() {
        try {
            PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
            print.println(nickname + ": " + message.getText());
            textArea.append("��: " + message.getText() + "\n");//������޸�
            message.setText("");//����ı�
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //������Ϣ���ý�����Ҫһֱ���У�����Ҫ���¿���һ���߳�������
    class Receive implements Runnable {

        //������ѭ�����շ���������Ϣ
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
        //˫��
//        Client3 c2 = new Client3();
//        c2.launchFrame();
    }
}
