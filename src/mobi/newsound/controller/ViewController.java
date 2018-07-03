package mobi.newsound.controller;

import javafx.stage.Stage;
import mobi.newsound.Main;
import mobi.newsound.model.Assignment;
import mobi.newsound.view.AssignmentsTableView;
import mobi.newsound.view.SubmissionsTableView;
import ui.UIViewController;

import java.util.ResourceBundle;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class ViewController extends UIViewController {

    private AssignmentsTableView tableView;

    /**
     * Create a UIViewController instance from any fxml file.
     */
    public ViewController() {
        super("/mobi/newsound/controller/ViewController.fxml");
    }

    @Override
    public void viewWillLoad(ResourceBundle bundle) {
        tableView = new AssignmentsTableView();
        tableView.delegate = this::didSelectAssignment;
        getRoot().getChildren().add(tableView);
    }

    @Override
    public String title() {
        return "Assignments";
    }

    public void didSelectAssignment(Assignment assignment) {
        //show new stage
        SubmissionsTableView tableView = new SubmissionsTableView(assignment);
        Stage s = Main.createNewStageFrom(tableView);
        s.setTitle(assignment.getName());
        s.show();
    }

    @FunctionalInterface
    public interface AssignmentsTableViewCallback{
        void didSelectAssignment(Assignment assignment);
    }

}
