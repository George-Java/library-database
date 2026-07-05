package com.wsy.iframe;

import com.wsy.entity.BookType;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class BookCategoryAddIFrameFX {
    private TextField categoryIdField;
    private TextField categoryNameField;
    private Spinner<Integer> daysSpinner;
    private Spinner<Double> fineSpinner;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("添加图书类别");
        dialog.setMinWidth(520);
        dialog.setMinHeight(410);
        dialog.setResizable(true);

        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 38));
        logoBox.getChildren().add(UIUtils.createLabel("添加图书类别", 22, true, Color.rgb(26, 35, 126)));

        GridPane form = new GridPane();
        form.setPadding(new Insets(32, 24, 24, 24));
        form.setHgap(28);
        form.setVgap(18);

        categoryIdField = new TextField();
        categoryIdField.setPromptText("如 T0001");
        categoryNameField = new TextField();
        categoryNameField.setPromptText("如 计算机类");
        daysSpinner = new Spinner<>(1, 365, 30, 1);
        daysSpinner.setEditable(true);
        fineSpinner = new Spinner<>(0.0, 100.0, 0.5, 0.1);
        fineSpinner.setEditable(true);

        form.add(UIUtils.createLabel("类别编号：", 14, true, Color.BLACK), 0, 0);
        form.add(categoryIdField, 1, 0);
        form.add(UIUtils.createLabel("类别名称：", 14, true, Color.BLACK), 0, 1);
        form.add(categoryNameField, 1, 1);
        form.add(UIUtils.createLabel("可借天数：", 14, true, Color.BLACK), 0, 2);
        form.add(daysSpinner, 1, 2);
        form.add(UIUtils.createLabel("罚款金额（元/天）：", 14, true, Color.BLACK), 0, 3);
        form.add(fineSpinner, 1, 3);

        Button addBtn = UIUtils.styledBtn("添加类别", "#388e3c", "#43a047");
        Button resetBtn = UIUtils.styledBtn("重置", "#78909c", "#b0bec5");
        HBox btnBox = new HBox(36, addBtn, resetBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(18, 0, 0, 0));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setTop(logoBox);
        root.setCenter(new VBox(form));
        root.setBottom(btnBox);

        addBtn.setOnAction(event -> addBookCategory());
        resetBtn.setOnAction(event -> resetForm());

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private void addBookCategory() {
        String categoryId = categoryIdField.getText().trim().toUpperCase();
        String categoryName = categoryNameField.getText().trim();
        int borrowDays = daysSpinner.getValue();
        double fineAmount = fineSpinner.getValue();

        if (!Pattern.matches("^[A-Z]\\d{4}$", categoryId)) {
            AlertHelper.showError("输入错误", "类别编号格式应为1位大写字母+4位数字，例如 T0001");
            categoryIdField.requestFocus();
            return;
        }
        if (categoryName.isEmpty()) {
            AlertHelper.showError("输入错误", "类别名称不能为空");
            categoryNameField.requestFocus();
            return;
        }

        BookType type = new BookType(categoryId, categoryName, borrowDays, (float) fineAmount);
        try {
            if (DBUtils.query(mapper -> mapper.countBookTypeByNumber(categoryId) > 0)) {
                AlertHelper.showError("输入错误", "类别编号已存在");
                return;
            }
            if (DBUtils.query(mapper -> mapper.countBookTypeByName(categoryName) > 0)) {
                AlertHelper.showError("输入错误", "类别名称已存在");
                return;
            }
            if (DBUtils.transaction(mapper -> mapper.insertBookType(type)) > 0) {
                AlertHelper.showInfo("添加成功", "图书类别添加成功！");
                resetForm();
            }
        } catch (SQLException ex) {
            AlertHelper.showError("添加失败", ex.getMessage());
        }
    }

    private void resetForm() {
        categoryIdField.clear();
        categoryNameField.clear();
        daysSpinner.getValueFactory().setValue(30);
        fineSpinner.getValueFactory().setValue(0.5);
        categoryIdField.requestFocus();
    }
}
