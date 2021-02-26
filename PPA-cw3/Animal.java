import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // The simulator the animal is in
    protected Simulator simulator;
    // Variable that holds the gender value of an animal 0-male 1-female
    private int gender;
    // Tracks if an animal is sleeping or not
    private boolean isSleeping;
    // Tracks if the animal is sick or not
    private boolean hasDisease;
    // The probability of a sick animal spreading the disease
    private double diseaseSpreadProbability = 0.8;
    // random number generator used for this class
    private Random rand;
    
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        setGender();
        setDisease();
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    /**
     * method used to set the gender of an animal upon its creation
     */
    protected void setGender()
    {
        Random rand = Randomizer.getRandom();     
        gender = rand.nextInt(2);
    }
    /**
     * return thje gender of the animal
     * @return 0 if the animal is male and 1 if the animal is female
     */
    protected int getGender(){
        return gender;
    }
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }        
    /**
     * changes wether or not the animal is sleeping
     * based on the status parameter
     * @param status wether or not the animal is to be made to sleep
     */
    protected void sleep(boolean status){
        isSleeping = status;
    }
    /**
     * return the animal's isSleeping attribute
     * @return the animal's isSleeping attribute
     */
    protected boolean getIsSleeping(){
        return isSleeping;
    }
    /**
     * gives the animal the disease
     */
    protected void giveDisease(){
         hasDisease = true;
    }
    /**
     * return the animal's hasDisease attribute
     * @return the animal's hasDisease attribute
     */
    protected boolean isSick(){
        return hasDisease;
    }
    /**
     * method used to make some animals already have
     * the disease upon creation
     */
    protected void setDisease(){
        Random rand = Randomizer.getRandom();
        if(rand.nextDouble() <= 0.2 ){
            giveDisease();
        }
    }
    /**
     * checks to see if there are animals in adjacent locations
     * if so then there is a chance that each of those animals will
     * bee given the disease
     */
    protected void spreadDisease(){       
        if(isSick()){
            Random rand = Randomizer.getRandom();
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if(animal instanceof Animal) {
                    Animal animal_ = (Animal) animal;
                    if(animal_.isAlive()) {
                        if(rand.nextDouble() <= diseaseSpreadProbability){
                            animal_.giveDisease();
                        }
                    }
                }
            }
        }
    }
    
    
}
