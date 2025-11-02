package pt.ulusofona.lp2.greatprogrammingjourney;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.util.ArrayList;

public class GameManager {


    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_DICE = 1;
    private static final int MAX_DICE = 6;


    private static class Player {

        int id;
        String name;
        String colorLower;
        int pos;
    }


    private final Player[] players = new Player[MAX_PLAYERS];

    private int nPlayers = 0;

    private int worldSize = 0;
    private int currentIdx = 0;
    private boolean finished = false;
    private int turnCount = 0;

    public GameManager() {

    }


    private static String normalizeColor(String raw) {

        if (raw == null) return null;
        String c = raw.trim().toLowerCase();

        if (c.equals("blue") || c.equals("green") || c.equals("brown") || c.equals("purple")) {

            return c;
        }

        return null;
    }


    private static String toJarCase(String colorLower) {

        if (colorLower == null || colorLower.length() == 0) return "";

        char first = Character.toUpperCase(colorLower.charAt(0));
        String rest = colorLower.substring(1);
        return first + rest;
    }

    private Player findById(int id) {

        for (int i = 0; i < nPlayers; i++) {

            if (players[i].id == id) return players[i];
        }

        return null;
    }

    private boolean idAlreadyUsed(int id) {

        for (int i = 0; i < nPlayers; i++) {

            if (players[i].id == id) return true;
        }
        return false;
    }

    private boolean colorAlreadyUsed(String colorLower) {

        for (int i = 0; i < nPlayers; i++) {

            if (players[i].colorLower.equals(colorLower)) return true;
        }
        return false;
    }




    public boolean createInitialBoard(String[][] playerInfo, int worldSize) {

        if (playerInfo == null)
            return false;

        int n = playerInfo.length;

        if (n < MIN_PLAYERS || n > MAX_PLAYERS)
            return false;

        if (worldSize < n * 2)
            return false;


        Player[] tmp = new Player[MAX_PLAYERS];
        int tmpCount = 0;

        for (int i = 0; i < n; i++) {

            String[] row = playerInfo[i];

            if (row == null || row.length < 3)
                return false;


            int id;

            try {

                id = Integer.parseInt(row[0].trim());

            } catch (Exception e) {

                return false;
            }
            if (id <= 0)
                return false;


            String name = (row[1] == null) ? "" : row[1].trim();

            if (name.length() == 0)
                return false;


            String colorRaw = (row.length >= 4) ? row[3] : row[2];
            String colorLower = normalizeColor(colorRaw);

            if (colorLower == null)
                return false;


            for (int k = 0; k < tmpCount; k++) {

                if (tmp[k].id == id) return false;
                if (tmp[k].colorLower.equals(colorLower)) return false;
            }


            Player p = new Player();

            p.id = id;
            p.name = name;
            p.colorLower = colorLower;
            p.pos = 0;

            tmp[tmpCount] = p;
            tmpCount++;
        }


        for (int i = 0; i < MAX_PLAYERS; i++)
            players[i] = null;

        for (int i = 0; i < tmpCount; i++)
            players[i] = tmp[i];

        this.nPlayers = tmpCount;

        this.worldSize = worldSize;
        this.currentIdx = 0;
        this.finished = false;
        this.turnCount = 0;

        return true;
    }


    public String[] getSlotInfo(int position) {
        int idx = position - 1;
        if (idx < 0 || idx >= worldSize) return new String[]{""};

        String csv = "";
        boolean first = true;
        for (int i = 0; i < nPlayers; i++) {
            if (players[i].pos == idx) {
                if (!first) csv = csv + ",";
                csv = csv + players[i].id;
                first = false;
            }
        }
        return new String[]{ csv };
    }


    public String[] getProgrammerInfo(int id) {
        Player p = findById(id);
        if (p == null) return null;

        String[] out = new String[5];
        out[0] = String.valueOf(p.id);
        out[1] = p.name;
        out[2] = "";
        out[3] = toJarCase(p.colorLower);
        out[4] = String.valueOf(p.pos + 1);
        return out;
    }

    public String getProgrammerInfoAsStr(int id) {
        Player p = findById(id);
        if (p == null) return null;
        return p.id + " | " + p.name + " | " + (p.pos + 1) + " | " + toJarCase(p.colorLower) + " | Em Jogo";
    }

    public int getCurrentPlayerID() {
        if (nPlayers == 0) return -1;
        return players[currentIdx].id;
    }


    public boolean moveCurrentPlayer(int nrSpaces) {
        if (finished || nPlayers == 0) return false;
        if (nrSpaces < MIN_DICE || nrSpaces > MAX_DICE) return false;

        Player p = players[currentIdx];
        int last = worldSize - 1;
        int target = p.pos + nrSpaces;

        if (target == last) {
            p.pos = last;
            finished = true;
        } else if (target < last) {
            p.pos = target;
        } else {
            int overflow = target - last;
            int newPos = last - overflow;
            if (newPos < 0) newPos = 0;
            p.pos = newPos;
            if (newPos == last) finished = true;
        }

        turnCount++;
        if (!finished) {
            currentIdx = currentIdx + 1;
            if (currentIdx >= nPlayers) currentIdx = 0;
        }
        return true;
    }

    public boolean gameIsOver() {
        return finished;
    }

    public ArrayList<String> getGameResults() {
        ArrayList<String> out = new ArrayList<>();
        out.add("THE GREAT PROGRAMMING JOURNEY");
        out.add("");
        out.add("NR. DE TURNOS");
        out.add(String.valueOf(turnCount));

        if (!finished) return out;

        // encontrar vencedor (pos == last)
        int last = worldSize - 1;
        Player winner = null;
        for (int i = 0; i < nPlayers; i++) {
            if (players[i].pos == last) { winner = players[i]; break; }
        }

        out.add("");
        out.add("VENCEDOR");
        out.add(winner != null ? winner.name : "-");

        out.add("");
        out.add("RESTANTES");


        Player[] rest = new Player[nPlayers > 0 ? (nPlayers - 1) : 0];
        int r = 0;
        for (int i = 0; i < nPlayers; i++) {
            if (winner == null || players[i].id != winner.id) {
                rest[r] = players[i];
                r++;
            }
        }

        for (int i = 0; i < r - 1; i++) {
            int best = i;
            for (int j = i + 1; j < r; j++) {
                if (rest[j].pos > rest[best].pos) best = j;
            }
            if (best != i) {
                Player tmp = rest[i];
                rest[i] = rest[best];
                rest[best] = tmp;
            }
        }
        for (int i = 0; i < r; i++) {
            out.add(rest[i].name + " " + (rest[i].pos + 1));
        }
        return out;
    }


    public String getImagePng(int nrSquare) {
        if (worldSize > 0 && nrSquare == worldSize) {
            return "glory.png"; // existe no .jar
        }
        return null;
    }

    public JPanel getAuthorsPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 160));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("THE GREAT PROGRAMMING JOURNEY"));
        panel.add(new JLabel("Autores:"));
        panel.add(new JLabel("a22407606 - Fabio Farhat"));
        panel.add(new JLabel("a22403960 - Rodrigo Nascimento"));
        return panel;
    }

    public java.util.HashMap<String, String> customizeBoard() {

        return new java.util.HashMap<>();
    }
}
