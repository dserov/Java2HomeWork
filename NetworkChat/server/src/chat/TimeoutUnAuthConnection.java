package chat;

public class TimeoutUnAuthConnection implements Runnable {
    TCPConnection connection;

    @Override
    public void run() {
        if (connection.getName().isEmpty()) connection.disconnect();
    }

    public TimeoutUnAuthConnection(TCPConnection connection) {
        this.connection = connection;
    }
}
