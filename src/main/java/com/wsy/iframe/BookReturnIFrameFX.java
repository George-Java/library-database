package com.wsy.iframe;

import com.wsy.entity.Borrow;
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
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookReturnIFrameFX {
    private final ObservableList<Borrow> borrowRecords = FXCollections.observableArrayList();
    private final TextField[] infoFields = new TextField[5];
    private TextField readerField;
    private TableView<Borrow> table;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("图书归还管理");
        dialog.setWidth(950);
        dialog.setHeight(600);

        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 32));
        logoBox.getChildren().add(UIUtils.createLabel("图书归还管理", 22, true, Color.rgb(26, 35, 126)));

        readerField = new TextField();
        readerField.setPromptText("输入读者编号后回车查询");
        Button queryBtn = UIUtils.styledBtn("查询", "#1976d2", "#64b5f6");
        HBox searchBox = new HBox(10, UIUtils.createLabel("读者编号：", 16, true, Color.BLACK), readerField, queryBtn);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        queryBtn.setOnAction(event -> loadBorrowRecords());
        readerField.setOnAction(event -> loadBorrowRecords());

        table = buildTable();
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> fillReturnInfo(newVal));

        GridPane infoGrid = buildInfoGrid(dialog);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(new VBox(10, logoBox, searchBox));
        root.setCenter(new HBox(20, table, infoGrid));

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private TableView<Borrow> buildTable() {
        TableView<Borrow> result = new TableView<>(borrowRecords);
        result.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addColumn(result, "借阅ID", "borrowId");
        addColumn(result, "图书ISBN", "bookISBN");
        addColumn(result, "读者编号", "readerNumber");
        addColumn(result, "借阅日期", "borrowDate");
        result.setPlaceholder(new Label("请输入读者编号并查询"));
        return result;
    }

    private void addColumn(TableView<Borrow> tableView, String title, String property) {
        TableColumn<Borrow, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        tableView.getColumns().add(column);
    }

    private GridPane buildInfoGrid(Stage dialog) {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(22, 18, 22, 18));
        String[] labels = {"借书日期：", "规定天数：", "实际天数：", "超出天数：", "罚款金额："};
        for (int i = 0; i < labels.length; i++) {
            grid.add(UIUtils.createLabel(labels[i], 15, true, Color.BLACK), 0, i);
            infoFields[i] = new TextField();
            infoFields[i].setEditable(false);
            grid.add(infoFields[i], 1, i);
        }
        Button returnBtn = UIUtils.styledBtn("图书归还", "#388e3c", "#43a047");
        Button exitBtn = UIUtils.styledBtn("退出", "#78909c", "#b0bec5");
        returnBtn.setOnAction(event -> handleReturn());
        exitBtn.setOnAction(event -> dialog.close());
        grid.add(new HBox(22, returnBtn, exitBtn), 0, 5, 2, 1);
        return grid;
    }

    private void loadBorrowRecords() {
        String readerNum = readerField.getText().trim();
        borrowRecords.clear();
        if (readerNum.isEmpty()) return;
        try {
            borrowRecords.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectUnreturnedBorrowsByReader(readerNum))));
        } catch (SQLException e) {
            AlertHelper.showError("查询失败", e.getMessage());
        }
    }

    private void fillReturnInfo(Borrow record) {
        if (record == null) {
            for (TextField field : infoFields) field.clear();
            return;
        }
        Timestamp borrowTs = record.getBorrowDate();
        infoFields[0].setText(borrowTs == null ? "" : borrowTs.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        int ruleDays = fetchRuleDays(record.getBookISBN());
        long realDays = calcDays(borrowTs);
        long extraDays = Math.max(0, realDays - ruleDays);
        double fine = extraDays * fetchFinePerDay(record.getBookISBN());
        infoFields[1].setText(String.valueOf(ruleDays));
        infoFields[2].setText(String.valueOf(realDays));
        infoFields[3].setText(String.valueOf(extraDays));
        infoFields[4].setText(String.format("%.2f", fine));
    }

    private void handleReturn() {
        Borrow record = table.getSelectionModel().getSelectedItem();
        if (record == null) {
            AlertHelper.showWarn("提示", "请先选择要归还的借阅记录");
            return;
        }
        try {
            DBUtils.transaction(mapper -> {
                mapper.markBorrowReturned(record.getBorrowId());
                mapper.addStockQuantity(record.getBookISBN(), 1);
                return null;
            });
            AlertHelper.showInfo("归还成功", "该图书已归还！");
            loadBorrowRecords();
            for (TextField field : infoFields) field.clear();
        } catch (SQLException e) {
            AlertHelper.showError("归还失败", e.getMessage());
        }
    }

    private int fetchRuleDays(String bookISBN) {
        try {
            Integer days = DBUtils.query(mapper -> mapper.selectBookRuleDays(bookISBN));
            return days == null ? 30 : days;
        } catch (SQLException ignored) {
            return 30;
        }
    }

    private double fetchFinePerDay(String bookISBN) {
        try {
            Double fine = DBUtils.query(mapper -> mapper.selectBookFinePerDay(bookISBN));
            return fine == null ? 0.5 : fine;
        } catch (SQLException ignored) {
            return 0.5;
        }
    }

    private long calcDays(Timestamp borrowDate) {
        if (borrowDate == null) return 1;
        long days = Duration.between(borrowDate.toLocalDateTime(), LocalDateTime.now()).toDays();
        return Math.max(1, days);
    }
}
