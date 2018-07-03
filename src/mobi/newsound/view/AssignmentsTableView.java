package mobi.newsound.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mobi.newsound.Main;
import mobi.newsound.controller.ViewController;
import mobi.newsound.model.Assignment;
import mobi.newsound.network.APIManager;
import ui.UITableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class AssignmentsTableView extends UITableView<Assignment> {

    public ViewController.AssignmentsTableViewCallback delegate;

    private TableColumn col_action;
    private List<Assignment> assignments = new ArrayList<>();

    public AssignmentsTableView(){
        //fetch data and then reload
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        this.tableView.setPlaceholder(progressIndicator);
        APIManager.getInstance().getAssignments((assignments, e)
                -> {
            this.assignments.addAll(assignments);
            this.reloadData();
            this.tableView.setPlaceholder(new Label("Nothing found"));
        });
    }


    @Override
    public void layoutSubviews(ResourceBundle bundle) {
        super.layoutSubviews(bundle);
        col_action = new TableColumn<>();
        tableView.getColumns().add(col_action);

        col_action.setCellFactory(param ->
                new ButtonCell("View", index -> {
                    Assignment assignment = this.assignments.get(index);
                    //SubmissionsTableView submissionsTableView = new SubmissionsTableView(assignmentId);
                    //Main.createNewStageFrom(submissionsTableView);
                    if (delegate != null){
                        delegate.didSelectAssignment(assignment);
                    }
                })
        );

        Button button = new Button("View Participants");
        button.setOnAction(event -> {
            ParticipantsTableView participantsTableView = new ParticipantsTableView();
            Stage stage = Main.createNewStageFrom(participantsTableView);
            stage.setTitle("Participants");
            stage.show();
        });

        getToolBar().getChildren().add(button);
    }

    @Override
    public int numberOfColumns() {
        return 6;
    }

    @Override
    public Collection<? extends Assignment> dataSource() {
        return assignments;
    }

    @Override
    public String bundleIdForIndex(int index) {
        switch (index){
            case 0: return "ID";
            case 1: return "cmid";
            case 2: return "course";
            case 3: return "name";
            case 4: return "allow submissions from date";
            case 5: return "due date";
        }
        return null;
    }

    @Override
    public TableColumnValue<Assignment> cellValueForColumnAt(int index) {
        switch (index){
            case 0: return Assignment::getId;
            case 1: return Assignment::getCmid;
            case 2: return Assignment::getCourse;
            case 3: return Assignment::getName;
            case 4: return Assignment::getStringStartDate;
            case 5: return Assignment::getStringDueDate;
        }
        return null;
    }

    public static class ButtonCell extends TableCell {
        final Button cellButton;

        public ButtonCell(String title,ActionCallBack callBack){
            cellButton = new Button(title);
            cellButton.setOnAction(t -> callBack.didSelectAction(ButtonCell.this.getIndex()));
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setTextFill(null);
                setGraphic(null);
                return;
            }

            if (!empty) {
                setAlignment(Pos.CENTER);
                setGraphic(cellButton);
            }
        }

        public interface ActionCallBack{
            void didSelectAction(int index);
        }
    }
}
