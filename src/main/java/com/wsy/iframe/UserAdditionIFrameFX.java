package com.wsy.iframe;

import com.wsy.entity.Operator;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class UserAdditionIFrameFX {
    private TextField idField, nameField, ageField, phoneField, idCardField, usernameField;
    private PasswordField pwdField, confirmPwdField;
    private ComboBox<String> genderBox;
    private DatePicker workDatePicker;
    private CheckBox adminBox;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.setTitle("添加系统用户");
        dialog.setWidth(600);
        dialog.setHeight(680);
        dialog.setResizable(false);
        dialog.setScene(new Scene(getAddUserPane(dialog, this::resetForm)));
        dialog.show();
    }

    public VBox getAddUserPane(Stage parent, Runnable onSuccess) {
        VBox mainBox = new VBox();
        mainBox.setSpacing(0);
        mainBox.setFillWidth(true);

        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10, 24, 10, 24));
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 34));
        logoBox.getChildren().add(UIUtils.createLabel("注册新用户", 22, true, Color.rgb(26, 35, 126)));
        mainBox.getChildren().add(logoBox);

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(17);
        form.setPadding(new Insets(18, 32, 18, 32));
        form.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.99), new CornerRadii(10), Insets.EMPTY)));
        form.setEffect(new DropShadow(8, Color.gray(0, 0.13)));

        int row = 0;
        idField = addTextField(form, row++, "用户编号：", "3-4位数字");
        nameField = addTextField(form, row++, "姓名：", "必填");
        form.add(UIUtils.createLabel("性别：", 16, true, Color.BLACK), 0, row);
        genderBox = new ComboBox<>();
        genderBox.getItems().addAll("男", "女");
        genderBox.getSelectionModel().selectFirst();
        genderBox.setPrefWidth(140);
        form.add(genderBox, 1, row++);
        ageField = addTextField(form, row++, "年龄：", "18-65");
        phoneField = addTextField(form, row++, "电话：", "11位手机号");
        idCardField = addTextField(form, row++, "身份证号：", "18位");
        form.add(UIUtils.createLabel("工作日期：", 16, true, Color.BLACK), 0, row);
        workDatePicker = new DatePicker(LocalDate.now());
        workDatePicker.setPrefWidth(140);
        form.add(workDatePicker, 1, row++);
        form.add(UIUtils.createLabel("管理员：", 16, true, Color.BLACK), 0, row);
        adminBox = new CheckBox("是管理员");
        adminBox.setFont(Font.font("Microsoft YaHei", 15));
        adminBox.setSelected(false);
        adminBox.setDisable(true);
        form.add(adminBox, 1, row++);
        usernameField = addTextField(form, row++, "用户名：", "4-20位，必须唯一");
        pwdField = addPasswordField(form, row++, "密码：", "6-20位");
        confirmPwdField = addPasswordField(form, row, "确认密码：", "必须一致");

        HBox btnBox = new HBox(28);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(16, 0, 0, 0));
        Button addBtn = UIUtils.styledBtn("注册", "#388e3c", "#43a047");
        Button resetBtn = UIUtils.styledBtn("重置", "#757575", "#bdbdbd");
        Button cancelBtn = UIUtils.styledBtn("取消", "#d32f2f", "#ff6659");
        btnBox.getChildren().addAll(addBtn, resetBtn, cancelBtn);

        addBtn.setOnAction(event -> {
            if (handleAddUser()) {
                if (onSuccess != null) onSuccess.run();
            }
        });
        resetBtn.setOnAction(event -> resetForm());
        cancelBtn.setOnAction(event -> parent.close());

        mainBox.getChildren().add(new VBox(form, btnBox));
        return mainBox;
    }

    private TextField addTextField(GridPane form, int row, String label, String prompt) {
        form.add(UIUtils.createLabel(label, 16, true, Color.BLACK), 0, row);
        TextField field = new TextField();
        field.setPromptText(prompt);
        form.add(field, 1, row);
        return field;
    }

    private PasswordField addPasswordField(GridPane form, int row, String label, String prompt) {
        form.add(UIUtils.createLabel(label, 16, true, Color.BLACK), 0, row);
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        form.add(field, 1, row);
        return field;
    }

    private boolean handleAddUser() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String gender = genderBox.getValue();
        String ageStr = ageField.getText().trim();
        String phone = phoneField.getText().trim();
        String idCard = idCardField.getText().trim();
        LocalDate workDate = workDatePicker.getValue();
        String username = usernameField.getText().trim();
        String password = pwdField.getText();
        String confirmPassword = confirmPwdField.getText();

        if (!validateInput(id, name, ageStr, phone, idCard, workDate, username, password, confirmPassword)) {
            return false;
        }

        int userId = Integer.parseInt(id);
        Operator user = new Operator(
                userId,
                name,
                gender,
                Integer.parseInt(ageStr),
                phone,
                idCard,
                Timestamp.valueOf(workDate.atStartOfDay()),
                false,
                username,
                password
        );

        try {
            if (DBUtils.query(mapper -> mapper.countOperatorById(userId) > 0)) {
                AlertHelper.showWarn("注册失败", "用户编号已存在：" + id);
                idField.requestFocus();
                return false;
            }
            if (DBUtils.query(mapper -> mapper.countOperatorByIdentityCard(idCard) > 0)) {
                AlertHelper.showWarn("注册失败", "身份证号已存在：" + idCard);
                idCardField.requestFocus();
                return false;
            }
            if (DBUtils.query(mapper -> mapper.countOperatorByUsername(username) > 0)) {
                AlertHelper.showWarn("注册失败", "用户名已存在：" + username);
                usernameField.requestFocus();
                return false;
            }
            if (DBUtils.transaction(mapper -> mapper.insertOperator(user)) > 0) {
                AlertHelper.showInfo("注册成功", "新用户注册成功，请使用该账号登录！");
                resetForm();
                return true;
            }
        } catch (SQLException ex) {
            AlertHelper.showError("数据库错误", ex.getMessage());
        }
        return false;
    }

    private boolean validateInput(String id, String name, String ageStr, String phone, String idCard,
                                  LocalDate workDate, String username, String password, String confirmPassword) {
        if (id.isEmpty() || !id.matches("\\d{3,4}")) {
            AlertHelper.showWarn("输入错误", "用户编号必须是3-4位数字");
            idField.requestFocus();
            return false;
        }
        if (name.isEmpty()) {
            AlertHelper.showWarn("输入错误", "姓名不能为空");
            nameField.requestFocus();
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                AlertHelper.showWarn("输入错误", "年龄必须在18-65岁之间");
                ageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarn("输入错误", "年龄必须是数字");
            ageField.requestFocus();
            return false;
        }
        if (!phone.matches("\\d{11}")) {
            AlertHelper.showWarn("输入错误", "电话必须是11位数字");
            phoneField.requestFocus();
            return false;
        }
        if (idCard.isEmpty() || !idCard.matches("\\d{17}[\\dX]")) {
            AlertHelper.showWarn("输入错误", "身份证号格式不正确");
            idCardField.requestFocus();
            return false;
        }
        if (workDate == null) {
            AlertHelper.showWarn("输入错误", "请选择工作日期");
            return false;
        }
        if (username.length() < 4 || username.length() > 20) {
            AlertHelper.showWarn("输入错误", "用户名长度应在4-20个字符之间");
            usernameField.requestFocus();
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            AlertHelper.showWarn("输入错误", "密码长度应在6-20个字符之间");
            pwdField.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            AlertHelper.showWarn("输入错误", "两次输入的密码不一致");
            pwdField.clear();
            confirmPwdField.clear();
            pwdField.requestFocus();
            return false;
        }
        return true;
    }

    private void resetForm() {
        idField.clear();
        nameField.clear();
        genderBox.getSelectionModel().selectFirst();
        ageField.clear();
        phoneField.clear();
        idCardField.clear();
        workDatePicker.setValue(LocalDate.now());
        adminBox.setSelected(false);
        usernameField.clear();
        pwdField.clear();
        confirmPwdField.clear();
        idField.requestFocus();
    }
}
