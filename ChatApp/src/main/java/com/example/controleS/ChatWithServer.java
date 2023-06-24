package com.example.controleS;

import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatWithServer extends Thread{
    private int ClientNbre;
    private List<Communication> clientsconnectés= new ArrayList<Communication>();

    public static void main(String[] args) {
        new ChatWithServer().start();
    }

    @Override
    public void run(){
        try {
            ServerSocket ss=new ServerSocket(1234);
            System.out.println("Le serveur essaie de démarrer ....");
            while(true){
                Socket s=ss.accept();
                ++ClientNbre;

                Communication NewCommunication=new Communication(s,ClientNbre);
                clientsconnectés.add(NewCommunication);
                NewCommunication.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Communication extends Thread {
        private Socket s;

        private int ClientNumber;
        Communication(Socket s,int ClientNumber){
            this.s=s;
            this.ClientNumber=ClientNumber;
        }
        @Override
        public void run(){
            try {
                InputStream is = s.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br= new BufferedReader(isr);
                OutputStream os = s.getOutputStream();
                String Ip = s.getRemoteSocketAddress().toString();
                System.out.println("Le client numéro"+ClientNbre+"et son IP "+Ip);
                PrintWriter pw=new PrintWriter(os,true);
                pw.println("Vous etes le client "+ClientNumber);
                pw.println("Envoyer le message que vous voulez ....");

                while(true){
                    String UserRequest = br.readLine();
                    if (UserRequest.contains("=>")){
                        String[] usermessage = UserRequest.split("=>");
                        if(usermessage.length == 2){
                            String msg = usermessage[1];
                            int numeroClient = Integer.parseInt(usermessage[0]);
                            Send(msg,s,numeroClient);
                        }
                    }
                    else{
                        Send(UserRequest,s,-1);
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        void Send(String UserRequest,Socket socket,int nbre ) throws IOException {
            for(Communication client : clientsconnectés){
                if(client.s != socket) {
                    if (client.ClientNumber == nbre || nbre == -1) {
                        PrintWriter pw = new PrintWriter(client.s.getOutputStream(), true);
                        pw.println(UserRequest);
                    }
                }
            }
        }
    }}
