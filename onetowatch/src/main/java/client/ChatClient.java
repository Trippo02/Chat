/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class ChatClient {
    private final String servername;
    private final int serverport;
    private Socket socket;
    String messaggio;
    String risposta;
    
    private BufferedReader bufferedin;
    private DataOutputStream serverout;
    private BufferedReader serverin;
    private final String nome;
    
    
    ChatClient(String servername, int serverport, String nome) {
        this.servername = servername;
        this.serverport = serverport;
        this.nome = nome;
    }

    boolean Connetti() {
        try {
            this.socket = new Socket(servername, serverport);
            System.out.println("Client porta: " + socket.getLocalPort());
            //Messaggi dal client al server
            this.serverout = new DataOutputStream(socket.getOutputStream());
            //Messaggi dal server al client
            this.serverin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
        }

    public String Comunica(String nome, String messaggio){
            String risposta = null;
            try {
            System.out.println("Invio del messaggio");
            serverout.writeBytes(nome + " " + messaggio +"\n");
            String r = serverin.readLine();
            risposta = r;
            }
       catch (Exception e) {
            System.out.println(e.getMessage());
            risposta = "Errore durante la comunicazione col server.";
            System.exit(1);
        }       
        return risposta;
    }
    
    boolean chatPrivata(String hashtag, String nome) throws IOException {   
        String cmd = hashtag + " " + nome + "\n";
        serverout.write(cmd.getBytes());
        String risposta = serverin.readLine();
        //log += "Risposta linea: " + risposta +"\n";
        if("Ok".equalsIgnoreCase(risposta)){
            return true;
        }else{
            return false;
        }
    }
}
