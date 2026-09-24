module ni.edu.ni.uam.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;


    opens ni.edu.ni.uam.fact_app to javafx.fxml;
    exports ni.edu.ni.uam.fact_app;
    exports ni.edu.ni.uam.fact_app.application;
    exports ni.edu.ni.uam.fact_app.model;
    exports ni.edu.ni.uam.fact_app.dao;
    opens ni.edu.ni.uam.fact_app.controller to javafx.fxml;
    opens ni.edu.ni.uam.fact_app.model to javafx.base;
}