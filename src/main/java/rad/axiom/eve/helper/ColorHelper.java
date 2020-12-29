package rad.axiom.eve.helper;

import org.springframework.stereotype.Component;
import rad.axiom.eve.mtg.Color;

import java.util.ArrayList;

@Component
public class ColorHelper {

    public ArrayList<Color> convertFromList(ArrayList<String> _colors) {
        ArrayList<Color> colors = new ArrayList<Color>();
        for (String color : _colors) {
            if (color.equalsIgnoreCase("W")) {
                colors.add(Color.WHITE);
            } else if (color.equalsIgnoreCase("U")) {
                colors.add(Color.BLUE);
            } else if (color.equalsIgnoreCase("B")) {
                colors.add(Color.BLACK);
            } else if (color.equalsIgnoreCase("R")) {
                colors.add(Color.RED);
            } else if (color.equalsIgnoreCase("G")) {
                colors.add(Color.GREEN);
            }
        }
        return colors;
    }

}
