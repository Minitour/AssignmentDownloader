package mobi.newsound.network;

import com.google.gson.*;
import javafx.application.Platform;
import mobi.newsound.model.Assignment;
import mobi.newsound.model.Participant;
import mobi.newsound.model.Submission;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static mobi.newsound.network.Constants.course_id;
import static mobi.newsound.network.Constants.ws_token;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class APIManager {
    public static APIManager getInstance(){
        return manager;
    }
    private final static APIManager manager = new APIManager();
    public final OkHttpClient client;
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    /**
     * Private constructor
     */
    private APIManager(){

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    public void getAssignments(final Callbacks.GetAssignments callback){

        Map<String,String> headers = new HashMap<>();
        headers.put("host","mw5.haifa.ac.il");
        headers.put("content-type","application/x-www-form-urlencoded;charset=UTF-8");
        headers.put("origin","file://");
        headers.put("accept","application/json, text/plain, */*");
        headers.put("user-agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_2 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C202 MoodleMobile");
        headers.put("user-language","en-us");
        headers.put("cache-control","no-cache");

        String payload = "courseids[0]=" + course_id + "&moodlewssettingfilter=true&moodlewssettingfileurl=true&wsfunction=mod_assign_get_assignments&wstoken=" + ws_token;

        makeRequest(Constants.Routes.main(),headers,payload,(json, exception) -> {

            //parse data
            List<Assignment> assignments = new ArrayList<>();

            JsonArray arr =json.getAsJsonObject().get("courses").getAsJsonArray().get(0).getAsJsonObject().get("assignments").getAsJsonArray();

            for(JsonElement object : arr){
                try {
                    Assignment assignment = gson.fromJson(object, Assignment.class);
                    assignments.add(assignment);
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }

            callback.make(assignments,exception);
        });
    }

    public void getParticipants(final Callbacks.GetParticipants callback){
        Map<String,String> headers = new HashMap<>();
        headers.put("host","mw5.haifa.ac.il");
        headers.put("content-type","application/x-www-form-urlencoded;charset=UTF-8");
        headers.put("origin","file://");
        headers.put("accept","application/json, text/plain, */*");
        headers.put("user-agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_2 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C202 MoodleMobile");
        headers.put("user-language","en-us");
        headers.put("cache-control","no-cache");

        String payload = "courseid=" + course_id + "&options[0][name]=limitfrom&options[0][value]=1&options[1][name]=limitnumber&options[1][value]=100&options[2][name]=sortby&options[2][value]=siteorder&moodlewssettingfilter=true&moodlewssettingfileurl=true&wsfunction=core_enrol_get_enrolled_users&wstoken=" + ws_token;

        makeRequest(Constants.Routes.main(),headers,payload,(json, exception) -> {

            //parse data
            List<Participant> participants = new ArrayList<>();

            JsonArray arr = json.getAsJsonArray();

            for(JsonElement object : arr){
                try {
                    Participant participant = gson.fromJson(object, Participant.class);
                    participants.add(participant);
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }

            callback.make(participants,exception);
        });
    }

    public void getSubmissions(int assignmentId, final Callbacks.GetSubmissions callback){
        Map<String,String> headers = new HashMap<>();
        headers.put("host","mw5.haifa.ac.il");
        headers.put("content-type","application/x-www-form-urlencoded;charset=UTF-8");
        headers.put("origin","file://");
        headers.put("accept","application/json, text/plain, */*");
        headers.put("user-agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_2 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C202 MoodleMobile");
        headers.put("user-language","en-us");
        headers.put("cache-control","no-cache");

        String payload = "assignmentids%5B0%5D=" + assignmentId + "&moodlewssettingfilter=true&moodlewssettingfileurl=true&wsfunction=mod_assign_get_submissions&wstoken=" + ws_token;

        makeRequest(Constants.Routes.main(),headers,payload,(json, exception) -> {

            //parse data
            List<Submission> submissions = new ArrayList<>();

            JsonArray arr = json
                    .getAsJsonObject()
                    .get("assignments")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("submissions")
                    .getAsJsonArray();

            for(JsonElement object : arr){
                try {
                    Submission submission = gson.fromJson(object, Submission.class);
                    submissions.add(submission);
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }

            callback.make(submissions,exception);
        });
    }

    private void makeRequest(String url,
                             Map<String,String> headers,
                             String payload,
                             final Callbacks.Inner callback){
        //define media type
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        //create request body from params

        RequestBody body = RequestBody.create(mediaType, payload);
        //create request
        Request request;
        Request.Builder builder = new Request
                .Builder()
                .url(url)
                .post(body)
                .addHeader("content-type","application/x-www-form-urlencoded;charset=UTF-8");

        //add additional headers
        if(headers != null)
            headers.forEach(builder::addHeader);

        request = builder.build();


        //make request
        System.out.println("SENDING: "+payload);
        makeOkHttpRequest(request,callback);
    }


    private void makeOkHttpRequest(Request request, Callbacks.Inner callback){
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null)
                    Platform.runLater(()-> callback.make(null,e));

                System.err.println( "onFailure: " + e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (callback!= null){
                    try (ResponseBody responseBody = response.body()) {
                        String res = responseBody.string();
                        System.out.println("onResponse: " + res);

                        //make thread safe.
                        Platform.runLater(() -> {
                            try{
                                JsonParser parser = new JsonParser();
                                JsonElement o = parser.parse(res);
                                callback.make(o,null);
                            }catch (Exception e) {
                                callback.make(null, e);
                            }

                        });
                        responseBody.close();
                    }
                }
            }
        });
    }
}
