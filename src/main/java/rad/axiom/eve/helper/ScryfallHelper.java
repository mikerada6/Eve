package rad.axiom.eve.helper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rad.axiom.eve.controller.CardController;
import rad.axiom.eve.mtg.Card;
import rad.axiom.eve.mtg.Color;
import rad.axiom.eve.mtg.Rarity;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.server.ExportException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ScryfallHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScryfallHelper.class);
    @Autowired
    CardController cardController;
    //    @Value("${mtg.datapath}")
    @Autowired
    private JSONHelper jsonHelper;
    @Autowired
    private ColorHelper colorHelper;
    private String downloadLocation;

    public String downloadDailyBulkData() throws ParseException, IOException {
        downloadLocation = "src/main/resources/downloads/json/";
        String url = "https://api.scryfall.com/bulk-data";
        String defaultCardsLocation = null;

        String result = jsonHelper.getRequest(url);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(result);
        JSONObject object = (JSONObject) obj;
        String updateTime = "";
        if (object.containsKey("data")) {
            JSONArray data = (JSONArray) object.get("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject datum = (JSONObject) data.get(i);
                if (datum.get("name").equals("Default Cards")) {
                    defaultCardsLocation = (String) datum.get("download_uri");
                    updateTime = (String) datum.get("updated_at");
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyLLdd'_'kkmm");
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(updateTime);
                    updateTime = zonedDateTime.format(format);
                    break;
                }
            }
        }


        String file = downloadLocation + updateTime + ".json";

        URL website = new URL(defaultCardsLocation);
        try (InputStream in = website.openStream()) {
            logger.info("Starting to download data from {}.",
                    defaultCardsLocation);
            Files.copy(in,
                    Paths.get(file),
                    StandardCopyOption.REPLACE_EXISTING);
            logger.info("Data downloaded to {}.", file);
        } catch (ExportException e) {
            return null;
        }
        return file;
    }

    public JsonParser openDownloadedJson(String filePath) throws ParseException, IOException {
        Object obj = null;
        JsonFactory jsonfactory = new JsonFactory();
        try (FileReader reader = new FileReader(filePath)) {
            //Read JSON file
            logger.info("Going to open file {}",
                    filePath);

            File jsonFile = new File(filePath);
            JsonParser jsonParser = jsonfactory.createParser(jsonFile);
            logger.info("File is loaded into the factory");
            return jsonParser;

        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException {}",
                    e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("IOException {}",
                    e);
            e.printStackTrace();
        }
        logger.error("We were not able to open the file {}.",
                filePath);
        return null;
    }

    public void setCardId(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setId(temp);
            }
        }
    }

    public void setCardName(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setName(temp);
            }
        }
    }

    public void setCardColors(Card card, HashMap<String, Object> map, String id) {
        ArrayList<Color> colors = new ArrayList<Color>();
        if (map.containsKey(id)) {
            ArrayList<String> temp = null;
            try {
                temp = (ArrayList<String>) map.get(id);
            } catch (Exception e) {
                return;
            }
            if (temp != null) {
                card.setColors(colorHelper.convertFromList(temp));
            }
        }
    }

    public void setCardColorIdentity(Card card, HashMap<String, Object> map, String id) {
        ArrayList<Color> colors = new ArrayList<Color>();
        if (map.containsKey(id)) {
            ArrayList<String> temp = null;
            try {
                temp = (ArrayList<String>) map.get(id);
            } catch (Exception e) {
                return;
            }
            if (temp != null) {
                card.setColor_identity(colorHelper.convertFromList(temp));
            }
        }
    }

    public void setColorIndicator(Card card, HashMap<String, Object> map, String id) {
        ArrayList<Color> colors = new ArrayList<Color>();
        if (map.containsKey(id)) {
            ArrayList<String> temp = null;
            try {
                temp = (ArrayList<String>) map.get(id);
            } catch (Exception e) {
                return;
            }
            if (temp != null) {
                card.setColor_indicator(colorHelper.convertFromList(temp));
            }
        }
    }

    public void setCMC(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = null;
            double cmc = 0;
            try {
                temp = map.get(id).toString();
                cmc = Double.parseDouble(temp);
            } catch (Exception e) {
                return;
            }

            if (temp != null) {
                card.setCmc(cmc);
            }
        }
    }

    public void setCollectorNumber(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setCollectorNumber(temp);
            }
        }
    }

    public void setLang(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setLang(temp);
            }
        }
    }

    public void setSetType(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setSetType(temp);
            }
        }
    }
    public void setImage_uris(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            HashMap<String, String> hashMap = (HashMap<String, String>) map.get(id);
            if (hashMap != null) {
                if(hashMap.containsKey("small"))
                {
                    card.setSmallUri(hashMap.get("small"));
                }
                if(hashMap.containsKey("normal"))
                {
                    card.setNormalUri(hashMap.get("normal"));
                }
                if(hashMap.containsKey("large"))
                {
                    card.setLargeUri(hashMap.get("large"));
                }
                if(hashMap.containsKey("art_crop"))
                {
                    card.setArtCropUri(hashMap.get("art_crop"));
                }
                if(hashMap.containsKey("border_crop"))
                {
                    card.setBorderCropUri(hashMap.get("border_crop"));
                }
                if(hashMap.containsKey("png"))
                {
                    card.setPngUri(hashMap.get("png"));
                }
            }
        }
    }


    public void setTypeLine(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setTypeLine(temp);
            }
        }
    }

    public void setManaCost(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setManaCost(temp);
            }
        }
    }

    public void setRarity(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                Rarity rarity = Rarity.fromLabel(temp);
                if (rarity != null)
                    card.setRarity(rarity);
            }
        }
    }

    public void setSetCode(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setSetCode(temp);
            }
        }
    }

    public void setSetName(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            if (temp != null) {
                card.setSetName(temp);
            }
        }
    }

    public boolean setVariation(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            boolean variation = Boolean.parseBoolean(temp);

            if (temp != null) {
                card.setVariation(variation);
                return variation;
            }
        }
        return false;
    }

    public boolean setSetBooster(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            boolean variation = Boolean.parseBoolean(temp);

            if (temp != null) {
                card.setBooster(variation);
                return variation;
            }
        }
        return false;
    }

    public void setVariationOf(Card card, HashMap<String, Object> map, String id) {
        if (map.containsKey(id)) {
            String temp = map.get(id).toString();
            try {
                card.setVariationOf(temp);
                card = cardController.getCardById(temp);
            } catch (Exception e) {
                return;
            }

            if (temp != null) {
                card.setVariationOf(temp);
            }
        }
    }


}
