package de.uni_leipzig.asv.toolbox.util;
/**
 * Queue class represents a queue of objects in which elements are removed in the same 
 * order they were entered.This is often referred to as first-in-first-out (FIFO).
 *
 * @author Yasser EL-Manzalawy <ymelamanz@yahoo.com>
 *
 *  This software is provided as is, without representation as to its
 *  fitness for any purpose, and without warranty of any kind, either
 *  express or implied, including without limitation the implied
 *  warranties of merchantability and fitness for a particular purpose.
 *  The author shall not be liable for any damages, including special, 
 *  indirect,incidental, or consequential damages, with respect to any claim
 *  arising out of or in connection with the use of the software, even
 *  if they have been or are hereafter advised of the possibility of
 *  such damages.
 */
 
import java.util.LinkedList;

import de.uni_leipzig.asv.toolbox.gui.RecognizerPanel;



public class BlockingQueue 
{
    public class EmptyQueueException extends RuntimeException 
    {
        public EmptyQueueException()  { } 
    }

	private LinkedList items;
    private int maxsize;

	/**
	 * Creats an empty queue
	 */
	 
	public BlockingQueue (int maxsize)
	{
		items = new LinkedList();
        this.maxsize=maxsize;
	}
	
	/**
	 * Inserts a new element at the rear of the queue.
	 * @param element element to be inserted.
	 */
	
	public synchronized Object enqueue (Object element)
	{
        while(items.size()>= maxsize)
            try {
                wait();
            } catch (InterruptedException e) {
              }
		items.add (element);
        notifyAll();
		return element;
	}
	
	/**
	 * Removes the element at the top of the queue.
	 * @return the removed element.
	 * @throws EmptyQueueException if the queue is empty.
	 */

	public synchronized Object dequeue () 
	{
		while (items.isEmpty())
            try {
                wait();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        Object obj=items.removeFirst();
        notifyAll();
		return obj;
	}

	/**
	 * Inspects the element at the top of the queue without removing it.
	 * @return the element at the top of the queue.
	 * @throws EmptyQueueException if the queue is empty.
	 */
	
	public synchronized Object front () 
	{
        while (items.isEmpty())
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		return items.getFirst();	
	}
	
	/**
	 * @return the number of elements at the queue.
	 */

	public synchronized int size()
	{
		return items.size();
	}

	/**
	 * @return true of the queue is empty.
	 */	

	public synchronized boolean empty()
	{
		return (size()==0);
	}

	/**
	 * Removes all elements at the queue.
	 */	

	public synchronized void clear ()
	{
		items.clear();
        notifyAll();
	}

    private int oldsize=-1, oldmod=-1;
    
    public synchronized void waitTillEmpty(int samples, String prefix) {
        ProcessEstimator est=null;
        while( !empty() ) {
            int aktsize=size();
            if(est==null) {
                est=new ProcessEstimator(aktsize,samples);
                est.start();
                oldsize=aktsize;
                oldmod=aktsize/samples;
            }
            est.unitsCompleted(oldsize-aktsize);
            oldsize=aktsize;
            if(oldmod > aktsize/samples) {
                RecognizerPanel.getInstance().setStatus(prefix+ProcessEstimator.getTimeString(est.projectedTimeRemaining()/1000));
                oldmod=aktsize/samples;
            }
            try {
                wait(3000);
            } catch (InterruptedException e) {
            }
        }
        notifyAll();
    }



}