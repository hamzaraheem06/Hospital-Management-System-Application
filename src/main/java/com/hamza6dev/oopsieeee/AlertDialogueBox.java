package com.hamza6dev.oopsieeee;

import javafx.scene.control.Alert;


public class AlertDialogueBox {
    /**
     * Displays an alert dialog box with the specified type, title, and message.
     *
     * @param alertType The type of alert to display (e.g., INFORMATION, WARNING, ERROR, CONFIRMATION).
     * @param title     The title of the alert dialog box.
     * @param message   The message to display in the content area of the alert dialog box.
     *
     * @see javafx.scene.control.Alert.AlertType
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
    }
}
