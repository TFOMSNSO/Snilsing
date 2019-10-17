package org.tfoms.snils.fxcontrollers;

import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import org.tfoms.snils.model.TablePerson;


import java.net.URL;
import java.util.ResourceBundle;

public class TableSearchController implements Initializable {
    private TableView<TablePerson> personTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(location + "\n" + resources);
    }


    public TableView getPersonTable() {
        return personTable;
    }

    public void setPersonTable(TableView personTable) {
        this.personTable = personTable;
    }
}
