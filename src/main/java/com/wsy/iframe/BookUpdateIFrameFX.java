package com.wsy.iframe;

import com.wsy.entity.BookInfo;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BookUpdateIFrameFX {

    private final Map<String, String> categoryMap = new HashMap<>();        // 编号->名称
    private final Map<String, String> reverseCategoryMap = new HashMap<>();// 名称->编号
    private final ObservableList<String> publishers = FXCollections.observableArrayList();

    private TableView<BookInfo> tableView;
    private TextField isbnField;
    private ComboBox<String> categoryBox;
    private TextField nameField;
    private TextField authorField;
    private ComboBox<String> publisherBox;
    private TextField translatorField;
    private DatePicker datePicker;
    private TextField priceField;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("图书信息修改与删除");
        dialog.setWidth(900);
        dialog.setHeight(600);

        // 顶部：横幅
        HBox topBox = getHBox();

        // 中间：表格
        tableView = new TableView<>();
        tableView.setPlaceholder(new Label("无数据"));
        tableView.setPrefHeight(300);
        initTableColumns();
        loadCategoriesAndPublishers();
        loadTableData();

        // 选中行时，填充下面的表单
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, sel) -> {
            if (sel != null) fillForm(sel);
        });

        // 下方：表单 + 按钮
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        isbnField = new TextField();
        isbnField.setDisable(true);
        categoryBox = new ComboBox<>(FXCollections.observableArrayList(categoryMap.values()));
        nameField = new TextField();
        authorField = new TextField();
        publisherBox = new ComboBox<>(publishers);
        publisherBox.setEditable(true);
        translatorField = new TextField();
        datePicker = new DatePicker();
        priceField = new TextField();

        form.add(UIUtils.createLabel("ISBN：", 14, true, Color.BLACK), 0, 0);
        form.add(isbnField, 1, 0);
        form.add(UIUtils.createLabel("类别：", 14, true, Color.BLACK), 2, 0);
        form.add(categoryBox, 3, 0);

        form.add(UIUtils.createLabel("书名：", 14, true, Color.BLACK), 0, 1);
        form.add(nameField, 1, 1);
        form.add(UIUtils.createLabel("作者：", 14, true, Color.BLACK), 2, 1);
        form.add(authorField, 3, 1);

        form.add(UIUtils.createLabel("出版社：", 14, true, Color.BLACK), 0, 2);
        form.add(publisherBox, 1, 2);
        form.add(UIUtils.createLabel("译者：", 14, true, Color.BLACK), 2, 2);
        form.add(translatorField, 3, 2);

        form.add(UIUtils.createLabel("出版日期：", 14, true, Color.BLACK), 0, 3);
        form.add(datePicker, 1, 3);
        form.add(UIUtils.createLabel("单价：", 14, true, Color.BLACK), 2, 3);
        form.add(priceField, 3, 3);

        Button updateBtn = UIUtils.styledBtn("修改", "#388e3c", "#43a047");
        Button deleteBtn = UIUtils.styledBtn("删除", "#d32f2f", "#ff6659");
        HBox btnBox = new HBox(20, updateBtn, deleteBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.setPadding(new Insets(10));

        updateBtn.setOnAction(event -> updateBook());
        deleteBtn.setOnAction(event -> deleteBook());

        VBox bottomBox = new VBox(form, btnBox);

        // 整合
        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(tableView);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }

    private static HBox getHBox() {
        ImageView banner = UIUtils.createImageView("/res/matching-map.png", BookUpdateIFrameFX.class, 0, 100, true);
        HBox topBox = new HBox(banner);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        topBox.setStyle("-fx-background-color: #1e3a8a;");
        return topBox;
    }

    private void initTableColumns() {
        String[] cols = {"bookISBN", "category", "bookname", "writer", "publisher", "translator", "date", "price"};
        String[] names = {"ISBN", "类别", "书名", "作者", "出版社", "译者", "出版日期", "单价"};
        for (int i = 0; i < cols.length; i++) {
            TableColumn<BookInfo, ?> col = new TableColumn<>(names[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(cols[i]));
            col.setPrefWidth(100);
            tableView.getColumns().add(col);
        }
    }

    private void loadCategoriesAndPublishers() {
        try {
            for (var type : DBUtils.query(mapper -> mapper.selectAllBookTypes())) {
                categoryMap.put(type.getNumber(), type.getTypeName());
                reverseCategoryMap.put(type.getTypeName(), type.getNumber());
            }
            publishers.setAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectDistinctPublishers())));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void loadTableData() {
        ObservableList<BookInfo> data = FXCollections.observableArrayList();
        try {
            data.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectBooksForUpdate())));
            tableView.setItems(data);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void fillForm(BookInfo row) {
        isbnField.setText(row.getBookISBN());
        categoryBox.getSelectionModel().select(categoryMap.get(row.getCategory()));
        nameField.setText(row.getBookname());
        authorField.setText(row.getWriter());
        publisherBox.getSelectionModel().select(row.getPublisher());
        translatorField.setText(row.getTranslator());
        if (row.getDate() != null)
            datePicker.setValue(row.getDate().toLocalDate());
        priceField.setText(String.valueOf(row.getPrice()));
    }

    private void updateBook() {
        BookInfo sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String isbn = sel.getBookISBN();
        String catNo = reverseCategoryMap.get(categoryBox.getValue());
        try {
            BookInfo book = new BookInfo(
                    isbn,
                    catNo,
                    nameField.getText(),
                    authorField.getText(),
                    publisherBox.getValue(),
                    translatorField.getText(),
                    datePicker.getValue() == null ? null : Date.valueOf(datePicker.getValue()),
                    Double.parseDouble(priceField.getText())
            );
            int r = DBUtils.transaction(mapper -> mapper.updateBookInfo(book));
            if (r > 0) {
                AlertHelper.showInfo("提示", "修改成功");
                loadTableData();
            }
        } catch (Exception ex) {
            AlertHelper.showError("修改失败", ex.getMessage());
        }    }

    private void deleteBook() {
        BookInfo sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String isbn = sel.getBookISBN();
        if (!AlertHelper.showConfirm("确认", "确定删除ISBN=" + isbn + "？")) return;
        try {
            DBUtils.transaction(mapper -> {
                mapper.deleteStockpile(isbn);
                mapper.deleteBookInfo(isbn);
                return null;
            });
            AlertHelper.showInfo("提示", "删除成功");
            loadTableData();
        } catch (Exception ex) {
            AlertHelper.showError("删除失败", ex.getMessage());
        }    }
}
