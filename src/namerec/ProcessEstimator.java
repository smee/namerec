/*
 * Created on 10.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.math.stat.regression.SimpleRegression;

public class ProcessEstimator { 
    private SimpleRegression regression = new SimpleRegression( ); 
    private StopWatch stopWatch = new StopWatch( ); // Total number of units 
    private int units = 0; // Number of units completed 
    private int completed = 0; // Sample rate for regression 
    private int sampleRate = 1; 
    
    public ProcessEstimator( int numUnits, int sampleRate ) { 
        this.units = numUnits; 
        this.sampleRate = sampleRate; 
    } 
    public void start( ) {
        stopWatch.start( ); 
    } 
    public void stop( ) { 
        stopWatch.stop( ); 
    } 
    public void unitCompleted( ) { 
        completed++; 
        if( completed % sampleRate == 0 ) { 
            regression.addData( units - completed, stopWatch.getTime( )); 
        } 
    }
    public long projectedFinish( ) {
        return (long) regression.getIntercept( );
    } 
    public long getTimeSpent( ) { 
        return stopWatch.getTime( );
    } 
    public long projectedTimeRemaining( ) { 
        long timeRemaining = projectedFinish() - getTimeSpent( ); 
        return timeRemaining; 
    }
    public int getUnits( ) { 
        return units; 
    } 
    public int getCompleted( ) { 
        return completed; 
    } 
}