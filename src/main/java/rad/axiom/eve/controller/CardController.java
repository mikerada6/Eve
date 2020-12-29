package rad.axiom.eve.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rad.axiom.eve.exception.ResourceNotFoundException;
import rad.axiom.eve.helper.ScryfallHelper;
import rad.axiom.eve.mtg.Card;
import rad.axiom.eve.repository.CardRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(path = "/card")
public
class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);
    @Autowired
    ScryfallHelper scryfallHelper;
    @Autowired
    private CardRepository cardRepository;

    @GetMapping(path = "/{cardId}")
    public @ResponseBody
    Card getCard(@PathVariable("cardId") String cardId) {
        logger.info("Getting card with Id: {}.", cardId);
        return cardRepository
                .findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("could not find card with id: " + cardId));
    }

    @GetMapping(path = "set/{setCode}")
    public @ResponseBody
    List<Card> getCardBySet(@PathVariable("setCode") String setCode) {
        logger.info("Geting all the cards from set {}.", setCode);
        List<Card> cards = cardRepository.findAllBySetCode(setCode);
        logger.info("Found {} cards in set {}.", cards.size(), setCode);
        return cards;
    }


    /**
     * Endpoint to check if the application is "alive". For use by load-balancer？
     *
     * @return true if alive
     */
    @RequestMapping(value = "/setup", method = RequestMethod.GET)
    public @ResponseBody
    Boolean setup() {
        ArrayList<Card> cards = new ArrayList<>();
        logger.info("Starting the setup");
        String fileLocation = downloadTodaysData();
        JsonParser jsonParser = null;

        if (fileLocation.equals("")) {
            logger.error("unable to get the download");
            return false;
        }
        jsonParser = openJSON(fileLocation);
        if (jsonParser == null) {
            logger.error("unable open the JSON");
            return false;
        }
        int count = 0;
        while (!jsonParser.isClosed()) {
            count++;
            JsonToken jsonToken = null;
            try {
                jsonToken = jsonParser.nextToken();
            } catch (IOException e) {
                logger.error("Unable to parse token with error {}.", e);
                return false;
            }
            if (JsonToken.START_OBJECT.equals(jsonToken)) {
                HashMap<String, Object> map = getObject(jsonParser);
                Card card = new Card();

                scryfallHelper.setCardId(card, map, "id");
                scryfallHelper.setCardName(card, map, "name");
                scryfallHelper.setCardColors(card, map, "colors");
                scryfallHelper.setCardColorIdentity(card, map, "color_identity");
                scryfallHelper.setColorIndicator(card, map, "color_indicator");
                scryfallHelper.setCMC(card, map, "cmc");
                scryfallHelper.setCollectorNumber(card, map, "collector_number");
                scryfallHelper.setManaCost(card, map, "mana_cost");
                scryfallHelper.setLang(card, map, "lang");
                scryfallHelper.setRarity(card, map, "rarity");
                scryfallHelper.setSetCode(card, map, "set");
                scryfallHelper.setSetName(card, map, "set_name");
                scryfallHelper.setSetBooster(card, map, "booster");
                scryfallHelper.setSetType(card, map, "set_type");
                scryfallHelper.setTypeLine(card, map, "type_line");
                boolean variation = scryfallHelper.setVariation(card, map, "variation");
                if (variation) {
                    scryfallHelper.setVariationOf(card, map, "variation_of");
                }
                if (card.getId() != null) {
                    cards.add(card);
                    if (cards.size() >= 500) {
                        logger.info("Saving a batrch of {} cards.", cards.size());
                        cardRepository.saveAll(cards);
                        cards.clear();
                    }

                } else {
                    logger.warn("Could not save card {}.", map);
                }
            }
        }
        if (cards.size() > 0) {
            logger.info("Saving a batrch of {} cards.", cards.size());
            cardRepository.saveAll(cards);
            cards.clear();
        }
        return true;
    }

    private String downloadTodaysData() {
        String fileLocation = "";
        try {
            fileLocation = scryfallHelper.downloadDailyBulkData();
        } catch (ParseException e) {
            logger.error("ParseException: {} error during downloadDailyBulkData", e);
            return null;
        } catch (IOException e) {
            logger.error("IOException: {} error during downloadDailyBulkData", e);
            return null;
        }
        return fileLocation;
    }

    private JsonParser openJSON(String file) {
        JsonParser jsonParser = null;
        try {
            jsonParser = scryfallHelper.openDownloadedJson(file);
        } catch (ParseException e) {
            logger.error("ParseException: {} error during openJSON", e);
            return null;
        } catch (IOException e) {
            logger.error("IOException: {} error during openJSON", e);
            return null;
        }
        return jsonParser;
    }

    private HashMap<String, Object> getObject(JsonParser jsonParser) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            JsonToken jsonToken = jsonParser.nextToken();
            while (!JsonToken.END_OBJECT.equals(jsonToken)) {
                if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                    String fieldName = jsonParser.getCurrentName();
                    jsonToken = jsonParser.nextToken();
                    if (JsonToken.START_ARRAY.equals(jsonToken)) {
                        jsonToken = jsonParser.nextToken();
                        ArrayList<Object> list = new ArrayList<>();
                        while (!JsonToken.END_ARRAY.equals(jsonToken)) {
                            list.add(jsonParser.getValueAsString());
                            jsonToken = jsonParser.nextToken();
                        }
                        map.put(fieldName,
                                list);
                    } else if (JsonToken.START_OBJECT.equals(jsonToken)) {
                        HashMap<String, Object> temp = getObject(jsonParser);
                        map.put(fieldName,
                                temp);
                    } else {
                        String value = jsonParser.getValueAsString();
                        map.put(fieldName,
                                value);
                    }
                } else {
                    jsonToken = jsonParser.nextToken();
                }
            }
        } catch (IOException e) {
            logger.error("IOException {}.",
                    e);
            e.printStackTrace();
        }
        return map;
    }


}