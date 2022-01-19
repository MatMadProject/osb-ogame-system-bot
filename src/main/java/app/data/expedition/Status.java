package app.data.expedition;

import java.io.Serializable;

public enum Status implements Serializable {

    SENDING,
    DATA_DOWNLOADING,
    SENT,
    EXPEDITION,
    RETURN,
    RETURN_CHANGED,
    FINISHED,
    NO_FLEET,
    NO_FUEL,
    MAX_EXPEDITION,
    MAX_FLEET_SLOT,
    DATA_ERROR;

    private static final long serialVersionUID = 1992L;
}
