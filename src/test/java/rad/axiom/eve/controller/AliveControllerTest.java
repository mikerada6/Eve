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
        String isAlive = aliveController.alive();
        assertEquals("directive?", isAlive);
    }
}