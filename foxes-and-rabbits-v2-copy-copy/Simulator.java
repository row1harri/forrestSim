import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing shrimps and stingrays.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a stingray will be created in any given grid position.
    private static final double STINGRAY_CREATION_PROBABILITY = 0.01;
    // The probability that a shrimp will be created in any given grid position.
    private static final double SHRIMP_CREATION_PROBABILITY = 0.2;   
    // The probability that a whale will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.03;
    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.03;
    // The probability that a tuna will be created in any given grid position.
    private static final double TUNA_CREATION_PROBABILITY = 0.1;
    // The probability that a seaweed will be created in any given grid position.
    private static final double SEAWEED_CREATION_PROBABILITY = 0.1;

    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    //keeps track of wether it is day or night,0-day,1-night
    private boolean isDay = true;
    // List of seaweeds in the field.
    private List<Seaweed> seaweeds;
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        seaweeds = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Shrimp.class, Color.ORANGE);
        view.setColor(Stingray.class, Color.BLUE);
        view.setColor(Whale.class, Color.RED);
        view.setColor(Shark.class, Color.BLACK);
        view.setColor(Tuna.class, Color.YELLOW);
        view.setColor(Seaweed.class, Color.GREEN);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * Stingray and shrimp.
     */
    public void simulateOneStep()
    {
        step++;
        changeTimeOfDay();
        nightAct();
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all shrimps act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born Stingrayes and shrimps to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        seaweeds.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with Stingrayes and shrimps.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= STINGRAY_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Stingray stingray = new Stingray(true, field, location);
                    animals.add(stingray);
                }
                else if(rand.nextDouble() <= SHRIMP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shrimp shrimp = new Shrimp(true, field, location);
                    animals.add(shrimp);
                }
                else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, field, location);
                    animals.add(whale);
                }
                else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    animals.add(shark);
                }
                else if(rand.nextDouble() <= TUNA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tuna tuna = new Tuna(true, field, location);
                    animals.add(tuna);
                }
                else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(true, field, location);
                    seaweeds.add(seaweed);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
    /**
     * switches the time of day in the simulation 
     * between day and night
     */
    private void changeTimeOfDay(){
        if ((step-20) % 20==0){
            isDay = !isDay; 
        }   
    }
    
    /**
     * changes the behaviour of animals during night-time
     */
    public void nightAct(){
        if(!isDay){
            for(Animal animal: animals ){
                
            if(!(animal instanceof Stingray)){ animal.setIsSleeping(true);}
            else {animal.setIsSleeping(false);}
        }
    }
        else{
            for(Animal animal: animals ){    
                if(animal instanceof Stingray){ animal.setIsSleeping(true);}
                else {animal.setIsSleeping(false);}
            }
        }

}
}