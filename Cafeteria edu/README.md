# Coffee-Shop-Simulation-using-Multithreaded-Programming

Coffee shop simulation restaurant using parallel and multithread Java program.

Part-1

The main focus of the project is to use intrinsic lock, synchronization when two or more threads try to access the same resource simultaneously.
You should not use synchronized collections or concurrent collections

-Customers in the coffee shop place their orders (a list of food items) when they enter. 

-Available cooks then handle these orders. Each cook handles one order at a time. 

-A cook handles an order by using machines to cook the food items. There will be one machine for each kind of food item. 

-Each machine produces food items in parallel (for different orders, or even the same order) up to their stated capacity.

-Coffee shop will only have three food items, each made by a single machine: a burger, made by machine Grill, which takes 600 ms to make; fries, made by machine Fryer, which take 450ms to make; and a coffee, made by machine CoffeeMaker2000, which takes 150 ms to make.
 
 
 Part-2

The major difference in part 2 is to now use java.util.concurrent or the synchronized collections in the Collections class in
java.util.collections. Also used Non-Blocking Algorithm that makes use of CAS type Java operation.
