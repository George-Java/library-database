package com.wsy.iframe;

import com.wsy.entity.BookInfo;
import com.wsy.entity.BookType;
import com.wsy.entity.Operator;
import com.wsy.entity.Order;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

public class BookAcceptanceIFrameFX {
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();
    private final Map<Integer, Operator> operatorMap = new HashMap<>();
    private final Map<String, BookInfo> bookInfoMap = new HashMap<>();
    private final Map<String, BookType> bookTypeMap = new HashMap<>();
    private final TextField[] formFields = new TextField[9];
    private RadioButton yesRadio;
    private RadioButton noRadio;
    private Order selectedOrder;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("图书验收");
        dialog.setWidth(1100);
        dialog.setHeight(750);

        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 38));
        logoBox.getChildren().add(UIUtils.createLabel("图书验收", 22, true, Color.rgb(26, 35, 126)));

        loadOperatorMap();
        loadBookInfoMap();
        loadBookTypeMap();

        TableView<Order> table = buildTable();
        loadUnacceptedOrders();
        VBox formBox = buildForm();

        Button acceptBtn = UIUtils.styledBtn("验收", "#388e3c", "#43a047");
        Button deleteBtn = UIUtils.styledBtn("删除", "#d32f2f", "#ff6659");
        Button exitBtn = UIUtils.styledBtn("退出", "#78909c", "#b0bec5");
        acceptBtn.setOnAction(event -> acceptOrder());
        deleteBtn.setOnAction(event -> deleteOrder());
        exitBtn.setOnAction(event -> dialog.close());
        HBox buttons = new HBox(28, acceptBtn, deleteBtn, exitBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> fillForm(newVal));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setTop(logoBox);
        root.setCenter(new HBox(36, table, new VBox(20, formBox, buttons)));
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private TableView<Order> buildTable() {
        TableView<Order> table = new TableView<>(orderList);
        table.setPrefWidth(660);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Order, String> dateCol = new TableColumn<>("订购日期");
        dateCol.setCellValueFactory(i -> new SimpleStringProperty(i.getValue().getDate() == null
                ? ""
                : i.getValue().getDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        TableColumn<Order, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(i -> new SimpleStringProperty(i.getValue().getBookISBN()));
        TableColumn<Order, String> nameCol = new TableColumn<>("书名");
        nameCol.setCellValueFactory(i -> {
            BookInfo book = bookInfoMap.get(i.getValue().getBookISBN());
            return new SimpleStringProperty(book == null ? "" : book.getBookname());
        });
        TableColumn<Order, String> qtyCol = new TableColumn<>("数量");
        qtyCol.setCellValueFactory(i -> new SimpleStringProperty(String.valueOf(i.getValue().getNumber())));
        TableColumn<Order, String> operatorCol = new TableColumn<>("操作员");
        operatorCol.setCellValueFactory(i -> {
            Operator operator = operatorMap.get(i.getValue().getOperator());
            return new SimpleStringProperty(operator == null ? String.valueOf(i.getValue().getOperator()) : operator.getName());
        });
        table.getColumns().addAll(dateCol, isbnCol, nameCol, qtyCol, operatorCol);
        table.setPlaceholder(new Label("暂无未验收订单"));
        return table;
    }

    private VBox buildForm() {
        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(16);
        form.setPadding(new Insets(24));
        String[] labels = {"订购日期：", "书籍ISBN：", "书籍名称：", "图书类别：", "订购数量：", "折扣：", "图书原价：", "订购价格：", "操作员："};
        for (int i = 0; i < labels.length; i++) {
            form.add(UIUtils.createLabel(labels[i], 16, true, Color.BLACK), 0, i);
            formFields[i] = new TextField();
            formFields[i].setEditable(false);
            formFields[i].setPrefWidth(260);
            form.add(formFields[i], 1, i);
        }
        yesRadio = new RadioButton("是");
        noRadio = new RadioButton("否");
        ToggleGroup group = new ToggleGroup();
        yesRadio.setToggleGroup(group);
        noRadio.setToggleGroup(group);
        noRadio.setSelected(true);
        form.add(UIUtils.createLabel("是否验收：", 16, true, Color.BLACK), 0, labels.length);
        form.add(new HBox(30, yesRadio, noRadio), 1, labels.length);
        return new VBox(form);
    }

    private void loadOperatorMap() {
        operatorMap.clear();
        try {
            for (Operator operator : DBUtils.query(mapper -> mapper.selectAllOperators())) {
                operatorMap.put(operator.getId(), operator);
            }
        } catch (SQLException ignored) {
            operatorMap.clear();
        }
    }

    private void loadBookInfoMap() {
        bookInfoMap.clear();
        try {
            for (BookInfo book : DBUtils.query(mapper -> mapper.selectAllBookInfos())) {
                bookInfoMap.put(book.getBookISBN(), book);
            }
        } catch (SQLException ignored) {
            bookInfoMap.clear();
        }
    }

    private void loadBookTypeMap() {
        bookTypeMap.clear();
        try {
            for (BookType type : DBUtils.query(mapper -> mapper.selectAllBookTypes())) {
                bookTypeMap.put(type.getNumber(), type);
            }
        } catch (SQLException ignored) {
            bookTypeMap.clear();
        }
    }

    private void loadUnacceptedOrders() {
        orderList.clear();
        try {
            orderList.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.selectUnacceptedOrders())));
        } catch (SQLException e) {
            AlertHelper.showError("加载订单失败", e.getMessage());
        }
    }

    private void fillForm(Order order) {
        if (order == null) {
            clearForm();
            return;
        }
        selectedOrder = order;
        BookInfo book = bookInfoMap.get(order.getBookISBN());
        BookType type = book == null ? null : bookTypeMap.get(book.getCategory());
        Operator operator = operatorMap.get(order.getOperator());
        double price = book == null ? 0.0 : book.getPrice();
        double total = price * order.getNumber() * (1 - order.getDiscount());
        formFields[0].setText(order.getDate() == null ? "" : order.getDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        formFields[1].setText(order.getBookISBN());
        formFields[2].setText(book == null ? "" : book.getBookname());
        formFields[3].setText(type == null ? "" : type.getTypeName());
        formFields[4].setText(String.valueOf(order.getNumber()));
        formFields[5].setText(String.valueOf(order.getDiscount()));
        formFields[6].setText(String.format("%.2f", price));
        formFields[7].setText(String.format("%.2f", total));
        formFields[8].setText(operator == null ? "" : operator.getName());
        noRadio.setSelected(true);
    }

    private void clearForm() {
        selectedOrder = null;
        for (TextField field : formFields) field.clear();
        noRadio.setSelected(true);
    }

    private void acceptOrder() {
        if (selectedOrder == null) {
            AlertHelper.showWarn("提示", "请先选择要验收的订单");
            return;
        }
        if (!yesRadio.isSelected()) {
            AlertHelper.showWarn("提示", "请选择“是”后再点击验收");
            return;
        }
        try {
            DBUtils.transaction(mapper -> {
                int rows = mapper.markOrderAccepted(selectedOrder.getOrderId());
                if (rows == 0) throw new SQLException("未找到订单");
                int updatedRows = mapper.addStockQuantity(selectedOrder.getBookISBN(), selectedOrder.getNumber());
                if (updatedRows == 0) mapper.insertStockpile(selectedOrder.getBookISBN(), selectedOrder.getNumber());
                return null;
            });
            loadUnacceptedOrders();
            clearForm();
            AlertHelper.showInfo("操作成功", "图书验收成功，库存已更新！");
        } catch (SQLException e) {
            AlertHelper.showError("数据库错误", e.getMessage());
        }
    }

    private void deleteOrder() {
        if (selectedOrder == null) {
            AlertHelper.showWarn("提示", "请先选择要删除的订单");
            return;
        }
        if (!AlertHelper.showConfirm("确认删除", "确定要删除此订购订单吗？")) {
            return;
        }
        try {
            if (DBUtils.transaction(mapper -> mapper.deleteOrder(selectedOrder.getOrderId())) > 0) {
                loadUnacceptedOrders();
                clearForm();
                AlertHelper.showInfo("删除成功", "订购订单已删除。");
            }
        } catch (SQLException e) {
            AlertHelper.showError("数据库错误", e.getMessage());
        }
    }
}
