package app.data.autobuilder;

import java.io.Serializable;

public enum Status implements Serializable {

    ADDED,
    DATA_DOWNLOADING,
    DATA_DOWNLOADED,
    NOT_ENOUGH_RESOURCES,
    NOT_ENOUGH_ENERGY,
    DISABLED,
    WAIT,
    STARTING,
    UPGRADING,
    FINISHED,
    OFF;

    private static final long serialVersionUID = 1992L;
}
