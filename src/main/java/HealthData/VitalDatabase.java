package HealthData;
import User.Patient;
import java.util.Map;
import java.util.HashMap;

public class VitalDatabase {
    private static Map<Patient, Vitals> vitalMap;

    // Constructor to initialize the vital map
    public VitalDatabase() {
        this.vitalMap = new HashMap<>();
    }

    // Add or update the vital signs for a patient
    public static void addOrUpdatePatientVitals(Patient patient, Vitals vitalSign) {
        vitalMap.put(patient, vitalSign);
        System.out.println("Vital signs for " + patient.getName() + " have been added/updated.");
    }

    // Retrieve the vital signs for a specific patient
    public static Vitals getPatientVitals(Patient patient) {
        return vitalMap.get(patient);
    }

    // Display the vital signs for a specific patient
    public static void displayPatientVitals(Patient patient) {
        Vitals vitalSign = vitalMap.get(patient);
        if (vitalSign != null) {
            System.out.println();
            System.out.println("Displaying vitals for " + patient.getName() + " (Patient ID: " + patient.getUserID() + ")");
            System.out.println(vitalSign.toString());
        } else {
            System.out.println("No vitals found for " + patient.getName());
        }
    }
}
