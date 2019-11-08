package org.tfoms.snils.model.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



public class StatusBar {

    private ProgressBar progressBar;

    private Label statusLabel;
    private final Tooltip defaultTooltip = new Tooltip("Здесь будут показываться возможные ошибки");

    public StatusBar(ProgressBar pb,Label l){
        this.progressBar = pb;
        this.statusLabel = l;
    }

    public void update(String labelText){
        this.statusLabel.setText(labelText);
    }

    public void update(String labelText,String tooltipText){
        this.statusLabel.setText(labelText);

        if(!tooltipText.equals(""))
            this.statusLabel.setTooltip(new Tooltip(tooltipText));
    }

    public void update(String labelText, double progress){
        this.statusLabel.setText(labelText);
        this.progressBar.setProgress(progress);
        this.statusLabel.setTooltip(defaultTooltip);
    }

    public void update(String labelText,String tooltipText,double progress){
        this.statusLabel.setText(labelText);
        if(!tooltipText.equals(""))
            this.statusLabel.setTooltip(new Tooltip(tooltipText));
        this.progressBar.setProgress(progress);
    }

    public void reset(){
        statusLabel.setTooltip(defaultTooltip);
        statusLabel.setText("Статус");
    }
}
