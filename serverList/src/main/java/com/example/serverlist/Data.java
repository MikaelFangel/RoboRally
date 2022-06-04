package com.example.serverlist;

import org.springframework.stereotype.Service;

@Service
public class Data implements IServerList{

    private final ServerList listOfServers = new ServerList();

    @Override
    public void addServer(String s) {
        String name = s.split("\n")[0];
        String ip = s.split("\n")[1];
        Server server = new Server(name, ip);
        listOfServers.addServer(server);
    }

    @Override
    public String getServerList() {
        return listOfServers.getServerList();
    }

    public boolean uniqueIp(){
        return listOfServers.uniqueIp();
    }
}

class ServerList {

    private Server firstServer;

    public boolean isEmpty(){
        return firstServer == null;
    }

    public void addServer(Server s){
        if (isEmpty()) {
            firstServer = s;
            return;
        }
        Server next = firstServer;
        firstServer = s;
        firstServer.setNext(next);
    }

    public String getServerList(){
        if (isEmpty())
            return "";
        StringBuilder stringOut = new StringBuilder();
        Server s = firstServer;
        stringOut.append(firstServer.getName()).append("\n").append(firstServer.getIp());
        while (s.getNext() != null){
            s = s.getNext();
            stringOut.append(s.getName()).append("\n").append(s.getIp());
        }
        return stringOut.toString();
    }

    public boolean uniqueIp(){
        if (isEmpty())
            return true;
        return false;
    }
}

class Server{

    private String name;
    private String ip;

    private Server next;

    public Server(String name, String ip){
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Server getNext() {
        return next;
    }

    public void setNext(Server s){
        this.next = s;
    }

}
