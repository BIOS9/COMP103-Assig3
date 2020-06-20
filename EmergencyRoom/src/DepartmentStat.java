/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

/**
 * Stores and calculates statistical information for one department in the ER
 */
public class DepartmentStat {
    public final String departmentName;

    int waitedCount = 0;
    int treatedCount = 0;
    double totalWaitTime = 0;
    double totalTreatmentTime = 0;

    public DepartmentStat(String departmentName)
    {
        this.departmentName = departmentName;
    }

    /**
     * Gets the average time for a patient to be treated
     */
    public double getAverageTreatmentTime()
    {
        return totalTreatmentTime / treatedCount;
    }

    /**
     * Gets the average time that a patient has to wait to be treated
     */
    public double getAverageWaitingTime()
    {
        return totalWaitTime / waitedCount;
    }

    /**
     * Gets the total time spent by all patients in treatment
     */
    public double getTotalTreatmentTime()
    {
        return totalTreatmentTime;
    }

    /**
     * Gets the total time spent by all patients waiting for treatment
     */
    public double getTotalWaitTime()
    {
        return totalWaitTime;
    }

    /**
     * Gets the total number of patients who have finished treatment
     */
    public int getTreatedCount()
    {
        return treatedCount;
    }

    /**
     * Record an entry for a patient who has finished waiting / started treatment in this dept
     * @param patient Patient who has finished waiting
     */
    public void logWaited(Patient patient)
    {
        waitedCount++;
        totalWaitTime += patient.getLastWaitTime();
    }

    /**
     * Record an entry for a patient who has finished treatment
     * @param patient Patient who has finished treatment
     */
    public void logTreatment(Patient patient)
    {
        treatedCount++;
        totalTreatmentTime += patient.getLastTreatmentTime();
    }
}
