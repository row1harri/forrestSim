import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 * wolves age, move, eat tunas, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Shark extends Animal
{
    // Characteristics shared by all wolves (class variables).
    
    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a shark can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.03;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single tuna. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static  int TUNA_FOOD_VALUE = 30;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating tunas.
    private int foodLevel;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(TUNA_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = TUNA_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the shark does most of the time: it hunts for
     * tunas. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSharks A list to return newly born tunas.
     */
    public void act(List<Animal> newSharks)
    {
        incrementAge();
        if(!getIsSleeping()){
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSharks);            
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
     * Increase the age. This could result in the shark's death.
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
     * Make this shark more hungry. This could result in the shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for tunas adjacent to the current location.
     * Only the first live tuna is eaten.
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
            if(animal instanceof Tuna) {
                Tuna tuna = (Tuna) animal;
                if(tuna.isAlive()) { 
                    tuna.setDead();
                    foodLevel = TUNA_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newwolves A list to return newly born wolves.
     */
    private void giveBirth(List<Animal> newwolves)
    {
        // New wolves are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newwolves.add(young);
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
     * A shark can breed if it has reached the breeding age.
     * @returns wether or not it has reached breeding age
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    public void changeDigestionSpeed(int increment){
        if(digestion == 0){
            TUNA_FOOD_VALUE += 60;//slow
        }
        else if(digestion == 1){
            TUNA_FOOD_VALUE = 30;//fast
        }
    }
}