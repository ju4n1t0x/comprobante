package org.ignia.comprobante;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.ignia.comprobante.ui.SpringFXMLLoader;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init(){
        context = new SpringApplicationBuilder(ComprobanteApplication.class)
                .web(WebApplicationType.NONE)
                .run(getParameters().getRaw().toArray(new String[0]));
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        loadFonts();

        SpringFXMLLoader loader = context.getBean(SpringFXMLLoader.class);

        Parent root = loader.load("/fxml/main-gate.fxml");
        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(
                getClass().getResource("/css/theme.css").toExternalForm());
        primaryStage.setTitle("Comprobante");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/JetBrainsMono-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/JetBrainsMono-Medium.ttf"), 14);
    }

    @Override
    public void stop(){
        context.close();
        Platform.exit();
    }
}
