package de.skuzzle.stringz;

import java.util.ResourceBundle.Control;

public interface ControlConfigurator {

    public abstract Control configure(ResourceMapping mapping, String[] args);
}
