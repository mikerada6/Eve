package rad.axiom.eve.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@ManagedResource(description = "Alive Controller")
@Controller
@RequestMapping("/eve")
public class AliveController {

    private static boolean _alive = true;
    private static final Logger logger = LoggerFactory.getLogger(AliveController.class);

    /**
     * Endpoint to check if the application is "alive". For use by load-balancer？
     *
     * @return true if alive
     */
    @RequestMapping(value = "/alive", method = RequestMethod.GET)
    public @ResponseBody
    Boolean alive() {
        logger.info("alive");
        return _alive;
    }

}
