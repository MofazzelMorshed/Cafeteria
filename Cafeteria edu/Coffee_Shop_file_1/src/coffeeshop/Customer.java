package coffeeshop;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, an customer
 * attempts to enter the coffee shop (only successful if the coffee shop has a
 * free table), place its order, and then leave the coffee shop when the order
 * is complete.
 */
public class Customer implements Runnable {

    private static int custID = 0;
    private final String name;
    private final List<Food> order;
    private final int orderNum;
    private final int noOfHours;
    private final int customerNo;

    private static int runningCounter = 0;

    public Customer(String name, List<Food> order, int noOfHours) {

        this.order = order;
        this.orderNum = ++runningCounter;
        this.noOfHours = noOfHours;
        this.customerNo = ++custID;
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public List<Food> getOrder() {
        return order;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public int getCustomerNo() {
        return customerNo;
    }

    /**
     * This method defines what an Customer does: The customer attempts to enter
     * the coffee shop (only successful when the coffee shop has a free table),
     * place its order, and then leave the coffee shop when the order is
     * complete.
     */
    public void run() {

        Simulation.logEvent(SimulationEvent.customerStarting(this));
        synchronized (Simulation.capacity_customer_list) {
            while (Simulation.capacity_customer_list.size() >= Simulation.events.get(0).simParams[2]) {
                try {
                    Simulation.capacity_customer_list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Simulation.capacity_customer_list.add(this);
            Simulation.capacity_customer_list.notifyAll();
            Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
        }
        synchronized (Simulation.listForOrders) {
            Simulation.listForOrders.add(this);
            Simulation.listForOrders.notifyAll();
            Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
        }
        synchronized (Simulation.Order) {
            Simulation.Order.put(this, false);
        }
        synchronized (Simulation.Order) {
            while (!Simulation.Order.get(this)) {
                try {
                    Simulation.Order.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
            Simulation.Order.notifyAll();
        }
        synchronized (Simulation.capacity_customer_list) {
            Simulation.capacity_customer_list.remove(this);
            Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
            Simulation.capacity_customer_list.notifyAll();
        }
    }
}
