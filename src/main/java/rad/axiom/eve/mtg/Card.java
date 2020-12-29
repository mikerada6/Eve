package rad.axiom.eve.mtg;



import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {
    @Id
    String id;
    private String name;
    @ElementCollection
    @CollectionTable(name="listOfColors")
    private List<Color> colors= new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="listOfColorIdentity")
    private List<Color> color_identity= new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="listOfColorIndicator")
    private List<Color> color_indicator= new ArrayList<>();
    private Rarity rarity;
    private String collectorNumber;
    private double cmc;
    private String typeLine;
    private Date released_at;
    private boolean promo;
    private boolean variation;
    private String lang;
    private String manaCost;
    private String uri;
    private String oracleText;
    private String setName;
    private String setCode;
    private String setType;
    private boolean reserved;
    private String variationOf;
    private boolean booster;
    private String pngUri;
    private String borderCropUri;
    private String artCropUri;
    private String largeUri;
    private String normalUri;
    private String smallUri;

}
