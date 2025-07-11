module me.fadlan.passwordgen {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires kotlin.stdlib;

    requires net.synedra.validatorfx;
    requires com.google.gson;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.desktop;


    opens me.fadlan.passwordgen to javafx.fxml;
    exports me.fadlan.passwordgen;}