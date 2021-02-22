
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
    // Tracks the current size of the plant
    private int size;
    // Tracks wether or not the plant has been eaten
    private boolean eaten = false;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
   
    
    /**
     * Constructor for objects of class plant
     */
    public Plant(Boolean RandomSize, Field field, Location location)
    {
        this.field = field;
        setLocation(location);
    }

    public void setEaten(){
        eaten = true;
    }
    
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
}
