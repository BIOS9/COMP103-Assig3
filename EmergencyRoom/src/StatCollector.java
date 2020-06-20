/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */


import ecs100.UI;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects and collates statistics for an ER simulation
 */
public class StatCollector {

    int totalDischarged = 0;
    Map<String, DepartmentStat> departmentStats = new HashMap<>(); //Each department gets its own statistics object

    public StatCollector()
    {
        //Initialize departments
        departmentStats.put("ER beds", new DepartmentStat("ER beds"));
        departmentStats.put("Operating Theatre", new DepartmentStat("Operating Theatre"));
        departmentStats.put("X-ray", new DepartmentStat("X-ray"));
        departmentStats.put("MRI", new DepartmentStat("MRI"));
        departmentStats.put("Ultrasound", new DepartmentStat("Ultrasound"));
        departmentStats.put("Physiotherapy", new DepartmentStat("Physiotherapy"));
    }

    /**
     * Store an entry for when a patient has finished treatment
     * @param patient Patient who has finished treatment
     * @param department Department that the patient has finished treatment from
     */
    public void logPatientTreatment(Patient patient, Department department)
    {
        departmentStats.get(department.getName()).logTreatment(patient);
    }

    /**
     * Store an entry for when a patient has finished waiting for treatment / has started treatment
     * @param patient Patient who has finished waiting
     * @param department Department that the patient has started treatment from
     */
    public void logPatientWaited(Patient patient, Department department)
    {
        departmentStats.get(department.getName()).logWaited(patient);
    }

    /**
     * Increments the number of patients discharged from the ER
     * @param patient Patient who was discharged
     */
    public void logPatientDischarged(Patient patient)
    {
        ++totalDischarged;
    }

    /**
     * Get the combined average wait time from all departments
     */
    public double getAverageWaitTime()
    {
        double averageWaitTime = 0;

        for(DepartmentStat stat : departmentStats.values()) {
            double time = stat.getAverageWaitingTime();
            averageWaitTime += Double.isNaN(time) ? 0 : time;
        }

        averageWaitTime /= departmentStats.size();

        return averageWaitTime;
    }

    /**
     * Get the combined average treatment time from all departments
     */
    public double getAverageTreatmentTime()
    {
        double averageTreatmentTime = 0;

        for(DepartmentStat stat : departmentStats.values()) {
            double time = stat.getAverageTreatmentTime();
            //Some departments may have not had any treatments yet, so use 0 as value instead of NaN to avoid invalid number issues
            averageTreatmentTime += Double.isNaN(time) ? 0 : time;
        }

        averageTreatmentTime /= departmentStats.size();

        return averageTreatmentTime;
    }

    /**
     * Get the total wait time from all departments
     */
    public double getTotalWaitTime()
    {
        double waitTime = 0;

        for(DepartmentStat stat : departmentStats.values()) {
            double time = stat.getTotalWaitTime();
            waitTime += Double.isNaN(time) ? 0 : time;
        }

        return waitTime;
    }

    /**
     * Get the total treatment time from all departments
     */
    public double getTotalTreatmentTime()
    {
        double treatmentTime = 0;

        for(DepartmentStat stat : departmentStats.values()) {
            double time = stat.getTotalTreatmentTime();
            treatmentTime += Double.isNaN(time) ? 0 : time;
        }

        return treatmentTime;
    }

    /**
     * Get the total number of patients treated in the ER
     */
    public double getTotalPatientsTreated()
    {
        int patients = 0;

        for(DepartmentStat stat : departmentStats.values()) {
            patients += stat.getTreatedCount();
        }

        return patients;
    }

    /**
     * Print out the statistics for the ER and each department in it
     */
    public void printStats()
    {
        //ER stats
        UI.println("-----------------");
        UI.println("Stats:\n");
        UI.printf("Average treatment time: %.2f\n", getAverageTreatmentTime());
        UI.printf("Average wait time: %.2f\n", getAverageWaitTime());
        UI.println("Total treatment time: " + getTotalTreatmentTime());
        UI.println("Total wait time: " + getTotalWaitTime());
        UI.println("Total treatments: " + getTotalPatientsTreated());
        UI.println("Patients discharged: " + totalDischarged);


        //Department stats
        for(DepartmentStat stat : departmentStats.values()) {
            UI.println("\nDepartment: " + stat.departmentName);
            UI.printf("   Average treatment time: %.2f\n", stat.getAverageTreatmentTime());
            UI.printf("   Average wait time: %.2f\n", stat.getAverageWaitingTime());
            UI.println("   Total treatment time: " + stat.getTotalTreatmentTime());
            UI.println("   Total wait time: " + stat.getTotalWaitTime());
            UI.println("   Total patients treated: " + stat.getTreatedCount());
        }

        UI.println("-----------------");
    }
}
