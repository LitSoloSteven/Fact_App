module ni.edu.ni.uam.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.ni.uam.fact_app to javafx.fxml;
    exports ni.edu.ni.uam.fact_app;
}