/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicoreproject.version5;

/**
 *
 * @author Giovanni
 */
public class GridItem {
    private int population;
    private boolean lock;
    
    public GridItem()
    {
        this.population = 0;
        this.lock = false;
    }
    
    public void updatePopulation(int populationSize)
    {
        this.lock = true;
        population += populationSize;
        this.lock = false;
    }
    
    public boolean locked()
    {
        return this.lock;
    }
    public int getValue()
    {
        return this.population;
    }
            
}
