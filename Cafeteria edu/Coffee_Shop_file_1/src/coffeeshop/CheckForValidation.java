package coffeeshop;

import java.util.List;

public class CheckForValidation {
	public static boolean validateCustomer(List<SimulationEvent> events, int noOfCustomer){
		boolean flag = true;
		int count = 0; 
		for(SimulationEvent simulationEvent: events){
			if(simulationEvent.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop){
				count++;
			}
			if(count > noOfCustomer){
				return false;
			}
		}
		return flag;
	}
	
	
	public static boolean validateNoOfCooks(List<SimulationEvent> events, int noOfCooks){
		boolean flag = true;
		int count = 0;
		for(SimulationEvent simulationEvent: events){
			if(simulationEvent.event == SimulationEvent.EventType.CookStarting){
				count++;
			}
			if(count > noOfCooks){
				flag = false;
			}
		}
		return flag;
	}
	public static boolean validateMachineCapacity(List<SimulationEvent> events, int machineCapacity){
		boolean flag = true;
		int bCount = 0; 
		int fCount = 0;
		int cCount = 0;
		for(SimulationEvent simulationEvent: events){
			if(simulationEvent.event == SimulationEvent.EventType.MachineStarting){
				if(simulationEvent.machine.machineName.equalsIgnoreCase("Grill")){
					bCount++;
				}
				if(simulationEvent.machine.machineName.equalsIgnoreCase("Fryer")){
					fCount++;
				}
				if(simulationEvent.machine.machineName.equalsIgnoreCase("CoffeeMaker2000")){
					cCount++;
				}
			}
			if(simulationEvent.event == SimulationEvent.EventType.MachineDoneFood){
				if(simulationEvent.machine.machineName.equalsIgnoreCase("Grill")){
					bCount--;
				}
				if(simulationEvent.machine.machineName.equalsIgnoreCase("Fryer")){
					fCount--;
				}
				if(simulationEvent.machine.machineName.equalsIgnoreCase("CoffeeMaker2000")){
					cCount--;
				}
			}
			if(bCount > machineCapacity){
				flag = false;
			}
			if(fCount > machineCapacity){
				flag = false;
			}
			if(cCount > machineCapacity){
				flag = false;
			}
		}
		return flag;
	}
	
	public static boolean vaidateOrder(List<SimulationEvent> events, int noOfCustomer){
		boolean flag = true;
		int count = 0; 
		for(SimulationEvent simulatioEvent: events){
			if(simulatioEvent.event == SimulationEvent.EventType.CustomerPlacedOrder){
				count++;
			}
			if(count > noOfCustomer){
				flag = false;
			}
		}
		return flag;
	}
	public static boolean validateBeforePlacingOrder(List<SimulationEvent> events){
		boolean flag = false;
		int orderNo = 0;
		for(SimulationEvent simulationEvent: events){
			if(simulationEvent.event == SimulationEvent.EventType.CookStartedFood){
				orderNo = simulationEvent.orderNumber;
			}
			for(SimulationEvent simulationEv: events){
				if(simulationEv.event == SimulationEvent.EventType.CustomerPlacedOrder){
					if(orderNo == simulationEv.orderNumber){
						flag = true;
					}
				}
			}
		}
		return flag;
	}
	public static boolean validateTableAvailability(List<SimulationEvent> events, int noOfTable){
		boolean flag = true;
		int count = 0; 
		for(SimulationEvent simulationEvent: events){
			if(simulationEvent.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop){
				count++;
			}
			if(simulationEvent.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop){
				count--;
			}
			if(count > noOfTable){
				return false;
			}
		}
		return flag;
	}
	public static boolean validateBeforeCustomerLeavesShop(List<SimulationEvent> events){
		boolean flag = false;
		int customerNo = 0;
		for(SimulationEvent simulationEvent: events){
			if(simulationEvent.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop){
				customerNo = simulationEvent.customer.getOrderNum();
			}
			for(SimulationEvent sumilationEv: events){
				if(sumilationEv.event == SimulationEvent.EventType.CustomerReceivedOrder){
					if(customerNo == sumilationEv.customer.getOrderNum()){
						flag = true;
					}
				}
			}
		}
		return flag;
	}
}
