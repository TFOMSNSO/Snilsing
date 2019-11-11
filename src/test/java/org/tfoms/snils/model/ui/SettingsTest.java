package org.tfoms.snils.model.ui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SettingsTest {
    String propertiesPath = "props/setting.properites";
    Settings settings;

    @Test
    public void testInit() {
        settings = new Settings();
        assertNotNull(settings);
        assertNotNull(settings.getErrorFolder());
        assertNotNull(settings.getRequestFolder());
        assertNotNull(settings.getResponseFolder());
        assertNotNull(settings.getTimeWait());
        assertNotNull(settings.getVersion());
    }

    @Test
    public void testChangeSettings() {
        settings = new Settings();
        assertNotNull(settings);
        boolean old = settings.isSaveResponse();
        settings.setSaveResponse(!old);
        settings.save();


        settings = new Settings();
        assertNotNull(settings);
        boolean newValue = settings.isSaveResponse();
        assertNotEquals(old,newValue);
    }
}