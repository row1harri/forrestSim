import java.util.Random;
/**
 * Write a description of class plant here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Plant
{
    //the 
    private static final int GROWTH_RATE = 1;
    
    private static final int MAX_SIZE = 10;
    // Tracks the current size of the plant
    private int size;
    // Tracks wether or not the plant has been eaten
    private boolean eaten = false;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    //Wether or not he plant is large enough to be eaten
    private boolean eatable = false;
    
   private static final Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for objects of class plant
     */
    public Plant(Boolean randomSize, Field field, Location location)
    {
        this.field = field;
        setLocation(location);
        size = 0;
        if(randomSize){size = rand.nextInt(MAX_SIZE);}
    }

    public void setAsEaten(){
        eaten = true;
    }
    
    private void setEatable(){
        if(size >= 5 && size <= 20){eatable = true;}
        else{eatable = false;}
    }
    
    public boolean isEatable(){
        return eatable;
    }
    
    public void resetSize(){
        size = 0;
    }
    
    private void reset(){
        resetSize();
        setEatable();
    }
    
    private void grow(){
        size++;
    }
    
    private void die(){
        if(size >= MAX_SIZE){
            reset();
        }
    }
    
    private void act(){
        grow();
        if(eaten){reset();}
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
}
