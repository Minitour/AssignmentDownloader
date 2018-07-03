package mobi.newsound.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import mobi.newsound.model.Participant;
import mobi.newsound.network.APIManager;
import ui.UITableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class ParticipantsTableView extends UITableView<Participant> {

    List<Participant> participants = new ArrayList<>();
    TextField textField = new TextField();
    Predicate<Participant> predicate = null;

    public ParticipantsTableView(){
        //fetch data and then reload
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        tableView.setPlaceholder(progressIndicator);

        getToolBar().setVisible(false);
        APIManager.getInstance().getParticipants((participants, e)
                -> { this.participants.addAll(participants); this.reloadData(); getToolBar().setVisible(true); });

        textField.setPromptText("Search");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0)
                predicate = null;
            else
                predicate = participant
                        -> participant
                        .searchableValue()
                        .toLowerCase()
                        .contains(newValue.toLowerCase());

            reloadData();
        });
        getToolBar().getChildren().add(textField);
    }

    @Override
    public void layoutSubviews(ResourceBundle bundle) {
        super.layoutSubviews(bundle);

    }

    @Override
    public int numberOfColumns() {
        return 6;
    }

    @Override
    public Collection<? extends Participant> dataSource() {
        if (predicate == null)
            return participants;

        return participants.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public String bundleIdForIndex(int index) {
        switch (index){
            case 0: return "MOODLE_ID";
            case 1: return "ID";
            case 2: return "First Name";
            case 3: return "Last Name";
            case 4: return "Email";
            case 5: return "Role ID";
        }
        return null;
    }

    @Override
    public TableColumnValue<Participant> cellValueForColumnAt(int index) {
        switch (index){
            case 0: return Participant::getId;
            case 1: return Participant::getIdnumber;
            case 2: return Participant::getFirstname;
            case 3: return Participant::getLastname;
            case 4: return Participant::getEmail;
            case 5: return Participant::getMainRole;
        }
        return null;
    }
}
