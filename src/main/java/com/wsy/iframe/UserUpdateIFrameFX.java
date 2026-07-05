package com.wsy.iframe;

import com.wsy.entity.Operator;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UserUpdateIFrameFX {
    private TableView<Operator> userTable;
    private ObservableList<Operator> tableRows;
    private TextField searchField;
    private ComboBox<String> searchTypeCombo;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("用户管理");
        dialog.setWidth(980);
        dialog.setHeight(660);
        dialog.setResizable(true);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // 顶部logo+工具栏
        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 34));
        logoBox.getChildren().add(UIUtils.createLabel("用户管理", 22, true, Color.rgb(26, 35, 126)));

        HBox toolBar = new HBox(24);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(12, 0, 10, 0));
        toolBar.setSpacing(20);
        toolBar.setBackground(new Background(new BackgroundFill(
                Color.rgb(240, 248, 255, 0.98), new CornerRadii(8), Insets.EMPTY)));

        searchTypeCombo = new ComboBox<>();
        searchTypeCombo.getItems().addAll("全部", "编号", "姓名", "用户名", "身份证号");
        searchTypeCombo.getSelectionModel().selectFirst();
        searchTypeCombo.setPrefWidth(110);

        searchField = new TextField();
        searchField.setPromptText("输入搜索内容");
        searchField.setPrefWidth(180);

        Button searchBtn = UIUtils.styledBtn("搜索", "#1565c0", "#42a5f5");
        searchBtn.setOnAction(event -> loadUserData(searchField.getText().trim()));

        searchField.setOnAction(event -> loadUserData(searchField.getText().trim()));

        toolBar.getChildren().addAll(
                UIUtils.createLabel("搜索类型:", 15, true, Color.BLACK), searchTypeCombo,
                UIUtils.createLabel("搜索内容:", 15, true, Color.BLACK), searchField, searchBtn
        );

        VBox topBox = new VBox(logoBox, toolBar);
        topBox.setSpacing(2);
        root.setTop(topBox);

        // 表格
        userTable = buildTable();
        loadUserData("");
        VBox tableBox = new VBox(userTable);
        tableBox.setPadding(new Insets(10, 0, 10, 0));
        VBox.setVgrow(userTable, Priority.ALWAYS);

        // 按钮区
        HBox btnBox = new HBox(36);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(20, 0, 16, 0));
        Button updateBtn = UIUtils.styledBtn("修改用户", "#1976d2", "#64b5f6");
        Button deleteBtn = UIUtils.styledBtn("删除用户", "#d32f2f", "#ff6659");
        Button refreshBtn = UIUtils.styledBtn("刷新数据", "#757575", "#bdbdbd");
        btnBox.getChildren().addAll(updateBtn, deleteBtn, refreshBtn);

        // 事件
        updateBtn.setOnAction(event -> updateSelectedUser());
        deleteBtn.setOnAction(event -> deleteSelectedUser());
        refreshBtn.setOnAction(event -> loadUserData(""));

        // 双击表格行进入编辑
        userTable.setRowFactory(tv -> {
            TableRow<Operator> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && (!row.isEmpty())) {
                    updateSelectedUser();
                }
            });
            return row;
        });

        VBox centerBox = new VBox(tableBox, btnBox);
        VBox.setVgrow(tableBox, Priority.ALWAYS);
        root.setCenter(centerBox);

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private TableView<Operator> buildTable() {
        TableView<Operator> t = new TableView<>();
        tableRows = FXCollections.observableArrayList();
        t.setItems(tableRows);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"编号", "姓名", "性别", "年龄", "电话", "身份证号", "工作日期", "管理员", "用户名"};
        int[] minWidths = {70, 90, 60, 60, 120, 150, 130, 70, 120};

        TableColumn<Operator, Integer> colId = new TableColumn<>(columns[0]);
        colId.setMinWidth(minWidths[0]);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, String> colName = new TableColumn<>(columns[1]);
        colName.setMinWidth(minWidths[1]);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, String> colSex = new TableColumn<>(columns[2]);
        colSex.setMinWidth(minWidths[2]);
        colSex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        colSex.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, Integer> colAge = new TableColumn<>(columns[3]);
        colAge.setMinWidth(minWidths[3]);
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colAge.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, String> colPhone = new TableColumn<>(columns[4]);
        colPhone.setMinWidth(minWidths[4]);
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, String> colIdCard = new TableColumn<>(columns[5]);
        colIdCard.setMinWidth(minWidths[5]);
        colIdCard.setCellValueFactory(new PropertyValueFactory<>("identityCard"));
        colIdCard.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, String> colWorkDate = getOperatorStringTableColumn(columns, minWidths);

        TableColumn<Operator, String> colAdmin = new TableColumn<>(columns[7]);
        colAdmin.setMinWidth(minWidths[7]);
        colAdmin.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAdmin() ? "是" : "否"));
        colAdmin.setStyle("-fx-alignment: CENTER;");

        TableColumn<Operator, String> colUserName = new TableColumn<>(columns[8]);
        colUserName.setMinWidth(minWidths[8]);
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colUserName.setStyle("-fx-alignment: CENTER;");

        t.getColumns().addAll(colId, colName, colSex, colAge, colPhone, colIdCard, colWorkDate, colAdmin, colUserName);
        t.setPlaceholder(new Label("无用户数据"));
        return t;
    }

    private static TableColumn<Operator, String> getOperatorStringTableColumn(String[] columns, int[] minWidths) {
        TableColumn<Operator, String> colWorkDate = new TableColumn<>(columns[6]);
        colWorkDate.setMinWidth(minWidths[6]);
        colWorkDate.setCellValueFactory(cellData -> {
            Timestamp ts = cellData.getValue().getWorkDate();
            String dateStr = ts == null ? "" : ts.toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            return javafx.beans.property.SimpleStringProperty.stringExpression(javafx.beans.binding.Bindings.createStringBinding(() -> dateStr));
        });
        colWorkDate.setStyle("-fx-alignment: CENTER;");
        return colWorkDate;
    }

    private void loadUserData(String searchText) {
        tableRows.clear();
        int searchType = searchTypeCombo.getSelectionModel().getSelectedIndex();
        try {
            if (searchText.isEmpty() || searchType == 0) {
                tableRows.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectAllOperators())));
            } else {
                tableRows.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.searchOperators(searchType, searchText))));
            }
        } catch (SQLException ex) {
            AlertHelper.showError("加载失败", "加载用户数据失败: " + ex.getMessage());
        }
    }
    private void updateSelectedUser() {
        Operator selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarn("操作提示", "请选择要修改的用户");
            return;
        }
        showEditDialog(selected);
    }

    private void showEditDialog(Operator user) {
        Stage dialog = new Stage();
        dialog.setTitle("修改用户信息 - ID: " + user.getId());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(450);
        dialog.setHeight(620);
        dialog.setResizable(false);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20, 30, 20, 30));
        root.setBackground(new Background(new BackgroundFill(Color.rgb(245, 251, 255), new CornerRadii(8), Insets.EMPTY)));

        Label title = UIUtils.createLabel("修改用户信息", 20, true, Color.rgb(26, 35, 126));
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(14);

        int row = 0;
        form.add(UIUtils.createLabel("编号：", 15, true, Color.BLACK), 0, row);
        TextField idField = new TextField(String.valueOf(user.getId()));
        idField.setEditable(false);
        form.add(idField, 1, row);

        form.add(UIUtils.createLabel("姓名：", 15, true, Color.BLACK), 0, ++row);
        TextField nameField = new TextField(user.getName());
        form.add(nameField, 1, row);

        form.add(UIUtils.createLabel("性别：", 15, true, Color.BLACK), 0, ++row);
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("男", "女");
        genderBox.setValue(user.getSex());
        form.add(genderBox, 1, row);

        form.add(UIUtils.createLabel("年龄：", 15, true, Color.BLACK), 0, ++row);
        TextField ageField = new TextField(String.valueOf(user.getAge()));
        form.add(ageField, 1, row);

        form.add(UIUtils.createLabel("电话：", 15, true, Color.BLACK), 0, ++row);
        TextField phoneField = new TextField(user.getPhone());
        form.add(phoneField, 1, row);

        form.add(UIUtils.createLabel("身份证号：", 15, true, Color.BLACK), 0, ++row);
        TextField idCardField = new TextField(user.getIdentityCard());
        form.add(idCardField, 1, row);

        form.add(UIUtils.createLabel("工作日期：", 15, true, Color.BLACK), 0, ++row);
        DatePicker workDatePicker = new DatePicker();
        try {
            workDatePicker.setValue(user.getWorkDate() != null ? user.getWorkDate().toLocalDateTime().toLocalDate() : LocalDate.now());
        } catch (Exception ex) {
            workDatePicker.setValue(LocalDate.now());
        }
        form.add(workDatePicker, 1, row);

        form.add(UIUtils.createLabel("管理员：", 15, true, Color.BLACK), 0, ++row);
        ComboBox<String> adminCombo = new ComboBox<>();
        adminCombo.getItems().addAll("是", "否");
        adminCombo.setValue(user.isAdmin() ? "是" : "否");
        form.add(adminCombo, 1, row);

        form.add(UIUtils.createLabel("用户名：", 15, true, Color.BLACK), 0, ++row);
        TextField usernameField = new TextField(user.getUserName());
        form.add(usernameField, 1, row);

        form.add(UIUtils.createLabel("新密码：", 15, true, Color.BLACK), 0, ++row);
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("留空表示不修改");
        form.add(pwdField, 1, row);

        form.add(UIUtils.createLabel("确认密码：", 15, true, Color.BLACK), 0, ++row);
        PasswordField confirmPwdField = new PasswordField();
        form.add(confirmPwdField, 1, row);

        HBox btnBox = new HBox(22);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(20, 0, 0, 0));
        Button saveBtn = UIUtils.styledBtn("保存", "#388e3c", "#43a047");
        Button cancelBtn = UIUtils.styledBtn("取消", "#757575", "#bdbdbd");
        btnBox.getChildren().addAll(saveBtn, cancelBtn);

        saveBtn.setOnAction(event -> {
            String name = nameField.getText().trim();
            String gender = genderBox.getValue();
            String ageStr = ageField.getText().trim();
            String phone = phoneField.getText().trim();
            String idCard = idCardField.getText().trim();
            LocalDate workDate = workDatePicker.getValue();
            boolean isAdmin = "是".equals(adminCombo.getValue());
            String username = usernameField.getText().trim();
            String password = pwdField.getText();
            String confirmPassword = confirmPwdField.getText();

            if (name.isEmpty() || ageStr.isEmpty() || idCard.isEmpty() || username.isEmpty()) {
                AlertHelper.showWarn("输入错误", "姓名、年龄、身份证号和用户名不能为空！");
                return;
            }
            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 18 || age > 65) {
                    AlertHelper.showWarn("输入错误", "年龄必须在18-65岁之间");
                    return;
                }
            } catch (NumberFormatException e) {
                AlertHelper.showWarn("输入错误", "年龄必须为有效的整数");
                return;
            }
            if (!phone.isEmpty() && !phone.matches("\\d{11}")) {
                AlertHelper.showWarn("输入错误", "电话必须是11位数字");
                return;
            }
            if (!idCard.matches("\\d{17}[\\dX]")) {
                AlertHelper.showWarn("输入错误", "身份证号格式不正确（应为18位数字或最后一位X）");
                return;
            }
            if (!password.isEmpty() || !confirmPassword.isEmpty()) {
                if (!password.equals(confirmPassword)) {
                    AlertHelper.showWarn("输入错误", "两次输入的密码不一致");
                    return;
                }
                if (password.length() < 6 || password.length() > 10) {
                    AlertHelper.showWarn("输入错误", "密码长度应在6-10个字符之间（数据库限制）");
                    return;
                }
            }

            try {
                Operator updated = new Operator(
                        user.getId(),
                        name,
                        gender,
                        age,
                        phone,
                        idCard,
                        workDate == null ? null : Timestamp.valueOf(workDate.atStartOfDay()),
                        isAdmin,
                        username,
                        password
                );
                int rows = DBUtils.transaction(mapper -> mapper.updateOperator(updated));
                if (rows > 0) {
                    AlertHelper.showInfo("操作成功", "用户信息更新成功！");
                    loadUserData("");
                    dialog.close();
                } else {
                    AlertHelper.showWarn("操作提示", "没有更新任何记录");
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) {
                    AlertHelper.showError("唯一性冲突", "用户名或身份证号已存在！");
                } else {
                    AlertHelper.showError("更新失败", "更新失败: " + ex.getMessage());
                }
            }
        });
        cancelBtn.setOnAction(event -> dialog.close());

        root.getChildren().addAll(title, form, btnBox);
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private void deleteSelectedUser() {
        Operator selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarn("操作提示", "请选择要删除的用户");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("确定要删除该用户？");
        alert.setContentText("编号: " + selected.getId() + "  姓名: " + selected.getName() + "  用户名: " + selected.getUserName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int affectedRows = DBUtils.transaction(mapper -> mapper.deleteOperator(selected.getId()));
                if (affectedRows > 0) {
                    AlertHelper.showInfo("操作成功", "用户删除成功！");
                    loadUserData("");
                } else {
                    AlertHelper.showWarn("操作提示", "没有删除任何记录");
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    AlertHelper.showError("操作失败", "该用户有相关操作记录，无法删除！");
                } else {
                    AlertHelper.showError("删除失败", "删除失败: " + ex.getMessage());
                }
            }
        }
    }
}



