package mobi.newsound;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mobi.newsound.controller.ViewController;
import ui.UIView;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewController controller = new ViewController();
        controller.insertInto(primaryStage);
        primaryStage.show();
    }

    public static Stage createNewStageFrom(UIView view){
        Stage stage = new Stage();
        stage.setScene(new Scene(view));

        return stage;
    }
}
