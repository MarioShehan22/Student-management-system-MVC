package com.shehan.edumanage.controller;

import com.shehan.edumanage.db.DbConnection;
import com.shehan.edumanage.model.Intake;
import com.shehan.edumanage.model.Program;
import com.shehan.edumanage.model.Teacher;
import com.shehan.edumanage.view.tm.IntakeTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IntakeFormController {

    public AnchorPane context;
    public TextField txtId;
    public TextField txtSearch;
    public Button btn;
    public TableView<IntakeTm> tblIntakes;
    public TableColumn colId;
    public TableColumn colIntake;
    public TableColumn colStartDate;
    public TableColumn colProgram;
    public TableColumn colCompleteState;
    public TableColumn colOption;
    public TextField txtName;
    public DatePicker txtDate;
    public ComboBox<String> cmbProgram;

    ArrayList<String> intakeArray = new ArrayList<>();
    public void initialize() {
        setIntakes();
        setIntakeCode();
        colId.setCellValueFactory(new PropertyValueFactory<>("code1"));
        colIntake.setCellValueFactory(new PropertyValueFactory<>("intakeName"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("programs"));
        colCompleteState.setCellValueFactory(new PropertyValueFactory<>("completeState"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }
    private void setIntakes() {
        try{
            for (Program t : ProgramDb()) {
                intakeArray.add( t.getName());
            }
            ObservableList<String> obList = FXCollections.observableArrayList(intakeArray);
            cmbProgram.setItems(obList);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private void setIntakeCode() {
        try{
            String lastId = getLastIntakeId();
            if (lastId == null ) {
                txtId.setText("I-1");
            } else {
                String[] splitData = lastId.split("-");
                String lastIdIntegerNumberAsAString = splitData[1];
                int lastIntegerIdAsInt = Integer.parseInt(lastIdIntegerNumberAsAString);
                lastIntegerIdAsInt++;
                String generatedStudentId = "P-" + lastIntegerIdAsInt;
                txtId.setText(generatedStudentId);
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public void newIntakeOnAction(ActionEvent actionEvent) {

    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm");
    }

    public void saveOnAction(ActionEvent actionEvent) {

    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
    private List<Program> ProgramDb() throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM lms.program WHERE name <> ''");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Program> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new Program(
                            resultSet.getString(1)
                    )
            );
        }
        return list;
    }
    private String getLastIntakeId() throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT intake_id FROM intake ORDER BY CAST(SUBSTRING(code,3) AS UNSIGNED ) DESC LIMIT 1");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }
}
