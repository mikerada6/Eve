package rad.axiom.eve.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SessionHelper {
    private static final Logger logger = LoggerFactory.getLogger(SessionHelper.class);

    public String getUUID()
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }
}
