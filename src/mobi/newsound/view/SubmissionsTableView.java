package mobi.newsound.view;

import com.jfoenix.controls.JFXSnackbar;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mobi.newsound.Main;
import mobi.newsound.controller.DownloadController;
import mobi.newsound.model.Assignment;
import mobi.newsound.model.Participant;
import mobi.newsound.model.Submission;
import mobi.newsound.network.APIManager;
import mobi.newsound.utils.CSVExportRequest;
import mobi.newsound.utils.CSVExporter;
import ui.UITableView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class SubmissionsTableView extends UITableView<Submission> {

    private List<Submission> submissions = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();

    private Button downloadAllButton = new Button("Download");
    private Button exportCSV = new Button("Export CSV");
    private Assignment assignment;

    public SubmissionsTableView(Assignment assignment) {
        this.assignment = assignment;
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        tableView.setPlaceholder(progressIndicator);

        APIManager.getInstance().getSubmissions(assignment.getId(),(submissions, e)
                -> {
            this.submissions.addAll(submissions);
            this.reloadData();
        });


        ProgressIndicator loader = new ProgressIndicator();
        loader.setMaxHeight(20);
        getToolBar().getChildren().add(loader);
        APIManager.getInstance().getParticipants((participants, e) -> {
            getToolBar().setVisible(true);
            this.participants.addAll(participants.stream().filter(participant -> participant.getMainRole() == 5).collect(Collectors.toList()));
            Pane toolbar = getToolBar();
            toolbar.getChildren().clear();
            toolbar.getChildren().add(downloadAllButton);
            toolbar.getChildren().add(exportCSV);

            exportCSV.setOnAction(event -> {
                JFXSnackbar bar = new JFXSnackbar(this);
                bar.enqueue(new JFXSnackbar.SnackbarEvent("Exporting File..."));
                exportReport();
            });

            downloadAllButton.setOnAction(event -> downloadItems());
        });
    }

    private void  downloadItems(){
        DownloadController controller = new DownloadController();
        controller.setAssignment(assignment);

        Stage s = Main.createNewStageFrom(controller.view);
        s.initStyle(StageStyle.UNDECORATED);
        controller.currentStage = s;
        s.show();
        controller.startDownloading(joinMap());
    }

    private void exportReport(){

        Map<String,Submission>  submissionMap = joinMap();
        List<String[]> exportData = new ArrayList<>();
        submissionMap.forEach((s, submission) -> {
            if (submission != null)
                exportData.add(new String[]{
                        s,
                        submission.getStatus(),
                        String.valueOf(submission.getTimeModified()),
                        submission.getFileURL()
                });
            else
                exportData.add(new String[]{s,"-","-","-"});
        });
        File file = new File("./output/reports/");
        String path = file.getAbsolutePath();

        try {
            Files.createDirectories(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVExportRequest request = new CSVExportRequest(path+"/assignment_"+assignment.getId(),exportData,new String[]{"ID","Status","Last Modified","URL"});
        CSVExporter.export(request,(fileName, success) -> {
            System.out.println(success);
            System.out.println(fileName);
        },true);


    }

    private Map<String,Submission> joinMap(){
        Map<String,Submission>  submissionMap = new HashMap<>();
        for(Participant p : participants){
            for(Submission s : submissions)
                if (p.getId() == s.getUserid()) {
                    submissionMap.put(p.getIdnumber(), s);
                    break;
                }

            if (!submissionMap.containsKey(p.getIdnumber()))
                submissionMap.put(p.getIdnumber(),null);
        }
        return submissionMap;
    }

    @Override
    public int numberOfColumns() {
        return 6;
    }

    @Override
    public Collection<? extends Submission> dataSource() {
        return submissions;
    }

    @Override
    public String bundleIdForIndex(int index) {
        switch (index){
            case 0: return "id";
            case 1: return "user id";
            case 2: return "status";
            case 3: return "file name";
            case 4: return "file url";
            case 5: return "time modified";
        }
        return null;
    }

    @Override
    public TableColumnValue<Submission> cellValueForColumnAt(int index) {
        switch (index){
            case 0: return Submission::getId;
            case 1: return Submission::getUserid;
            case 2: return Submission::getStatus;
            case 3: return Submission::getFileName;
            case 4: return Submission::getFileURL;
            case 5: return Submission::getTimeModified;
        }
        return null;
    }
}
