package rad.axiom.eve.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import rad.axiom.eve.helper.JSONHelper;
import rad.axiom.eve.mtg.Card;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final String allDownloadLocation = "src/main/resources/downloads/images/all";
    @Autowired
    private CardController cardController;
    @Autowired
    private JSONHelper jsonHelper;

    /**
     * Endpoint to setup the database of all magic card images？
     *
     * @return true if alive
     */
    @RequestMapping(value = "/setup", method = RequestMethod.GET)
    public @ResponseBody
    Boolean setup() {
        List<Card> cards = cardController.getAllCards();
        setupFolders();
        int count = 0;
        for (Card card : cards) {
            count++;
            if (count % 1000 == 0) {
                logger.info("We have download {}/{} images.", count, cards.size());
            }
            String uri = getHighestResImageURI(card);
            try {
                byte[] image = getImageFromURL(uri);
                FileOutputStream fos = new FileOutputStream(allDownloadLocation + "/" + card.getId() + ".jpg");
                fos.write(image);
                fos.close();
                Thread.sleep(100);

            } catch (IOException | InterruptedException e) {
                logger.error("Could not get image for Card {}/{}.", card.getName(), card.getId());
            }
        }
        return true;
    }

    /**
     * Endpoint to setup the database of all magic card images？
     *
     * @return true if alive
     */
    @RequestMapping(value = "/setup/set/{setCode}", method = RequestMethod.GET)
    public @ResponseBody
    Boolean setupBySet(@PathVariable("setCode") String setCode) {
        List<Card> cards = cardController.getCardsBySet(setCode);
        if (cards.size() == 0) {
            logger.warn("Did not find any cards for set {}.  Therefore we could not match the image.", setCode);
            return null;
        }
        int count = 0;
        for (Card card : cards) {
            count++;
            if (count % 100 == 0) {
                logger.info("We have download {}/{} images.", count, cards.size());
            }
            String uri = getHighestResImageURI(card);
            try {
                byte[] image = getImageFromURL(uri);
                FileOutputStream fos = new FileOutputStream("src/main/resources/downloads/images/sets/" + "/" + setCode + "/" + card.getId() + ".jpg");
                fos.write(image);
                fos.close();
                Thread.sleep(100);

            } catch (IOException | InterruptedException e) {
                logger.error("Could not get image for Card {}/{}.", card.getName(), card.getId());
            }
        }
        return true;
    }

    @RequestMapping(value = "/identify/set/{setCode}", method = RequestMethod.GET)
    public @ResponseBody
    Card identifyGivenSet(@PathVariable("setCode") String setCode) {
        List<Card> cards = cardController.getCardsBySet(setCode);
        if (cards.size() == 0) {
            logger.warn("Did not find any cards for set {}.  Therefore we could not match the image.", setCode);
            return null;
        }
        List<String> ids = cards.stream().map(c -> c.getId()).collect(Collectors.toList());
        BufferedImage img = null;
        ArrayList<BufferedImage> setImages = new ArrayList<>();
        logger.info("Starting to load images");
        for (String id : ids) {
            try {
                img = ImageIO.read(new File(allDownloadLocation + "/" + id + ".jpg"));
                setImages.add(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("We have loaded {} images from set {}", setImages.size(), setCode);
        return null;
    }

    private byte[] getImageFromURL(String s) throws IOException {
        URL url = new URL(s);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }


    private String getHighestResImageURI(Card card) {
        String png = card.getPngUri();
        String large = card.getLargeUri();
        String normal = card.getNormalUri();
        String small = card.getSmallUri();

        if (png != null && png.length() > 0)
            return png;
        if (large != null && png.length() > 0)
            return large;
        if (normal != null && normal.length() > 0)
            return normal;
        if (small != null && small.length() > 0)
            return small;
        return null;
    }

    private void setupFolders() {
        String[] folders = {
                "src/main/resources/downloads/images/color/white",
                "src/main/resources/downloads/images/color/blue",
                "src/main/resources/downloads/images/color/black",
                "src/main/resources/downloads/images/color/red",
                "src/main/resources/downloads/images/color/green",
                "src/main/resources/downloads/images/color/basic",
                "src/main/resources/downloads/images/color/colorless",
                "src/main/resources/downloads/images/color/token",
                "src/main/resources/downloads/images/color/land",
                "src/main/resources/downloads/images/color/gold",
                "src/main/resources/downloads/images/color/other",
                "src/main/resources/downloads/images/rarities/basic",
                "src/main/resources/downloads/images/rarities/common",
                "src/main/resources/downloads/images/rarities/masterpiece",
                "src/main/resources/downloads/images/rarities/mythic",
                "src/main/resources/downloads/images/rarities/rare",
                "src/main/resources/downloads/images/rarities/special",
                "src/main/resources/downloads/images/rarities/token",
                "src/main/resources/downloads/images/rarities/uncommon",
                allDownloadLocation,

        };
        for (String basePath : folders) {
            File directory = new File(basePath);
            if (!directory.exists()) {
                directory.mkdirs();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
        }
        ArrayList<String> codes = new ArrayList<>();
        try {
            String url = "https://api.scryfall.com/sets/";
            String result = jsonHelper.getRequest(url);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(result);
            JSONObject object = (JSONObject) obj;
            JSONArray data = (JSONArray) object.get("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject datum = (JSONObject) data.get(i);
                String code = datum.get("code").toString();
                if (code.equalsIgnoreCase("con")) {
                    code = "_con";
                }
                codes.add(code);
            }
            String basePath = "src/main/resources/downloads/images/sets/";
            for (String code : codes) {
                File directory = new File(basePath + code);
                if (!directory.exists()) {
                    directory.mkdirs();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }
            }
        } catch (Exception e) {

        }
    }

}
