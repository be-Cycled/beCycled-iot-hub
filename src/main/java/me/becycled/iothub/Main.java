package me.becycled.iothub;

import me.becycled.iothub.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author binakot
 */
@SuppressWarnings({"HideUtilityClassConstructor", "PMD.UseUtilityClass"})
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String... args) {
        final ServerManager manager = new ServerManager();

        LOGGER.warn("Starting servers...");
        try {
            manager.startAll();
        } catch (Exception ex) {
            LOGGER.error("Error on servers running up.", ex);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.warn("Shutting down servers...");
            try {
                manager.stopAll();
            } catch (Exception ex) {
                LOGGER.error("Error on servers shutting down.", ex);
            }
        }));
    }
}
