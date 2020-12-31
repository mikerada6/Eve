package rad.axiom.eve.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rad.axiom.eve.exception.ResourceNotFoundException;
import rad.axiom.eve.repository.WalleRepository;
import rad.axiom.eve.session.Walle;

import java.util.List;
import java.util.Optional;

@ManagedResource(description = "Walle Controller")
@Controller
@RequestMapping("/walle")
public class WalleController {


    private static final Logger logger = LoggerFactory.getLogger(WalleController.class);
    @Autowired
    private WalleRepository walleRepository;

    @GetMapping(path = "/{id}")
    public @ResponseBody
    Walle findWalleById(@PathVariable("id") String id) {
        logger.info("Finding Wall-E with Id:{}.", id);
        return walleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not Wall-E with id: " + id));
    }

    @GetMapping(path = "/")
    public @ResponseBody
    List<Walle> findAllWalles() {
        logger.info("Get all Wall-E's.");
        List<Walle> walles = walleRepository.findAll();
        return walles;
    }

    /**
     * A Method that will allow a Wall-E to check in and be ready to work.
     *
     * @param walleId
     * @return
     */
    @GetMapping(path = "/checkin")
    public @ResponseBody
    Walle startSession(@RequestParam(value = "walleId", required = true) String walleId, @RequestParam(value = "address", required = true) String address) {
        logger.info("Wall-E {} is checking in");

        Optional<Walle> walleOptional = walleRepository.findById(walleId);
        if (walleOptional.isPresent()) {
            logger.info("Found a Wall-E with id {} updating the address to {}.", walleId, address);
            Walle walle = walleOptional.get();
            walle.setAddress(address);
            walleRepository.save(walle);
            return walle;
        } else {
            logger.info("No Wall-E found createing a new Wall-E with id {} and address {}", walleId, address);
            Walle walle = new Walle();
            walle.setId(walleId);
            walle.setAddress(address);
            walleRepository.save(walle);
            return walle;
        }
    }

}
