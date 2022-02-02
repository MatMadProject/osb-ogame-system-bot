package app.leaftask;

import app.data.DataLoader;
import app.data.shipyard.DefenceItem;
import app.data.shipyard.ShipItem;
import app.data.shipyard.Status;
import ogame.DataTechnology;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.planets.Planet;
import ogame.planets.ResourcesProduction;
import ogame.ships.Ship;
import ogame.tabs.Shipyard;
import ogame.utils.Waiter;
import ogame.utils.watch.Timer;
import ogame.watch.ProductionTime;

import java.util.List;

public class ShipLeafTask extends LeafTask {
    public ShipLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }
    private ShipItem shipItemToRemove;

    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                List<ShipItem> shipItems = DataLoader.listShipItem.getQueueList();
                if(!shipItems.isEmpty())
                    for(ShipItem shipItem : shipItems){
                        selectTask(shipItem);
                        DataLoader.listShipItem.save();
                    }
                if(shipItemToRemove != null){
                    DataLoader.listShipItem.getQueueList().remove(shipItemToRemove);
                    shipItemToRemove = null;
                }
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void selectTask(ShipItem shipItem) {
        Status status = shipItem.getStatus();
        switch (status){
            case ADDED:
                added(shipItem);
                break;
            case DATA_DOWNLOADING:
                dataDownloading(shipItem);
                break;
            case DATA_DOWNLOADED:
                dataDownloaded(shipItem);
                break;
            case STARTING:
                starting(shipItem);
                break;
            case CHECK:
                check(shipItem);
                break;
            case FINISHED:
                finished(shipItem);
                break;
            case BUILDING:
            case OFF:
            case WAIT:
            case WAIT_FOR_STATUS:
            case NEXT:
            case NOT_ENOUGH_RESOURCES:
                wait(shipItem);
        }
    }

    private void wait(ShipItem shipItem) {
        long timeToFinish = Timer.timeSeconds(shipItem.getTimer().getFinishDate(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            if(shipItem.getStatus() == Status.BUILDING)
                shipItem.setStatus(Status.FINISHED);
            else if(shipItem.getStatus() == Status.WAIT_FOR_STATUS)
                shipItem.setStatus(Status.DATA_DOWNLOADED);
            else
                shipItem.setStatus(Status.DATA_DOWNLOADING);

            shipItem.setStatusTime(System.currentTimeMillis());
        }
    }

    private void finished(ShipItem shipItem) {
        DataLoader.listShipItem.addToHistory(shipItem.copy());
        if(shipItem.isSingleExecute())
            shipItemToRemove = shipItem;
        else{
            shipItem.setStatus(Status.NEXT);
            long currentTimeMilliseconds = System.currentTimeMillis();
            shipItem.setStatusTime(currentTimeMilliseconds);
            shipItem.setTimer(new Timer(0,currentTimeMilliseconds/1000 + shipItem.getTimePeriod()));
        }
    }

    private void check(ShipItem shipItem) {
        Planet planet = shipItem.getPlanet();
        DataTechnology dataTechnology = shipItem.getShip().getDataTechnology();
        if(!clickPlanet(planet))
            return;
        if(!clickShipyard())
            return;
        ogame.Status status = Shipyard.statusOfShip(OgameWeb.webDriver,dataTechnology);
        if(status == ogame.Status.ACTIVE){
            long endDateOfUpgrade = Shipyard.endDateOfUpgradeBuilding(OgameWeb.webDriver,dataTechnology);
            shipItem.setStatus(Status.BUILDING);
            shipItem.setStatusTime(System.currentTimeMillis());
            shipItem.setTimer(new Timer(0,endDateOfUpgrade));
        }else{
            shipItem.setStatus(Status.DATA_DOWNLOADING);
            shipItem.setStatusTime(System.currentTimeMillis());
        }
    }

    private void starting(ShipItem shipItem) {
        Planet planet = shipItem.getPlanet();
        DataTechnology dataTechnology = shipItem.getShip().getDataTechnology();
        //Clicking on planet
        if(!clickPlanet(planet))
            return;
        if(!clickShipyard())
            return;
        if(!clickOnShipItem(dataTechnology))
            return;
        Shipyard.inputShipAmount(OgameWeb.webDriver, shipItem.getValue());
        Waiter.sleep(200,200);
        Shipyard.clickBuiltShip(OgameWeb.webDriver);
        shipItem.setStatus(Status.CHECK);
        shipItem.setStatusTime(System.currentTimeMillis());
    }

    private void dataDownloaded(ShipItem shipItem) {
        Ship ship = shipItem.getShip();
        ResourcesProduction resourcesProduction = shipItem.getPlanet().getResourcesProduction();
        Planet planet = shipItem.getPlanet();
        if(ship.getStatus() == ogame.Status.OFF){
            shipItem.setStatus(Status.OFF);
            long currentTimeMillis = System.currentTimeMillis();
            shipItem.setStatusTime(currentTimeMillis);
            shipItem.setTimer(new Timer(0,currentTimeMillis/1000 + shipItem.getTimePeriod()));
            return;
        }
        if(DataLoader.listShipItem.isShipBuildingOnPlanet(planet)){
            ShipItem firstOnQueue = DataLoader.listShipItem.getShipBuildingOnPlanet(planet);
            long currentTimeMillis = System.currentTimeMillis();
            long WAIT_SECONDS_FOR_CHANGE_STATUS = 10L;
            if(firstOnQueue == null) {
                shipItem.setTimer(new Timer(0,currentTimeMillis/1000 + WAIT_SECONDS_FOR_CHANGE_STATUS));
                shipItem.setStatus(Status.WAIT_FOR_STATUS);
            }
            else{
                shipItem.setTimer(new Timer(0,firstOnQueue.getTimer().getFinishDate()));
                shipItem.setStatus(Status.WAIT);
            }

            shipItem.setStatusTime(System.currentTimeMillis());
            return;
        }
        if(DataLoader.listDefenceItem.isDefenceBuildingOnPlanet(planet)){
            DefenceItem firstOnQueue = DataLoader.listDefenceItem.getDefenceBuildingOnPlanet(planet);
            long currentTimeMillis = System.currentTimeMillis();
            long WAIT_SECONDS_FOR_CHANGE_STATUS = 10L;
            if(firstOnQueue == null) {
                shipItem.setTimer(new Timer(0,currentTimeMillis/1000 + WAIT_SECONDS_FOR_CHANGE_STATUS));
                shipItem.setStatus(Status.WAIT_FOR_STATUS);
            }
            else{
                shipItem.setTimer(new Timer(0,firstOnQueue.getTimer().getFinishDate()));
                shipItem.setStatus(Status.WAIT);
            }

            shipItem.setStatusTime(System.currentTimeMillis());
            return;
        }
        if(ship.getStatus() == ogame.Status.DISABLED){
            if(DataLoader.listItemAutoBuilder.isNaniteFactoryUpradingOnPlanet(planet)){
                long naniteFactoryFinishTimeSeconds = DataLoader.listItemAutoBuilder.naniteFactoryUpgradingOnPlanet(planet).getEndTimeInSeconds();
                shipItem.setTimer(new Timer(0,naniteFactoryFinishTimeSeconds));
            } else if(DataLoader.listItemAutoBuilder.isShipyardUpradingOnPlanet(planet)){
                long shipyardFinishTimeSeconds = DataLoader.listItemAutoBuilder.shipyardUpgradingOnPlanet(planet).getEndTimeInSeconds();
                shipItem.setTimer(new Timer(0,shipyardFinishTimeSeconds));
            }else
                shipItem.setTimer(new Timer(0,System.currentTimeMillis()/1000 + shipItem.getTimePeriod()));

            shipItem.setStatus(Status.WAIT);
            shipItem.setStatusTime(System.currentTimeMillis());
            return;
        }

        long metal = ResourcesBar.metal(OgameWeb.webDriver);
        long crystal = ResourcesBar.crystal(OgameWeb.webDriver);
        long deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);

        long requiredMetal = ship.getRequiredResources().getMetal() * shipItem.getValue();
        long requiredCrystal = ship.getRequiredResources().getCrystal() * shipItem.getValue();
        long requiredDeuterium = ship.getRequiredResources().getDeuterium() * shipItem.getValue();

        long metalBalance = requiredMetal - metal;
        long crystalBalance = requiredCrystal - crystal;
        long deuteriumBalance = requiredDeuterium - deuterium;

        long timeToProductionResources = 0;

        if(requiredMetal > metal){
            long tmp = resourcesProduction.timeMilisecondsToMetalProduction(metalBalance);
            if(tmp > timeToProductionResources)
                timeToProductionResources = tmp;
        }
        if(requiredCrystal > crystal){
            long tmp = resourcesProduction.timeMilisecondsToCrystalProduction(crystalBalance);
            if(tmp > timeToProductionResources)
                timeToProductionResources = tmp;
        }
        if(requiredDeuterium > deuterium){
            long tmp = resourcesProduction.timeMilisecondsToDeuteriumProduction(deuteriumBalance);
            if(tmp > timeToProductionResources)
                timeToProductionResources = tmp;
        }

        if(timeToProductionResources > 0){
            shipItem.setStatus(Status.NOT_ENOUGH_RESOURCES);
            long currentTimeMillis = System.currentTimeMillis();
            shipItem.setStatusTime(currentTimeMillis);
            shipItem.setTimer(new Timer(0,currentTimeMillis/1000 + timeToProductionResources/1000));
            return;
        }

        shipItem.setStatus(Status.STARTING);
        shipItem.setStatusTime(System.currentTimeMillis());
        shipItem.setTimer(null);
    }

    private void dataDownloading(ShipItem shipItem) {
        Planet planet = shipItem.getPlanet();
        Ship ship = shipItem.getShip();
        DataTechnology dataTechnology = shipItem.getShip().getDataTechnology();

        if(!clickPlanet(planet))
            return;
        if(!clickShipyard())
            return;
        if(!clickOnShipItem(dataTechnology))
            return;

        ogame.Status status = Shipyard.statusOfShip(OgameWeb.webDriver,dataTechnology);
        ProductionTime productionTime = Shipyard.productionTimeOfShip(OgameWeb.webDriver);
        ship.setStatus(status);
        ship.setProductionTime(productionTime);

        shipItem.setStatus(Status.DATA_DOWNLOADED);
        shipItem.setStatusTime(System.currentTimeMillis());
    }

    private void added(ShipItem shipItem) {
        shipItem.setStatus(Status.DATA_DOWNLOADING);
        shipItem.setStatusTime(System.currentTimeMillis());
    }
}
