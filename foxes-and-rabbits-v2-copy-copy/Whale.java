import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a whale.
 * whales age, move, eat shrimps, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Whale extends Animal
{
    // Characteristics shared by all whales (class variables).
    
    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a whale can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single shrimp. In effect, this is the
    // number of steps a whale can go before it has to eat again.
    private static final int SHRIMP_FOOD_VALUE = 15;//9
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The whale's age.
    private int age;
    // The whale's food level, which is increased by eating shrimps.
    private int foodLevel;

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SHRIMP_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SHRIMP_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the whale does most of the time: it hunts for
     * shrimps. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWhales A list to return newly born whales.
     */
    public void act(List<Animal> newWhales)
    {
        incrementAge();
        incrementHunger();
        if(!getIsSleeping()){
        if(isAlive()) {
            giveBirth(newWhales);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            spreadDisease();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    }

    /**
     * Increase the age. This could result in the whale's death.
     */
    private void incrementAge()
    {
        age++;
        if(isSick()){
            age += 1;
        }
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this whale more hungry. This could result in the whale's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for shrimps adjacent to the current location.
     * Only the first live shrimp is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Shrimp) {
                Shrimp shrimp = (Shrimp) animal;
                if(shrimp.isAlive()) { 
                    shrimp.setDead();
                    foodLevel = SHRIMP_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    /**
     * Check whether or not this whale is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newwhales A list to return newly born whales.
     */
    private void giveBirth(List<Animal> newWhales)
    {
        // New whales are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Whale young = new Whale(false, field, loc);
            newWhales.add(young);
        }
    }
        
    /**
     * Checks wether or not the shrimp is in an adjacent location to another
     * shrimp of the opposite gender.
     * @return true if adjacent to another shrimp of the opposite gender,false otherwise
     */
    private boolean canMate(){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Shrimp) {
                Shrimp shrimp = (Shrimp) animal;
                if(shrimp.isAlive()&&(shrimp.getGender()!=this.getGender())) { 
                    return true;
                }
            }
            else return false;
        }
        return false;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A whale can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return ((age >= BREEDING_AGE)&&canMate());
    }
}