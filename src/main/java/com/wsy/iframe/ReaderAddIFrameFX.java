package com.wsy.iframe;

import com.wsy.entity.Reader;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * 读者添加页面，简化为直接用Reader实体类收集和插入数据。
 */
public class ReaderAddIFrameFX {
    private static final double BASE_WIDTH = 600;
    private static final double BASE_HEIGHT = 600;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("添加读者");
        dialog.setMinWidth(520);
        dialog.setMinHeight(540);
        dialog.setWidth(BASE_WIDTH);
        dialog.setHeight(BASE_HEIGHT);

        String[] idTypes = {"工作证", "身份证", "港澳台通行证", "学生证"};

        // --- 控件定义 ---
        TextField nameField = new TextField();
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton rbMale = new RadioButton("男");
        RadioButton rbFemale = new RadioButton("女");
        rbMale.setToggleGroup(genderGroup);
        rbFemale.setToggleGroup(genderGroup);
        rbMale.setSelected(true);
        HBox genderBox = new HBox(18, rbMale, rbFemale);

        TextField ageField = new TextField();
        TextField professionField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(idTypes));
        typeBox.getSelectionModel().selectFirst();
        TextField idField = new TextField();
        TextField maxNumField = new TextField("5");
        DatePicker expiryPicker = new DatePicker(LocalDate.now().plusYears(1));
        TextField phoneField = new TextField();
        TextField depositField = new TextField("100");
        DatePicker issuePicker = new DatePicker(LocalDate.now());
        TextField barcodeField = new TextField();

        // --- 表单布局 ---
        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(6, 36, 6, 36));
        form.setAlignment(Pos.CENTER);

        int row = 0;
        form.add(UIUtils.createLabel("姓名：", 15, true, Color.BLACK), 0, row);
        form.add(nameField, 1, row++);
        form.add(UIUtils.createLabel("性别：", 15, true, Color.BLACK), 0, row);
        form.add(genderBox, 1, row++);
        form.add(UIUtils.createLabel("年龄：", 15, true, Color.BLACK), 0, row);
        form.add(ageField, 1, row++);
        form.add(UIUtils.createLabel("职业：", 15, true, Color.BLACK), 0, row);
        form.add(professionField, 1, row++);
        form.add(UIUtils.createLabel("证件类型：", 15, true, Color.BLACK), 0, row);
        form.add(typeBox, 1, row++);
        form.add(UIUtils.createLabel("证件号码：", 15, true, Color.BLACK), 0, row);
        form.add(idField, 1, row++);
        form.add(UIUtils.createLabel("最大借书量：", 15, true, Color.BLACK), 0, row);
        form.add(maxNumField, 1, row++);
        form.add(UIUtils.createLabel("会员有效期：", 15, true, Color.BLACK), 0, row);
        form.add(expiryPicker, 1, row++);
        form.add(UIUtils.createLabel("电话：", 15, true, Color.BLACK), 0, row);
        form.add(phoneField, 1, row++);
        form.add(UIUtils.createLabel("押金：", 15, true, Color.BLACK), 0, row);
        form.add(depositField, 1, row++);
        form.add(UIUtils.createLabel("办证日期：", 15, true, Color.BLACK), 0, row);
        form.add(issuePicker, 1, row++);
        form.add(UIUtils.createLabel("读者条码：", 15, true, Color.BLACK), 0, row);
        form.add(barcodeField, 1, row++);

        // 列约束
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setHalignment(HPos.RIGHT);
        labelCol.setMinWidth(98);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labelCol, fieldCol);

        VBox formWrap = new VBox(form);
        VBox.setVgrow(formWrap, Priority.ALWAYS);

        // --- 按钮区 ---
        Button saveBtn = UIUtils.styledBtn("保存", "#388e3c", "#43a047");
        Button cancelBtn = UIUtils.styledBtn("取消", "#757575", "#bdbdbd");
        HBox btnBox = new HBox(32, saveBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(14, 0, 24, 0));
        btnBox.setMaxWidth(340);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(formWrap);
        mainPane.setBottom(btnBox);

        // 缩放支持
        Group scaleGroup = new Group(mainPane);
        StackPane rootPane = new StackPane(scaleGroup);
        Scene scene = new Scene(rootPane);
        dialog.setScene(scene);

        DoubleBinding scaleX = scene.widthProperty().divide(BASE_WIDTH);
        DoubleBinding scaleY = scene.heightProperty().divide(BASE_HEIGHT);
        DoubleBinding scale = (DoubleBinding) Bindings.min(scaleX, scaleY);
        scaleGroup.scaleXProperty().bind(scale);
        scaleGroup.scaleYProperty().bind(scale);

        dialog.show();

        // --- 保存事件 ---
        saveBtn.setOnAction(event -> {
            try {
                Reader reader = new Reader(
                        barcodeField.getText().trim(),
                        nameField.getText().trim(),
                        ((RadioButton) genderGroup.getSelectedToggle()).getText(),
                        ageField.getText().trim().isEmpty() ? 0 : Integer.parseInt(ageField.getText().trim()),
                        professionField.getText().trim(),
                        typeBox.getValue(),
                        idField.getText().trim(),
                        Integer.parseInt(maxNumField.getText().trim()),
                        java.sql.Timestamp.valueOf(expiryPicker.getValue().atStartOfDay()),
                        phoneField.getText().trim(),
                        Float.parseFloat(depositField.getText().trim()),
                        java.sql.Date.valueOf(issuePicker.getValue())
                );

                DBUtils.transaction(mapper -> mapper.insertReader(reader));
                AlertHelper.showInfo("成功", "读者添加成功！");
                nameField.clear();
                ageField.clear();
                professionField.clear();
                idField.clear();
                maxNumField.setText("5");
                expiryPicker.setValue(LocalDate.now().plusYears(1));
                phoneField.clear();
                depositField.setText("100");
                issuePicker.setValue(LocalDate.now());
                barcodeField.clear();
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                if (ex.getErrorCode() == 1062) {
                    if (msg.contains("barcode")) msg = "条码已存在";
                    else if (msg.contains("identityCard")) msg = "证件号已存在";
                }
                AlertHelper.showError("保存失败", msg);
            } catch (Exception ex) {
                AlertHelper.showError("未知错误", ex.getMessage());
            }
        });
        cancelBtn.setOnAction(event -> dialog.close());
    }
}
