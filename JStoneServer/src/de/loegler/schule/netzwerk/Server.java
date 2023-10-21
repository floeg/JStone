package de.loegler.schule.netzwerk;

import de.loegler.schule.datenstrukturen.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Objekte von Unterklassen der abstrakten Klasse Server ermöglichen das anbieten von Serverdiensten mittels TCP/IP-Protokoll.
 * Der Versand findet Zeilenweise statt. Verbindungsannahme, Nachrichtenempfang und Verbindungsende geschehen nebenläufig.
 * Es findet nur eine rudimentäre Fehlerbehandlung statt.
 * Einmal beendete Verbindungen können nicht wieder aufgenommen werden.
 */
public abstract class Server {

    /**
     * Der Port des Servers, wird einmalig festgelegt
     */
    private final int port;
    protected ServerSocket server;
    /**
     * Thread welcher mithilfe des {@link #server}-Sockets Verbindungen aufbaut
     */
    protected Thread acceptThread;
    /**
     * Aktueller Status des Servers
     */
    protected boolean stopped = false;
    /**
     * Momentan verbundene Clients
     */
    private final List<RemoteClient> remoteClients;


    /**
     * Erstellt eine Instanz des Servers.
     * @param port Der Port, auf welchem der Server lauschen soll.
     */
    public Server(int port) {
        this.port = port;
        remoteClients = new List<>();
        try {
            server = new ServerSocket(port);
            acceptThread = new Thread(new AcceptRunnable());
            acceptThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return true, falls der Server seinen Dienst auf dem Port anbietet
     */
    public boolean isOpen() {
        return server.isBound();
    }

    /**
     *
     * Die Anfrage liefert true, falls die angegebene Verbindung zu einem Client besteht.
     * @param clientIP
     * @param clientPort
     * @return
     */
    public boolean isConnectedTo(String clientIP, int clientPort) {
        return findSocket(clientIP, clientPort) != null;
    }

    /**
     * Die Nachricht pMessage wird um einen Zeilentrenner erweitert an den angegebenen Client gesendet.
     *
     * @param clientIP
     * @param port
     * @param message
     */
    public void send(String clientIP, int port, String message) {
        RemoteClient remoteClient = findSocket(clientIP, port);
        if (remoteClient != null) {
            try {
                remoteClient.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace(); /* */
            }
        }
    }

    /**
     * Die Nachricht wird an alle Clients gesendet. Im Fehlerfall wird der entsprechende Client uebersprungen.
     * @param message
     */
    public void sendToAll(String message) {
        synchronized (remoteClients) {
            for (remoteClients.toFirst(); remoteClients.hasAccess(); remoteClients.next()) {
                try {
                    remoteClients.getContent().sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Die Verbindung zum angegebenen Client wird geschlossen.
     *
     * @param clientIP
     * @param port
     */
    public void closeConnection(String clientIP, int port) {
        RemoteClient rClient = this.findSocket(clientIP, port);
        if (rClient != null) {
            this.processClosingConnection(rClient.getIP(), rClient.remoteSocket.getPort());
            rClient.closeStreams();
            synchronized (remoteClients) {
                for (remoteClients.toFirst(); remoteClients.hasAccess(); remoteClients.next()) {
                    if (remoteClients.getContent().equals(rClient)) {
                        remoteClients.remove();
                    }
                }
            }
        }
    }

    /**
     * Alle bestehenden Verbindungen zu Clients werden getrennt und der Server kann nicht mehr verwendet werden.
     * Ist der Server bereits vor Aufruf der Methode in diesem Zustand, geschieht nichts.
     */
    public void close() {
        if (!this.stopped) {
            this.stopped = true;
            this.closeAllClients(); //Nur nötig, wenn closeConnection aufgerufen werden soll
            try {
                Socket stopSocket = new Socket("127.0.0.1", port);//Damit accept Thread gestoppt wird
                Thread.sleep(200); //Socket Zeit geben sich mit dem Server zu verbinden
                stopSocket.close();
                server.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeAllClients() {
        List<RemoteClient> copyList = new List<>(); //current wird durch closeConnection verändert
        synchronized (remoteClients) {
            for (this.remoteClients.toFirst(); this.remoteClients.hasAccess(); this.remoteClients.next()) {
                copyList.append(this.remoteClients.getContent());
            }
        }
        for (copyList.toFirst(); copyList.hasAccess(); copyList.next()) {
            this.closeConnection(copyList.getContent().getIP(), copyList.getContent().remoteSocket.getPort());
        }
    }

    /**
     * Diese Ereignisbehandlungsmethode wird aufgerufen, wenn sich ein Client mit dem Server verbunden hat.
     * Der Aufruf erfolgt nicht synchronisiert.
     *
     * @param pClientIP
     * @param pClientPort
     */
    public abstract void processNewConnection(String pClientIP, int pClientPort);

    /**
     * Diese Ereignisbehandlungsmethode wird aufgerufen, wenn der Server die Nachricht pMessage von dem CLient erhalten hat.
     *
     * @param pClientIP
     * @param pClientPort
     * @param pMessage
     */
    public abstract void processMessage(String pClientIP, int pClientPort, String pMessage);

    /**
     * Sofern der Server die Verbindung zu dem Client trennt,
     * wird diese Ereignisbehandlungsmethode aufgerufen, unmittelbar bevor die Verbindungstrennung tatsächlich erfolgt.
     * Der Aufruf der Methode erfolgt nicht synchronisiert.
     *
     * @param pClientIP
     * @param pClientPort
     */
    public abstract void processClosingConnection(String pClientIP, int pClientPort);

    private RemoteClient findSocket(String clientIP, int clientPort) {
        synchronized (remoteClients) {
            for (this.remoteClients.toFirst(); this.remoteClients.hasAccess(); this.remoteClients.next()) {
                RemoteClient remoteClient = remoteClients.getContent();
                if (remoteClient.getIP().equals(clientIP) && remoteClient.remoteSocket.getPort() == clientPort) {
                    return remoteClients.getContent();
                }
            }
        }
        return null;
    }

    private class AcceptRunnable implements Runnable {
        public void run() {
            while (!stopped) {
                try {
                    System.out.println("Warte auf neue Clients...");
                    Socket remoteSocket = server.accept();
                    System.out.println("Neue Verbindung!");
                    RemoteClient rClient = new RemoteClient(remoteSocket);
                    synchronized (remoteClients) {
                        remoteClients.append(rClient);
                    }
                    processNewConnection(rClient.getIP(), rClient.remoteSocket.getPort());
                    rClient.startListenThread();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class RemoteClient {

        private final Socket remoteSocket;
        private final RemoteClientListenThread listenThread;
        private PrintWriter printWriter;
        private boolean clientClosedConnection = false;

        public RemoteClient(Socket socket) {
            this.remoteSocket = socket;

            try {
                printWriter = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            listenThread = new RemoteClientListenThread(this);
        }

        public String getIP() {
            return remoteSocket.getInetAddress().getHostAddress();
        }

        public void startListenThread() {
            this.listenThread.start();
        }

        public synchronized void sendMessage(String message) {
            printWriter.println(message);
        }

        public void closeStreams() {
            printWriter.close();
            try {
                remoteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class RemoteClientListenThread extends Thread {
        private final RemoteClient remoteClient;
        private BufferedReader reader;

        public RemoteClientListenThread(RemoteClient client) {
            this.remoteClient = client;
            try {
                reader = new BufferedReader(new InputStreamReader(client.remoteSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!stopped && !remoteClient.remoteSocket.isClosed()) {
                try {
                    String m = reader.readLine();
                    if (m == null) {
                        //EOF / Wenn der OutputStream des Clients geschlossen wurde
                        remoteClient.clientClosedConnection = true;
                        closeConnection(this.remoteClient.getIP(), this.remoteClient.remoteSocket.getPort());
                    } else {
                        processMessage(remoteClient.getIP(), remoteClient.remoteSocket.getPort(), m);
                    }
                } catch (IOException e) {
                    closeConnection(remoteClient.getIP(), remoteClient.remoteSocket.getPort());
                    e.printStackTrace();
                }
            }
        }
    }
}

