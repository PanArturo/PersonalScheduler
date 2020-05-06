/**
 * Oscar Bedolla
 * Charles Bickham - UI Team
 * Natalie Dinh - UI Team
 * Markus Hernandez - Coding Team (Head Honcho)
 * Christopher Leung - JSON Team
 * Arturo Pan Loo - UI Team
 * Adam VanRiper - JSON Team
 */

package ui;

import core.*;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Set;


public class Main extends Application {

    //Arturo's Code
    // DayFormatScene
    private BorderPane bpDay = new BorderPane();
    private VBox bpDayTopSection = new VBox();
    private ScrollPane bpDayMidSection = new ScrollPane();
    private GridPane bpDayNestedMidSection = new GridPane();
    private HBox bpDayBottomSection = new HBox();
    private Label dayDailyTasks = new Label();  // set of Task display
    private Label dayDailyDateString = new Label(); // weekDay display i.e. Sunday, Monday, ...

    // WeekFormatScene
    private BorderPane bpWeek = new BorderPane();
    private VBox bpWeekTopSection = new VBox();
    private ScrollPane bpWeekMidSection = new ScrollPane();
    private GridPane bpWeekNestedMidSection = new GridPane();
    private HBox bpWeekBottomSection = new HBox();
    private Label[] weekDailyTasks = new Label[7];

    // MonthFormatScene
    private BorderPane bpMonth = new BorderPane();
    private VBox bpMonthTopSection = new VBox();
    private ScrollPane bpMonthMidSection = new ScrollPane();
    private GridPane bpMonthNestedMidSection = new GridPane();
    private HBox bpMonthBottomSection = new HBox();
    private Label[] monthDailyTasks = new Label[7];

    private LocalDate today = LocalDate.now();
    private Date currentSelectionDateWeek = new Date(today.getMonthValue(), today.getDayOfMonth(), today.getYear());
    private Date currentSelectionDateMonth = new Date(today.getMonthValue(), 1, today.getYear());
    private Date currentSelectionDateDay = new Date(today.getMonthValue(), today.getDayOfMonth(), today.getYear());

    private Label topDate = new Label();

    Stage primaryStage;
    File curFilePath;
    private MenuBar menuBar;
    private DatePicker startDatePicker = new DatePicker(LocalDate.now());
    private DatePicker endDatePicker = new DatePicker(LocalDate.now());

    private Scene openScene, scheduleScene, addTaskScene, deleteTaskScene;
    private TextField nameField = new TextField();
    private TextField startField = new TextField();
    private TextField durationField = new TextField();
    private ChoiceBox<String> taskTypeBox = new ChoiceBox<>();
    private ChoiceBox<String> transValidCatBox = new ChoiceBox<>();
    private ChoiceBox<String> recurringValidCatBox = new ChoiceBox<>();
    private ChoiceBox<String> freqChoice = new ChoiceBox<>();
    private BorderPane bp = new BorderPane();
    private Button viewSchedule, addTask, deleteTask;
    private Button addButton = new Button("Add Task");
    private Button cancelButton = new Button("Cancel");
    private Button deleteCancelButton = new Button("Cancel");
    private Button deleteButton = new Button("Delete Task");

    private Label startDateLabel = new Label("Start date of Task:");
    private Label endDateLabel = new Label("End date of Task:");
    private Label typeTaskLabel = new Label("Task Type:");
    private Label nameLabel = new Label("Name of Task:");
    private Label categoryLabel = new Label("Category:");
    private Label dayLabel = new Label("Day of Task:");
    private Label monthLabel = new Label("Month of Task:");
    private Label yearLabel = new Label("Year of Task:");
    private Label startLabel = new Label("Starting Time:");
    private Label durationLabel = new Label("Duration:");
    private Label freqLabel = new Label("Frequency of Task: ");


    private GridPane gp = new GridPane();
    private GridPane deleteGP = new GridPane();
    private Schedule scheduleObj = new Schedule();
    private ImportJson importJson = new ImportJson();
    private ExportJson exportJson = new ExportJson();

    private ChoiceBox<String> nameOptions = new ChoiceBox<>();
    private ChoiceBox<String> activeCategoryBox = new ChoiceBox<>();
    private String taskCategory;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("PSS");
        taskTypeBox.getItems().addAll("Recurring", "Transient", "Anti Task");
        freqChoice.getItems().addAll("Daily", "Weekly", "Monthly");
        freqChoice.getSelectionModel().selectFirst();
        transValidCatBox.getItems().addAll("Visit", "Shopping", "Appointment");
        recurringValidCatBox.getItems().addAll("Class", "Study", "Sleep", "Exercise",
                                                "Work", "Meal");
        taskTypeBox.getSelectionModel().selectFirst();

        viewSchedule = new Button(" View Schedule");
        addTask = new Button("Add Task");
        deleteTask = new Button("Delete Task");

        menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem importFunction = new MenuItem("Import Schedule");
        MenuItem saveSchedule = new MenuItem("Save Schedule");
        MenuItem exitButton = new MenuItem("Exit");

        Menu viewMenu = new Menu("View");
        MenuItem mainMenu = new MenuItem("Main Menu");
        MenuItem schedule = new MenuItem("Schedule");

        Menu editMenu = new Menu("Edit");
        MenuItem addTaskMenu = new MenuItem("Add Task");
        MenuItem deleteTaskMenu = new MenuItem("Delete Task");

        fileMenu.getItems().addAll(importFunction,saveSchedule, exitButton);
        viewMenu.getItems().addAll(mainMenu, schedule);
        editMenu.getItems().addAll(addTaskMenu, deleteTaskMenu);
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);

        buildOpenScene();
        openScene = new Scene(bp, 500, 700);

        //Builds scene for adding tasks
        buildAddTaskScene();
        gp.setHgap(10);
        gp.setVgap(50);
        gp.setAlignment(Pos.CENTER);
        HBox buttonBox = new HBox(25, addButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(30, gp,buttonBox);
        vbox.setAlignment(Pos.CENTER);
        addTaskScene = new Scene(vbox, 500, 700);

        //Builds scene for deleting tasks
        buildDeleteScene();
        HBox deleteButtonBox = new HBox(25, deleteButton, deleteCancelButton);
        deleteGP.setHgap(10);
        deleteGP.setVgap(50);
        deleteGP.setAlignment(Pos.CENTER);
        deleteButtonBox.setAlignment(Pos.CENTER);
        VBox deleteVbox = new VBox(30, deleteGP, deleteButtonBox);
        deleteVbox.setAlignment(Pos.CENTER);
        deleteTaskScene = new Scene(deleteVbox, 500, 700);

        activeCategoryBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if((int) t1 > -1) {
                    taskCategory = activeCategoryBox.getItems().get((int) t1);
                    nameOptions.getItems().clear();
                    for (Task task : scheduleObj.getTasksByCategory(taskCategory))
                        nameOptions.getItems().add(task.getTaskName());
                    nameOptions.getSelectionModel().selectFirst();
                }
            }
        });

        taskTypeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(primaryStage.getScene() == addTaskScene)
                    buildAddTaskScene();
                else
                    buildDeleteScene();
            }
        });

        importFunction.setOnAction(actionEvent -> {
            try {
                openFile();
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        });
        saveSchedule.setOnAction(actionEvent ->{
            try {
                saveFile();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        });

        addTask.setOnAction(actionEvent -> {
            clearTextFields();
            buildAddTaskScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(addTaskScene);
        });
        addTaskMenu.setOnAction(actionEvent -> {
            clearTextFields();
            buildAddTaskScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(addTaskScene);
        });

        deleteTaskMenu.setOnAction(actionEvent -> {
            buildDeleteScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(deleteTaskScene);
            nameOptions.getItems().clear();
            activeCategoryBox.getItems().clear();
            checkActiveTasks();
            if(activeCategoryBox.getItems().isEmpty()) {
                if(bp.getCenter() == bpWeekMidSection || bp.getCenter() == bpDayMidSection ||
                        bp.getCenter() == bpMonthMidSection) {
                    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                    primaryStage.setWidth(primaryScreenBounds.getWidth());
                    primaryStage.setHeight(primaryScreenBounds.getHeight());
                    primaryStage.centerOnScreen();
                }
                primaryStage.setScene(openScene);
            }
        });
        deleteTask.setOnAction(actionEvent -> {
            buildDeleteScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(deleteTaskScene);
            nameOptions.getItems().clear();
            activeCategoryBox.getItems().clear();
            checkActiveTasks();
            if(activeCategoryBox.getItems().isEmpty()) {
                if(bp.getCenter() == bpWeekMidSection || bp.getCenter() == bpDayMidSection ||
                        bp.getCenter() == bpMonthMidSection) {
                    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                    primaryStage.setWidth(primaryScreenBounds.getWidth());
                    primaryStage.setHeight(primaryScreenBounds.getHeight());
                    primaryStage.centerOnScreen();
                }
                primaryStage.setScene(openScene);
            }
        });
        deleteButton.setOnAction(actionEvent -> {
            try {
                Task selectedTask = scheduleObj.getTask(nameOptions.getValue());
                if (activeCategoryBox.getValue().equals("Visit") || activeCategoryBox.getValue().equals("Shopping") ||
                        activeCategoryBox.getValue().equals("Appointment"))
                    scheduleObj.removeTask((TransientTask) selectedTask);
                else if (activeCategoryBox.getValue().equals("Cancellation"))
                    scheduleObj.removeTask((AntiTask) selectedTask);
                else
                    scheduleObj.removeTask((RecurringTask) selectedTask);
                Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
                deletedAlert.setHeaderText("Task successfully deleted.");
                deletedAlert.showAndWait();
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
            buildOpenScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(openScene);
        });

        viewSchedule.setOnAction(actionEvent -> {
            buildWeekBorderPane();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
            primaryStage.centerOnScreen();
            primaryStage.setScene(openScene);
        });
        schedule.setOnAction(actionEvent -> {
            buildWeekBorderPane();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
            primaryStage.centerOnScreen();
            primaryStage.setScene(openScene);
        });
        mainMenu.setOnAction(actionEvent -> {
            buildOpenScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(openScene);
        });

        addButton.setOnAction(actionEvent -> {
            if(taskTypeBox.getSelectionModel().isSelected(2))
                parseAntiInfo();
            else
                parseInfo();
            buildOpenScene();
            primaryStage.setWidth(500);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.setScene(openScene);
        });
        cancelButton.setOnAction(actionEvent ->  {
            if(bp.getCenter() == bpWeekMidSection || bp.getCenter() == bpDayMidSection ||
               bp.getCenter() == bpMonthMidSection){
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setWidth(primaryScreenBounds.getWidth());
                primaryStage.setHeight(primaryScreenBounds.getHeight());
                primaryStage.centerOnScreen();
            }
            primaryStage.setScene(openScene);
        });
        deleteCancelButton.setOnAction(actionEvent ->{
            if(bp.getCenter() == bpWeekMidSection || bp.getCenter() == bpDayMidSection ||
                    bp.getCenter() == bpMonthMidSection){
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setWidth(primaryScreenBounds.getWidth());
                primaryStage.setHeight(primaryScreenBounds.getHeight());
                primaryStage.centerOnScreen();
            }
            primaryStage.setScene(openScene);
        });

        exitButton.setOnAction(actionEvent -> System.exit(0));

        primaryStage.setScene(openScene);
        primaryStage.show();

    }

    public void checkActiveTasks(){
        Set<String> categories = scheduleObj.getActiveCategories();
        for(String category : categories){
            activeCategoryBox.getItems().add(category);
        }
        activeCategoryBox.getSelectionModel().selectFirst();
        if(activeCategoryBox.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("No tasks found.");
            alert.showAndWait();
        }
    }

    public void openFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files","*.json"));
        curFilePath = fileChooser.showOpenDialog(primaryStage);
        if(curFilePath != null) {
            importJson.getFromUINoReturn(curFilePath);
            scheduleObj = scheduleObj.merge(importJson.passImport());
        }
    }

    public void saveFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files","*.json"));
        curFilePath = fileChooser.showSaveDialog(primaryStage);
        if(curFilePath != null)
            exportJson.export(scheduleObj, curFilePath);
    }

    public void buildAddTaskScene(){
        if(taskTypeBox.getSelectionModel().isSelected(0)) {
            gp.getChildren().clear();
            gp.addRow(0, typeTaskLabel, taskTypeBox);
            gp.addRow(1, categoryLabel, recurringValidCatBox);
            gp.addRow(2, nameLabel, nameField);
            gp.addRow(3, startLabel, startField);
            gp.addRow(4, durationLabel, durationField);
            gp.addRow(5, startDateLabel, startDatePicker);
            gp.addRow(6, endDateLabel, endDatePicker);
            gp.addRow(7, freqLabel, freqChoice);

        } else if(taskTypeBox.getSelectionModel().isSelected(1)){
            gp.getChildren().clear();
            gp.addRow(0, typeTaskLabel, taskTypeBox);
            gp.addRow(1, categoryLabel, transValidCatBox);
            gp.addRow(2, nameLabel, nameField);
            gp.addRow(3, startLabel, startField);
            gp.addRow(4, durationLabel, durationField);
            gp.addRow(5, startDateLabel, startDatePicker);
        } else {
            gp.getChildren().clear();
            gp.addRow(0, typeTaskLabel, taskTypeBox);
            gp.addRow(1, nameLabel, nameField);
            gp.addRow(2, startLabel, startField);
            gp.addRow(3, durationLabel, durationField);
            gp.addRow(4, startDateLabel, startDatePicker);
        }

    }

    public void buildDeleteScene(){
        nameOptions.getItems().clear();
        taskTypeBox.getSelectionModel().selectFirst();
        if(taskTypeBox.getSelectionModel().isSelected(0)) {
            deleteGP.getChildren().clear();
            deleteGP.addRow(1, categoryLabel, activeCategoryBox);
            deleteGP.addRow(2, nameLabel, nameOptions);

        } else if(taskTypeBox.getSelectionModel().isSelected(1)){
            deleteGP.getChildren().clear();
            deleteGP.addRow(0, typeTaskLabel, taskTypeBox);
            deleteGP.addRow(1, categoryLabel, transValidCatBox);
            deleteGP.addRow(2, nameLabel, nameOptions);
        } else {
            deleteGP.getChildren().clear();
            deleteGP.addRow(0, typeTaskLabel, taskTypeBox);
            taskTypeBox.getSelectionModel().selectFirst();
            deleteGP.addRow(1, nameLabel, nameOptions);
        }
    }

    public void buildOpenScene(){
        VBox buttonVBox = new VBox(25, viewSchedule, addTask, deleteTask);
        buttonVBox.setAlignment(Pos.CENTER);
        bp.setBottom(null);
        bp.setCenter(buttonVBox);
        bp.setTop(menuBar);
    }

    public void clearTextFields(){
        nameField.clear();
        startField.clear();
        durationField.clear();
    }

    public void parseInfo(){
        String name = nameField.getText();
        String category;

        int startTime = 0;
        int duration = 0;
        try{
            startTime = Integer.parseInt(startField.getText());
            duration = Integer.parseInt(durationField.getText());
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Start Time or Duration Time is invalid.");
            alert.showAndWait();
        }
        try {
            Timeframe timeframe = new Timeframe(startTime, duration);
            if (taskTypeBox.getSelectionModel().isSelected(0)) {
                category = recurringValidCatBox.getSelectionModel().getSelectedItem();
                parseRecurInfo(name, category, timeframe);
            } else {
                category = transValidCatBox.getSelectionModel().getSelectedItem();
                parseTransInfo(name, category, timeframe);
            }
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void parseRecurInfo(String name, String category, Timeframe timeframe){

        LocalDate startLocalDate = startDatePicker.getValue();
        int startDay = startLocalDate.getDayOfMonth();
        int startMonth = startLocalDate.getMonthValue();
        int startYear = startLocalDate.getYear();

        LocalDate endLocalDate = endDatePicker.getValue();
        int endDay = endLocalDate.getDayOfMonth();
        int endMonth = endLocalDate.getMonthValue();
        int endYear = endLocalDate.getYear();

        Date startDate = new Date(startMonth, startDay, startYear);
        Date endDate = new Date(endMonth, endDay, endYear);

        TaskFrequency frequency;
        if(freqChoice.getSelectionModel().isSelected(0))
            frequency = TaskFrequency.DAILY;
        else if(freqChoice.getSelectionModel().isSelected(1))
            frequency = TaskFrequency.WEEKLY;
        else
            frequency = TaskFrequency.MONTHLY;
        RecurringTask newTask = new RecurringTask(name, category, timeframe, startDate, endDate, frequency);
        try {
            scheduleObj.addTask(newTask);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void parseTransInfo(String name, String category, Timeframe timeframe){
        LocalDate startLocalDate = startDatePicker.getValue();
        int startDay = startLocalDate.getDayOfMonth();
        int startMonth = startLocalDate.getMonthValue();
        int startYear = startLocalDate.getYear();
        Date startDate = new Date(startMonth, startDay, startYear);

        TransientTask newTask = new TransientTask(name, category, timeframe, startDate);
        try {
            scheduleObj.addTask(newTask);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void parseAntiInfo(){
        String name = nameField.getText();
        int startTime = 0;
        int duration = 0;
        try{
            startTime = Integer.parseInt(startField.getText());
            duration = Integer.parseInt(durationField.getText());
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Start Time or Duration Time is invalid.");
            alert.showAndWait();
        }
        Timeframe timeframe = new Timeframe(startTime, duration);
        LocalDate startLocalDate = startDatePicker.getValue();
        int startDay = startLocalDate.getDayOfMonth();
        int startMonth = startLocalDate.getMonthValue();
        int startYear = startLocalDate.getYear();
        Date startDate = new Date(startMonth, startDay, startYear);

        AntiTask newTask = new AntiTask(name, timeframe, startDate);
        try {
            scheduleObj.addTask(newTask);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void buildDayBorderPane(){
        bpDayTopSection = bpTopSectionInitializer();
        bpDayNestedMidSection = bpDayNestedMidSectionInitializer();
        bpDayMidSection.setContent(bpDayNestedMidSection);
        bpDayBottomSection = bpDayBottomSectionInitializer();
        bp.setTop(bpDayTopSection);
        bp.setCenter(bpDayMidSection);
        bp.setBottom(bpDayBottomSection);
    }
    public void buildWeekBorderPane(){
        bpWeekTopSection = bpTopSectionInitializer();
        bpWeekNestedMidSection = bpWeekNestedMidSectionInitializer();
        bpWeekMidSection.setContent(bpWeekNestedMidSection);
        bpWeekBottomSection = bpWeekBottomSectionInitializer();
        bp.setTop(bpWeekTopSection);
        bp.setCenter(bpWeekMidSection);
        bp.setBottom(bpWeekBottomSection);
    }
    public void buildMonthBorderPane(){
        bpMonthTopSection = bpTopSectionInitializer();
        bpMonthNestedMidSection = bpMonthNestedMidSectionInitializer();
        bpMonthMidSection.setContent(bpMonthNestedMidSection);
        bpMonthBottomSection = bpMonthBottomSectionInitializer();
        bp.setTop(bpMonthTopSection);
        bp.setCenter(bpMonthMidSection);
        bp.setBottom(bpMonthBottomSection);
    }

    public VBox bpTopSectionInitializer(){
        VBox vb = new VBox();
        vb.setPadding(new Insets(10));
        vb.setSpacing(8);
        vb.setStyle("-fx-background-color: #B0ADAC;");

        HBox hbox = calendarFormatOptions();

        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(menuBar,hbox);
        return vb;
    }

    public HBox calendarFormatOptions(){
        int day = today.getDayOfMonth(), month = today.getMonthValue(), year = today.getYear();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        Button days = new Button("Day");
        days.setPrefSize(100, 20);
        days.setStyle("-fx-background-color:white");
        days.setOnAction(e -> {
            buildDayBorderPane();
        });

        Button weeks = new Button("Week");
        weeks.setPrefSize(100, 20);
        weeks.setStyle("-fx-background-color:white");
        weeks.setOnAction(e -> {
            buildWeekBorderPane();
        });

        Button months = new Button("Month");
        months.setPrefSize(100, 20);
        months.setStyle("-fx-background-color:white");
        months.setOnAction(e -> {
            buildMonthBorderPane();
        });

        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(days,weeks,months);
        return hbox;
    }

    public GridPane bpDayNestedMidSectionInitializer(){
        GridPane gp = new GridPane();
        gp.setVgap(5);
        gp.setHgap(5);
        gp.setGridLinesVisible(true);
        gp.getRowConstraints().add(new RowConstraints(25));
        gp.getRowConstraints().add(new RowConstraints(1500));
        gp.getColumnConstraints().add(new ColumnConstraints(700));

        Weekday weekDayName = currentSelectionDateDay.getWeekday();
        String dayString = weekDayName.toString();
        Label lb = new Label();
        lb.setText(dayString);
        dayDailyDateString = lb;
        gp.add(dayDailyDateString, 0, 0);
        gp.setHalignment(dayDailyDateString, HPos.CENTER);

        Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateDay);
        String taskString = "\n\n" + "Date: " + currentSelectionDateDay.toString() + "\n";

        if (dailyTasks != null && dailyTasks.size() > 0) {
            for (Task task : dailyTasks) {
                taskString += "Task Name: " + task.getTaskName() + "\n"; // task name
                taskString += "Task Category: " + task.getCategory() + "\n"; // task category
                taskString += "Task time: ";
                for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateDay))
                    taskString +=(timeframe) + "\n"; // task time frame
            }
        } else
            taskString += "\nNo tasks today!";

        Label lb1 = new Label();
        lb1.setText(taskString);
        dayDailyTasks = lb1;
        gp.add(dayDailyTasks, 0, 1);
        gp.setHalignment(dayDailyTasks, HPos.CENTER);
        gp.setValignment(dayDailyTasks, VPos.TOP);

        return gp;
    }
    public HBox bpDayBottomSectionInitializer(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #B0ADAC;");

        Button previous = new Button("Previous");
        previous.setPrefSize(100, 20);
        previous.setOnAction(e -> {
            System.out.println("previous day");
            currentSelectionDateDay = currentSelectionDateDay.getPreviousDay();
            Weekday weekDayName = currentSelectionDateDay.getWeekday();
            String dayString = weekDayName.toString();
            dayDailyDateString.setText(dayString);

            Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateDay);
            String taskString = "\n\n" + "Date: " + currentSelectionDateDay.toString() + "\n";

            if (dailyTasks != null && dailyTasks.size() > 0) {
                for (Task task : dailyTasks) {
                    taskString += "Task Name: " + task.getTaskName() + "\n"; // task name
                    taskString += "Task Category: " + task.getCategory() + "\n"; // task category
                    taskString += "Task time: ";
                    for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateDay))
                        taskString +=(timeframe) + "\n"; // task time frame
                }
            } else
                taskString += "\nNo tasks today!";
            dayDailyTasks.setText(taskString);
        });

        Button next = new Button("Next");
        next.setPrefSize(100, 20);
        next.setOnAction(e -> {
            System.out.println("next day");
            currentSelectionDateDay = currentSelectionDateDay.getNextDay();
            Weekday weekDayName = currentSelectionDateDay.getWeekday();
            String dayString = weekDayName.toString();
            dayDailyDateString.setText(dayString);

            Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateDay);
            String taskString = "\n\n" + "Date: " + currentSelectionDateDay.toString() + "\n";

            if (dailyTasks != null && dailyTasks.size() > 0) {
                for (Task task : dailyTasks) {
                    taskString += "Task Name: " + task.getTaskName() + "\n"; // task name
                    taskString += "Task Category: " + task.getCategory() + "\n"; // task category
                    taskString += "Task time: ";
                    for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateDay))
                        taskString +=(timeframe) + "\n"; // task time frame
                }
            } else
                taskString += "\nNo tasks today!";
            dayDailyTasks.setText(taskString);
        });
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(previous, next);
        return hbox;
    }

    public GridPane bpWeekNestedMidSectionInitializer(){
        currentSelectionDateWeek = new Date(today.getMonthValue(), today.getDayOfMonth(), today.getYear());
        GridPane gp = new GridPane();
        gp.setVgap(5);
        gp.setHgap(5);
        gp.setGridLinesVisible(true);
        for (int i = 0; i < 7; i++)
            gp.getColumnConstraints().add(new ColumnConstraints(200));  // column 0 ~ 6 is 200 wide
        gp.getRowConstraints().add(new RowConstraints(25));             // row 0 is 25 wide
        gp.getRowConstraints().add(new RowConstraints(3000));           // row 1 is 3000 wide

        Label[] weekDays = new Label[7];
        for(int i = 0; i < 7; i++) {
            // Weekday display
            Weekday weekDayName = currentSelectionDateWeek.getWeekday();
            String dayString = weekDayName.toString();
            Label lb = new Label();
            lb.setText(dayString);
            weekDays[i] = lb;
            gp.add(weekDays[i], i, 0);
            gp.setHalignment(weekDays[i], HPos.CENTER);

            // Weekday Task
            Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateWeek);
            String taskString = "\n\n" + "Date: " + currentSelectionDateWeek.toString() + "\n";

            if (dailyTasks != null && dailyTasks.size() > 0) {
                for (Task task : dailyTasks) {
                    taskString += "Task Name: " + task.getTaskName() + "\n"; // task name
                    taskString += "Task Category: " + task.getCategory() + "\n"; // task category
                    taskString += "Task time: ";
                    for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateWeek))
                        taskString +=(timeframe) + "\n"; // task time frame
                }
            } else
                taskString += "\nNo tasks today!";      //no task
            currentSelectionDateWeek = currentSelectionDateWeek.getNextDay();

            Label lb1 = new Label();
            lb1.setText(taskString);
            weekDailyTasks[i] = lb1;
            gp.add(weekDailyTasks[i], i, 1);
            gp.setHalignment(weekDailyTasks[i], HPos.CENTER);
            gp.setValignment(weekDailyTasks[i], VPos.TOP);
        }//////////////////////////////////////////////////////////////////////
        return gp;
    }
    public HBox bpWeekBottomSectionInitializer(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #B0ADAC;");

        Button previous = new Button("Previous");
        previous.setPrefSize(100, 20);
        previous.setOnAction(e -> {
            System.out.println("previous week");
            String taskString = "";
            for(int i = 0; i < 14; i++)
                currentSelectionDateWeek = currentSelectionDateWeek.getPreviousDay();
            for(int i = 0; i < 7; i++) {
                // Weekday Task
                Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateWeek);
                taskString = "\n\n" + "Date: " + currentSelectionDateWeek.toString() + "\n";

                if (dailyTasks != null && dailyTasks.size() > 0) {
                    for (Task task : dailyTasks) {
                        taskString += "Task Name: " + task.getTaskName() + "\n"; // task name
                        taskString += "Task Category: " + task.getCategory() + "\n"; // task category
                        taskString += "Task time: ";
                        for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateWeek))
                            taskString +=(timeframe) + "\n"; // task time frame
                    }
                } else
                    taskString += "\nNo tasks today!";      //no task
                currentSelectionDateWeek = currentSelectionDateWeek.getNextDay();
                weekDailyTasks[i].setText(taskString);
            }
        });

        Button next = new Button("Next");
        next.setPrefSize(100, 20);
        next.setOnAction(e -> {
            System.out.println("next week");
            String taskString = "";
            for(int i = 0; i < 7; i++) {
                // Weekday Task
                Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateWeek);
                taskString = "\n\n" + "Date: " + currentSelectionDateWeek.toString() + "\n";

                if (dailyTasks != null && dailyTasks.size() > 0) {
                    for (Task task : dailyTasks) {
                        taskString += "Task Name: " + task.getTaskName() + "\n"; // task name
                        taskString += "Task Category: " + task.getCategory() + "\n"; // task category
                        taskString += "Task time: ";
                        for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateWeek))
                            taskString +=(timeframe) + "\n"; // task time frame
                    }
                } else
                    taskString += "\nNo tasks today!";      //no task
                currentSelectionDateWeek = currentSelectionDateWeek.getNextDay();
                weekDailyTasks[i].setText(taskString);
            }
        });

        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(previous, next);
        return hbox;
    }

    public GridPane bpMonthNestedMidSectionInitializer(){
        currentSelectionDateMonth = new Date(today.getMonthValue(), 1, today.getYear());
        GridPane gp = new GridPane();
        gp.setVgap(5);
        gp.setHgap(5);
        gp.setGridLinesVisible(true);
        for (int i = 0; i < 7; i++)
            gp.getColumnConstraints().add(new ColumnConstraints(200));      // column 0 ~ 6 is 200 wide
        gp.getRowConstraints().add(new RowConstraints(25));                 // row 0 is 25 wide
        gp.getRowConstraints().add(new RowConstraints(3000));               // row 1 is 3000 wide

        int maxDay = currentSelectionDateMonth.getMaxDay(currentSelectionDateMonth.getMonth(), currentSelectionDateMonth.getYear());

        // Weekday set of Task display
        String[] taskString = new String[7];
        for(int i = 0; i < 7; i++)
            taskString[i] = "";

        for(int i = 0; i < maxDay; i++) {
            Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateMonth);
            taskString[i%7] += "\n\n\n" + "Date: " + currentSelectionDateMonth.toString() + "\n";

            if (dailyTasks != null && dailyTasks.size() > 0) {
                for (Task task : dailyTasks) {
                    taskString[i%7] += "Task Name: " + task.getTaskName() + "\n";       // task name
                    taskString[i%7] += "Task Category: " + task.getCategory() + "\n";   // task category
                    taskString[i%7] += "Task time: ";
                    for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateMonth))
                        taskString[i%7] +=(timeframe) + "\n";                           // task time frame
                }
            } else
                taskString[i%7] += "No tasks today!";                                   //no task
            currentSelectionDateMonth = currentSelectionDateMonth.getNextDay();
        }

        // display the string containing the set of tasks to the Grid Pane
        for (int i = 0; i < 7; i++){
            if (taskString[i] == null)
                continue;
            Label lb1 = new Label();
            lb1.setText(taskString[i] + "\n");
            monthDailyTasks[i] = lb1;
            gp.add(monthDailyTasks[i], i%7, 1);
            gp.setHalignment(monthDailyTasks[i], HPos.CENTER);
            gp.setValignment(monthDailyTasks[i], VPos.TOP);
        }
        return gp;
    }
    public HBox bpMonthBottomSectionInitializer(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #B0ADAC;");

        Button previous = new Button("Previous");
        previous.setPrefSize(100, 20);
        previous.setOnAction(e -> {
            System.out.println("previous month");
            if ((currentSelectionDateMonth.getMonth() - 2) == 0)
                currentSelectionDateMonth = new Date(12,Date.getMaxDay(12,currentSelectionDateMonth.getYear() - 1), currentSelectionDateMonth.getYear() - 1);
            else if ((currentSelectionDateMonth.getMonth() - 2) <= -1)
                currentSelectionDateMonth = new Date(11,Date.getMaxDay(11,currentSelectionDateMonth.getYear() - 1), currentSelectionDateMonth.getYear() - 1);
            else
                currentSelectionDateMonth.setMonth(currentSelectionDateMonth.getMonth() - 2);

            currentSelectionDateMonth.setDay(1);
            int maxDay = currentSelectionDateMonth.getMaxDay(currentSelectionDateMonth.getMonth(), currentSelectionDateMonth.getYear());

            // Weekday Task
            String[] taskString = new String[7];
            for(int i = 0; i < 7; i++)
                taskString[i] = "";
            for(int i = 0; i < maxDay; i++) {
                // Weekday Task
                Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateMonth);
                taskString[i%7] += "\n\n\n" + "Date: " + currentSelectionDateMonth.toString() + "\n";

                if (dailyTasks != null && dailyTasks.size() > 0) {
                    for (Task task : dailyTasks) {
                        taskString[i%7] += "Task Name: " + task.getTaskName() + "\n"; // task name
                        taskString[i%7] += "Task Category: " + task.getCategory() + "\n"; // task category
                        taskString[i%7] += "Task time: ";
                        for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateMonth))
                            taskString[i%7] +=(timeframe) + "\n"; // task time frame
                    }
                } else
                    taskString[i%7] += "No tasks today!";      //no task
                currentSelectionDateMonth = currentSelectionDateMonth.getNextDay();
            }
            for (int i = 0; i < 7; i++)
                monthDailyTasks[i].setText(taskString[i]);
        });

        Button next = new Button("Next");
        next.setPrefSize(100, 20);
        next.setOnAction(e -> {
            System.out.println("next month");
            int maxDay = currentSelectionDateMonth.getMaxDay(currentSelectionDateMonth.getMonth(), currentSelectionDateMonth.getYear());
            String[] taskString = new String[7];
            for(int i = 0; i < 7; i++)
                taskString[i] = "";

            // Weekday Task
            for(int i = 0; i < maxDay; i++) {
                Set<Task> dailyTasks = scheduleObj.getDailyTasks(currentSelectionDateMonth);
                taskString[i%7] += "\n\n\n" + "Date: " + currentSelectionDateMonth.toString() + "\n";

                if (dailyTasks != null && dailyTasks.size() > 0) {
                    for (Task task : dailyTasks) {
                        taskString[i%7] += "Task Name: " + task.getTaskName() + "\n"; // task name
                        taskString[i%7] += "Task Category: " + task.getCategory() + "\n"; // task category
                        taskString[i%7] += "Task time: ";
                        for (Timeframe timeframe : task.getDailyTimeframes(currentSelectionDateMonth))
                            taskString[i%7] +=(timeframe) + "\n"; // task time frame
                    }
                } else
                    taskString[i%7] += "No tasks today!";      //no task
                currentSelectionDateMonth = currentSelectionDateMonth.getNextDay();
            }
            for (int i = 0; i < 7; i++)
                monthDailyTasks[i].setText(taskString[i]);
        });

        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(previous, next);
        return hbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
