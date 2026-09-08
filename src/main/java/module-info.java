module ni.edu.ni.uam.fact_app {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.ni.uam.fact_app to javafx.fxml;
    exports ni.edu.ni.uam.fact_app;
}