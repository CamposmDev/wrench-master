package controller;

import app.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import model.database.DB;
import model.ui.FX;
import model.work_order.WorkOrder;

import java.sql.Date;

public class WorkOrderTableController {
    @FXML
    TextField tfFirstName, tfLastName, tfCompanyName;
    @FXML
    ComboBox<String> cbDateCreated;
    @FXML
    DatePicker dateCreatedPicker;
    @FXML
    TableView<WorkOrder> tv;
    @FXML
    TableColumn<WorkOrder, Integer> colId;
    @FXML
    TableColumn<WorkOrder, String> colCustomer;
    @FXML
    TableColumn<WorkOrder, String> colCompany;
    @FXML
    TableColumn<WorkOrder, String> colVehicle;
    @FXML
    TableColumn<WorkOrder, String> colDateCreated;
    @FXML
    TableColumn<WorkOrder, String> colDateCompleted;
    @FXML
    TableColumn<WorkOrder, String> colInvoiceTotal;

    public WorkOrderTableController() {
        Platform.runLater(() -> {
            tfFirstName.textProperty().addListener((o, oldText, newText) -> { // TODO

            });
            tfLastName.textProperty().addListener((o, oldText, newText) -> { // TODO

            });
            tfCompanyName.textProperty().addListener((o, oldText, newText) -> { // TODO

            });
            cbDateCreated.setItems(FXCollections.observableArrayList("Exactly", "Before", "After"));
            cbDateCreated.setValue("Exactly");
            dateCreatedPicker.setOnAction(e -> { // TODO
                Date date = Date.valueOf(dateCreatedPicker.getValue());
                System.out.println(cbDateCreated.getValue() + " " + date);

            });
            colId.setCellValueFactory(c -> c.getValue().idProperty());
            colCustomer.setCellValueFactory(c -> c.getValue().getCustomer().nameProperty());
            colCompany.setCellValueFactory(c -> c.getValue().getCustomer().companyProperty());
            colVehicle.setCellValueFactory(c -> c.getValue().vehicleProperty());
            colDateCreated.setCellValueFactory(c -> c.getValue().dateCreatedProperty());
            colDateCompleted.setCellValueFactory(c -> c.getValue().dateCompletedProperty());
            colInvoiceTotal.setCellValueFactory(c -> c.getValue().billProperty());
            tv.getItems().setAll(DB.get().workOrders().getAll());
            tv.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
                    editWorkOrder();
                }
            });
            FX.autoResizeColumns(tv);
        });
    }

    public void editWorkOrder() {
        WorkOrder workOrder = tv.getSelectionModel().getSelectedItem();
        if (workOrder != null) {
            WorkOrderWorkspaceController controller = new WorkOrderWorkspaceController(workOrder);
            Parent node = FX.view("Work_Order_Workspace.fxml", controller);
            App.setDisplay(node);
        }
    }

    public void deleteWorkOrder() {
        WorkOrder workOrder = tv.getSelectionModel().getSelectedItem();
        if (workOrder != null) {
            tv.getItems().remove(workOrder);
            DB.get().workOrders().deleteById(workOrder);
        }
    }
}
