package org.ignia.comprobante;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.ignia.comprobante.security.SessionService;
import org.ignia.comprobante.ui.SpringFXMLLoader;
import org.ignia.comprobante.ui.view.security.LoginDialog;
import org.ignia.comprobante.user.IUserService;
import org.ignia.comprobante.user.UserModel;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

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

        //Login
        IUserService userService = context.getBean(IUserService.class);
        BCryptPasswordEncoder encoder = context.getBean(BCryptPasswordEncoder.class);
        LoginDialog loginDialog = new LoginDialog(userService, encoder);
        Optional<UserModel> user = loginDialog.showAndWait();

        if (user.isEmpty()){
            Platform.exit();
            return;
        }

        context.getBean(SessionService.class).login(user.get());

        //cargar ui principal
        SpringFXMLLoader loader = context.getBean(SpringFXMLLoader.class);

        Parent root = loader.load("/fxml/main-gate.fxml");
        Scene scene = new Scene(root, 1340, 760);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(750);
        primaryStage.setMaximized(true);
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
