package mobi.newsound.network;

/**
 * Created By Tony on 14/02/2018
 */
final public class Constants {

    /**
     * Protocol
     */
    public final static String service_url = Config.config.get("moodle_service").getAsString();

    public final static String moodle_session = Config.config.get("moodle_session").getAsString();

    public final static String ws_token = Config.config.get("ws_token").getAsString();

    public final static String course_id = Config.config.get("course_id").getAsString();

    /**
     * Routes class, contains all API routes.
     */
    public static class Routes {

        public static String main(){
            return service_url + "?moodlewsrestformat=json";
        }
    }

}
