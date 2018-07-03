package mobi.newsound.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mobi.newsound.model.Assignment;
import mobi.newsound.model.Participant;
import mobi.newsound.model.Submission;

import java.util.List;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public final class Callbacks {

    private Callbacks(){}

    @FunctionalInterface
    interface Inner{ void make(JsonElement json, Exception exception); }

    @FunctionalInterface
    public interface GetAssignments { void make(List<Assignment> assignments,Exception e); }


    @FunctionalInterface
    public interface GetParticipants{ void make(List<Participant> participants, Exception e); }

    @FunctionalInterface
    public interface GetSubmissions{ void make(List<Submission> submissions, Exception e); }


}
