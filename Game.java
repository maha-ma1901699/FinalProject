package osFinalProject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    private Map<String, Integer> playerGuesses = new HashMap<String, Integer>();//List to store player guesses

    private Map<String, AtomicInteger> scores = new HashMap<>();

    //private Set<String> players = new HashSet<>();

    private List<String> summary = new ArrayList<>();

    private boolean running = false;
    private int round = 1;
    private int totalPlayer;

    String currentWin = null;

    public Game() {
        playerGuesses.clear();
        round = 1;
    }

    public boolean isOnlyOne() {
        return scores.size() == 1;
    }

    public void addPlayer(String nickName) {
        this.scores.put(nickName, new AtomicInteger(5));
    }

    public void loseRound(String nickName) {
        this.scores.get(nickName).decrementAndGet();
    }

    public void winRound(String nickName) {
        this.scores.get(nickName).incrementAndGet();
        // pushed already 
    }

    public String formatInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("game  round  ").append(round);
        Set<String> sets = scores.keySet();
        int i = 1;
        stringBuffer.append("  ");
        for (String set : sets) {
            if (i != 1) {
                stringBuffer.append(",");
            }
            i++;
            stringBuffer.append(set);
        }

        stringBuffer.append("  ");
        i = 1;
        for (String set : sets) {
            if (i != 1) {
                stringBuffer.append(",");
            }
            i++;
            stringBuffer.append(playerGuesses.get(set));
        }

        stringBuffer.append("  ");
        i = 1;
        for (String set : sets) {
            if (i != 1) {
                stringBuffer.append(",");
            }
            i++;
            stringBuffer.append(scores.get(set));
        }

        stringBuffer.append("  ");
        i = 1;
        for (String set : sets) {
            if (i != 1) {
                stringBuffer.append(",");
            }
            i++;
            String str = "lose";
            if (currentWin.equals(set)) {

                str = "win";
            } else {

                str = "lose";
            }
            stringBuffer.append(str);
        }

        String msg =  stringBuffer.toString();

        this.summary.add(msg);
        return  msg;
    }

    // Method to add a player's guess
    public void addGuess(String nickName, int guess) {
        playerGuesses.put(nickName, guess);
    }

    // Method to calculate the average of player guesses
    private double calculateAverage() {
        //this.round++;
        int sum = 0;
        for (int guess : playerGuesses.values()) {
            sum += guess;
        }
        return (double) sum / playerGuesses.size();
    }

    // Method to calculate the target value (2/3 of the average)
    private double calculateTargetValue() {
        return (2.0 / 3.0) * calculateAverage();
    }





    // Method to get player name from guess (assuming you have a way to map player names to guesses)
    private String getPlayerNameFromGuess(int guess) {
        // Implement logic to retrieve player name based on the guess
        return "Player Name"; // Replace with actual player name retrieval logic
    }


    public void reset() {
        playerGuesses.clear();
        round = 1;
        scores.clear();
    }

    public boolean canStart() {
        return this.scores.size() >= 2;
    }

    public String getRound() {
        return round + "";
    }

    public boolean isFinishCurrentRound() {
        if(this.scores.size()==this.playerGuesses.size()){
            return  true;
        }
        return false;
    }

    public void calcWin() {

        double lastDiff = Double.MAX_VALUE;
        double targetValue = calculateTargetValue();
        System.out.println("targetValue:"+targetValue);
        for (Map.Entry<String, Integer> stringIntegerEntry : playerGuesses.entrySet()) {
            double difference = Math.abs(stringIntegerEntry.getValue() - targetValue);

            if (difference < lastDiff) {
                lastDiff = difference;
                currentWin = stringIntegerEntry.getKey();
            }
        }
        System.out.println("currentWin:"+currentWin);

    }

    Set<String> r = new HashSet<>();
    public void calcScore() {

        r.clear();
        for (Map.Entry<String, AtomicInteger> entry : scores.entrySet()) {
           if(entry.getKey().equals(currentWin)){
               entry.getValue().incrementAndGet();
           }else{

               int value = entry.getValue().decrementAndGet();
               if(value==0){
                   //
                   r.add(entry.getKey());
               }
           }
        }


    }

    public String getLastInfo() {
        return this.summary.get(this.summary.size()-1);
    }
    public void beginNext() {



    }


    public void calcNextRoundPlayer() {
        Set<String> sets = new HashSet<>();

        for (Map.Entry<String, AtomicInteger> stringAtomicIntegerEntry : scores.entrySet()) {
            if(stringAtomicIntegerEntry.getValue().get()>0){
                sets.add(stringAtomicIntegerEntry.getKey());
            }
        }
        for (String set : sets) {
            scores.remove(set);
        }

    }

    public Set<String> getPlayers() {

        Set<String> sets = new HashSet<>();

        for (Map.Entry<String, AtomicInteger> stringAtomicIntegerEntry : scores.entrySet()) {
            if(stringAtomicIntegerEntry.getValue().get()>0){
                sets.add(stringAtomicIntegerEntry.getKey());
            }
        }
        return sets;
    }

    public boolean contain(String user){
        return scores.containsKey(user);

    }
    public void nextRound() {
        this.round++;
        playerGuesses.clear();
        for (String s : r) {
            scores.remove(s);
        }
        r.clear();
        //


    }
}