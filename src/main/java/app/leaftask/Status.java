package app.leaftask;

import java.io.Serializable;

public enum Status implements Serializable {
    ADDED,
    DATA_DOWNLOADING,
    DATA_DOWNLOADED,
    STARTING,
    BUILDING,
    FINISHED,
    WAIT,
    NOT_ENOUGH_RESOURCES,
    OFF,
    CHECK,
    NEXT,
    WAIT_FOR_STATUS,
    SENDING,
    SENT,
    EXPEDITION,
    RETURN,
    RETURN_CHANGED,
    NO_FLEET,
    NO_FUEL,
    MAX_EXPEDITION,
    MAX_FLEET_SLOT,
    DATA_ERROR,
    NOT_ENOUGH_ENERGY,
    DISABLED,
    SHIP_BUILD,
    DEFENCE_BUILD,
    LABORATORY_BUILD,
    RESEARCH_UPGRADE,
    UPGRADING;

    private static final long serialVersionUID = 1992L;
}
