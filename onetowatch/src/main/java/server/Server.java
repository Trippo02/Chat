package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread{
    private final int serverport;
    private ArrayList<ServerWorker> workerlist = new ArrayList();
    
    public Server(int serverport){
     this.serverport = serverport;
    }
    
    public List<ServerWorker> getWorkerList(){
        return workerlist;
    }
    
        public void run(){
        try{
        ServerSocket serversocket = new ServerSocket(serverport);
        while(true){
            System.out.println("In attesa del client..");
            Socket clientsocket = serversocket.accept();
            System.out.println("Accettato client: " + clientsocket);
            ServerWorker worker = new ServerWorker(this, clientsocket);
            workerlist.add(worker);
            worker.start();
        }  
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker worker) {
        workerlist.remove(worker);
    }
}