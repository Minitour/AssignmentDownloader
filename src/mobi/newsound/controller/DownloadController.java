package mobi.newsound.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mobi.newsound.data_manager.FileDownloader;
import mobi.newsound.data_manager.FileHandler;
import mobi.newsound.model.Assignment;
import mobi.newsound.model.Submission;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.codehaus.plexus.util.FileUtils;
import ui.UIViewController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Handler;

/**
 * Created by Antonio Zaitoun on 03/07/2018.
 */
public class DownloadController extends UIViewController{

    public Stage currentStage;

    @FXML
    private Label titleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button actionButton;

    private boolean isCanceled = false;

    private boolean isFinished = false;

    private Assignment assignment;
    private static double xOffset = 0;
    private static double yOffset = 0;

    /**
     * Create a UIViewController instance from any fxml file.
     *
     */
    public DownloadController() {
        super("/mobi/newsound/controller/DownloadController.fxml");
    }

    @Override
    public void viewWillLoad(ResourceBundle bundle) {
        super.viewWillLoad(bundle);
        actionButton.setText("Cancel");
        actionButton.setOnAction(event -> {
            if (isFinished){
                if( Desktop.isDesktopSupported() )
                {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().open(new File("./output/assignment/"+assignment.getId()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }).start();
                }
                currentStage.close();
            }else {
                isCanceled = true;
            }
        });

        getRoot().setOnMouseDragged(event -> {
            currentStage.setX(event.getScreenX() + xOffset);
            currentStage.setY(event.getScreenY() + yOffset);
        });
        getRoot().setOnMousePressed(event -> {
            xOffset = currentStage.getX() - event.getScreenX();
            yOffset = currentStage.getY() - event.getScreenY();
        });
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
        titleLabel.setText(assignment.getName());
    }

    public void startDownloading(Map<String,Submission> submissionMap){
        progressIndicator.setProgress(0);
        Thread t = new Thread(() -> {
            int size = submissionMap.size();
            final int[] counter = {0};
            submissionMap.forEach((s, submission) -> {
                if (isCanceled){
                    File file = new File("./output/assignment/"+assignment.getId());
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException ignored) { }
                    Platform.runLater(() -> currentStage.close());
                    throw new RuntimeException();
                }

                //create folder
                Platform.runLater(() -> statusLabel.setText("Downloading "+s));

                File file = new File("./output/assignment/"+assignment.getId());
                File errorDir = new File(file.getPath() +"/failed");

                try { Files.createDirectories(file.toPath()); } catch (IOException ignored) { }
                try { Files.createDirectories(errorDir.toPath()); } catch (IOException ignored) { }


                if (submission != null && submission.getFileURL().length() > 4) {
                    //download file into assignment dir
                    String fileName = "/"+s +"." + getFileExtension(submission.getFileName());
                    File destination = new File(
                            System.getProperty("java.io.tmpdir"),
                            UUID.randomUUID().toString() + "." + getFileExtension(submission.getFileName())
                    );

                    try {
                        FileDownloader
                                .download(
                                        submission.getFileURL(),
                                        destination.getPath());

                        Platform.runLater(() -> statusLabel.setText("Unzipping "+s));

                        try {
                            ZipFile zipFile = new ZipFile(destination);
                            zipFile.extractAll(file.getAbsolutePath() + "/" + s);
                        } catch (ZipException e) {
                            //try to unrar
                            try {
                                FileHandler.extractRar(destination.getPath(),file.getAbsoluteFile() + "/" + s);
                            }catch (Exception ex){
                                //try 7z
                                try {
                                    FileHandler.decompress7z(destination.getPath(),new File(file.getAbsoluteFile() + "/" + s));
                                } catch (IOException e1) {
                                    copy(destination.toPath(),new File(file.getPath() + "/failed"+fileName).toPath());
                                    Platform.runLater(() -> statusLabel.setText("Failed to unzip "+s));
                                }
                            }
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> statusLabel.setText("Failed Downloading "+s));
                    }

                }

                ++counter[0];

                Platform.runLater(()-> {
                    progressIndicator.setProgress((double) counter[0] / size );
                    //download and unzip has completed
                    if(progressIndicator.getProgress() == 1){
                        //rename project names if possible
                        startRenaming(assignment.getId());
                    }
                });
            });
        });
        t.start();
    }

    void startRenaming(int assignmentId){

        //start renaming on another thread.
        Thread t1 = new Thread(() -> {
            //rename files
            FileHandler.updateProjectNames(assignmentId,progress -> Platform.runLater(() -> {
                statusLabel.setText("Renaming projects");
                if (progress == 1) {
                    statusLabel.setText("Download Completed");
                    isFinished = true;
                    actionButton.setText("Open Folder");
                }
            }));
        });

        t1.start();

    }

    static String getFileExtension(String name) {
        int i = name.lastIndexOf('.');
        String ext = i > 0 ? name.substring(i + 1) : "";
        return ext;
    }

    static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
