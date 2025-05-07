package coffeeshop;

import java.util.LinkedList;
import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	private Customer customer;
	public List<Food> foodCompletedList = new LinkedList<>();
	
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */

	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				synchronized (Simulation.listForOrders){
					while(Simulation.listForOrders.isEmpty()){
						Simulation.listForOrders.wait();
					}
					customer = Simulation.listForOrders.remove();
					Simulation.listForOrders.notifyAll();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.getOrder(), customer.getOrderNum()));
				}
				for(int i = 0; i < customer.getOrder().size(); i++){
					Food currentFoodOrder = customer.getOrder().get(i);
					if(currentFoodOrder.equals(FoodType.burger)){
						synchronized(Simulation.grill.machineFood){
							while(Simulation.grill.machineFood.size() >= Simulation.grill.size){
								Simulation.grill.machineFood.wait();
							}
							Simulation.grill.makeFood(this, customer.getOrderNum());
							Simulation.grill.machineFood.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.burger, customer.getOrderNum()));
						}
					}
					else if(currentFoodOrder.equals(FoodType.fries)){
						synchronized(Simulation.fryer.machineFood){
							while(Simulation.fryer.machineFood.size() >= Simulation.fryer.size){
								Simulation.fryer.machineFood.wait();
							}
							Simulation.fryer.makeFood(this, customer.getOrderNum());
							Simulation.fryer.machineFood.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.fries, customer.getOrderNum()));
						}
					}
					else{
						synchronized(Simulation.coffeeMaker2000.machineFood){
							while(Simulation.coffeeMaker2000.machineFood.size() >= Simulation.coffeeMaker2000.size){
								Simulation.coffeeMaker2000.wait();
							}
							Simulation.coffeeMaker2000.makeFood(this, customer.getOrderNum());
							Simulation.coffeeMaker2000.machineFood.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.coffee, customer.getOrderNum()));
						}
					}
				}
				synchronized(foodCompletedList){
					while(customer.getOrder().size() != foodCompletedList.size()){
						foodCompletedList.wait();
						foodCompletedList.notifyAll();
					}
				}
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, customer.getOrderNum()));
				foodCompletedList = new LinkedList<>();
				synchronized (Simulation.Order){
					Simulation.Order.put(customer, true);
					Simulation.Order.notifyAll();
				}
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class 
                        //that interrupts each cook thread when all customers are done.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}