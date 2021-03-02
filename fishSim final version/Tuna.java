import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a tuna.
 * tunas age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Tuna extends Animal
{
    // Characteristics shared by all tunas (class variables).

    // The age at which a tuna can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a tuna can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a tuna breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // number of steps a rabbit can go before it has to eat again.
    private static int SEAWEED_FOOD_VALUE =300;
    // Individual characteristics (instance fields).
    
    // The tuna's age.
    private int age;
    // The rabbit's food level, which is increased by eating seaweeds.
    private int foodLevel;
    /**
     * Create a new tuna. A tuna may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the tuna will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tuna(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SEAWEED_FOOD_VALUE);
        }
        else {
            foodLevel = SEAWEED_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the tuna does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newTuna A list to return newly born tunas.
     */
    public void act(List<Animal> newTuna)
    {
        incrementAge();
        if(!getIsSleeping()){
            incrementHunger();
            if(isAlive()) {
                giveBirth(newTuna);            
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
     * Increase the age.
     * This could result in the tuna's death.
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
     * Make this tuna more hungry. This could result in the tuna's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this tuna is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newtunas A list to return newly born tunas.
     */
    private void giveBirth(List<Animal> newTunas)
    {
        // New tunas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Tuna young = new Tuna(false, field, loc);
            newTunas.add(young);
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
     * A tuna can breed if it has reached the breeding age.
     * @return true if the tuna can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
     /**
     * Look for seaweeds adjacent to the current location.
     * Only the first live seaweed is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if(object instanceof Seaweed) {
                Seaweed seaweed = (Seaweed) object;
                if(seaweed.isEatable()) { 
                    seaweed.setAsEaten();
                    foodLevel = SEAWEED_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    public void changeDigestionSpeed(int increment){
        if(digestion == 0){
            SEAWEED_FOOD_VALUE += 330;//slow
        }
        else if(digestion == 1){
            SEAWEED_FOOD_VALUE = 300;//fast
        }
    } 
}