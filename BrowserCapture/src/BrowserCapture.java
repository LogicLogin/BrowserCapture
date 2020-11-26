import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BrowserCapture extends Application {

    private Stage stage = null;
    private WebEngine webEngine = null;
    private WebView view = null;
    private String url = "https://www.clocktab.com/";

    private String getParameter( int index ) {
        Parameters params = getParameters();
        List<String> parameters = params.getRaw();
        return !parameters.isEmpty() ? parameters.get(index) : "";
    }

    public HBox addressBar() {
        HBox addressBar = new HBox();

        TextField addressField = new TextField( );
        addressBar.getChildren().add( addressField );
        addressBar.setPadding(new Insets(5, 4, 5, 4));
        addressBar.setStyle("-fx-background-color: #421da8;");
        HBox.setHgrow(addressField, Priority.ALWAYS);
        addressField.setOnAction( (ActionEvent event) -> {
            url = addressField.getText();
            if ( !url.startsWith( "http:" ) && !url.startsWith( "file:")) {
                url = "http://" + url;
            }
            webEngine.load( url );
        } );
        stage.titleProperty().bind(webEngine.titleProperty());

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED)
                    addressField.setText(webEngine.getLocation());
            }
        });

        return addressBar;
    }

    private WebView website() {
        view = new WebView();
        webEngine = view.getEngine();
        return view;
    }

    private HBox screenshot() {
        HBox screenshotPane = new HBox();
        screenshotPane.setPadding(new Insets(5, 4, 5, 4));
        screenshotPane.setSpacing(10);
        screenshotPane.setStyle("-fx-background-color: #421da8;");
        Text description = new Text("Will take a screenshot of the above URL's website every hour (also choose a folder to save images) ---> ");
        description.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
        HBox.setHgrow(description, Priority.ALWAYS);

        Button start = new Button("Begin");
        start.setOnAction( event -> {

            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose a Directory to Store Images");
            File directory = chooser.showDialog(stage);

            System.out.println(directory);

            WebDriver

            //BrowserCapture browser = new BrowserCapture();
            //WritableImage image = browser.snapshot(new SnapshotParameters(), null);

            for (int i = 1; true; i++) {
                try {
                    String finalDirectory = directory + File.separator + "WebsiteCapture" + i + ".png";

                    System.out.println(finalDirectory);

                    TimeUnit.SECONDS.sleep(1);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        screenshotPane.getChildren().addAll(description, start);
        screenshotPane.setAlignment(Pos.CENTER);
        return screenshotPane;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BorderPane pane = new BorderPane();

        pane.setCenter( website() );
        String arg = getParameter( 0 );
        webEngine.load( arg == null || arg.isEmpty() ? "https://www.clocktab.com/" : arg );

        pane.setTop(addressBar());

        pane.setBottom(screenshot());

        Scene scene = new Scene(pane, 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
