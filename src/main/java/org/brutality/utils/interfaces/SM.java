package org.brutality.utils.interfaces;

import org.brutality.BrutalityClient;
import org.brutality.settings.SettingsManager;

public interface SM {
    SettingsManager sm = BrutalityClient.INSTANCE.settingsManager;
}
