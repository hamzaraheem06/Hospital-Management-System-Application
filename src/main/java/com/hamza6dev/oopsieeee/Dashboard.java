package com.hamza6dev.oopsieeee;

import Appointment.*;
import Appointment.Appointment.AppointmentStatus;
import D_P_Interaction.Feedback;
import D_P_Interaction.Prescription;
import User.*;

import Exceptions.DuplicateAppointmentException;
import Exceptions.InvalidAppointmentException;
import HealthData.Vitals;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Dashboard extends Application {
    private String accountType;
    private String accountID;
    private User user;
    private VBox content;

    public Dashboard(String accountType, String accountID) {
        this.accountType = accountType;
        this.accountID = accountID;
        initializeUser();
        System.out.println(user);
    }

    @Override
    /**
     * Initializes and starts the primary stage for the application, rendering the dashboard UI based on the user's role.
     *
     * <p>This method configures the primary layout of the application, including:
     * <ul>
     *   <li>A navigation bar at the top with a logo and logout functionality.</li>
     *   <li>A sidebar for navigation with role-specific menu options and their corresponding event handlers.</li>
     *   <li>A main content area for rendering dynamic pages, such as user profiles, appointments, or evaluation pages.</li>
     * </ul>
     * <p>
     * The layout dynamically adapts its content and sidebar options depending on the type of user:
     * <ul>
     *   <li><b>Doctor:</b> Displays options to view patients, vitals, appointments, video requests, and schedule a video call.</li>
     *   <li><b>Patient:</b> Displays options to view doctors, upload vitals, appointments, and a treatment summary tab.</li>
     *   <li><b>Admin:</b> Displays options to manage all patients, doctors, and appointments.</li>
     * </ul>
     *
     * <h3>Key Components:</h3>
     * <ul>
     *   <li><b>Navigation Bar:</b>
     *       <ul>
     *         <li>Displays the application logo ("CheapAHH").</li>
     *         <li>Includes a "Log out" button that redirects the user to the login screen by restarting {@code HelloApplication}.</li>
     *       </ul>
     *   </li>
     *   <li><b>Sidebar:</b>
     *       <ul>
     *         <li>Includes buttons for navigation. These buttons trigger the rendering of specific pages or features in the main content area.</li>
     *         <li>Dynamically adjusts its content based on the user's role: Doctor, Patient, or Admin.</li>
     *       </ul>
     *   </li>
     *   <li><b>Content Area:</b>
     *       <ul>
     *         <li>Displays detailed views based on the sidebar button selected.</li>
     *         <li>Includes a profile card at the top featuring the user's details (e.g., name, age, gender, email, phone).</li>
     *       </ul>
     *   </li>
     *   <li><b>Role-Specific Options:</b>
     *       <ul>
     *         <li>For <b>Doctors:</b> The sidebar provides options such as "View Vitals," "Patients," "Appointments," and video call management (requests and scheduling).</li>
     *         <li>For <b>Patients:</b> The sidebar provides options like "Doctors," "Appointments," "Upload Vitals," and a treatment summary view.</li>
     *         <li>For <b>Admins:</b> Options to view and manage all patients, doctors, and appointments are displayed.</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <b>Behavior of Key Buttons:</b>
     * <ul>
     *   <li><b>Dashboard Button:</b> Displays the user's profile card and general content (main container page).</li>
     *   <li><b>Patients/Doctors Button:</b> Displays the relevant users (e.g., patients for doctors or doctors for patients).</li>
     *   <li><b>Appointments Button:</b> Loads the appointments management page.</li>
     *   <li><b>Email Button:</b> Displays a page for sending or managing emails.</li>
     *   <li><b>Video Call Options:</b> Doctors can handle incoming video consultation requests or schedule new video calls directly from the sidebar.</li>
     *   <li><b>Vitals Button:</b> Allows doctors to view patient vitals, while patients can upload their own vitals data.</li>
     * </ul>
     *
     * <h3>Implementation Details:</h3>
     * <ul>
     *   <li>Uses a {@link BorderPane} layout as the primary structure, with the navigation bar at the top, sidebar on the left, and content in the center.</li>
     *   <li>Handles navigation events for each button in the sidebar, clearing and repopulating the content area dynamically for the selected action.</li>
     *   <li>Fetches data dynamically (e.g., user details, patients list, appointments) for rendering content pages based on the logged-in user's role.</li>
     *   <li>Ensures role-based UI customization using {@code if-else} blocks to configure the sidebar buttons and events.</li>
     * </ul>
     *
     * @param primaryStage The primary stage of the JavaFX application.
     */
    public void start(Stage primaryStage) {
        // === Navigation Bar (Top) ===
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: white;");
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setSpacing(30);

        // Logo (Left)
        HBox logoBox = new HBox(5);
        Label logo = new Label("CheapAHH");
        logo.setStyle("-fx-text-fill: blue; -fx-font-family: Poppins");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logoBox.getChildren().add(logo);

        Button logout = new Button("Log out");
        logout.setAlignment(Pos.CENTER_RIGHT);

        Region middleSpacer = new Region();
        HBox.setHgrow(middleSpacer, Priority.ALWAYS);

        logout.setStyle(
                "-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 5px 20px");

        logout.setOnAction(e -> {
            HelloApplication home = new HelloApplication();
            this.user = null;
            this.accountType = null;
            this.accountID = null;
            try {
                home.start((Stage) ((Node) e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Add logo to the navbar
        navBar.getChildren().addAll(logoBox, middleSpacer, logout);

        // === Sidebar (Left) ===
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px;");
        sidebar.setPrefWidth(200);

        // Sidebar buttons
        Button dashboardBtn = createSidebarButton("Dashboard");
        Button patientsBtn = createSidebarButton("Patients");
        Button doctorsBtn = createSidebarButton("Doctors");
        Button appointmentsBtn = createSidebarButton("Appointments");
        Button emailBtn = createSidebarButton("Email");
        Button patientEvaluation = createSidebarButton("Patient Evaluation");
        Button treatmentSummary = createSidebarButton("Medical");
        Button allPatientsBtn = createSidebarButton("Patients");
        Button allDoctorsBtn = createSidebarButton("Doctors");

        if (user instanceof Doctor) {
            Button vitalsBtn = createSidebarButton("View Vitals");
            vitalsBtn.setOnAction(_ -> {
                content.getChildren().clear();
                content.getChildren().add(createVitalsViewPage());
            });
            sidebar.getChildren().addAll(dashboardBtn, patientsBtn, appointmentsBtn, vitalsBtn, patientEvaluation, emailBtn);
        } else if (user instanceof Patient) {
            Button uploadVitalsBtn = createSidebarButton("Upload Vitals");
            uploadVitalsBtn.setOnAction(_ -> {
                content.getChildren().clear();
                content.getChildren().add(createVitalsUploadPage());
            });
            sidebar.getChildren().addAll(dashboardBtn, doctorsBtn, appointmentsBtn, uploadVitalsBtn, treatmentSummary,  emailBtn);
        } else if (user instanceof Admin) {
            sidebar.getChildren().addAll(dashboardBtn, allPatientsBtn, allDoctorsBtn, appointmentsBtn, emailBtn);
        }

        // === Content Area (Right) ===
        content = new VBox(30);
        content.setPadding(new Insets(30));

        // Profile picture (placeholder icon)
        ImageView profileImage = new ImageView(new Image("default-avatar.png"));
        profileImage.setFitWidth(100);
        profileImage.setFitHeight(100);
        profileImage.setPreserveRatio(true);

        // Text labels
        Label nameLabel = new Label(user.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold  ;");

        Label infoLabel = new Label(user.getGender() + ", " + user.getAge() + " years");

        Label emailLabel = new Label(user.getEmail());
        Label phoneLabel = new Label(user.getPhone());

        // Layouts for info
        HBox emailPhoneBox = new HBox(20, emailLabel, phoneLabel);
        VBox textBox = new VBox(5, nameLabel, infoLabel, emailPhoneBox);
        textBox.setPadding(new Insets(20, 0, 20, 0));
        emailPhoneBox.setAlignment(Pos.CENTER_LEFT);

        // Card layout
        HBox profileCard = new HBox(20, profileImage, textBox);
        profileCard.setPadding(new Insets(20));
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Main Layout
        VBox main = renderMainContainer();

        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        content.getChildren().addAll(profileCard, main);

        // ***************************** USERS TAB ****************************

        VBox users = null;
        if (user instanceof Doctor) {
            users = createDoctorPatients(user.getUserID());
        }

        if (user instanceof Patient) {
            users = createPatientDoctors(user.getUserID());
        }

        // ****************************** APPOINTMENT TAB **********************8
        VBox appointmentsLayout = createAppointmentPage();

        // making all the sidebar buttons in use
        dashboardBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().addAll(profileCard, main);
        });
        VBox finalUsers = users;
        patientsBtn.setOnAction(event -> {
            content.getChildren().clear();

            Text patients = new Text("Patients");
            patients.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            patients.setFill(Color.BLUE);
            patients.setTextAlignment(TextAlignment.LEFT);
            VBox.setMargin(patients, new Insets(0, 0, 10, 0));

            content.getChildren().addAll(patients, finalUsers);
        });

        VBox finalUsers1 = users;
        doctorsBtn.setOnAction(event -> {
            content.getChildren().clear();

            Text doctors = new Text("Patients");
            doctors.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            doctors.setFill(Color.BLUE);
            doctors.setTextAlignment(TextAlignment.LEFT);
            VBox.setMargin(doctors, new Insets(0, 0, 10, 0));

            content.getChildren().addAll(doctors, finalUsers1);
        });

        appointmentsBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().addAll(appointmentsLayout);
        });

        emailBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(createEmailPage());
        });

        patientEvaluation.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(createEvaluationPage(user.getUserID()));
        });

        treatmentSummary.setOnAction( e -> {
            content.getChildren().clear();
            content.getChildren().add(createTreatmentSummaryPage(user.getUserID()));
        });

        allPatientsBtn.setOnAction(e -> {
            content.getChildren().clear();
            content.getChildren().add(createAllPatientsTab());
        });

        allDoctorsBtn.setOnAction( e -> {
            content.getChildren().clear();
            content.getChildren().add(createAllDoctorsTab());
        });

        // === Final Layout with BorderPane ===
        BorderPane root = new BorderPane();
        root.setTop(navBar); // Add the navigation bar to the top
        root.setLeft(sidebar); // Add the sidebar to the left
        root.setCenter(content); // Add the main content to the center

        // === Scene and Stage ===
        Scene scene = new Scene(root, 1080, 720);
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a styled button for use in a sidebar.
     * The button has a default transparent background and white text, with
     * hover effects that change the background color to white and the text color to blue.
     *
     * @param text The text to be displayed on the button.
     * @return A {@link Button} instance styled for the sidebar with hover effects applied.
     */
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: white; -fx-text-fill: blue; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-text-decoration: none;");
        });

        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    /**
     * Creates a horizontal row containing a label and its corresponding value.
     * The label is styled with bold text, and the components are spaced and aligned to the left.
     *
     * @param labelText The text to display in the label (usually the name or category of the information).
     * @param valueText The text to display as the value corresponding to the label.
     * @return An {@link HBox} instance containing the label and value, styled and aligned appropriately.
     */
    private HBox createInfoRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        Label value = new Label(valueText);
        HBox row = new HBox(10, label, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Renders the main container for the application's UI.
     * The container is a vertically arranged {@link VBox} with spacing, padding, and stylized appearance,
     * supporting dynamic content based on the user account type.
     *
     * <p>The function determines the account type (e.g., "doctor", "patient", or "admin")
     * and dynamically renders corresponding content into the container.</p>
     *
     * @return A {@link VBox} styled as the main container, containing dynamically generated content
     *         specific to the user's account type.
     */
    private VBox renderMainContainer() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Dynamically render content based on accountType
        if ("doctor".equals(accountType)) {
            mainContainer.getChildren().addAll(renderDoctorContent());
        } else if ("patient".equals(accountType)) {
            mainContainer.getChildren().addAll(renderPatientContent());
        } else if ("admin".equals(accountType)) {
            mainContainer.getChildren().addAll(renderAdminContent());
        }

        return mainContainer;
    }

    /**
     * Renders the content specific to a doctor, including their availability, personal information,
     * specialization details, and professional experience. The layout is organized into sections
     * including availability, user information, and additional details such as consultation fee
     * and years of experience.
     *
     * <p>This method attempts to cast the current user to a {@link Doctor} object.
     * If the cast fails, a message is printed to the console.</p>
     *
     * @return A {@link VBox} containing the doctor's information, styled and organized into multiple sections.
     */
    private VBox renderDoctorContent() {
        Doctor doc = null;
        try {
            doc = (Doctor) user;
        } catch (Exception e) {
            System.out.println("Unable to cast user to Doctor");
        }
        // Doctor-related content
        // Section Title
        Label availabilityTitle = new Label("Availability");
        availabilityTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Availability Section
        GridPane availabilityGrid = new GridPane();
        availabilityGrid.setVgap(10);
        availabilityGrid.setHgap(40);
        availabilityGrid.setPadding(new Insets(10, 0, 20, 0));

        availabilityGrid.add(new Label("Start Time:"), 0, 1);
        availabilityGrid.add(new Label(String.valueOf(doc.getStartTime())), 1, 1);

        availabilityGrid.add(new Label("End Time:"), 0, 2);
        availabilityGrid.add(new Label(String.valueOf(doc.getEndTime())), 1, 2);

        VBox availabilitySection = new VBox(10, availabilityTitle, availabilityGrid);
        availabilitySection.setPadding(new Insets(10));

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", user.getUserID()),
                createInfoRow("Address :", user.getAddress()));
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(" -fx-border-radius: 5; -fx-background-radius: 5;");

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Specialization :", doc.getSpeciality()),
                createInfoRow("License Number :", doc.getPMDC_NO()));
        rightInfo.setPadding(new Insets(10));
        rightInfo.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        // Info Grid
        HBox infoSection = new HBox(40, leftInfo, rightInfo);
        infoSection.setPadding(new Insets(0, 0, 20, 0));

        // Fee and Experience
        VBox bottomRow = new VBox(10,
                createInfoRow("Consultation Fee :", String.valueOf(doc.getConsulationFee())),
                createInfoRow("Experience Years :", (doc.getExperience() + " years")));
        bottomRow.setPadding(new Insets(10));

        VBox doctorInfo = new VBox(10, availabilitySection, infoSection, bottomRow);
        doctorInfo.setPadding(new Insets(10));

        return doctorInfo;
    }

    /**
     * Renders the content specific to a patient, displaying their personal information,
     * admission status, and medical diagnosis details. The layout is divided into sections
     * for user information (left) and diagnosis details (right), arranged horizontally.
     *
     * <p>The method attempts to cast the current user to a {@link Patient} object. If the casting
     * fails, an error message is logged. Additionally, it fetches the patient's diagnosis data
     * using an external data-fetching mechanism and handles any SQL exceptions appropriately.</p>
     *
     * @return A {@link VBox} containing the patient's information, organized and styled into multiple sections.
     */
    private VBox renderPatientContent() {
        Patient patient = null;
        try {
            patient = (Patient) user;
        } catch (Exception e) {
            System.out.println("Unable to cast user to Patient");
        }

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", patient.getUserID()),
                createInfoRow("Address :", patient.getAddress()));
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(" -fx-border-radius: 5; -fx-background-radius: 5;");

        List<String> rightInfoData = List.of();
        try {
            rightInfoData = DataFetcher.fetchPatientDiagnosis(patient.getUserID());
        } catch (SQLException ex) {
            AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while fetching patient diagnosis: " + ex.getMessage());
            ex.printStackTrace();
        }

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Admit Status :", patient.isAdmit() ? "Admitted" : "Not Admitted"),
                createInfoRow("Diagnosis:", Objects.equals(rightInfoData.get(0), "") ? "nill" : rightInfoData.get(0)),
                createInfoRow("Diagnosed by:", Objects.equals(rightInfoData.get(1), "") ? "nill" : rightInfoData.get(1)),
                createInfoRow("Medications :", Objects.equals(rightInfoData.get(2), "") ? "nill" : rightInfoData.get(2)));
        rightInfo.setPadding(new Insets(10));
        rightInfo.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        // Info Grid
        HBox infoSection = new HBox(40, leftInfo, rightInfo);
        infoSection.setPadding(new Insets(0, 0, 20, 0));

        VBox patientInfo = new VBox(10, infoSection);
        patientInfo.setPadding(new Insets(10));

        return patientInfo;
    }

    /**
     * Renders the content specific to an admin, displaying their personal details,
     * role information, and a description of their system-level responsibilities.
     * The layout consists of a section title, user information, and a role description,
     * arranged in a structured format.
     *
     * <p>This method attempts to cast the current user to an {@link Admin} object. If the
     * casting fails, an error message is logged. It organizes the content into sections
     * for user and role-related information, styled for presentation.</p>
     *
     * @return A {@link VBox} styled as the admin dashboard, containing personal information,
     *         role information, and a summarized description of responsibilities.
     */
    private VBox renderAdminContent() {
        Admin admin = null;
        try {
            admin = (Admin) user;
        } catch (Exception e) {
            System.out.println("Unable to cast user to Admin");
        }

        // Section Title
        Label adminTitle = new Label("Administrator Dashboard");
        adminTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Left Info Block
        VBox leftInfo = new VBox(10,
                createInfoRow("User ID :", admin.getUserID()),
                createInfoRow("Full Name :", admin.getName()),
                createInfoRow("Email :", admin.getEmail()));
        leftInfo.setPadding(new Insets(10));
        leftInfo.setStyle(""
                + "-fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Right Info Block
        VBox rightInfo = new VBox(10,
                createInfoRow("Role :", "Administrator"),
                createInfoRow("Phone Number :", admin.getPhone()),
                createInfoRow("Address :", admin.getAddress()));
        rightInfo.setPadding(new Insets(10));
        rightInfo.setStyle(""
                + "-fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Info Grid
        HBox infoSection = new HBox(40, leftInfo, rightInfo);
        infoSection.setPadding(new Insets(0, 0, 20, 0));

        // Bottom Summary Section
        VBox bottomRow = new VBox(10,
                createInfoRow("System Role Description :", "The administrator has access to manage users, monitor system activity, and oversee hospital operations."));
        bottomRow.setPadding(new Insets(10));
        bottomRow.setStyle(""
                + "-fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Wrapper for the Admin Dashboard
        VBox adminDashboard = new VBox(15, adminTitle, infoSection, bottomRow);
        adminDashboard.setPadding(new Insets(20));
        adminDashboard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; "
                + "-fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        return adminDashboard;
    }

    /**
     * Initializes the user data based on the account type and account ID.
     * This method retrieves the appropriate user data (Doctor, Patient, or Admin) by
     * delegating the data fetching logic to the {@link DataFetcher} class.
     *
     * <p>In case of a database error during the data retrieval process, an exception is
     * caught and an error message is logged to the console.</p>
     *
     * @throws Exception if data fetching fails unexpectedly.
     */
    private void initializeUser() {
        try {
            if (accountType.equals("doctor"))
                user = DataFetcher.getDoctorData(accountType, accountID);
            else if (accountType.equals("patient"))
                user = DataFetcher.getPatientData(accountType, accountID);
            else if (accountType.equals("admin"))
                user = DataFetcher.getAdminData(accountType, accountID);
        } catch (Exception e) {
            System.out.println("Database Error: Unable to retrieve data.");
        }
    }

    /**
     * Retrieves a list of appointments based on the currently logged-in user's role.
     *
     * <p>If the user is:</p>
     * <ul>
     *   <li><strong>Doctor:</strong> Fetches appointments where the doctor is the current user.</li>
     *   <li><strong>Patient:</strong> Fetches appointments where the patient is the current user.</li>
     *   <li><strong>Admin:</strong> Fetches all appointments in the system without filtering by user ID.</li>
     *   <li><strong>Invalid user:</strong> Logs the error and returns an empty list.</li>
     * </ul>
     *
     * <p>The appointments are fetched using a query based on the user's type (Doctor, Patient, or Admin),
     * and additional details about the doctor and patient (if needed) are retrieved from the {@link DataFetcher}.
     * Each appointment is represented as an {@link Appointment} object containing the relevant data.</p>
     *
     * @return A {@link List} of {@link Appointment} objects for the current user,
     *         or an empty list if no user is logged in or other issues occur.
     *
     * @throws RuntimeException If an {@link InvalidAppointmentException} occurs while processing appointments.
     */
    public List<Appointment> getAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        // Check if the user is null
        if (this.user == null) {
            System.out.println("No user is currently logged in.");
            return appointments;
        }

        // Determine whether the user is a doctor, patient, or admin
        String query;
        if (user instanceof Doctor) {
            query = "SELECT appointment_id, date_time, doctor_id, patient_id, status " +
                    "FROM appointment WHERE doctor_id = ?";
        } else if (user instanceof Patient) {
            query = "SELECT appointment_id, date_time, doctor_id, patient_id, status " +
                    "FROM appointment WHERE patient_id = ?";
        } else if (user instanceof Admin) {
            query = "SELECT appointment_id, date_time, doctor_id, patient_id, status " +
                    "FROM appointment";
        } else {
            System.out.println("Invalid user type: " + user.getClass().getSimpleName());
            return appointments;
        }

        // Execute the query
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the user ID as a parameter for doctor or patient
            if (user instanceof Doctor || user instanceof Patient) {
                stmt.setString(1, user.getUserID());
            }

            // Execute the query and process the ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a new Appointment object
                    String appointmentId = rs.getString("appointment_id");
                    LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                    String doctorId = rs.getString("doctor_id");
                    String patientId = rs.getString("patient_id");
                    AppointmentStatus status = AppointmentStatus.valueOf(rs.getString("status"));

                    Doctor doctor = null;
                    Patient patient = null;

                    if (user instanceof Doctor) {
                        doctor = (Doctor) user; // Current user is a doctor
                        patient = DataFetcher.getPatientData("patient", patientId); // Fetch patient details
                    } else if (user instanceof Patient) {
                        patient = (Patient) user; // Current user is a patient
                        doctor = DataFetcher.getDoctorData("doctor", doctorId); // Fetch doctor details
                    } else if (user instanceof Admin) {
                        patient = DataFetcher.getPatientData("patient", patientId);
                        doctor = DataFetcher.getDoctorData("doctor", doctorId);
                    }

                    appointments.add(new Appointment(appointmentId, dateTime, doctor, patient, status));
                }
            } catch (InvalidAppointmentException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching appointments: " + e.getMessage());
        }

        return appointments;
    }

    /**
     * Displays a popup dialog showing the details of a given appointment.
     *
     * <p>The dialog contains information about the appointment, including:</p>
     * <ul>
     *   <li>Appointment ID</li>
     *   <li>Doctor's name (if available)</li>
     *   <li>Patient's name (if available)</li>
     *   <li>Date and time of the appointment</li>
     *   <li>Appointment status</li>
     * </ul>
     *
     * <p>A close button is provided to dismiss the dialog. The dialog is styled
     * for readability, with each detail displayed in a labeled row.</p>
     *
     * @param appointment The {@link Appointment} object containing the details to be displayed.
     *                     If any field (e.g., doctor or patient) is null, "N/A" will be displayed
     *                     instead of the missing information.
     */
    private void showAppointmentDetailsPopup(Appointment appointment) {
        // Create a new Dialog for displaying details
        Dialog<Void> detailsDialog = new Dialog<>();
        detailsDialog.setTitle("Appointment Details");

        // Create the Dialog's content
        VBox detailsContainer = new VBox(10);
        detailsContainer.setPadding(new Insets(20));

        // Add details fields
        Label titleLabel = new Label("Appointment Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox idRow = createDetailRow("Appointment ID:", appointment.getAppointmentID());
        HBox doctorRow = createDetailRow("Doctor:",
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : "N/A");
        HBox patientRow = createDetailRow("Patient:",
                appointment.getPatient() != null ? appointment.getPatient().getName() : "N/A");
        HBox dateRow = createDetailRow("Date:", appointment.getDateTime().toLocalDate().toString());
        HBox timeRow = createDetailRow("Time:", appointment.getDateTime().toLocalTime().toString());
        HBox statusRow = createDetailRow("Status:", appointment.getStatus().toString());

        // Add all rows to container
        detailsContainer.getChildren().addAll(titleLabel, idRow, doctorRow, patientRow, dateRow, timeRow, statusRow);

        // Set the content of the dialog
        detailsDialog.getDialogPane().setContent(detailsContainer);

        // Disable the default buttons
        detailsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Show the dialog
        detailsDialog.showAndWait();
    }

    /**
     * Creates a horizontal row consisting of a labeled value pair for displaying details.
     *
     * <p>The row contains:</p>
     * <ul>
     *   <li>A bold label describing the field.</li>
     *   <li>The associated value styled with appropriate coloring and font size.</li>
     * </ul>
     *
     * <p>The row elements are spaced evenly by 10 pixels and aligned to the left.</p>
     *
     * @param label The text to be displayed as the field label (e.g., "Name:", "Date:").
     * @param value The value corresponding to the label (e.g., actual name or date value).
     * @return An {@link HBox} containing the styled label and value for use in UI components.
     */
    private HBox createDetailRow(String label, String value) {
        Label labelField = new Label(label);
        labelField.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");

        Label valueField = new Label(value);
        valueField.setStyle("-fx-text-fill: #555; -fx-font-size: 14px;");

        HBox row = new HBox(10, labelField, valueField);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Creates a horizontal row for displaying a label and an associated input field.
     *
     * <p>The row contains:</p>
     * <ul>
     *   <li>A bold label describing the input field (e.g., "Name:", "Email:").</li>
     *   <li>An input field (e.g., a {@link TextField}, {@link ComboBox}, or other {@link Node})
     *       styled with padding and font size.</li>
     * </ul>
     *
     * <p>The elements in the row are spaced by 10 pixels and aligned to the left.</p>
     *
     * @param labelText The text to be displayed as the label for the input field.
     * @param inputField The {@link Node} representing the input element (e.g., {@link TextField}).
     * @return An {@link HBox} containing the styled label and input field for use in UI components.
     */
    private HBox createUpdateRow(String labelText, Node inputField) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");

        inputField.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

        HBox row = new HBox(10, label, inputField);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Opens a dialog to update the details of an existing appointment.
     *
     * <p>This dialog provides the user with the ability to update the following details of an appointment:</p>
     * <ul>
     *   <li>Appointment date (using a {@link DatePicker})</li>
     *   <li>Appointment time (using a {@link ComboBox} of selectable {@link LocalTime})</li>
     *   <li>Assigned doctor (using a {@link ComboBox} populated with available doctors)</li>
     * </ul>
     *
     * <p>The dialog validates the user input to ensure:</p>
     * <ul>
     *   <li>All fields are filled</li>
     *   <li>The selected date and time are in the future</li>
     *   <li>There are no duplicate appointments for the selected date, time, and doctor</li>
     * </ul>
     *
     * <p>If the input passes validation, the appointment is updated in the database, the associated
     * {@link TableView} is refreshed with the updated data, and a confirmation is displayed to the user.
     * If validation fails or an error occurs, appropriate error messages are shown.</p>
     *
     * @param appointment The {@link Appointment} object to be updated. Its current details are pre-filled
     *                     in the dialog to assist the user.
     * @param table The {@link TableView} displaying the list of appointments. It is updated
     *              automatically upon successful update of the appointment in the database.
     */
    private void openUpdateAppointmentDialog(Appointment appointment, TableView<Appointment> table) {
        // Create a Dialog window for updating the appointment
        Dialog<Void> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Appointment");

        VBox dialogContainer = new VBox(15);
        dialogContainer.setPadding(new Insets(20));

        // Add Title
        Label titleLabel = new Label("Update Appointment Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create Input Fields
        DatePicker datePicker = new DatePicker(appointment.getDateTime().toLocalDate());
        ComboBox<LocalTime> timeComboBox = new ComboBox<>();
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();

        // Populate Times
        for (int hour = 8; hour <= 17; hour++) {
            timeComboBox.getItems().add(LocalTime.of(hour, 0));
            timeComboBox.getItems().add(LocalTime.of(hour, 30));
        }
        timeComboBox.setValue(appointment.getDateTime().toLocalTime());

        doctorComboBox.setPrefWidth(250);
        doctorComboBox.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            doctors.addAll(DataFetcher.getAllDoctors());
        } catch (SQLException e) {
            AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Unable to fetch doctors from database.");
        }

        doctorComboBox.getItems().addAll(doctors);

        // Set CellFactory to display the combined text in the ComboBox
        doctorComboBox.setCellFactory(param -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                }
            }
        });

        // Set the ButtonCell to display the selected doctor's name and specialty
        doctorComboBox.setButtonCell(new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                }
            }
        });
        doctorComboBox.setValue(appointment.getDoctor());

        // Add Input Fields to Dialog
        dialogContainer.getChildren().addAll(
                titleLabel,
                createUpdateRow("New Date", datePicker),
                createUpdateRow("New Time", timeComboBox),
                createUpdateRow("New Doctor", doctorComboBox)
        );

        // Add Buttons
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        HBox buttons = new HBox(10, updateButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        dialogContainer.getChildren().add(buttons);

        // Handle Update Button Click
        updateButton.setOnAction(e -> {
            // Validation
            LocalDate date = datePicker.getValue();
            LocalTime time = timeComboBox.getValue();
            Doctor doctor = doctorComboBox.getValue();

            if (date == null || time == null || doctor == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
                return;
            }

            LocalDateTime newDateTime = LocalDateTime.of(date, time);
            if (newDateTime.isBefore(LocalDateTime.now())) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "The selected date and time must be in the future.");
                return;
            }

            try {
                // Check for Duplicate Appointments
                AppointmentManager appointmentManager = new AppointmentManager();
                if (appointmentManager.isDuplicateAppointment(newDateTime, doctor, appointment.getPatient())) {
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Duplicate Error",
                            "An appointment already exists at this date and time for the selected doctor.");
                    return;
                }

                // Update Appointment in Database
                updateAppointmentInDatabase(appointment.getAppointmentID(), newDateTime, doctor);

                // Refresh the TableView
                table.setItems(FXCollections.observableArrayList(getAppointments()));

                // Show Success Notification
                AlertDialogueBox.showAlert(Alert.AlertType.CONFIRMATION, "Update Successful",
                        "The appointment has been successfully updated.");
                updateDialog.close();
            } catch (SQLException ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                        "An error occurred while updating the appointment: " + ex.getMessage());
            }
        });

        // Set Dialog Content and Show Dialog
        updateDialog.getDialogPane().setContent(dialogContainer);
        updateDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); // Disable default buttons
        updateDialog.showAndWait();
    }

    /**
     * Updates an existing appointment in the database with new details.
     *
     * <p>The fields updated in the appointment record include:</p>
     * <ul>
     *   <li>New appointment date and time</li>
     *   <li>New doctor assigned to the appointment</li>
     *   <li>Appointment status, which is reset to "PENDING"</li>
     * </ul>
     *
     * <p>If the specified appointment ID does not exist, a {@link SQLException}
     * is thrown. The database connection and prepared statement are managed via
     * a try-with-resources block to ensure proper resource cleanup.</p>
     *
     * @param appointmentId The {@link String} representing the unique ID of the
     *                      appointment to be updated.
     * @param newDateTime   The new {@link LocalDateTime} value for the appointment's
     *                      date and time.
     * @param newDoctor     The new {@link Doctor} object representing the doctor
     *                      assigned to the appointment.
     *
     * @throws SQLException If an error occurs while updating the database, or if the
     *                      appointment ID does not exist in the database.
     */
    private void updateAppointmentInDatabase(String appointmentId, LocalDateTime newDateTime, Doctor newDoctor)
            throws SQLException {
        // SQL Query to Update Appointment in the Database
        String updateQuery = "UPDATE appointment " +
                "SET date_time = ?, doctor_id = ?, status = ? " +
                "WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            // Set Parameters
            stmt.setTimestamp(1, Timestamp.valueOf(newDateTime)); // New Date and Time
            stmt.setString(2, newDoctor.getUserID()); // New Doctor ID
            stmt.setString(3, "PENDING");
            stmt.setString(4, appointmentId); // Appointment ID

            // Execute the Update Query
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update appointment. Appointment ID not found.");
            }
        }
    }

    /**
     * Opens a dialog to change the status of a specific appointment.
     *
     * <p>This dialog allows the user to update the status of an appointment by selecting
     * from a predefined list of statuses. The supported statuses include:</p>
     * <ul>
     *   <li>{@link AppointmentStatus#APPROVED}</li>
     *   <li>{@link AppointmentStatus#REJECTED}</li>
     *   <li>{@link AppointmentStatus#COMPLETED}</li>
     *   <li>{@link AppointmentStatus#NO_SHOW}</li>
     * </ul>
     *
     * <p>The dialog validates the input to ensure that a status is selected before submitting
     * the change. Once updated, the appointment's status is saved in the database,
     * and the associated {@link TableView} displaying appointments is refreshed to reflect
     * the updated data.</p>
     *
     * <p>In case of an error during the update process, an error message is displayed to the user.</p>
     *
     * @param appointment The {@link Appointment} object whose status needs to be updated.
     *                     The dialog displays the current status of this appointment.
     * @param table The {@link TableView} containing the list of appointments. This table will
     *              be refreshed upon a successful status update.
     */
    private void openChangeStatusDialog(Appointment appointment, TableView<Appointment> table) {
        // Create a Dialog for Changing Status
        Dialog<Void> statusDialog = new Dialog<>();
        statusDialog.setTitle("Change Appointment Status");

        VBox dialogContainer = new VBox(15);
        dialogContainer.setPadding(new Insets(20));

        Label titleLabel = new Label("Change Status for Appointment: " + appointment.getAppointmentID());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<AppointmentStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(AppointmentStatus.APPROVED, AppointmentStatus.REJECTED, AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW);
        statusComboBox.setValue(appointment.getStatus());

        HBox statusRow = createUpdateRow("New Status", statusComboBox);
        dialogContainer.getChildren().addAll(titleLabel, statusRow);

        // Add Change Button
        Button changeButton = new Button("Change Status");
        changeButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");

        changeButton.setOnAction(e -> {
            AppointmentStatus selectedStatus = statusComboBox.getValue();

            if (selectedStatus == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a status.");
                return;
            }

            try {
                AppointmentManager appointmentManager = new AppointmentManager();
                appointmentManager.updateAppointmentStatus(appointment, selectedStatus);

                // Refresh Table Data
                table.setItems(FXCollections.observableArrayList(getAppointments()));

                // Success Notification
                AlertDialogueBox.showAlert(Alert.AlertType.CONFIRMATION, "Status Update Successful",
                        "The status has been successfully updated.");
                statusDialog.close();
            } catch (Exception ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Update Failed",
                        "Unable to update status: " + ex.getMessage());
            }
        });

        HBox buttons = new HBox(10, changeButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        dialogContainer.getChildren().add(buttons);

        // Set Dialog Content
        statusDialog.getDialogPane().setContent(dialogContainer);
        statusDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        statusDialog.showAndWait();
    }

    /**
     * Creates and returns a {@link VBox} layout containing a {@link TableView}
     * that displays a list of patients assigned to a specific doctor.
     *
     * <p>The patients for the specified doctor are retrieved using the {@code doctorID}
     * parameter, and the table is populated with relevant information about each patient:</p>
     * <ul>
     *   <li>Patient ID</li>
     *   <li>Name</li>
     *   <li>Date of Birth</li>
     *   <li>Gender</li>
     *   <li>Address</li>
     *   <li>Phone Number</li>
     *   <li>Admitted Status</li>
     * </ul>
     *
     * <p>The table includes a placeholder that appears when no patient data is available
     * for the specified doctor.</p>
     *
     * <p>The table is also styled to provide an improved user interface using JavaFX CSS styles.</p>
     *
     * @param doctorID A {@link String} representing the ID of the doctor whose patients
     *                 need to be displayed in the table.
     * @return A {@link VBox} containing the {@link TableView} with the list of patients.
     */
    public VBox createDoctorPatients(String doctorID) {
        // Get the list of patients returned from the getAllPatientsForDoctor method
        List<Patient> patientList = DataFetcher.getAllPatientsForDoctor(doctorID);

        // Convert the list of patients to an ObservableList for TableView
        ObservableList<Patient> patients = FXCollections.observableArrayList(patientList);

        // Create the TableView
        TableView<Patient> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style the table
        tableView.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Create columns for Patient Table
        TableColumn<Patient, String> userIdColumn = new TableColumn<>("Patient ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Patient, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Patient, Boolean> isAdmittedColumn = new TableColumn<>("Is Admitted");
        isAdmittedColumn.setCellValueFactory(new PropertyValueFactory<>("admit"));

        // Add the columns to the table
        tableView.getColumns().addAll(userIdColumn, nameColumn, dobColumn, genderColumn, addressColumn, phoneColumn,
                isAdmittedColumn);

        // Set the data to the table
        tableView.setItems(patients);

        // Set a placeholder for the table
        Label placeholder = new Label("No patients found for " + user.getName() + ".");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        tableView.setPlaceholder(placeholder);

        // Add the table to a layout (VBox in this case)
        return new VBox(20, tableView);
    }

    /**
     * Creates and returns a {@link VBox} layout containing a {@link TableView}
     * that displays a list of doctors associated with a specific patient.
     *
     * <p>The doctors for the specified patient are retrieved using the {@code patientID}
     * parameter, and the table is populated with relevant information about each doctor:</p>
     * <ul>
     *   <li>Doctor ID</li>
     *   <li>Name</li>
     *   <li>Gender</li>
     *   <li>Phone Number</li>
     *   <li>Email</li>
     *   <li>Speciality</li>
     * </ul>
     *
     * <p>The table includes a placeholder that appears when no doctor data is available
     * for the specified patient.</p>
     *
     * <p>The table is styled to provide an improved user interface using JavaFX CSS styles.</p>
     *
     * @param patientID A {@link String} representing the ID of the patient whose doctors
     *                  need to be displayed in the table.
     * @return A {@link VBox} containing the {@link TableView} with the list of doctors.
     */
    public VBox createPatientDoctors(String patientID) {
        // Get the list of doctors returned from the getAllPatientsForDoctor method
        List<Doctor> doctorList = DataFetcher.getAllDoctorsForPatient(patientID);

        // Convert the list of doctors to an ObservableList for TableView
        ObservableList<Doctor> doctors = FXCollections.observableArrayList(doctorList);

        // Create the TableView
        TableView<Doctor> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style the table
        tableView.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Create columns for Patient Table
        TableColumn<Doctor, String> userIdColumn = new TableColumn<>("Doctor ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Doctor, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Doctor, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Doctor, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Doctor, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Doctor, String> specialityColumn = new TableColumn<>("Speciality");
        specialityColumn.setCellValueFactory(new PropertyValueFactory<>("speciality"));

        // Add the columns to the table
        tableView.getColumns().addAll(userIdColumn, nameColumn, genderColumn, phoneColumn, emailColumn,
                specialityColumn);

        // Set the data to the table
        tableView.setItems(doctors);

        // Set a placeholder for the table
        Label placeholder = new Label("No doctors found for " + user.getName() + ".");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        tableView.setPlaceholder(placeholder);

        // Add the table to a layout (VBox in this case)
        return new VBox(20, tableView);
    }

    /**
     * Creates and returns a {@link VBox} layout containing a {@link TableView} that
     * displays a list of all patients in the system.
     *
     * <p>The data is fetched from the database using the {@code DataFetcher.getAllPatients()}
     * method. The table is populated with the following details about each patient:</p>
     * <ul>
     *   <li>Patient ID</li>
     *   <li>Name</li>
     *   <li>Date of Birth</li>
     *   <li>Gender</li>
     *   <li>Address</li>
     *   <li>Phone Number</li>
     *   <li>Admission Status</li>
     *   <li>Pending Fee</li>
     * </ul>
     *
     * <p>The table includes a placeholder message when no patient data is available in
     * the system.</p>
     *
     * <p>The layout also includes a styled heading, "Patients," at the top and additional
     * visual styling to enhance the user interface.</p>
     *
     * @return A {@link VBox} layout containing a title and a table with patient details.
     */
    public VBox createAllPatientsTab() {

        Text heading = new Text("Patients");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setFill(Color.BLUE);

        // Fetch the list of all patients from the database
        List<Patient> patientList = DataFetcher.getAllPatients();

        // Convert the patient list to an ObservableList for TableView
        ObservableList<Patient> patients = FXCollections.observableArrayList(patientList);

        // Create the TableView
        TableView<Patient> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style the table
        tableView.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Create columns for the Patient Table
        TableColumn<Patient, String> userIdColumn = new TableColumn<>("Patient ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Patient, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Patient, Boolean> isAdmittedColumn = new TableColumn<>("Is Admitted?");
        isAdmittedColumn.setCellValueFactory(new PropertyValueFactory<>("admit"));

        TableColumn<Patient, Double> pendingFeeColumn = new TableColumn<>("Pending Fee");
        pendingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("pendingFee"));

        // Add all columns to the table
        tableView.getColumns().addAll(userIdColumn, nameColumn, dobColumn, genderColumn, addressColumn, phoneColumn,
                isAdmittedColumn, pendingFeeColumn);

        // Set the data to the table
        tableView.setItems(patients);

        // Set a placeholder for the table
        Label placeholder = new Label("No patients found in the system.");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        tableView.setPlaceholder(placeholder);

        // Wrap the table in a VBox for styling and layout
        VBox patientTab = new VBox(20, heading, tableView);
        patientTab.setPadding(new Insets(20));
        patientTab.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10px;");

        return patientTab;
    }

    /**
     * Creates and returns a {@link VBox} layout containing a {@link TableView}
     * that displays a list of all doctors in the system.
     *
     * <p>The data is fetched from the database using the {@code DataFetcher.getAllDoctors()}
     * method. The table is populated with the following details about each doctor:</p>
     * <ul>
     *   <li>Doctor ID</li>
     *   <li>Name</li>
     *   <li>Date of Birth</li>
     *   <li>Gender</li>
     *   <li>Address</li>
     *   <li>Phone Number</li>
     *   <li>Speciality</li>
     *   <li>Experience (Years)</li>
     *   <li>Salary</li>
     *   <li>Shift Time</li>
     * </ul>
     *
     * <p>The table includes a placeholder message when no doctor data is available in
     * the system or if an error occurs during data fetching.</p>
     *
     * <p>In case of a database error, an error alert is displayed to the user indicating
     * that the data could not be fetched.</p>
     *
     * <p>The layout also includes a styled heading, "Doctors," at the top and additional
     * visual styling to enhance the user interface.</p>
     *
     * @return A {@link VBox} layout containing a title and a table with doctor details.
     */
    public VBox createAllDoctorsTab() {

        Text heading = new Text("Doctors");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setFill(Color.BLUE);

        List<Doctor> doctorList = List.of();
        // Fetch the list of all doctors
        try {
            doctorList = DataFetcher.getAllDoctors();
        } catch (SQLException e) {
            AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Unable to fetch doctors from database.");
        }

        // Convert the list to an ObservableList
        ObservableList<Doctor> doctors = FXCollections.observableArrayList(doctorList);

        // Create the TableView
        TableView<Doctor> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style the table
        tableView.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Create Table Columns
        TableColumn<Doctor, String> userIdColumn = new TableColumn<>("Doctor ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Doctor, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Doctor, String> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<Doctor, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Doctor, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Doctor, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Doctor, String> specialityColumn = new TableColumn<>("Speciality");
        specialityColumn.setCellValueFactory(new PropertyValueFactory<>("speciality"));

        TableColumn<Doctor, Integer> experienceColumn = new TableColumn<>("Experience (Y)");
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));

        TableColumn<Doctor, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Doctor, String> shiftTimeColumn = new TableColumn<>("Shift");
        shiftTimeColumn.setCellValueFactory(doctor -> {
            String shift = doctor.getValue().getStartTime() + " to " + doctor.getValue().getEndTime();
            return new SimpleStringProperty(shift);
        });

        // Add columns to the TableView
        tableView.getColumns().addAll(userIdColumn, nameColumn, dobColumn, genderColumn, addressColumn, phoneColumn, specialityColumn, experienceColumn,
                salaryColumn, shiftTimeColumn);

        // Set the data for the TableView
        tableView.setItems(doctors);

        // Set a placeholder for when there's no data
        Label placeholder = new Label("No doctors found in the system.");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        tableView.setPlaceholder(placeholder);

        // Wrap the TableView in a VBox for styling and layout
        VBox doctorTab = new VBox(20, heading, tableView);
        doctorTab.setPadding(new Insets(20));
        doctorTab.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10px;");

        return doctorTab;
    }

    /**
     * Creates and returns a {@link VBox} layout containing the appointment management interface.
     *
     * <p>The interface provides a {@link TableView} displaying a list of appointments along with
     * action buttons to perform the following operations:</p>
     * <ul>
     *   <li>Book an appointment</li>
     *   <li>Update an appointment</li>
     *   <li>Cancel an appointment</li>
     *   <li>View appointment details</li>
     *   <li>Change the status of an appointment</li>
     * </ul>
     *
     * <p>The table includes the following details for each appointment:</p>
     * <ul>
     *   <li>Appointment ID</li>
     *   <li>Patient Name</li>
     *   <li>Doctor Name</li>
     *   <li>Date</li>
     *   <li>Time</li>
     *   <li>Status</li>
     * </ul>
     *
     * <p>The interface also includes a search bar for real-time filtering of appointments based
     * on patient name, doctor name, or appointment ID. The data is dynamically updated in the
     * {@link TableView} as the user types in the search bar.</p>
     *
     * <p>The layout is context-sensitive—offering different functionalities depending on whether
     * the logged-in user is a patient or a doctor:</p>
     * <ul>
     *   <li>If the user is a patient, they can book, update, cancel, or view the details of their
     *       appointments.</li>
     *   <li>If the user is a doctor, they can view and modify the status of their appointments.</li>
     * </ul>
     *
     * <p>In case of errors during operations (e.g., database errors), appropriate alerts are displayed
     * to the user.</p>
     *
     * @return A {@link VBox} layout containing the appointment management interface,
     *         including a searchable table, action buttons, and user-specific functionality.
     */
    public VBox createAppointmentPage() {
        // Appointments Table
        TableView<Appointment> table = new TableView<>();

        // Columns
        // Appointment ID Column
        TableColumn<Appointment, String> idCol = new TableColumn<>("Appointment ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));

        // Patient Name Column
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor Name");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        // Date Column (local date as an object or formatted string)
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate")); // Use formatted date

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, timeCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // appointment data
        List<Appointment> appointments = getAppointments();
        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList(appointments);
        table.setItems(appointmentData);

        // Styling for TableView
        table.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-width: 0;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");

        // Set a placeholder for the table
        Label placeholder = new Label("No appointments available.");
        placeholder.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        table.setPlaceholder(placeholder);

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by patient name, doctor name or appointment ID");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);

        // Search Bar Listener for Real-Time Filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filter the list of appointments
            ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

            for (Appointment appointment : appointmentData) {
                // Check if the patient name or appointment ID contains the search term
                // (case-insensitive)
                if ((appointment.getPatient().getName().toLowerCase().contains(newValue.toLowerCase())) ||
                        (appointment.getAppointmentID().toLowerCase().contains(newValue.toLowerCase())) ||
                        (appointment.getDoctor().getName().toLowerCase().contains(newValue.toLowerCase()))) {
                    filteredAppointments.add(appointment);
                }
            }

            // Update the table view with the filtered appointments
            table.setItems(filteredAppointments);
        });

        // Action Buttons
        Button addAppointmentBtn = new Button("Add Appointment");
        addAppointmentBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        addAppointmentBtn.setPrefHeight(40);
        addAppointmentBtn.setOnAction(event -> {
            AppointmentManager appointmentManager = new AppointmentManager();

            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(10));

            // Title
            Text heading1 = new Text("Book an Appointment");
            heading1.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            heading1.setFill(Color.BLUE); // ← your requested color
            heading1.setTextAlignment(TextAlignment.LEFT);
            VBox.setMargin(heading1, new Insets(0, 0, 10, 0));

            VBox formContainer = new VBox(15);
            formContainer.setPadding(new Insets(30));
            formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
            formContainer.setMaxWidth(600);

            Label patientSectionLabel = new Label("Patient Information");
            patientSectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            GridPane patientGrid = new GridPane();
            patientGrid.setHgap(15);
            patientGrid.setVgap(15);
            patientGrid.setPadding(new Insets(10, 0, 20, 0));

            Label patientName = new Label("Patient Name:");
            patientName.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            TextField nameFieldAppointment = new TextField(user.getName());
            nameFieldAppointment.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");
            nameFieldAppointment.setEditable(false);

            patientGrid.add(patientName, 0, 0);
            patientGrid.add(nameFieldAppointment, 1, 0);

            Label detailsSectionLabel = new Label("Appointment Details");
            detailsSectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(15);
            detailsGrid.setVgap(15);
            detailsGrid.setPadding(new Insets(10, 0, 20, 0));

            Label doctorLabel = new Label("Select Doctor:");
            doctorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            ComboBox<Doctor> doctorComboBox = new ComboBox<>();
            doctorComboBox.setPrefWidth(250);
            doctorComboBox.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

            ArrayList<Doctor> doctors = new ArrayList<>();
            try {
                doctors.addAll(DataFetcher.getAllDoctors());
            } catch (SQLException e) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Unable to fetch doctors from database.");
            }

            doctorComboBox.getItems().addAll(doctors);

            // Set CellFactory to display the combined text in the ComboBox
            doctorComboBox.setCellFactory(param -> new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor doctor, boolean empty) {
                    super.updateItem(doctor, empty);
                    if (empty || doctor == null) {
                        setText(null);
                    } else {
                        setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                    }
                }
            });

            // Set the ButtonCell to display the selected doctor's name and specialty
            doctorComboBox.setButtonCell(new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor doctor, boolean empty) {
                    super.updateItem(doctor, empty);
                    if (empty || doctor == null) {
                        setText(null);
                    } else {
                        setText(doctor.getName() + ", " + doctor.getSpeciality()); // Display Name, Speciality
                    }
                }
            });

            Label dateLabel = new Label("Select Date:");
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            DatePicker datePicker = new DatePicker();
            datePicker.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");

            Label timeLabel = new Label("Select Time:");
            timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            ComboBox<LocalTime> timeComboBox = new ComboBox<>();
            timeComboBox.setPrefWidth(250);
            timeComboBox.setStyle("-fx-padding: 8px; -fx-font-size: 14px;");
            // Add times every 30 minutes
            IntStream.rangeClosed(8, 17).forEach(hour -> {
                timeComboBox.getItems().add(LocalTime.of(hour, 0));
                timeComboBox.getItems().add(LocalTime.of(hour, 30));
            });

            detailsGrid.add(doctorLabel, 0, 0);
            detailsGrid.add(doctorComboBox, 1, 0);
            detailsGrid.add(dateLabel, 0, 1);
            detailsGrid.add(datePicker, 1, 1);
            detailsGrid.add(timeLabel, 0, 2);
            detailsGrid.add(timeComboBox, 1, 2);

            Button bookBtn = new Button("Book Appointment");
            bookBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-padding: 10px 20px; -fx-background-radius: 5px;");
            bookBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(bookBtn, Priority.ALWAYS);

            Label statusLabel = new Label();
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            bookBtn.setOnAction(e -> {
                try {
                    LocalDate date = datePicker.getValue();
                    LocalTime time = timeComboBox.getValue();
                    Doctor selectedDoctor = doctorComboBox.getValue();

                    if (date == null || time == null || selectedDoctor == null) {
                        statusLabel.setText("Please fill all fields correctly.");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    LocalDateTime dateTime = LocalDateTime.of(date, time);
                    appointmentManager.requestAppointment(dateTime, selectedDoctor, (Patient) user);
                    statusLabel.setText("Appointment booked successfully!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                    // === Update the TableView ===
                    List<Appointment> updatedAppointments = getAppointments(); // Fetch new appointments
                    ObservableList<Appointment> updatedData = FXCollections.observableArrayList(updatedAppointments);
                    table.setItems(updatedData); // Update the TableView

                    content.getChildren().clear();
//                    content.getChildren().addAll(profileCard, main);

                } catch (InvalidAppointmentException | DuplicateAppointmentException ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                } catch (Exception ex) {
                    statusLabel.setText("An unexpected error occurred.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            });

            formContainer.getChildren().addAll(
                    patientSectionLabel,
                    patientGrid,
                    detailsSectionLabel,
                    detailsGrid,
                    bookBtn,
                    statusLabel);

            mainContainer.getChildren().addAll(heading1, formContainer);
            content.getChildren().clear();
            content.getChildren().add(mainContainer);
        });

        Button updateAppointmentBtn = new Button("Update Appointment");
        updateAppointmentBtn.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-size: 14px;");
        updateAppointmentBtn.setPrefHeight(40);
        updateAppointmentBtn.setOnAction(event -> {
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Update Error",
                        "Please select an appointment to update.");
                return;
            }

            if (!(String.valueOf(selectedAppointment.getStatus()).equalsIgnoreCase("PENDING"))) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Status Update Error",
                        "Cannot change status for an appointment that is not in pending stage.");
                return;
            }

            // Open the Update Appointment Dialog
            openUpdateAppointmentDialog(selectedAppointment, table);
        });

        Button cancelAppointment = new Button("Cancel Appointment");
        cancelAppointment.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelAppointment.setPrefHeight(40);
        cancelAppointment.setOnAction(event -> {
            // Get the selected appointment from the table
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Cancel Error",
                        "Please select an appointment to delete.");
                return;
            }

            // Prompt user for confirmation before deletion
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Cancel Confirmation");
            confirmationAlert.setHeaderText("Are you sure you want to cancel this appointment?");

            // Wait for user's response (OK or Cancel)
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        // Prepare the DELETE query
                        String cancelQuery = "UPDATE appointment " +
                                "SET status = 'CANCELED' " +
                                "WHERE appointment_id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(cancelQuery)) {
                            // Set the appointment ID parameter
                            stmt.setString(1, selectedAppointment.getAppointmentID());

                            // Execute the query
                            int rowsAffected = stmt.executeUpdate();

                            if (rowsAffected > 0) {
                                // Refresh the TableView by fetching updated appointments
                                List<Appointment> updatedAppointments = getAppointments();
                                ObservableList<Appointment> updatedData = FXCollections
                                        .observableArrayList(updatedAppointments);
                                table.setItems(updatedData);

                                // Display success message
                                AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Cancel Success",
                                        "Appointment cancelled successfully!");
                            } else {
                                throw new SQLException("Appointment not found in the database.");
                            }
                        }
                    } catch (SQLException ex) {
                        // Handle database-related errors
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Cancel Error",
                                "An error occurred while cancelling the appointment: " + ex.getMessage());
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        // Handle other errors
                        AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Cancel Error",
                                "An unexpected error occurred: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
        });

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");
        viewDetailsBtn.setPrefHeight(40);
        viewDetailsBtn.setOnAction(event -> {
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();
            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "View Error",
                        "Please select an appointment to view.");
                return;
            }
            // Display the appointment details in a pop-up dialog
            showAppointmentDetailsPopup(selectedAppointment);
        });

        Button changeStatusButton = new Button("Change Status");
        changeStatusButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        changeStatusButton.setPrefHeight(40);
        changeStatusButton.setOnAction(event -> {
            Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Status Update Error",
                        "Please select an appointment to update the status.");
                return;
            }

            if (!(String.valueOf(selectedAppointment.getStatus()).equalsIgnoreCase("PENDING") || String.valueOf(selectedAppointment.getStatus()).equalsIgnoreCase("APPROVED"))) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Status Update Error",
                        "Cannot change status for an appointment that is not in pending stage.");
                return;
            }

            // Open Change Status Dialog
            openChangeStatusDialog(selectedAppointment, table);
        });

        // Button Layout
        HBox buttonLayout = new HBox(10);

        if (user instanceof Patient)
            buttonLayout = new HBox(10, addAppointmentBtn, updateAppointmentBtn, cancelAppointment, viewDetailsBtn);
        else if (user instanceof Doctor)
            buttonLayout = new HBox(10, changeStatusButton, viewDetailsBtn);

        buttonLayout.setAlignment(Pos.CENTER_LEFT);
        buttonLayout.setPadding(new Insets(10, 0, 10, 0));

        // Final Layout for Appointments Tab
        VBox appointmentsLayout = new VBox(20);
        appointmentsLayout.getChildren().addAll(searchField, table, buttonLayout);
        appointmentsLayout.setPadding(new Insets(20, 0, 0, 0));

        return appointmentsLayout;
    }


    /**
     * Creates and returns a {@link VBox} layout for sending email notifications.
     *
     * <p>This layout includes the following components:</p>
     * <ul>
     *   <li>A title text: "Send Email Notification"</li>
     *   <li>A form to input the email details:
     *     <ul>
     *       <li>Sender's email address</li>
     *       <li>Sender's app password (for authentication)</li>
     *       <li>Recipient's email address</li>
     *       <li>Subject of the email</li>
     *       <li>Message content</li>
     *     </ul>
     *   </li>
     *   <li>A "Send Email" button to send the message via an SMTP server.</li>
     * </ul>
     *
     * <p>When the "Send Email" button is clicked, the provided email details are validated and used
     * to send an email using the JavaMail API. Appropriate alerts are displayed in the following cases:</p>
     * <ul>
     *   <li>If any input field is empty, a warning alert is shown prompting the user to fill all fields.</li>
     *   <li>If authentication fails (e.g., incorrect email or app password), a detailed error message
     *       is shown with specific instructions for setting up an app password in Gmail accounts.</li>
     *   <li>If the email fails to send due to other errors (e.g., network issues), an error alert is displayed.</li>
     *   <li>If the email is sent successfully, an information alert confirms the email was sent,
     *       including the recipient's email address.</li>
     * </ul>
     *
     * <p>This layout is styled with a simple, modern appearance, including padding, rounded form
     * fields, and shadow effects for the container.</p>
     *
     * @return A {@link VBox} layout containing the email notification form.
     */
    public VBox createEmailPage() {
        // Main container
        VBox container = new VBox(30);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_LEFT);

        Text title = new Text("Send Email Notification");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setFill(Color.BLUE);

        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setMaxWidth(500);
        form.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        // Input fields
        TextField senderEmailField = new TextField();
        senderEmailField.setPromptText("Sender Email");
        senderEmailField.setPrefHeight(40);

        PasswordField senderPasswordField = new PasswordField();
        senderPasswordField.setPromptText("App Password");
        senderPasswordField.setPrefHeight(40);

        TextField recipientField = new TextField();
        recipientField.setPromptText("Recipient Email");
        recipientField.setPrefHeight(40);

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        subjectField.setPrefHeight(40); // New field for subject

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message...");
        messageArea.setPrefHeight(150);

        Button sendBtn = new Button("Send Email");
        sendBtn.setPrefHeight(40);
        sendBtn.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");

        sendBtn.setOnAction(e -> {
            String senderEmail = senderEmailField.getText().trim();
            String senderPassword = senderPasswordField.getText().trim();
            String recipientEmail = recipientField.getText().trim();
            String subject = subjectField.getText().trim(); // Get the subject
            String message = messageArea.getText().trim();

            if (senderEmail.isEmpty() || senderPassword.isEmpty() || recipientEmail.isEmpty() || message.isEmpty()
                    || subject.isEmpty()) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all fields.");
                return;
            }

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                Message email = new MimeMessage(session);
                email.setFrom(new InternetAddress(senderEmail));
                email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                email.setSubject(subject); // Set the subject of the email
                email.setText(message);
                Transport.send(email);

                AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Success", "Email sent to: " + recipientEmail);

            } catch (AuthenticationFailedException ex) {
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Authentication Failed",
                        "It looks like your email or password is incorrect.\n\n"
                                + "If you're using Gmail, you must enable 2-Step Verification and generate an App Password.\n\n"
                                + "Steps:\n"
                                + "1. Go to your Google Account > Security\n"
                                + "2. Turn on 2-Step Verification\n"
                                + "3. Under 'Signing in to Google', choose 'App Passwords'\n"
                                + "4. Generate a password and paste it here instead of your regular password.");
            } catch (MessagingException ex) {
                ex.printStackTrace();
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Error", "Failed to send email. Please try again.");
            }
        });

        form.getChildren().addAll(senderEmailField, senderPasswordField, recipientField, subjectField, messageArea,
                sendBtn); // Add subjectField to form
        container.getChildren().addAll(title, form);

        return container;
    }

    /**
     * Creates and returns a {@link VBox} layout for uploading vitals data from a CSV file.
     *
     * <p>This interface allows the user to select a CSV file containing vitals data and upload it to the system.
     * It includes:</p>
     * <ul>
     *   <li>A title: "Upload Vitals CSV"</li>
     *   <li>A button to open a {@link FileChooser} dialog for selecting a CSV file.</li>
     * </ul>
     *
     * <p>The button triggers the following actions:</p>
     * <ol>
     *   <li>Displays a file chooser dialog to select a CSV file with an extension filter for "*.csv".</li>
     *   <li>If a file is selected, the function attempts to parse and store the vitals data using the
     *       {@code parseAndStoreVitalsCSV(File)} method.</li>
     *   <li>If the operation succeeds, an informational alert is displayed to notify the user of the successful upload.</li>
     *   <li>If any error occurs during the operation, an error alert is shown with the associated error message.</li>
     * </ol>
     *
     * <p>The layout is styled with a clean and user-friendly design, including padding and alignment adjustments.</p>
     *
     * @return A {@link VBox} layout containing the vitals upload interface.
     */
    private VBox createVitalsUploadPage() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Upload Vitals CSV");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Button uploadBtn = new Button("Select and Upload CSV");
        uploadBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        uploadBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                try {
                    parseAndStoreVitalsCSV(file);
                    AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Success", "Vitals uploaded successfully.");
                } catch (Exception ex) {
                    AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Error",
                            "Failed to upload vitals: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        container.getChildren().addAll(title, uploadBtn);
        return container;
    }

    /**
     * Parses a CSV file containing vital sign data and stores the records in the database.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Opens the provided CSV file for reading.</li>
     *   <li>Creates a database connection and prepares an SQL insert statement to store the data
     *       in the {@code vitalsign_history} table.</li>
     *   <li>Reads each line from the CSV file and splits the line into fields, which are expected to
     *       include:
     *       <ul>
     *         <li>Heart rate (integer)</li>
     *         <li>Oxygen level (integer)</li>
     *         <li>Blood pressure (string)</li>
     *         <li>Temperature (double)</li>
     *       </ul>
     *   </li>
     *   <li>Uses the logged-in user's ID as the patient identifier for each record.</li>
     *   <li>Executes the batch insert to store all valid records in the database.</li>
     *   <li>Skips invalid rows (e.g., lines with less than 4 fields).</li>
     * </ol>
     *
     * <p>If any errors occur during file parsing or database operations, the exception will be propagated
     * to the caller, allowing appropriate error handling.</p>
     *
     * @param file The CSV file containing the vital sign data.
     * @throws Exception If an unexpected error occurs while reading the file or writing to the database.
     *
     * <p><b>Database Schema:</b></p>
     * <ul>
     *   <li>Table: {@code vitalsign_history}</li>
     *   <li>Columns:
     *       <ul>
     *         <li>{@code patient_id} - The ID of the patient (from the logged-in user).</li>
     *         <li>{@code recorded_at} - The timestamp of when the data is recorded (defaults to the current time using {@code NOW()}).</li>
     *         <li>{@code heart_rate} - The heart rate of the patient (integer).</li>
     *         <li>{@code oxygen_level} - The oxygen saturation of the patient (integer).</li>
     *         <li>{@code blood_pressure} - The blood pressure of the patient (string).</li>
     *         <li>{@code temperature} - The body temperature of the patient (double).</li>
     *       </ul>
     *   </li>
     * </ul>
     */
    private void parseAndStoreVitalsCSV(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             Connection conn = DatabaseConnection.getConnection()) {

            String line;
            String sql = "INSERT INTO vitalsign_history (patient_id, recorded_at, heart_rate, oxygen_level, blood_pressure, temperature) "
                    +
                    "VALUES (?, NOW(), ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4)
                    continue; // heartRate, oxygenLevel, bloodPressure, temperature

                stmt.setString(1, user.getUserID()); // patient_id from logged-in user
                stmt.setInt(2, Integer.parseInt(fields[0])); // heart_rate
                stmt.setInt(3, Integer.parseInt(fields[1])); // oxygen_level
                stmt.setString(4, fields[2]); // blood_pressure
                stmt.setDouble(5, Double.parseDouble(fields[3])); // temperature

                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    /**
     * Creates and returns a {@link VBox} layout for viewing patient vitals data.
     *
     * <p>This interface allows doctors to select a specific patient and view their vitals
     * history both as a line chart and a tabular format. The layout includes:</p>
     * <ul>
     *   <li>A heading: "Patient Vitals"</li>
     *   <li>A dropdown ({@link ComboBox}) for selecting a patient.</li>
     *   <li>A {@link LineChart} to visualize trends for:
     *       <ul>
     *         <li>Heart Rate</li>
     *         <li>Oxygen Level</li>
     *         <li>Blood Pressure (Systolic)</li>
     *       </ul>
     *   </li>
     *   <li>A {@link TableView} displaying a detailed tabular view of the vitals history,
     *       including columns such as "Recorded At," "Heart Rate," "Oxygen Level," "Blood Pressure,"
     *       and "Temperature."</li>
     * </ul>
     *
     * <p>The following functionalities are implemented:</p>
     * <ol>
     *   <li><b>Patient Selection:</b> When a patient is selected, their vitals history is fetched from
     *       the database using {@code fetchVitalsForPatient(String userID)} and displayed in both the
     *       table and the line chart.</li>
     *   <li><b>Line Chart:</b>
     *       <ul>
     *         <li>Vital sign data is visualized as separate series plotted against the recorded timestamps.</li>
     *         <li>Unique X-axis values are created by appending an index to each timestamp to prevent overwriting overlapping records.</li>
     *         <li>Blood pressure is parsed to extract systolic values for visualization.</li>
     *       </ul>
     *   </li>
     *   <li><b>Table View:</b> Displays detailed data rows with the following columns:
     *       <ul>
     *         <li>Recorded At - The timestamp of when the vitals were recorded.</li>
     *         <li>Heart Rate - The patient's heart rate (integer).</li>
     *         <li>Oxygen Level - The patient's oxygen level (integer).</li>
     *         <li>Blood Pressure - The patient's blood pressure (e.g., "120/80").</li>
     *         <li>Temperature - The patient's body temperature (double).</li>
     *       </ul>
     *   </li>
     *   <li><b>Error Handling:</b> Potential issues such as missing timestamps or invalid data (e.g.,
     *       incorrectly formatted blood pressure) are logged for debugging.</li>
     * </ol>
     *
     * @return A {@link VBox} layout containing the vitals viewing interface.
     */
    private VBox createVitalsViewPage() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        Text heading = new Text("Patient Vitals");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setFill(Color.BLUE);

        // Patient selection dropdown
        ComboBox<Patient> patientSelector = new ComboBox<>();
        patientSelector.setPromptText("Select a patient");

        // Load patients under this doctor
        List<Patient> patients = DataFetcher.getAllPatientsForDoctor(user.getUserID());
        patientSelector.getItems().addAll(patients);

        // Formatting of dropdown
        patientSelector.setCellFactory(param -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.getName());
            }
        });

        patientSelector.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.getName());
            }
        });

        // === LineChart setup ===
        CategoryAxis xAxis = new CategoryAxis(); // Use timestamp strings
        xAxis.setLabel("Recorded At");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");

        LineChart<String, Number> vitalsChart = new LineChart<>(xAxis, yAxis);
        vitalsChart.setTitle("Vitals Trend");
        vitalsChart.setCreateSymbols(true); // Enables debugging of plotted points

        // Series for each vital sign
        XYChart.Series<String, Number> heartRateSeries = new XYChart.Series<>();
        heartRateSeries.setName("Heart Rate");

        XYChart.Series<String, Number> oxygenSeries = new XYChart.Series<>();
        oxygenSeries.setName("Oxygen Level");

        XYChart.Series<String, Number> bloodPressureSeries = new XYChart.Series<>();
        bloodPressureSeries.setName("Blood Pressure (Systolic)");

        // === Table for vitals ===
        TableView<Vitals> vitalsTable = new TableView<>();
        vitalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Vitals, String> timeCol = new TableColumn<>("Recorded At");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));

        TableColumn<Vitals, Integer> heartRateCol = new TableColumn<>("Heart Rate");
        heartRateCol.setCellValueFactory(new PropertyValueFactory<>("heartRate"));

        TableColumn<Vitals, Integer> oxygenCol = new TableColumn<>("Oxygen Level");
        oxygenCol.setCellValueFactory(new PropertyValueFactory<>("oxygenLevel"));

        TableColumn<Vitals, String> bpCol = new TableColumn<>("Blood Pressure");
        bpCol.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));

        TableColumn<Vitals, Double> tempCol = new TableColumn<>("Temperature");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        vitalsTable.getColumns().addAll(timeCol, heartRateCol, oxygenCol, bpCol, tempCol);

        // === When patient is selected ===
        patientSelector.setOnAction(e -> {
            Patient selected = patientSelector.getValue();
            if (selected != null) {
                List<Vitals> vitalsList = fetchVitalsForPatient(selected.getUserID());
                ObservableList<Vitals> vitals = FXCollections.observableArrayList(vitalsList);
                vitalsTable.setItems(vitals);

                // Clear previous chart data
                heartRateSeries.getData().clear();
                oxygenSeries.getData().clear();
                bloodPressureSeries.getData().clear();

                // Add data points to the chart
                for (int i = 0; i < vitalsList.size(); i++) {
                    Vitals v = vitalsList.get(i);

                    if (v.getRecordedAt() != null) {
                        // Make the X-axis value unique using recordedAt + index
                        String uniqueTimestamp = v.getRecordedAt() + " (" + i + ")";

                        // Add heart rate to series
                        heartRateSeries.getData().add(new XYChart.Data<>(uniqueTimestamp, v.getHeartRate()));

                        // Add oxygen level to series
                        oxygenSeries.getData().add(new XYChart.Data<>(uniqueTimestamp, v.getOxygenLevel()));

                        // Parse blood pressure and add systolic to series
                        try {
                            if (v.getBloodPressure() != null && v.getBloodPressure().contains("/")) {
                                String[] parts = v.getBloodPressure().split("/");
                                int systolic = Integer.parseInt(parts[0].trim());
                                bloodPressureSeries.getData().add(new XYChart.Data<>(uniqueTimestamp, systolic));
                            }
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid blood pressure format: " + v.getBloodPressure());
                        }
                    } else {
                        // Handle null/invalid timestamps in vitals records
                        System.err.println("Missing timestamp for vitals record: " + v.toString());
                    }
                }

                // Update the chart with the new data
                vitalsChart.getData().setAll(heartRateSeries, oxygenSeries, bloodPressureSeries);
            }
        });

        // Add components to the container
        container.getChildren().addAll(heading, patientSelector, vitalsChart, vitalsTable);
        return container;
    }

    /**
     * Fetches the vitals history for a specific patient from the database.
     *
     * <p>This method retrieves the following vital sign data for the given patient ID:</p>
     * <ul>
     *   <li>The timestamp when the vitals were recorded.</li>
     *   <li>Heart rate.</li>
     *   <li>Oxygen level.</li>
     *   <li>Blood pressure (e.g., "120/80").</li>
     *   <li>Body temperature.</li>
     * </ul>
     *
     * <p>The data is fetched from the {@code vitalsign_history} table and returned as a list of
     * {@link Vitals} objects. The results are ordered by the recorded timestamp in descending
     * order (most recent first).</p>
     *
     * <p>In case of an SQL error, the exception is logged via {@code e.printStackTrace()}, and an
     * empty list of vitals is returned.</p>
     *
     * @param patientID The ID of the patient whose vitals history is to be retrieved.
     * @return A {@link List} of {@link Vitals} objects representing the patient's vitals history.
     */
    private List<Vitals> fetchVitalsForPatient(String patientID) {
        List<Vitals> vitalsList = new ArrayList<>();
        String query = "SELECT recorded_at, heart_rate, oxygen_level, blood_pressure, temperature FROM vitalsign_history WHERE patient_id = ? ORDER BY recorded_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patientID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vitalsList.add(new Vitals(
                            rs.getTimestamp("recorded_at").toString(),
                            rs.getInt("heart_rate"),
                            rs.getInt("oxygen_level"),
                            rs.getString("blood_pressure"),
                            rs.getDouble("temperature")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vitalsList;
    }

    /**
     * Creates and returns a {@link VBox} layout for evaluating patients by a doctor.
     *
     * <p>This interface allows a doctor to:
     * <ul>
     *   <li>Select a patient from a dropdown of patients associated with their account.</li>
     *   <li>View detailed patient information in a dynamic layout.</li>
     *   <li>Provide feedback for the selected patient, which is stored in the database.</li>
     *   <li>Create and record prescriptions for the patient, including medications, dosage,
     *       and schedule, which are stored in the database.</li>
     * </ul>
     * </p>
     *
     * <h3>Key Features:</h3>
     * <ol>
     *   <li><b>Patient Selection:</b>
     *       <ul>
     *         <li>A dropdown populated with patients associated with the doctor.</li>
     *         <li>Once a patient is selected, their information (such as name, date of birth,
     *             gender, etc.) is dynamically displayed in a formatted container.</li>
     *         <li>The data is fetched using {@code DataFetcher.getPatientData()}.</li>
     *       </ul>
     *   </li>
     *   <li><b>Feedback Entry:</b>
     *       <ul>
     *         <li>A {@link TextArea} for entering feedback about the patient's condition or evaluation.</li>
     *         <li>The feedback is validated and saved to a {@code Feedback} table in the database.</li>
     *         <li>If feedback is invalid (e.g., missing), the user is notified with a warning.</li>
     *       </ul>
     *   </li>
     *   <li><b>Prescription Creation:</b>
     *       <ul>
     *         <li>Fields for entering medication name, dosage, and schedule.</li>
     *         <li>An "Add Medication" button lets doctors add multiple medications to a list.</li>
     *         <li>A {@link ListView} displays the added medications for the prescription.</li>
     *         <li>Prescriptions, along with their medications, are saved to the {@code Prescription}
     *             and {@code Prescription_detail} tables in the database.</li>
     *       </ul>
     *   </li>
     *   <li><b>Database Integration:</b>
     *       <ul>
     *         <li>Feedback is inserted into the {@code Feedback} table with the doctor and patient IDs.</li>
     *         <li>Prescriptions (and their details) are inserted into the {@code Prescription}
     *             and {@code Prescription_detail} tables, respectively.</li>
     *       </ul>
     *   </li>
     *   <li><b>Error Handling:</b>
     *       <ul>
     *         <li>Database exceptions (e.g., {@link SQLException}) are logged, and appropriate
     *             error alerts are shown to the user.</li>
     *         <li>Validation errors (e.g., missing feedback or prescription information) are
     *             reported to the user with warnings.</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * @param doctorID The ID of the doctor currently logged in, used to filter patients and
     *                 associate feedback and prescriptions with the doctor.
     * @return A {@link VBox} containing the complete patient evaluation page layout.
     */
    public VBox createEvaluationPage(String doctorID) {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-padding: 20; -fx-alignment: top-left;");

        Text heading = new Text("Patient Evaluation");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setFill(Color.BLUE);

        ComboBox<String> patientDropdown = new ComboBox<>();
        patientDropdown.setPromptText("Select a patient");

        // Fetch patient data associated with the specific doctor
        ArrayList<Patient> patientsList = DataFetcher.getAllPatientsForDoctor(doctorID);
        for (Patient patient : patientsList) {
            patientDropdown.getItems().add(patient.getUserID() + " - " + patient.getName());
        }

        VBox detailsContainer = new VBox(20); // Box to hold patient details dynamically
        detailsContainer.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #ddd; -fx-border-width: 1; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");
        detailsContainer.setPrefWidth(800); // Optional: Adjust the width to match `renderPatientContent`

        // Event to display selected patient information
        patientDropdown.setOnAction(e -> {
            String selectedValue = patientDropdown.getSelectionModel().getSelectedItem();

            if (selectedValue != null) {
                String patientId = selectedValue.split(" - ")[0];

                try {
                    Patient patient = DataFetcher.getPatientData("patient", patientId);

                    if (patient != null) {
                        // Clear previous details
                        detailsContainer.getChildren().clear();

                        // Add sections for patient information (Left & Right Infos)
                        VBox leftInfo = new VBox(10,
                                createInfoRow("User ID:", patient.getUserID()),
                                createInfoRow("Name:", patient.getName()),
                                createInfoRow("Date of Birth:", String.valueOf(patient.getDateOfBirth())),
                                createInfoRow("Gender:", patient.getGender()));
                        leftInfo.setStyle("-fx-padding: 10;");

                        VBox rightInfo = new VBox(10,
                                createInfoRow("Email:", patient.getEmail()),
                                createInfoRow("Phone:", patient.getPhone()),
                                createInfoRow("Address:", patient.getAddress()),
                                createInfoRow("Is Admitted:", patient.isAdmit() ? "Yes" : "No"));
                        rightInfo.setStyle("-fx-padding: 10;");

                        // Combine left and right sections
                        HBox infoSection = new HBox(40, leftInfo, rightInfo);
                        infoSection.setAlignment(Pos.TOP_LEFT);

                        // Add all sections to the container
                        detailsContainer.getChildren().addAll(infoSection);
                    } else {
                        detailsContainer.getChildren().clear();
                        detailsContainer.getChildren().add(new Label("No details available."));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    detailsContainer.getChildren().clear();
                    detailsContainer.getChildren().add(new Label("Error fetching patient data."));
                }
            }
        });

        TextArea feedbackArea = new TextArea();
        feedbackArea.setPromptText("Enter feedback for the patient...");
        feedbackArea.setPrefHeight(100);

        VBox prescriptionContainer = new VBox(10);
        prescriptionContainer.setPadding(new Insets(10));
        prescriptionContainer.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-background-radius: 5px;");

        TextField medicationField = new TextField();
        medicationField.setPromptText("Medication Name");

        TextField dosageField = new TextField();
        dosageField.setPromptText("Dosage (e.g., 500mg)");

        TextField scheduleField = new TextField();
        scheduleField.setPromptText("Schedule (e.g., Morning, Evening)");

        Button addMedicationButton = new Button("Add Medication");
        addMedicationButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        ArrayList<String> medications = new ArrayList<>();
        ArrayList<String> dosages = new ArrayList<>();
        ArrayList<String> schedules = new ArrayList<>();

        ListView<String> prescriptionList = new ListView<>();
        prescriptionList.setPrefHeight(150);

        addMedicationButton.setOnAction(e -> {
            String medication = medicationField.getText();
            String dosage = dosageField.getText();
            String schedule = scheduleField.getText();
            if (!medication.isEmpty() && !dosage.isEmpty() && !schedule.isEmpty()) {
                String entry = medication + " - " + dosage + " - " + schedule;
                prescriptionList.getItems().add(entry);

                medications.add(medication);
                dosages.add(dosage);
                schedules.add(schedule);

                medicationField.clear();
                dosageField.clear();
                scheduleField.clear();
            } else {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Please fill out all medication details before adding.");
            }
        });

        Button submitButton = new Button("Submit Evaluation");
        submitButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");

        submitButton.setOnAction(e -> {
            String selectedValue = patientDropdown.getSelectionModel().getSelectedItem();
            if (selectedValue == null || feedbackArea.getText().isEmpty()) {
                AlertDialogueBox.showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Please select a patient and provide feedback.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String patientId = selectedValue.split(" - ")[0];

                // Save feedback to the database
                String feedbackMessage = feedbackArea.getText();
                String feedbackInsertQuery = "INSERT INTO Feedback (patient_id, doctor_id, feedback_message) " +
                        "VALUES (?, ?, ?)";
                PreparedStatement feedbackStmt = conn.prepareStatement(feedbackInsertQuery, Statement.RETURN_GENERATED_KEYS);
                feedbackStmt.setString(1, patientId);
                feedbackStmt.setString(2, doctorID);
                feedbackStmt.setString(3, feedbackMessage);
                feedbackStmt.executeUpdate();

                ResultSet feedbackGeneratedKeys = feedbackStmt.getGeneratedKeys();
                int feedbackId = -1;
                if (feedbackGeneratedKeys.next()) {
                    feedbackId = feedbackGeneratedKeys.getInt(1);
                }

                // Create Feedback object
                Patient selectedPatient = DataFetcher.getPatientData("patient", patientId);
                Doctor currentDoctor = (Doctor) user;
                Feedback feedback = new Feedback(selectedPatient, currentDoctor, feedbackMessage);

                // Save prescription to the database (if medications exist)
                if (!medications.isEmpty()) {
                    String prescriptionInsertQuery = "INSERT INTO Prescription (patient_id, doctor_id) VALUES (?, ?)";
                    PreparedStatement prescriptionStmt = conn.prepareStatement(prescriptionInsertQuery,
                            Statement.RETURN_GENERATED_KEYS);
                    prescriptionStmt.setString(1, patientId);
                    prescriptionStmt.setString(2, doctorID);
                    prescriptionStmt.executeUpdate();

                    ResultSet prescriptionGeneratedKeys = prescriptionStmt.getGeneratedKeys();
                    int prescriptionId = -1;
                    if (prescriptionGeneratedKeys.next()) {
                        prescriptionId = prescriptionGeneratedKeys.getInt(1);
                    }

                    // Add prescription details
                    String prescriptionDetailInsertQuery =
                            "INSERT INTO Prescription_detail (prescription_id, medication, dosage, schedule) VALUES (?, ?, ?, ?)";
                    PreparedStatement prescriptionDetailStmt = conn.prepareStatement(prescriptionDetailInsertQuery);

                    for (int i = 0; i < medications.size(); i++) {
                        prescriptionDetailStmt.setInt(1, prescriptionId);
                        prescriptionDetailStmt.setString(2, medications.get(i));
                        prescriptionDetailStmt.setString(3, dosages.get(i));
                        prescriptionDetailStmt.setString(4, schedules.get(i));
                        prescriptionDetailStmt.addBatch();
                    }
                    prescriptionDetailStmt.executeBatch();

                    // Create Prescription object
                    Prescription prescription = new Prescription(selectedPatient, currentDoctor);
                    for (int i = 0; i < medications.size(); i++) {
                        prescription.addMedication(medications.get(i), dosages.get(i), schedules.get(i));
                    }
                }

                AlertDialogueBox.showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Feedback and prescription recorded successfully.");

                // Reset fields
                feedbackArea.clear();
                prescriptionList.getItems().clear();
                medications.clear();
                dosages.clear();
                schedules.clear();

            } catch (SQLException ex) {
                ex.printStackTrace();
                AlertDialogueBox.showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Failed to save evaluation data. Please try again.");
            }
        });

        prescriptionContainer.getChildren().addAll(
                new HBox(10, medicationField, dosageField, scheduleField, addMedicationButton),
                prescriptionList
        );


        mainLayout.getChildren().addAll(heading, patientDropdown, detailsContainer, feedbackArea, prescriptionContainer,
                submitButton);

        return mainLayout;
    }

    /**
     * Creates and returns a {@link VBox} layout for displaying a treatment summary page for a specific patient.
     *
     * <p>This interface provides the following functionalities for the treatment summary:</p>
     * <ul>
     *   <li>Selecting a doctor from a dropdown list of all doctors who have interacted with the patient.</li>
     *   <li>Displaying feedback from the selected doctor about the patient's treatment.</li>
     *   <li>Displaying the prescription history provided by the selected doctor.</li>
     * </ul>
     *
     * <h3>Key Features:</h3>
     * <ol>
     *   <li><b>Doctor Selection:</b>
     *       <ul>
     *         <li>A dropdown ({@link ComboBox}) populated with the list of doctors who treated the patient.</li>
     *         <li>If a doctor is selected, feedback and prescriptions specific to that doctor and patient are fetched and displayed.</li>
     *         <li>Doctor data is fetched using {@code DataFetcher.getAllDoctorsForPatient()}.</li>
     *       </ul>
     *   </li>
     *   <li><b>Feedback Display:</b>
     *       <ul>
     *         <li>A container dynamically updates with feedback messages fetched from the {@code Feedback} database table.</li>
     *         <li>Feedback is queried specifically for the selected doctor and patient.</li>
     *         <li>If feedback cannot be loaded, an error message is displayed.</li>
     *       </ul>
     *   </li>
     *   <li><b>Prescription Display:</b>
     *       <ul>
     *         <li>A dynamically updated container displays the prescription history for the patient, specific to the selected doctor.</li>
     *         <li>Prescriptions are fetched from the {@code Prescription} and {@code Prescription_detail} tables.</li>
     *         <li>Includes detailed information such as medication name, dosage, and schedule.</li>
     *         <li>If prescription data cannot be loaded, an error message is displayed.</li>
     *       </ul>
     *   </li>
     *   <li><b>Error Handling:</b>
     *       <ul>
     *         <li>SQL exceptions are logged using {@code ex.printStackTrace()}.</li>
     *         <li>In cases of loading failures, error messages are displayed in the corresponding containers for feedback or prescriptions.</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * @param patientID The unique ID of the patient whose treatment summary is being displayed.
     * @return A {@link VBox} object containing the complete treatment summary page layout.
     */
    public VBox createTreatmentSummaryPage(String patientID) {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-padding: 20; -fx-alignment: top-left;");

        // Heading
        Text heading = new Text("Treatment Summary");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setFill(Color.BLUE);

        // Doctor Dropdown
        ComboBox<String> doctorDropdown = new ComboBox<>();
        doctorDropdown.setPromptText("Select a Doctor");

        // Fetch all doctors who have interacted with this patient
        ArrayList<Doctor> doctorsList = DataFetcher.getAllDoctorsForPatient(patientID);
        for (Doctor doctor : doctorsList) {
            doctorDropdown.getItems().add(doctor.getUserID() + " - " + doctor.getName());
        }

        // Feedback Section
        VBox feedbackContainer = new VBox(10);
        feedbackContainer.setStyle("-fx-background-color: white; -fx-border-color: #ddd; "
                + "-fx-border-width: 1; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");
        feedbackContainer.setPadding(new Insets(15));

        Label feedbackHeading = new Label("Doctor Feedback");
        feedbackHeading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        feedbackContainer.getChildren().add(feedbackHeading);

        ListView<String> feedbackList = new ListView<>();
        feedbackList.setPrefHeight(150);
        feedbackContainer.getChildren().add(feedbackList);

        // Prescription Section
        VBox prescriptionContainer = new VBox(10);
        prescriptionContainer.setStyle("-fx-background-color: white; -fx-border-color: #ddd; "
                + "-fx-border-width: 1; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 2);");
        prescriptionContainer.setPadding(new Insets(15));

        Label prescriptionHeading = new Label("Prescription History");
        prescriptionHeading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        prescriptionContainer.getChildren().add(prescriptionHeading);

        ListView<String> prescriptionList = new ListView<>();
        prescriptionList.setPrefHeight(150);
        prescriptionContainer.getChildren().add(prescriptionList);

        // Fetch and populate feedback and prescriptions based on doctor selection
        doctorDropdown.setOnAction(e -> {
            String selectedValue = doctorDropdown.getSelectionModel().getSelectedItem();
            if (selectedValue != null) {
                String doctorId = selectedValue.split(" - ")[0];

                // Clear previous data
                feedbackList.getItems().clear();
                prescriptionList.getItems().clear();

                try (Connection conn = DatabaseConnection.getConnection()) {
                    // Fetch and display feedback
                    String feedbackQuery = "SELECT feedback_message FROM Feedback WHERE patient_id = ? AND doctor_id = ?";
                    PreparedStatement feedbackStmt = conn.prepareStatement(feedbackQuery);
                    feedbackStmt.setString(1, patientID);
                    feedbackStmt.setString(2, doctorId);
                    ResultSet feedbackResults = feedbackStmt.executeQuery();

                    while (feedbackResults.next()) {
                        feedbackList.getItems().add(feedbackResults.getString("feedback_message"));
                    }

                    // Fetch and display prescriptions
                    String prescriptionQuery = "SELECT pd.medication, pd.dosage, pd.schedule " +
                            "FROM Prescription p " +
                            "JOIN Prescription_detail pd ON p.prescription_id = pd.prescription_id " +
                            "WHERE p.patient_id = ? AND p.doctor_id = ?";
                    PreparedStatement prescriptionStmt = conn.prepareStatement(prescriptionQuery);
                    prescriptionStmt.setString(1, patientID);
                    prescriptionStmt.setString(2, doctorId);
                    ResultSet prescriptionResults = prescriptionStmt.executeQuery();

                    while (prescriptionResults.next()) {
                        String medication = prescriptionResults.getString("medication");
                        String dosage = prescriptionResults.getString("dosage");
                        String schedule = prescriptionResults.getString("schedule");
                        prescriptionList.getItems().add(medication + " - " + dosage + " - " + schedule);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    feedbackList.getItems().add("Error loading feedback.");
                    prescriptionList.getItems().add("Error loading prescriptions.");
                }
            }
        });

        // Add all components to the main layout
        mainLayout.getChildren().addAll(heading, doctorDropdown, feedbackContainer, prescriptionContainer);

        return mainLayout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}