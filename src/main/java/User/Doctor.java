package User;

// importing all the mandatory Classes
import Appointment.Appointment;
import Appointment.AppointmentManager;
import D_P_Interaction.Feedback;
import Exceptions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.ArrayList;


public class Doctor extends User{
//    private static final String PMDC_PATTERN = "^[a-zA-Z]{2,4}-[0-9]{4,9}"; // According to what Google said to me, I am not accountable if is not the official
    // declaring all the attributes as private
//    private final String doctorID = User.randomIdGenerator();
    private String PMDC_NO;
    private String availabilityHours;
    private double salary;
    private String qualification;
    private String speciality;
    private int yearsOfExperience;
    private ArrayList<Patient> patients = new ArrayList<>();
    private double consultationFee;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentManager appointmentManager;

    public Doctor() {

    }
    // defining all the constructors
    public Doctor(String userID, String name, LocalDate dob, String gender, String address, String phone, String email, String password, String PMDC_NO, String availabilityHours, double salary, String qualification, String speciality, int experience, double fee, LocalTime startTime, LocalTime endTime)
            throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException {
        super(userID,name, dob, gender, address, phone, email, password );
        setPMDC_NO(PMDC_NO);
        setAvailabilityHours(availabilityHours);
        setSalary(salary);
        setQualification(qualification);
        setSpeciality(speciality);
        setExperience(experience);

        this.appointmentManager = new AppointmentManager();
    }

//    // a very demure copy constructor
//    public Doctor(Doctor doctor) throws InvalidNameException, InvalidDateOfBirthException, InvalidGenderException, InvalidEmailException, InvalidPasswordException{
//        super(doctor.getName(), doctor.getDateOfBirth(), doctor.getGender(), doctor.getAddress(), doctor.getPhone(), doctor.getEmail(), doctor.getPassword());
//        this.appointmentManager = doctor.appointmentManager;
//        setPMDC_NO(doctor.getPMDC_NO());
//        setAvailabilityHours(doctor.getAvailabilityHours());
//        setSalary(doctor.getSalary());
//        setQualification(doctor.getQualification());
//        setSpeciality(doctor.getSpeciality());
//        setExperience(doctor.getExperience());
//        this.patients = new ArrayList<Patient>(doctor.patients); // creating the new arraylist so that the copy object doesnt affect the original object
//    }

    // defining the setters with validations
    public void setPMDC_NO(String PMDC_NO) {
        this.PMDC_NO = PMDC_NO;
    }

    public void setAvailabilityHours(String availabilityHours) {
        this.availabilityHours = availabilityHours;
    }

    public void setSalary(double salary) {
        if(salary < 0.0) {
            System.out.println("Invalid salary");
            this.salary = 0.0;
            return;
        }
        this.salary = salary;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setExperience(int experience) {
        if(experience < 0) {
            System.out.println("Invalid experience");
            this.yearsOfExperience = 0;
            return;
        }
        this.yearsOfExperience = experience;
    }

    public void setConsultationFee(double fee) {
        this.consultationFee = fee;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    // defining all the getters
//    public String getDoctorID() {
//        return doctorID;
//    }

    public String getPMDC_NO() {
        return PMDC_NO;
    }

    public String getAvailabilityHours() {
        return availabilityHours;
    }

    public double getSalary() {
        return salary;
    }

    public String getQualification() {
        return qualification;
    }

    public String getSpeciality() {
        return speciality;
    }

    public int getExperience() {
        return yearsOfExperience;
    }

    public double getConsulationFee() {
        return consultationFee;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // validation methods
//    public static boolean isValidPMDC(String PMDC_NO) {
//        return Pattern.matches(PMDC_PATTERN, PMDC_NO);
//    }

    // defining class methods
    public void diagnosePatient(Patient patient, String diagnose) {
        if(!patients.contains(patient)) { // updating the doctor's independent patient list to keep the record
            patients.add(patient);
        }
        patient.addDiagnosis(diagnose);
    }

    public double calculateConsultation() {
        if (yearsOfExperience < 5) {
            return 50;
        } else if (yearsOfExperience <= 10) {
            return 100;
        } else {
            return 200;
        }
    }

    public void addFeedback(Patient patient, Feedback feedback) {
        if(!patients.contains(patient)) {
            patients.add(patient);
        }
        patient.addFeedback(feedback);
    }

    public String getPatientDetails(Patient patient) { // displaying only necessary patient details to doctor
        return String.format("Name: %s\nAge: %s\nGender: %s\nDiagnosis: %s\n", patient.getName(), patient.getAge(), patient.getGender(), patient.getDiagnosis());
    }

    @Override
    public boolean equals(Object obj) {
        // Check if the object is the same instance
        if (this == obj) return true;

        // Check if the object is null or not of the same class
        if (obj == null || getClass() != obj.getClass()) return false;

        // Cast to Doctor and compare userID
        Doctor other = (Doctor) obj;
        return this.getUserID().equals(other.getUserID());
    }

    // consistent appointment scheduler and cancel methods
    @Override
    public void scheduleAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime) {
        try {
            // Parameter checks
            if (patient == null) {
                throw new InvalidAppointmentException("Patient cannot be null.");
            }
            if (dateTime == null) {
                throw new InvalidAppointmentException("Appointment date and time cannot be null.");
            }
            if (dateTime.isBefore(LocalDateTime.now())) {
                throw new InvalidAppointmentException("Appointment date and time cannot be in the past.");
            }

            if(appointmentManager.isDuplicateAppointment(dateTime, doctor, patient)) {
                throw new DuplicateAppointmentException("Duplicate appointment exists.");
            }

            // Request a new appointment
            appointmentManager.requestAppointment(dateTime, this, patient);
        } catch (InvalidAppointmentException | DuplicateAppointmentException e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public void cancelAppointment(Appointment appointment) {
        try {
            // Parameter check
            if (appointment == null) {
                throw new InvalidAppointmentException("Appointment cannot be null.");
            }

            if(!appointmentManager.contains(appointment)) {
                throw new AppointmentNotFoundException("Appointment not found in the system.");
            }

            // Cancel the specified appointment
            appointmentManager.cancelAppointment(appointment);
        } catch (InvalidAppointmentException | AppointmentNotFoundException e) {
            System.out.println("Error canceling appointment: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void rescheduleAppointment(Appointment appointment, LocalDateTime newDateTime)
            throws InvalidAppointmentException, AppointmentNotFoundException, DuplicateAppointmentException {
        if (appointment == null || newDateTime == null) {
            throw new InvalidAppointmentException("Cannot reschedule. Appointment or new date & time cannot be null.");
        }

        if (!appointmentManager.contains(appointment)) {
            throw new AppointmentNotFoundException("Cannot reschedule. Appointment not found in the system.");
        }

        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Cannot reschedule. The new appointment date & time cannot be in the past.");
        }

        if (appointmentManager.isDuplicateAppointment(newDateTime, appointment.getDoctor(), appointment.getPatient())) {
            throw new DuplicateAppointmentException("Cannot reschedule. A similar appointment already exists at the chosen time.");
        }

        appointment.updateDateTime(newDateTime);
        System.out.println("Appointment rescheduled successfully to: " + newDateTime);
    }


    @Override
    public String toString() {
        return String.format("\nDetails:\nName: %s\tAge: %ss\tGender: %s\tAddress: %s\tPhone: %s\tEmail: %s\tPassword: %s\nProfessional Details:\nPMDC No: %s\tAvailability Hours: %s\tSalary: %.3f\tQualification: %s\tSpeciality: %s\tExperience: %d years\t",getName(), getAge(), getGender(), getAddress(), getPhone(), getEmail(), getPassword(), PMDC_NO, availabilityHours, salary, qualification, speciality, yearsOfExperience);
    }

    public static Doctor createMockDoctor(String name) {
        try {
            return new Doctor(User.randomIdGenerator(), name,
                    LocalDate.of(1980, 1, 1),
                    "male",
                    "123 Street",
                    "1234567890",
                    name.toLowerCase().replaceAll("\\s+", "") + "@example.com",
                    "Password@123",
                    "AB-12345",
                    "9 AM - 5 PM",
                    100000.0,
                    "MBBS",
                    "General Medicine",
                    10,
                    1300,
                    LocalTime.of(9, 0, 0),
                    LocalTime.of(17, 0, 0)
            );
        } catch (Exception e) {
            System.out.println("Failed to create doctor for: " + name);
            e.printStackTrace();
            return null;
        }
    }


}

