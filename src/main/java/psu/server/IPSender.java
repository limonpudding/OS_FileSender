package psu.server;

import psu.utils.GlobalConstants;

import java.io.IOException;
import java.net.*;

public class IPSender extends Thread {

    private DatagramSocket socket;
    private String localServerIP;

    IPSender() throws SocketException {
        try {
            socket = new DatagramSocket(GlobalConstants.PORT);
            socket.connect(InetAddress.getByName("8.8.8.8"), GlobalConstants.PORT); // Немного костыльный способ получения адреса в локальной сети
            localServerIP = socket.getLocalAddress().getHostAddress();
            socket.disconnect();
            this.setDaemon(true);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            //TODO нормально обработать
        }
    }

    @Override
    public void run() {
        DatagramPacket packet;
        byte[] buf;
        String received;

        try {
            while (true) {
                buf = new byte[GlobalConstants.BUF_SIZE_IP];
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
            //TODO нормально обработать
        } finally {
            socket.close();
        }
    }
}
