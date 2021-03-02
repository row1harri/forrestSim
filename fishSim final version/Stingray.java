import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a stingray.
 * Stingrays age, move, eat shrimps, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Stingray extends Animal
{
    // Characteristics shared by all stingrays (class variables).
    
    // The age at which a stingray can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a stingray can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a stingray breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single shrimp. In effect, this is the
    // number of steps a stingray can go before it has to eat again.
    private static int SHRIMP_FOOD_VALUE =8;//9
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The stingray's age.
    private int age;
    // The stingray's food level, which is increased by eating shrimps.
    private int foodLevel;

    /**
     * Create a stingray. A stingray can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the stingray will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Stingray(boolean randomAge, Field field, Location location)
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
     * This is what the stingray does most of the time: it hunts for
     * shrimps. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newStingrays A list to return newly born stingrays.
     */
    public void act(List<Animal> newStingrays)
    {
        incrementAge();
        if(!getIsSleeping()){
            incrementHunger();
            if(isAlive()) {
                giveBirth(newStingrays);            
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
     * Increase the age. This could result in the stingray's death.
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
     * Make this stingray more hungry. This could result in the stingray's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for SHRIMPs adjacent to the current location.
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
     * Check whether or not this stingray is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newStingrays A list to return newly born stingrays.
     */
    private void giveBirth(List<Animal> newStingrays)
    {
        // New stingrays are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Stingray young = new Stingray(false, field, loc);
            newStingrays.add(young);
        }
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
     * A stingray can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    
    public void changeDigestionSpeed(int increment){
        if(digestion == 0){
            SHRIMP_FOOD_VALUE += 38;//slow
        }
        else if(digestion == 1){
            SHRIMP_FOOD_VALUE = 8;//fast
        }
    }
}