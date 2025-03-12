module com.example.testcasegenerator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.testcasegenerator to javafx.fxml;
    exports com.example.testcasegenerator;
}