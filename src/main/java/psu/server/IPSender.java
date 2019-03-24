package psu.server;

import psu.utils.GlobalConstants;

import java.io.IOException;
import java.net.*;

public class IPSender extends Thread {

    private DatagramSocket socket;
    private String localServerIP;

    IPSender() throws SocketException {
        try {
            socket = new DatagramSocket(25565);
            socket.connect(InetAddress.getByName("8.8.8.8"), 25565); // Немного костыльный способ получения адреса в локальной сети
            localServerIP = socket.getLocalAddress().getHostAddress();
            socket.disconnect();
            this.setDaemon(true);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        DatagramPacket packet;
        byte[] buf = new byte[256];
        String received;

        try {
            while (true) {
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals(GlobalConstants.GET_SERVER_IP)) {
                    buf = localServerIP.getBytes();
                    packet = new DatagramPacket(
                            buf,
                            buf.length,
                            packet.getAddress(),
                            packet.getPort());

                    socket.send(packet);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
