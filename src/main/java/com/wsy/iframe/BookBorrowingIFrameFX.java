package com.wsy.iframe;

import com.wsy.entity.Borrow;
import com.wsy.entity.Operator;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BookBorrowingIFrameFX {
    private final Map<String, Integer> operatorMap = new HashMap<>();
    private final ObservableList<Borrow> borrowRecords = FXCollections.observableArrayList();
    private final TextField[] topFields = new TextField[8];
    private ComboBox<String> operatorBox;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("图书借阅");
        dialog.setWidth(1100);
        dialog.setHeight(670);

        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 38));
        logoBox.getChildren().add(UIUtils.createLabel("图书借阅", 22, true, Color.rgb(26, 35, 126)));

        GridPane topGrid = buildTopForm();
        TableView<Borrow> table = buildTable();
        HBox bottom = buildBottomBar();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setTop(logoBox);
        root.setCenter(new VBox(16, topGrid, table, bottom));

        loadOperatorMap();
        loadBorrowRecords();
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private GridPane buildTopForm() {
        String[] labels = {"读者编号：", "书籍ISBN：", "读者姓名：", "书籍名称：", "可借数量：", "书籍类别：", "押金：", "书籍价格："};
        GridPane grid = UIUtils.createFormGrid();
        grid.setPadding(new Insets(18, 10, 10, 10));
        for (int i = 0; i < labels.length; i++) {
            grid.add(UIUtils.createLabel(labels[i], 15, true, Color.BLACK), (i % 4) * 2, i / 4);
            topFields[i] = new TextField();
            if (i > 1) topFields[i].setEditable(false);
            grid.add(topFields[i], (i % 4) * 2 + 1, i / 4);
        }
        topFields[0].setOnAction(event -> fetchAndFillAllInfo());
        topFields[1].setOnAction(event -> fetchAndFillAllInfo());
        return grid;
    }

    private TableView<Borrow> buildTable() {
        TableView<Borrow> table = new TableView<>(borrowRecords);
        table.setPrefHeight(340);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addColumn(table, "借阅编号", "borrowId");
        addColumn(table, "读者编号", "readerNumber");
        addColumn(table, "书籍ISBN", "bookISBN");
        addColumn(table, "操作员ID", "operatorId");
        addColumn(table, "借阅日期", "borrowDate");
        addColumn(table, "是否归还", "isBack");
        table.setPlaceholder(new Label("暂无借阅记录"));
        return table;
    }

    private void addColumn(TableView<Borrow> table, String title, String property) {
        TableColumn<Borrow, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        table.getColumns().add(column);
    }

    private HBox buildBottomBar() {
        TextField timeField = new TextField(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        timeField.setEditable(false);
        operatorBox = new ComboBox<>();
        Button borrowBtn = UIUtils.styledBtn("借出当前图书", "#388e3c", "#43a047");
        Button clearBtn = UIUtils.styledBtn("清除表格显示", "#d32f2f", "#ff6659");
        borrowBtn.setOnAction(event -> borrowBook());
        clearBtn.setOnAction(event -> borrowRecords.clear());
        HBox box = new HBox(18,
                UIUtils.createLabel("当前时间：", 14, true, Color.BLACK), timeField,
                UIUtils.createLabel("操作员：", 14, true, Color.BLACK), operatorBox,
                borrowBtn, clearBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void loadOperatorMap() {
        operatorMap.clear();
        operatorBox.getItems().clear();
        try {
            for (Operator operator : DBUtils.query(mapper -> mapper.selectAllOperators())) {
                operatorMap.put(operator.getName(), operator.getId());
                operatorBox.getItems().add(operator.getName());
            }
            if (!operatorBox.getItems().isEmpty()) operatorBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            operatorMap.put("管理员", 101);
            operatorBox.getItems().add("管理员");
            operatorBox.getSelectionModel().selectFirst();
        }
    }

    private void loadBorrowRecords() {
        borrowRecords.clear();
        try {
            borrowRecords.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectAllBorrows())));
        } catch (SQLException ex) {
            AlertHelper.showError("加载借阅记录失败", ex.getMessage());
        }
    }

    private void fetchAndFillAllInfo() {
        String readerNumber = topFields[0].getText().trim();
        String bookISBN = topFields[1].getText().trim();
        if (readerNumber.isEmpty() || bookISBN.isEmpty()) return;
        try {
            Reader reader = DBUtils.query(mapper -> mapper.selectReaderByBarcode(readerNumber));
            Map<String, Object> book = DBUtils.query(mapper -> mapper.selectBorrowBookInfo(bookISBN));
            if (reader == null || book == null) {
                AlertHelper.showWarn("提示", "读者或图书不存在");
                return;
            }
            topFields[2].setText(reader.getName());
            topFields[3].setText(String.valueOf(book.get("bookname")));
            topFields[5].setText(String.valueOf(book.get("category")));
            topFields[6].setText(String.format("%.2f", reader.getKeepMoney()));
            topFields[7].setText(String.format("%.2f", ((Number) book.get("price")).doubleValue()));
            Object stockValue = book.get("stockQuantity");
            int stock = stockValue == null ? 0 : ((Number) stockValue).intValue();
            int borrowed = DBUtils.query(mapper -> mapper.countActiveBorrowsByReader(readerNumber));
            topFields[4].setText(String.valueOf(Math.max(0, Math.min(stock, reader.getMaxNum() - borrowed))));
        } catch (SQLException e) {
            AlertHelper.showError("查询错误", e.getMessage());
        }
    }

    private void borrowBook() {
        String readerNumber = topFields[0].getText().trim();
        String bookISBN = topFields[1].getText().trim();
        Integer operatorId = operatorMap.get(operatorBox.getValue());
        int canBorrow;
        try {
            canBorrow = Integer.parseInt(topFields[4].getText().trim());
        } catch (Exception ex) {
            AlertHelper.showError("输入错误", "可借数量无效");
            return;
        }
        if (readerNumber.isEmpty() || bookISBN.isEmpty() || operatorId == null || canBorrow <= 0) {
            AlertHelper.showError("输入错误", "请确认读者、图书、操作员和可借数量");
            return;
        }
        try {
            DBUtils.transaction(mapper -> {
                mapper.insertBorrow(readerNumber, bookISBN, operatorId);
                Integer stock = mapper.selectStockQuantity(bookISBN);
                int newStock = (stock == null ? 0 : stock) - 1;
                int rows = mapper.updateStockQuantity(bookISBN, newStock);
                if (rows == 0) mapper.insertStockpile(bookISBN, newStock);
                return null;
            });
            AlertHelper.showInfo("操作成功", "图书借阅成功！");
            loadBorrowRecords();
            for (TextField field : topFields) field.clear();
        } catch (SQLException ex) {
            AlertHelper.showError("借阅失败", ex.getMessage());
        }
    }
}
