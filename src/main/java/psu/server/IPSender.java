package psu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class IPSender extends Thread {

    private DatagramSocket socket;
    private byte[] buf = new byte[256];

    IPSender() throws SocketException {
        socket = new DatagramSocket(25566);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("GET_SERVER_IP")) {
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    buf = InetAddress.getByName("localhost").toString().getBytes();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }
}
