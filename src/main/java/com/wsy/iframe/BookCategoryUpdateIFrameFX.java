package com.wsy.iframe;

import com.wsy.entity.BookType;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

public class BookCategoryUpdateIFrameFX {
    private final ObservableList<BookType> categoryList = FXCollections.observableArrayList();
    private TableView<BookType> table;
    private TextField categoryIdField;
    private TextField categoryNameField;
    private TextField searchField;
    private Spinner<Integer> daysSpinner;
    private Spinner<Double> fineSpinner;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("图书类别管理");
        dialog.setMinWidth(760);
        dialog.setMinHeight(500);

        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 40));
        logoBox.getChildren().add(UIUtils.createLabel("图书类别管理", 24, true, Color.rgb(26, 35, 126)));

        searchField = new TextField();
        searchField.setPromptText("输入编号或名称");
        Button searchBtn = UIUtils.styledBtn("搜索", "#1976d2", "#64b5f6");
        Button refreshBtn = UIUtils.styledBtn("刷新", "#8d6e63", "#bdbdbd");
        HBox searchBox = new HBox(10, searchField, searchBtn, refreshBtn);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        table = buildTable();
        VBox left = new VBox(12, searchBox, table);
        left.setPadding(new Insets(10, 16, 10, 0));

        VBox form = buildForm();
        HBox center = new HBox(20, left, form);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setTop(logoBox);
        root.setCenter(center);

        searchBtn.setOnAction(event -> searchCategories());
        refreshBtn.setOnAction(event -> {
            searchField.clear();
            loadAllCategories();
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> fillForm(newVal));

        loadAllCategories();
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private TableView<BookType> buildTable() {
        TableView<BookType> result = new TableView<>(categoryList);
        result.setPrefWidth(360);
        result.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<BookType, String> idCol = new TableColumn<>("类别编号");
        idCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn<BookType, String> nameCol = new TableColumn<>("类别名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        result.getColumns().addAll(idCol, nameCol);
        result.setPlaceholder(new Label("暂无类别"));
        result.setRowFactory(tv -> {
            TableRow<BookType> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) fillForm(row.getItem());
            });
            return row;
        });
        return result;
    }

    private VBox buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(16);
        grid.setPadding(new Insets(8));
        categoryIdField = new TextField();
        categoryIdField.setEditable(false);
        categoryNameField = new TextField();
        daysSpinner = new Spinner<>(1, 365, 30, 1);
        daysSpinner.setEditable(true);
        fineSpinner = new Spinner<>(0.01, 100.0, 0.5, 0.1);
        fineSpinner.setEditable(true);
        grid.add(UIUtils.createLabel("类别编号：", 15, true, Color.BLACK), 0, 0);
        grid.add(categoryIdField, 1, 0);
        grid.add(UIUtils.createLabel("类别名称：", 15, true, Color.BLACK), 0, 1);
        grid.add(categoryNameField, 1, 1);
        grid.add(UIUtils.createLabel("可借天数：", 15, true, Color.BLACK), 0, 2);
        grid.add(daysSpinner, 1, 2);
        grid.add(UIUtils.createLabel("罚款金额：", 15, true, Color.BLACK), 0, 3);
        grid.add(fineSpinner, 1, 3);

        Button updateBtn = UIUtils.styledBtn("更新信息", "#1a2980", "#26d0ce");
        Button deleteBtn = UIUtils.styledBtn("删除类别", "#d32f2f", "#ff6659");
        Button resetBtn = UIUtils.styledBtn("重置", "#78909c", "#b0bec5");
        updateBtn.setOnAction(event -> updateCategory());
        deleteBtn.setOnAction(event -> deleteCategory());
        resetBtn.setOnAction(event -> clearForm());
        HBox buttons = new HBox(18, updateBtn, deleteBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(18, grid, buttons);
        box.setPadding(new Insets(10));
        return box;
    }

    private void searchCategories() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllCategories();
            return;
        }
        try {
            categoryList.clear();
            categoryList.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.searchBookTypes(keyword))));
        } catch (SQLException e) {
            AlertHelper.showError("搜索失败", e.getMessage());
        }
    }

    private void loadAllCategories() {
        try {
            categoryList.clear();
            categoryList.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectAllBookTypes())));
        } catch (SQLException e) {
            AlertHelper.showError("加载类别失败", e.getMessage());
        }
    }

    private void fillForm(BookType type) {
        if (type == null) return;
        categoryIdField.setText(type.getNumber());
        categoryNameField.setText(type.getTypeName());
        daysSpinner.getValueFactory().setValue(type.getDays());
        fineSpinner.getValueFactory().setValue((double) type.getFk());
    }

    private void clearForm() {
        table.getSelectionModel().clearSelection();
        categoryIdField.clear();
        categoryNameField.clear();
        daysSpinner.getValueFactory().setValue(30);
        fineSpinner.getValueFactory().setValue(0.5);
    }

    private void updateCategory() {
        String categoryId = categoryIdField.getText().trim();
        String categoryName = categoryNameField.getText().trim();
        if (categoryId.isEmpty()) {
            AlertHelper.showWarn("提示", "请先选择一个类别");
            return;
        }
        if (categoryName.isEmpty()) {
            AlertHelper.showError("输入错误", "类别名称不能为空");
            return;
        }
        BookType type = new BookType(categoryId, categoryName, daysSpinner.getValue(), fineSpinner.getValue().floatValue());
        try {
            String duplicateNumber = DBUtils.query(mapper -> mapper.selectDuplicateBookTypeNumber(categoryName, categoryId));
            if (duplicateNumber != null) {
                AlertHelper.showError("输入冲突", "类别名称已被其他类别使用，编号：" + duplicateNumber);
                return;
            }
            if (DBUtils.transaction(mapper -> mapper.updateBookType(type)) > 0) {
                AlertHelper.showInfo("操作成功", "类别信息已更新！");
                loadAllCategories();
            }
        } catch (SQLException e) {
            AlertHelper.showError("更新失败", e.getMessage());
        }
    }

    private void deleteCategory() {
        String categoryId = categoryIdField.getText().trim();
        if (categoryId.isEmpty()) {
            AlertHelper.showWarn("提示", "请先选择一个类别");
            return;
        }
        try {
            if (DBUtils.query(mapper -> mapper.countBooksByCategory(categoryId) > 0)) {
                AlertHelper.showError("无法删除", "该类别已被图书引用，请先修改相关图书的类别！");
                return;
            }
            if (!AlertHelper.showConfirm("确认删除", "确定要删除类别 " + categoryId + " 吗？")) {
                return;
            }
            if (DBUtils.transaction(mapper -> mapper.deleteBookType(categoryId)) > 0) {
                AlertHelper.showInfo("操作成功", "类别删除成功！");
                loadAllCategories();
                clearForm();
            }
        } catch (SQLException e) {
            AlertHelper.showError("删除失败", e.getMessage());
        }
    }
}
