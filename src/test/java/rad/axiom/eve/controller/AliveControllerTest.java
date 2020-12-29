package rad.axiom.eve.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AliveControllerTest {

    private static AliveController aliveController;

    @BeforeAll
    public static void setUp()
    {
        aliveController = new AliveController();
    }

    @Test
    void alive() {
        Boolean isAlive = aliveController.alive();
        assertEquals(true, isAlive);
    }
}