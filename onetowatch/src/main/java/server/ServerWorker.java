package server;
import java.lang.String;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.shared.utils.StringUtils;

public class ServerWorker extends Thread{
    
    private final Socket clientsocket;
    private final Server server;
    private String Nome = null;
    private BufferedReader inputstream;
    private DataOutputStream outputstream;
    private Vector chatti = new Vector();
    private HashSet<String> topicset = new HashSet<>();
    private HashSet<String> topicsetprivata = new HashSet<>();
    public ServerWorker(Server server, Socket clientsocket){
        this.server = server;
        this.clientsocket = clientsocket;
    }
    @Override
        public void run(){
        try {
            Comunica();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }
    private void Comunica() throws IOException, InterruptedException {
        //Messaggi dal client al server
        inputstream = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        //Messaggi dal server al client
        outputstream = new DataOutputStream(clientsocket.getOutputStream());
        //Stringa del messaggio
        String line;
        //Se il messaggio è diverso da null
        while((line = inputstream.readLine()) != null){
            //Istanzia il token del messaggio
            String[] tokens = StringUtils.split(line);
            //Il messaggio non deve essere troppo lungo
            if(tokens.length < 10 && tokens.length > 0){
                String h = tokens[0];
                String nome = tokens[1];
                      if(tokens.length == 2){
                          if(h.equals("#")){
                             if(chatPrivata(outputstream , tokens)){
                                   System.out.println("Chat presente");
                             }else{
                                   System.out.println("Utente non presente");
                             }}} 
                      else if("stop".equalsIgnoreCase(h) || "quit".equalsIgnoreCase(h)){
                        //Rimuove il client
                        removeClient();
                        break;
                      
                      }else if(tokens.length > 10){
                          System.out.println("Hai scritto troppo");
                            }   
                      else{
                          Messaggia(outputstream, tokens);
                      }
            }}
            
        clientsocket.close();
    }

    public String getNome(){
        return Nome;
    }
    private boolean chatPrivata(DataOutputStream outputstream, String[] tokens) throws IOException {
           String Nome = tokens[1];
           List<ServerWorker> workerlist = server.getWorkerList();
              String msg = "Ok\n";
              outputstream.write(msg.getBytes());
              this.Nome = Nome;
              System.out.println("Utente Presente: " + Nome);
              for(ServerWorker worker : workerlist){
                  if(!Nome.equals(worker.getNome())){
                      if(worker.getNome() != null){
                      String onlinemsg2 = "Online "+ worker.getNome();
                      send(onlinemsg2);
                      return true;
                      }
                  }else{
                      return false;  
                  }
              
           }
        return false;
    }
    
    private void removeClient() throws IOException {
        //Offline status
        List<ServerWorker> workerlist = server.getWorkerList();
        server.removeWorker(this);
        String onlinemsg = "Offline" + Nome + "\n";
        for(ServerWorker worker : workerlist){
            if(!Nome.equals(worker.getNome())){
            worker.send(onlinemsg);
            }
        }
        clientsocket.close();
    }
    
    private void send(String msg)throws IOException {
        if(msg != null){
        outputstream.write(msg.getBytes());
        }
    }
    
    /*private void Tempo() throws InterruptedException, IOException {
        outputstream = new DataOutputStream(clientsocket.getOutputStream());
        for(int i = 0; i < 10; i++){
            outputstream.write(("Time now is:" + new Date() + "\n").getBytes());
            Thread.sleep(1000);
        }
    }*/
    private void Messaggia(DataOutputStream outputstream, String[] tokens) throws IOException {
       String sendto = tokens[0];
       String messaggio = tokens[1-tokens.length];
       List<ServerWorker> workerlist = server.getWorkerList();
       for(ServerWorker worker : workerlist){
            if(worker.containsTopic(sendto)){
                  String outmsg = sendto + ":" + Nome + " " + messaggio + "\n";
                  worker.send(outmsg);
               }
            else{
               if(sendto.equalsIgnoreCase(worker.getNome())){
                  String outmsg = Nome + " " + messaggio + "\n";
                  worker.send(outmsg);
               }
           }
       }    
    }
    
    public boolean containsTopic(String topic){
        return topicset.contains(topic);
    }
    private void Join(String[] tokens) {
        if(tokens.length > 1){
            String topic = tokens[1];
            topicset.add(topic);
        } 
    }

    private void Leave(String[] tokens) {
       if(tokens.length > 1){
            String topic = tokens[1];
            topicset.remove(topic);
        } 
    }
}