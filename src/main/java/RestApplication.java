import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Felix Rottler
 */
@ApplicationPath("api")
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        packages("com.uni.share");
        register(MultiPartFeature.class);
    }
}
