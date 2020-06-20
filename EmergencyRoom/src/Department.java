// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;
import java.util.*;


/**
 * A treatment Department (operating theatre, X-ray room,  ER bed, Ultrasound, etc)
 * Each department has
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *
 * FOR THE CORE AND COMPLETION, YOU CAN COMPLETE THE METHODS HERE,
 * BUT DO NOT MODIFY ANY OF THE CODE THAT IS GIVEN!!!
 */

public class Department{

    private String name;
    private int maxPatients;
    private Set<Patient> currentPatients;
    private Queue<Patient> waitingPatients;

    /**
     * Construct a new Department object
     * Initialise the waiting queue and the current Set.
     */
    public Department(String name, int max){
        this.name = name;
        maxPatients = max;
        currentPatients = new HashSet<>();
        waitingPatients = new PriorityQueue<>();
    }

    public String getName()
    {
        return name;
    }

    /**
     * Return the collection of patients currently being treated
     * Note: returns it as an unmodifiable collection - the calling code
     * can step through the Patients, can call methods on the Patients,
     * but can't add new patients to the set or remove patients from the set.
     */
    public Collection<Patient> getCurrentPatients(){
        return Collections.unmodifiableCollection(currentPatients);
    }

    /**
     * Return the collection of patients waiting in the department
     * Note: returns it as an unmodifiable collection - the calling code
     * can step through the Patients, but can't add to them or remove them.
     */
    public Collection<Patient> getWaitingPatients(){
        return Collections.unmodifiableCollection(waitingPatients);
    }

    /**
     * A new patient for the department.
     * Always starts by being put on the wait queue.
     */
    public void addPatient(Patient p){
        waitingPatients.offer(p);
    }

    /**
     * Move patients off the wait queue (if any) into the set of patients
     * being treated (if there is space - fewer than the maximum)
     *
     * Returns a patient if they were successful;y moved from the waiting queue to the treatment list
     */
    public Patient moveFromWaiting(){
        Patient p = null;
        if(currentPatients.size() < maxPatients && waitingPatients.size() != 0)
            currentPatients.add(p = waitingPatients.poll());
        return p;
    }

    /**
     * Move patient out of the department
     */
    public void removePatient(Patient p){
        currentPatients.remove(p);
    }

    /**
     * Remove all patients in specified collection from this department
     * @param p Patients to remove
     */
    public void removePatientRange(Collection<Patient> p)
    {
        currentPatients.removeAll(p);
    }


    /**
     * Draw the department: the patients being treated and the patients waiting 
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : currentPatients){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingPatients){
            p.redraw(x, y);
            x += 10;
        }
    }
}
