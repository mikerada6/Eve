package rad.axiom.eve.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rad.axiom.eve.exception.ForbiddenException;
import rad.axiom.eve.exception.ResourceNotFoundException;
import rad.axiom.eve.helper.JSONHelper;
import rad.axiom.eve.mtg.Card;
import rad.axiom.eve.session.Session;
import rad.axiom.eve.session.SessionStatus;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(path = "/image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final String allDownloadLocation = "src/main/resources/downloads/images/all";
    @Autowired
    private CardController cardController;
    @Autowired
    private JSONHelper jsonHelper;
    @Autowired
    private SessionController sessionController;
    @Autowired
    private WalleController walleController;

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

    @RequestMapping(value = "/identify/set/{setCode}", method = RequestMethod.POST)
    public @ResponseBody
    Card identifyImageFromImage(@PathVariable("setCode") String setCode, @RequestParam(value = "sessionId", required = true) String sessionId, @RequestParam(value = "image", required = true) MultipartFile image) {
        logger.info("identifyFromImage");
        Session session = null;
        try {
            session = sessionController.findSessionById(sessionId);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Could not find a session with id: " + sessionId + ".");
        }

        if (session.getStatus() == SessionStatus.CLOSED) {
            throw new ForbiddenException("Session " + sessionId + " is closed and you can not add new scans to it.");
        }
        if(session.getStatus() == SessionStatus.PAUSED)
        {
            session.setStatus(SessionStatus.NORMAL);
        }


        List<Card> cards = cardController.getCardsBySet(setCode);
        UUID pictureID = UUID.randomUUID();
        try {
            byte[] byteArr = image.getBytes();
            saveImageToFile("src/main/resources/downloads/identify/",
                    pictureID.toString(),
                    byteArr);
            InputStream inputStream = new ByteArrayInputStream(byteArr);
        } catch (Exception e) {
            logger.error("error {}", e);
        }


        int index = getRandomNumber(0, cards.size() - 1);

        session.setLastUpdated(System.currentTimeMillis());
        sessionController.saveSession(session);

        return cards.get(index);
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

    /**
     * @param folder
     * @param file
     * @param image
     * @throws IOException
     */
    private void saveImageToFile(String folder, String file, byte[] image) throws IOException {
        logger.info("Folder: " + folder);
        logger.info("\tfile: " + file);
        String PATH = "";
        String directoryName = PATH.concat(folder);
        String fileName = file + ".jpg";

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        FileOutputStream fos = new FileOutputStream(folder + "/" + fileName);
        logger.info(folder + "/" + fileName);
        fos.write(image);
        fos.close();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}

