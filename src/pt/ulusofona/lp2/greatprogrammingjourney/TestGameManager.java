package pt.ulusofona.lp2.greatprogrammingjourney;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class TestGameManager {

    private String[][] jogadoresBasicos() {
        return new String[][]{

                {"1","Ada","Blue"},
                {"2","Grace","Purple"}
        };
    }

    @Test
    void criacaoValida_retornaTrue_ePrimeiroJogadorCorreto() {

        GameManager gm = new GameManager();

        boolean ok = gm.createInitialBoard(jogadoresBasicos(), 10);

        assertTrue(ok);
        assertEquals(1, gm.getCurrentPlayerID());
    }

    @Test
    void criacaoInvalida_worldSizePequeno_ouCoresRepetidas() {

        GameManager gm = new GameManager();

        assertFalse(gm.createInitialBoard(jogadoresBasicos(), 3));

        String[][] repetidas = {

                {"1","Ada","Blue"},
                {"2","Grace","blue"}
        };

        assertFalse(gm.createInitialBoard(repetidas, 8));
    }

    @Test
    void slotInfoEProgrammerInfo_corTitleCase_posicao1Based() {

        GameManager gm = new GameManager();

        assertTrue(gm.createInitialBoard(jogadoresBasicos(), 12));

        String[] slot1 = gm.getSlotInfo(1);

        assertEquals("1,2", slot1[0]);

        String[] info2 = gm.getProgrammerInfo(2);
        assertEquals("Grace", info2[1]);
        assertEquals("Purple", info2[3]);
        assertEquals("1", info2[4]);
    }

    @Test
    void movimento_bateEVolta_vitoria_eResultados() {

        GameManager gm = new GameManager();

        assertTrue(gm.createInitialBoard(new String[][]{

                {"1","Ada","Blue"},
                {"2","Grace","Brown"}
        }, 10));


        assertTrue(gm.moveCurrentPlayer(6));
        assertTrue(gm.moveCurrentPlayer(1));
        assertTrue(gm.moveCurrentPlayer(4));

        assertEquals("9", gm.getProgrammerInfo(1)[4]);
        assertTrue(gm.moveCurrentPlayer(1));
        assertTrue(gm.moveCurrentPlayer(1));

        assertTrue(gm.gameIsOver());

        ArrayList<String> rep = gm.getGameResults();

        assertEquals("THE GREAT PROGRAMMING JOURNEY", rep.get(0));
        assertEquals("VENCEDOR", rep.get(5));
        assertEquals("Ada", rep.get(6));
    }

    @Test
    void imagemGlory_apenasNaUltimaCasa() {

        GameManager gm = new GameManager();

        assertTrue(gm.createInitialBoard(jogadoresBasicos(), 8));
        assertNull(gm.getImagePng(1));
        assertEquals("glory.png", gm.getImagePng(8));
    }
}