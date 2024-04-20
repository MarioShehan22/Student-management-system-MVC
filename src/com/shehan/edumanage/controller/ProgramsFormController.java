package com.shehan.edumanage.controller;

import com.shehan.edumanage.db.Database;
import com.shehan.edumanage.db.DbConnection;
import com.shehan.edumanage.model.Program;
import com.shehan.edumanage.model.Student;
import com.shehan.edumanage.model.Teacher;
import com.shehan.edumanage.view.tm.ProgramTm;
import com.shehan.edumanage.view.tm.TechAddTm;
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
import java.util.Optional;

public class ProgramsFormController {

    public AnchorPane context;

    public TextField txtId;

    public TextField txtName;

    public TextField txtSearch;

    public Button btn;

    public TableView<ProgramTm> tblPrograms;

    public TableColumn<?, ?> colId;

    public TableColumn<?, ?> colName;

    public TableColumn<?, ?> colTeacher;

    public TableColumn<?, ?> colTech;

    public TableColumn<?, ?> colCost;

    public TableColumn<?, ?> colOption;

    public TextField txtCost;

    public ComboBox<String> cmbTeacher;

    public TextField txtTechnology;

    public TableView<TechAddTm> tblTechnologies;

    public TableColumn<?, ?> colTCode;

    public TableColumn<?, ?> colTName;

    public TableColumn<?, ?> colTRemove;

    public void initialize() {
        setProgramCode();
        setTeachers();
        loadPrograms();

        colTCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTRemove.setCellValueFactory(new PropertyValueFactory<>("btn"));

        colId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        colTech.setCellValueFactory(new PropertyValueFactory<>("btnTech"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

    }

    ArrayList<String> teachersArray = new ArrayList<>();
    private void setTeachers() {
        try{
            for (Teacher t : TeacherDb()) {
                teachersArray.add(t.getCode() + ". " + t.getName());
            }
            ObservableList<String> obList = FXCollections.observableArrayList(teachersArray);
            cmbTeacher.setItems(obList);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void setProgramCode() {
        try{
            String lastId = getLastProgramId();
            if (lastId == null ) {
                txtId.setText("P-1");
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


    public void backToHomeOnAction(ActionEvent event) throws IOException {
        setUi("DashboardForm");
    }


    public void newProgramOnAction(ActionEvent event) {
        clear();
        setProgramCode();
        btn.setText("Save Program");
    }
    private void clear() {
        txtId.clear();
        //txtName.setText("");
        txtName.clear();
        txtSearch.clear();
    }

    ObservableList<TechAddTm> tmList = FXCollections.observableArrayList();

    public void saveOnAction(ActionEvent event) {
        //<=Capture the technology list over the technology table
        String[] selectedTechs = new String[tmList.size()];
        int pointer = 0;
        for (TechAddTm t : tmList) {
            selectedTechs[pointer] = t.getName();
            pointer++;
        }
        //<=
        Program program = new Program(
                txtId.getText(),
                txtName.getText(),
                selectedTechs,
                cmbTeacher.getValue().split("\\.")[0],
                Double.parseDouble(txtCost.getText())
        );
        if (btn.getText().equalsIgnoreCase("Save Program")) {
            try {
                if (savePrograms(program)) {
                    setProgramCode();
                    clear();
                    loadPrograms();
                    new Alert(Alert.AlertType.INFORMATION, "Program saved!").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }

        } else {
            try {
                if (updateProgram(program)) {
                    clear();
                    loadPrograms();
                    new Alert(Alert.AlertType.INFORMATION, "Program Updated!").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }
        }
    }

    private void loadPrograms() {
        ObservableList<ProgramTm> programsTmList = FXCollections.observableArrayList();
        try{
            for (Program p : readPrograms()) {
                Button techButton = new Button("show Tech");
                Button removeButton = new Button("Delete");
                ProgramTm tm = new ProgramTm(
                        p.getCode(),
                        p.getName(),
                        p.getTeacherId(),
                        techButton,
                        p.getCost(),
                        removeButton
                );
                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get().equals(ButtonType.YES)) {

                        try {
                            if(deleteProgram(p.getCode())){
                                new Alert(Alert.AlertType.INFORMATION, "Deleted!").show();
                                setProgramCode();
                            }else{
                                new Alert(Alert.AlertType.WARNING, "Try Again!").show();
                            }
                        } catch (ClassNotFoundException | SQLException ex) {
                            new Alert(Alert.AlertType.ERROR, e.toString()).show();
                        }

                    }
                });
                programsTmList.add(tm);
            }
        }catch (SQLException| ClassNotFoundException e){
            e.printStackTrace();
        }
        tblPrograms.setItems(programsTmList);
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/" + location + ".fxml"))));
        stage.centerOnScreen();
    }


    public void addTechOnAction(ActionEvent actionEvent) {
        if (!isExists(txtTechnology.getText().trim())) {
            Button btn = new Button("Remove");
            TechAddTm tm = new TechAddTm(
                    tmList.size() + 1, txtTechnology.getText().trim(), btn
            );
            tmList.add(tm);
            tblTechnologies.setItems(tmList);
            txtTechnology.clear();
        } else {
            txtTechnology.selectAll();
            new Alert(Alert.AlertType.WARNING, "Already Exists").show();
        }
    }

    private boolean isExists(String tech) {
        for (TechAddTm tm : tmList) {
            if (tm.getName().equals(tech)) {
                return true;
            }
        }
        return false;
    }
    private boolean savePrograms(Program program) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO program VALUES(?,?,?,?,?)");
        preparedStatement.setString(1, program.getCode());
        preparedStatement.setString(2, program.getName());
        preparedStatement.setObject(3, String.join(",", program.getTechnologies()));
        preparedStatement.setString(4, program.getTeacherId());
        preparedStatement.setDouble(5, program.getCost());
        return preparedStatement.executeUpdate() > 0;
    }
    private String getLastProgramId() throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT code FROM program ORDER BY CAST(SUBSTRING(code,3) AS UNSIGNED ) DESC LIMIT 1");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private boolean deleteProgram(String id) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("DELETE FROM program WHERE student_Id=?");
        preparedStatement.setString(1,id);
        return preparedStatement.executeUpdate()>0;
    }

    private boolean updateProgram(Program program) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("UPDATE program SET code=?, name=?, technologies=?,teacherId=?,cost=? WHERE program_Id=?");
        preparedStatement.setString(1, program.getCode());
        preparedStatement.setObject(2, program.getName());
        preparedStatement.setObject(3, program.getTechnologies());
        preparedStatement.setDouble(4, program.getCost());
        preparedStatement.setString(5, program.getTeacherId());

        return preparedStatement.executeUpdate() > 0;
    }
    private ObservableList<Program> readPrograms() throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM program");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Program> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new Program(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString("technologies").split(","),
                            resultSet.getString(4),
                            resultSet.getDouble(5)
                    )
            );
        }
        return FXCollections.observableArrayList(list);
    }
    private List<Teacher> TeacherDb() throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT code, name FROM lms.teacher WHERE code <> '' AND name <> ''");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Teacher> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new Teacher(
                            resultSet.getString(1),
                            resultSet.getString(2)
                    )
            );
        }
        return list;
    }
}
