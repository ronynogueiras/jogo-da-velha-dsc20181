package sample;

public class Player {

    private String name;
    private String ip;
    private boolean isOnline;

    public Player(String name, String ip, boolean online) {
        this.name = name;
        this.ip = ip;
        this.isOnline = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return (isOnline ? "Online - " : "Offline - ") + name;
    }
}
