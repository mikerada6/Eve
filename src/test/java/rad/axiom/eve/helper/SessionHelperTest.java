package rad.axiom.eve.helper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SessionHelperTest {

    private static SessionHelper sessionHelper;

    @BeforeAll
    public static void setUp() {
        sessionHelper = new SessionHelper();
    }

    @Test
    void getUUID() {
        for(int i=0;i<10000;i++) {
            String uuid = sessionHelper.getUUID();
            assertFalse(uuid.contains("-"), "UUID contains -.");
            assertEquals(32, uuid.length(), "UUID is not the right size.");
        }
    }
}