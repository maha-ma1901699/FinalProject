import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Game game;
    //private List<PrintWriter> list =new ArrayList<>();
    private Map<String, PrintWriter> maps = new HashMap<>();

    public Server() {
        game = new Game();
        try {
            serverSocket = new ServerSocket(13337);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ExecutorService executor = Executors.newCachedThreadPool();

    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new Player(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    private class Player implements Runnable {

        private Socket socket;
        private String nickName;
        private PrintWriter printWriter = null;

        public Player(Socket socket) {
            this.socket = socket;
            nickName = socket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                OutputStream outputStream = socket.getOutputStream();
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)), true);
                //maps.put(nickName,printWriter);
                //broadcastGameMessage(nickName+" is online，current online player count is："+maps.size());
                String message;
                while ((message = bufferedReader.readLine()) != null) {

                    if (message.startsWith("PlayerGuess")) {
                        int guess = Integer.parseInt(message.split(" ")[1]);
                        if(!game.contain(nickName)){
                            sendMessage("you are not in game");
                        }else {
                            game.addGuess(nickName, guess);
                            if (game.isFinishCurrentRound()) {
                                game.calcWin();
                                game.calcScore();
                                String msg = game.formatInfo();
                                game.nextRound();
                                broadcastGameMessage(msg);
                            }
                        }
                    } else if (message.equals("CheckResults")) {
                        //broadcastGameResults();
                        game.reset(); // Reset the game for the next round
                    } else if (message.startsWith("Online")) {
                        nickName = message.split(" ")[1];
                        maps.put(nickName, printWriter);
                    }   else if (message.startsWith("SendMessage")) {
                        //SendMessage playerName message
                        String to = message.split(" ")[1].trim();
                        String mesg = message.split(" ")[2].trim();
                        maps.get(to).println(nickName+" say to you:"+mesg);
                }else if (message.startsWith("Join")) {
                        game.addPlayer(nickName);
                        broadcastGameMessage(nickName + " Join Game!");

                    } else if (message.startsWith("Start")) {
                        if (game.canStart()) {
                            broadcastGameMessage("Game is Starting....");
                            broadcastGameMessageForJoiner("Please Input Guess number?");

                        } else {
                            this.sendMessage("At least two people!!!!");
                        }

                    } else {
                        System.out.println("Received unknown message: " + message);
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (maps) {
                    maps.remove(nickName);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastGameMessageForJoiner(String msg) {
            Set<String> players = game.getPlayers();
            synchronized (maps) {

                for (Map.Entry<String, PrintWriter> entry : maps.entrySet()) {
                    if(players.contains(entry.getKey())){
                        entry.getValue().println(msg);
                    }
                }

            }

        }

        private void sendMessage(String msg) {
            this.printWriter.println(msg);

        }
    }

    private void broadcastToGameJoinerMessage(String msg) {
        System.out.println(msg);
        synchronized (maps) {
            for (Map.Entry<String, PrintWriter> stringPrintWriterEntry : maps.entrySet()) {
                if(game.contain(stringPrintWriterEntry.getKey())){
                    stringPrintWriterEntry.getValue().println(msg);
                }

            }

        }
    }

    private void broadcastGameMessage(String msg) {
        System.out.println(msg);
        synchronized (maps) {
            for (PrintWriter p : maps.values()) {
                p.println(msg);
            }
        }
    }


}
