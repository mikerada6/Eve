package rad.axiom.eve.helper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rad.axiom.eve.mtg.Color;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ColorHelperTest {

    private static ColorHelper colorHelper;

    @BeforeAll
    public static void setUp()
    {
        colorHelper = new ColorHelper();
    }

    @Test
    void convertFromListOneColorWhite() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("W");

        ArrayList<Color> ans = colorHelper.convertFromList(colors);

        assertTrue(ans.contains(Color.WHITE));
        assertFalse(ans.contains(Color.BLUE));
        assertFalse(ans.contains(Color.BLACK));
        assertFalse(ans.contains(Color.RED));
        assertFalse(ans.contains(Color.GREEN));
    }
    @Test
    void convertFromListOneColorBlue() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("U");

        ArrayList<Color> ans = colorHelper.convertFromList(colors);

        assertFalse(ans.contains(Color.WHITE));
        assertTrue(ans.contains(Color.BLUE));
        assertFalse(ans.contains(Color.BLACK));
        assertFalse(ans.contains(Color.RED));
        assertFalse(ans.contains(Color.GREEN));
    }
    @Test
    void convertFromListOneColorBlack() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("B");

        ArrayList<Color> ans = colorHelper.convertFromList(colors);

        assertFalse(ans.contains(Color.WHITE));
        assertFalse(ans.contains(Color.BLUE));
        assertTrue(ans.contains(Color.BLACK));
        assertFalse(ans.contains(Color.RED));
        assertFalse(ans.contains(Color.GREEN));
    }
    @Test
    void convertFromListOneColorRed() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("R");

        ArrayList<Color> ans = colorHelper.convertFromList(colors);

        assertFalse(ans.contains(Color.WHITE));
        assertFalse(ans.contains(Color.BLUE));
        assertFalse(ans.contains(Color.BLACK));
        assertTrue(ans.contains(Color.RED));
        assertFalse(ans.contains(Color.GREEN));
    }

    @Test
    void convertFromListTwoColorRedAndWhite() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("R");
        colors.add("W");

        ArrayList<Color> ans = colorHelper.convertFromList(colors);

        assertTrue(ans.contains(Color.WHITE));
        assertFalse(ans.contains(Color.BLUE));
        assertFalse(ans.contains(Color.BLACK));
        assertTrue(ans.contains(Color.RED));
        assertFalse(ans.contains(Color.GREEN));
    }
}