package coffeeshop;

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Simulation is the main class used to run the simulation. You may add any
 * fields (static or instance) or any methods you wish.
 */
public class Simulation {
    // List to track simulation events during simulation

    public static List<SimulationEvent> events;
    public static Queue<Customer> listForOrders = new LinkedList<>();
    public static Machine grill;
    public static Machine fryer;
    public static Queue<Customer> capacity_customer_list = new LinkedList<>();
    public static ArrayList<Cook> listForCooks = new ArrayList<>();
    public static Machine coffeeMaker2000;
    public static HashMap<Customer, Boolean> Order = new HashMap<>();

    /**
     * Used by other classes in the simulation to log events
     *
     * @param event
     */
    public static void logEvent(SimulationEvent event) {
        events.add(event);
        System.out.println(event);
    }

    /**
     * Function responsible for performing the simulation. Returns a List of
     * SimulationEvent objects, constructed any way you see fit. This List will
     * be validated by a call to Validate.validateSimulation. This method is
     * called from Simulation.main().
     *
     * Parameters:
     *
     * @param numCustomers the number of customers wanting to enter the coffee
     * shop
     * @param numCooks the number of cooks in the simulation
     * @param numTables the number of tables in the coffe shop (i.e. coffee shop
     * size)
     * @param machineCapacity the size of all machines in the coffee shop
     * @param randomOrders a flag say whether or not to give each customer a
     * random order
     *
     */
    public static List<SimulationEvent> runSimulation(
            int numCustomers, int numCooks,
            int numTables,
            int machineCapacity,
            boolean randomOrders
    ) {

        events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

        // Start the simulation
        logEvent(SimulationEvent.startSimulation(numCustomers,
                numCooks,
                numTables,
                machineCapacity));

        // Set things up you might need
        // Start up machines
        grill = new Machine("Grill", FoodType.burger, machineCapacity);
        fryer = new Machine("Fryer", FoodType.fries, machineCapacity);
        coffeeMaker2000 = new Machine("CoffeeMaker2000", FoodType.coffee, machineCapacity);

        //log machine starting event
        logEvent(SimulationEvent.machineStarting(grill, FoodType.burger, machineCapacity));
        logEvent(SimulationEvent.machineStarting(fryer, FoodType.fries, machineCapacity));
        logEvent(SimulationEvent.machineStarting(coffeeMaker2000, FoodType.coffee, machineCapacity));

        // Let cooks in
        Thread cooksCooking[] = new Thread[numCooks];
        for (int i = 0; i < cooksCooking.length; i++) {
            Cook cook = new Cook("Cook" + (i + 1));
            listForCooks.add(cook);
            cooksCooking[i] = new Thread(cook);
            cooksCooking[i].start();
        }

        // Build the customers.
        Thread[] customers = new Thread[numCustomers];
        LinkedList<Food> o;
        Random randomNo = new Random();
        if (!randomOrders) {
            o = new LinkedList<Food>();
            o.add(FoodType.burger);
            o.add(FoodType.fries);
            o.add(FoodType.fries);
            o.add(FoodType.coffee);
            for (int i = 0; i < customers.length; i++) {
                int noOfHours = randomNo.nextInt(2) + 2;
                customers[i] = new Thread(
                        new Customer("Customer " + (i + 1), o, noOfHours)
                );
            }
        } else {
            for (int i = 0; i < customers.length; i++) {
                Random rnd = new Random(10);

                int bCount = rnd.nextInt(3);
                int fCount = rnd.nextInt(3);
                int cCount = rnd.nextInt(3);
                o = new LinkedList<Food>();

                for (int j = 0; j < bCount; j++) {
                    o.add(FoodType.burger);
                }
                for (int k = 0; k < fCount; k++) {
                    o.add(FoodType.fries);
                }
                for (int l = 0; l < cCount; l++) {
                    o.add(FoodType.coffee);
                }
                int priority = randomNo.nextInt(3) + 1;
                customers[i] = new Thread(
                        new Customer("Customer " + (i + 1), o, priority)
                );
            }
        }

        // Now "let the customers know the shop is open" by
        //    starting them running in their own thread.
        for (int i = 0; i < customers.length; i++) {
            customers[i].start();
            /*NOTE: Starting the customer does NOT mean they get to go right into the shop. 
                        There has to be a table for them.  The Customer class' run method has many jobs 
                        to do - one of these is waiting for an available table...*/
        }

        try {
            // Wait for customers to finish
            for (int i = 0; i < customers.length; i++) {
                customers[i].join();
            }

            /*Then send cooks home...The easiest way to do this might be the following, where
			 we interrupt their threads.  There are other approaches though, so you can change this if you want to.*/
            for (int i = 0; i < cooksCooking.length; i++) {
                cooksCooking[i].interrupt();
            }
            for (int i = 0; i < cooksCooking.length; i++) {
                cooksCooking[i].join();
            }

        } catch (InterruptedException e) {
            System.out.println("Simulation thread interrupted.");
        }

        // Shut down machines
        logEvent(SimulationEvent.machineEnding(grill));
        logEvent(SimulationEvent.machineEnding(fryer));
        logEvent(SimulationEvent.machineEnding(coffeeMaker2000));

        // Done with simulation		
        logEvent(SimulationEvent.endSimulation());

        return events;
    }

    /**
     * Entry point for the simulation.
     *
     * @param args the command-line arguments for the simulation. There should
     * be exactly four arguments: the first is the number of customers, the
     * second is the number of cooks, the third is the number of tables in the
     * coffee shop, and the fourth is the number of items each cooking machine
     * can make at the same time.
     */
    public static void main(String args[]) throws InterruptedException {
        /*int numCustomers = 2;
		int numCooks =1;
		int numTables = 5;
		int machineCapacity = 4;
         */
        int numCustomers = 10;
        int numCooks = 1;
        int numTables = 5;
        int machineCapacity = 4;

        boolean randomOrders = false;

        // Run the simulation and then 
        //   feed the result into the method to validate simulation.
        System.out.println("Did it work? "
                + Validate.validateSimulation(
                        runSimulation(
                                numCustomers, numCooks,
                                numTables, machineCapacity,
                                randomOrders
                        ), numCustomers, numCooks, numTables, machineCapacity
                )
        );
    }

}
