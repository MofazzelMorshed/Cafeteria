package cofeeshop;

/**
 * A Machine is used to make a particular Food. Each Machine makes just one kind
 * of Food. Each machine has a size: it can make that many food items in
 * parallel; if the machine is asked to produce a food item beyond its size, the
 * requester blocks. Each food item takes at least item.cookTimeMS milliseconds
 * to produce.
 */
public class Machine {

    public final String machineName;
    public final Food machineFoodType;

    public NonBlockingConcurrentLinkedQueue<Food> machineFood;
    public int size;
    
    
    public Machine(String nameIn, Food foodIn, int capacityIn) {
        this.machineName = nameIn;
        this.machineFoodType = foodIn;
        this.size = capacityIn;
        this.machineFood = new NonBlockingConcurrentLinkedQueue<>();
    }

    /**
     * This method is called by a Cook in order to make the Machine's food item.
     * It should block if the machine is currently at full size. If not, the
     * method should return, so the Cook making the call can proceed.Notify the
     * calling Cook when the food item is finished.
     */
    public void makeFood(Cook cook, int orderNo) throws InterruptedException {
        machineFood.add(machineFoodType);
        CookAnItem cookItem = new CookAnItem(cook, orderNo);
        Thread foodCookThread = new Thread(cookItem);
        foodCookThread.start();
    }

    // THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
    private class CookAnItem implements Runnable {

        private Cook cook;
        private int orderNo;

        public CookAnItem(Cook cook, int orderNo) {
            this.cook = cook;
            this.orderNo = orderNo;
        }

        public void run() {
            try {

                Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
                Thread.sleep(machineFoodType.cookTimeMS);
                Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
                Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, this.orderNo));
                {
                    machineFood.remove();
                }
                synchronized (cook.foodCompletedList) {
                    cook.foodCompletedList.add(machineFoodType);
                    cook.foodCompletedList.notifyAll();
                }
            } catch (InterruptedException e) {
            }
        }
    }

    public String toString() {
        return machineName;
    }
}
