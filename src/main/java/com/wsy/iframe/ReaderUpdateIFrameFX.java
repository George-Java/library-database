package com.wsy.iframe;

import com.wsy.entity.Reader;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class ReaderUpdateIFrameFX {
    private final ObservableList<Reader> readerList = FXCollections.observableArrayList();
    private TableView<Reader> table;
    private TextField nameField, ageField, professionField, idField, maxNumField, phoneField, depositField, barcodeField;
    private ComboBox<String> typeBox;
    private DatePicker expiryPicker, issuePicker;
    private ToggleGroup genderGroup;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("读者信息修改与删除");
        dialog.setWidth(1050);
        dialog.setHeight(670);

        HBox logoBox = new HBox(20);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 46));
        logoBox.getChildren().add(UIUtils.createLabel("读者信息维护", 24, true, Color.rgb(26, 35, 126)));

        table = buildTable();
        loadReaderData();

        VBox form = buildForm();
        Button saveBtn = UIUtils.styledBtn("保存修改", "#1a2980", "#26d0ce");
        Button deleteBtn = UIUtils.styledBtn("删除读者", "#d32f2f", "#ff6659");
        Button resetBtn = UIUtils.styledBtn("重置", "#7b7b7b", "#bdbdbd");
        HBox buttons = new HBox(30, saveBtn, deleteBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(15, 0, 0, 0));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> fillForm(newVal));
        saveBtn.setOnAction(event -> saveReader());
        deleteBtn.setOnAction(event -> deleteReader());
        resetBtn.setOnAction(event -> fillForm(table.getSelectionModel().getSelectedItem()));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setTop(logoBox);
        root.setCenter(table);
        root.setBottom(new VBox(12, form, buttons));

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private TableView<Reader> buildTable() {
        TableView<Reader> result = new TableView<>(readerList);
        result.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addColumn(result, "读者条码", "barcode");
        addColumn(result, "姓名", "name");
        addColumn(result, "性别", "sex");
        addColumn(result, "年龄", "age");
        addColumn(result, "职业", "profession");
        addColumn(result, "证件类型", "type");
        addColumn(result, "证件号码", "identityCard");
        addColumn(result, "最大借书量", "maxNum");
        addColumn(result, "会员有效期", "date");
        addColumn(result, "电话", "phone");
        addColumn(result, "押金", "keepMoney");
        addColumn(result, "办证日期", "dateOfIssuance");
        result.setPlaceholder(new Label("暂无读者信息"));
        return result;
    }

    private void addColumn(TableView<Reader> tableView, String title, String property) {
        TableColumn<Reader, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        tableView.getColumns().add(column);
    }

    private VBox buildForm() {
        GridPane form = new GridPane();
        form.setHgap(25);
        form.setVgap(16);
        form.setPadding(new Insets(18, 20, 16, 20));

        nameField = new TextField();
        genderGroup = new ToggleGroup();
        RadioButton rbMale = new RadioButton("男");
        RadioButton rbFemale = new RadioButton("女");
        rbMale.setToggleGroup(genderGroup);
        rbFemale.setToggleGroup(genderGroup);
        rbMale.setSelected(true);
        HBox sexBox = new HBox(18, rbMale, rbFemale);
        ageField = new TextField();
        professionField = new TextField();
        typeBox = new ComboBox<>(FXCollections.observableArrayList("工作证", "身份证", "港澳台通行证", "学生证"));
        typeBox.getSelectionModel().selectFirst();
        idField = new TextField();
        maxNumField = new TextField("5");
        expiryPicker = new DatePicker(LocalDate.now().plusYears(1));
        phoneField = new TextField();
        depositField = new TextField("100");
        issuePicker = new DatePicker(LocalDate.now());
        barcodeField = new TextField();

        form.add(UIUtils.createLabel("姓名：", 14, true, Color.BLACK), 0, 0);
        form.add(nameField, 1, 0);
        form.add(UIUtils.createLabel("性别：", 14, true, Color.BLACK), 2, 0);
        form.add(sexBox, 3, 0);
        form.add(UIUtils.createLabel("年龄：", 14, true, Color.BLACK), 0, 1);
        form.add(ageField, 1, 1);
        form.add(UIUtils.createLabel("职业：", 14, true, Color.BLACK), 2, 1);
        form.add(professionField, 3, 1);
        form.add(UIUtils.createLabel("证件类型：", 14, true, Color.BLACK), 0, 2);
        form.add(typeBox, 1, 2);
        form.add(UIUtils.createLabel("证件号码：", 14, true, Color.BLACK), 2, 2);
        form.add(idField, 3, 2);
        form.add(UIUtils.createLabel("最大借书量：", 14, true, Color.BLACK), 0, 3);
        form.add(maxNumField, 1, 3);
        form.add(UIUtils.createLabel("会员有效期：", 14, true, Color.BLACK), 2, 3);
        form.add(expiryPicker, 3, 3);
        form.add(UIUtils.createLabel("电话：", 14, true, Color.BLACK), 0, 4);
        form.add(phoneField, 1, 4);
        form.add(UIUtils.createLabel("押金：", 14, true, Color.BLACK), 2, 4);
        form.add(depositField, 3, 4);
        form.add(UIUtils.createLabel("办证日期：", 14, true, Color.BLACK), 0, 5);
        form.add(issuePicker, 1, 5);
        form.add(UIUtils.createLabel("读者条码：", 14, true, Color.BLACK), 2, 5);
        form.add(barcodeField, 3, 5);
        return new VBox(form);
    }

    private void loadReaderData() {
        try {
            readerList.clear();
            readerList.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectAllReaders())));
        } catch (SQLException ex) {
            AlertHelper.showError("数据库错误", "加载读者数据失败: " + ex.getMessage());
        }
    }

    private void fillForm(Reader reader) {
        if (reader == null) return;
        nameField.setText(reader.getName());
        genderGroup.selectToggle("女".equals(reader.getSex()) ? genderGroup.getToggles().get(1) : genderGroup.getToggles().get(0));
        ageField.setText(String.valueOf(reader.getAge()));
        professionField.setText(reader.getProfession());
        typeBox.setValue(reader.getType());
        idField.setText(reader.getIdentityCard());
        maxNumField.setText(String.valueOf(reader.getMaxNum()));
        expiryPicker.setValue(reader.getDate() == null ? LocalDate.now().plusYears(1) : reader.getDate().toLocalDateTime().toLocalDate());
        phoneField.setText(reader.getPhone());
        depositField.setText(String.valueOf(reader.getKeepMoney()));
        issuePicker.setValue(reader.getDateOfIssuance() == null ? LocalDate.now() : reader.getDateOfIssuance().toLocalDate());
        barcodeField.setText(reader.getBarcode());
    }

    private Reader getFormData() {
        String name = nameField.getText().trim();
        String identity = idField.getText().trim();
        String barcode = barcodeField.getText().trim();
        if (name.isEmpty() || identity.isEmpty() || barcode.isEmpty()) {
            AlertHelper.showError("输入错误", "姓名、证件号码和读者条码为必填项");
            return null;
        }
        return new Reader(
                barcode,
                name,
                ((RadioButton) genderGroup.getSelectedToggle()).getText(),
                ageField.getText().trim().isEmpty() ? 0 : Integer.parseInt(ageField.getText().trim()),
                professionField.getText().trim(),
                typeBox.getValue(),
                identity,
                Integer.parseInt(maxNumField.getText().trim()),
                Timestamp.valueOf(expiryPicker.getValue().atStartOfDay()),
                phoneField.getText().trim(),
                Float.parseFloat(depositField.getText().trim()),
                Date.valueOf(issuePicker.getValue())
        );
    }

    private void saveReader() {
        Reader selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarn("提示", "请先选择一个读者");
            return;
        }
        Reader modified = getFormData();
        if (modified == null) return;
        try {
            if (DBUtils.transaction(mapper -> mapper.updateReader(modified, selected.getBarcode())) > 0) {
                AlertHelper.showInfo("成功", "读者信息修改成功！");
                loadReaderData();
            }
        } catch (SQLException ex) {
            AlertHelper.showError("保存失败", ex.getMessage());
        }
    }

    private void deleteReader() {
        Reader selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarn("提示", "请先选择一个读者");
            return;
        }
        if (!AlertHelper.showConfirm("确认删除", "确定要删除读者 " + selected.getName() + " 吗？")) {
            return;
        }
        try {
            if (DBUtils.transaction(mapper -> mapper.deleteReader(selected.getBarcode())) > 0) {
                AlertHelper.showInfo("成功", "读者删除成功！");
                loadReaderData();
            }
        } catch (SQLException ex) {
            AlertHelper.showError("删除失败", ex.getMessage());
        }
    }
}
