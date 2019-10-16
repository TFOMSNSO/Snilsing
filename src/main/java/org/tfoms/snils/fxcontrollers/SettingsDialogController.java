package org.tfoms.snils.fxcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.tfoms.snils.model.ui.Settings;
import org.tfoms.snils.model.ui.StatusBar;

import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;


public class SettingsDialogController {
    private boolean isChanged = false;

    @FXML
    private TextField fieldTimeWait;

    @FXML
    private TextField fieldResponseFolder;

    @FXML
    private TextField fieldRequestFolder;

    @FXML
    private TextField fieldErrorFolder;

    @FXML
    private RadioButton radioSaveResponse;

    @FXML
    private Label statusLabel;

    private final Settings settings = new Settings();

    @FXML
    void initialize(){
        refresh();
    }

    @FXML
    private void saveSettings(){
            settings.setErrorFolder(fieldErrorFolder.getText().trim());
            settings.setRequestFolder(fieldRequestFolder.getText().trim());
            settings.setResponseFolder(fieldResponseFolder.getText().trim());
            settings.setTimeWait(fieldTimeWait.getText());
            settings.setSaveResponse(radioSaveResponse.isSelected());
            System.out.println("saving");
            settings.save();
            refresh();
            isChanged = false;
            reject();
    }

    @FXML
    private void reject(){
        if(isChanged){
            if(areYouSure("Выйти без сохраниня")){
                ((Stage) fieldTimeWait.getScene().getWindow()).close();
            }else{
                //не выходить
            }
        }else{
            ((Stage) fieldTimeWait.getScene().getWindow()).close();
        }
    }

    @FXML
    private void changesMade(){
        this.isChanged = true;
        System.out.println("changes");
    }

    private void refresh(){
        fieldRequestFolder.setText(settings.getRequestFolder());

        fieldResponseFolder.setText(settings.getResponseFolder());

        fieldErrorFolder.setText(settings.getErrorFolder());

        radioSaveResponse.setSelected(settings.isSaveResponse());

        fieldTimeWait.setText(settings.getTimeWait());
    }


    private boolean areYouSure(String action){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, action);
        alert.setHeaderText("Вы уверены?");
        alert.setTitle("Подтвердите действие");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) return true;
        return false;
    }

    @FXML
    private void resetSettings(){
        if(areYouSure("Сбросить настройки до настроек по умолчанию")) {
            settings.reset();
            settings.loadSettings();
            refresh();
        }
    }
}
