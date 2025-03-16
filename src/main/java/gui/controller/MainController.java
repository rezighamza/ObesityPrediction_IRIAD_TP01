package gui.controller;

import client.ClientService;
import common.InterfaceRMI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.rmi.Naming;

public class MainController {

    @FXML
    private Button uploadCsvButton;

    @FXML
    private Button trainModelButton;

    @FXML
    private Button predictButton;

    @FXML
    private TextArea logArea;

    private ClientService clientService;
    private InterfaceRMI stub;

    @FXML
    public void initialize() {
        connectToServer();

        uploadCsvButton.setOnAction(event -> uploadCsv());
        trainModelButton.setOnAction(event -> trainModel());
        predictButton.setOnAction(event -> predictObesity());
    }

    private void connectToServer() {
        try {
            stub = (InterfaceRMI) Naming.lookup("rmi://localhost/ObesityPrediction");
            clientService = new ClientService(stub);
            logArea.appendText("Connected to server.\n");
        } catch (Exception e) {
            logArea.appendText("Failed to connect to server.\n");
            e.printStackTrace();
        }
    }

    private void uploadCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                clientService.ajouterPatientsCSV(file.getAbsolutePath());
                logArea.appendText("CSV uploaded successfully: " + file.getName() + "\n");
            } catch (Exception e) {
                logArea.appendText("Error uploading CSV.\n");
                e.printStackTrace();
            }
        }
    }

    private void trainModel() {
        try {
            String status = stub.getServerStatus();
            if (status.contains("Modèle entraîné: true")) {
                logArea.appendText("Model already trained.\n");
            } else {
                logArea.appendText("Training will start automatically after 5 patients.\n");
            }
            logArea.appendText(status + "\n");
        } catch (Exception e) {
            logArea.appendText("Failed to check server status.\n");
            e.printStackTrace();
        }
    }

    private void predictObesity() {
        try {
            clientService.predirePatient();
        } catch (Exception e) {
            logArea.appendText("Error predicting obesity.\n");
            e.printStackTrace();
        }
    }
}
