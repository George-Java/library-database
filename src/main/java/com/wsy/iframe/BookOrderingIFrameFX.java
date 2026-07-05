package com.wsy.iframe;

import com.wsy.entity.BookInfo;
import com.wsy.entity.Order;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BookOrderingIFrameFX {
    // 数据映射
    private final Map<String, String> categoryMap = new HashMap<>(); // 类别名称->编号
    private final Map<String, Integer> operatorMap = new HashMap<>(); // 姓名->ID

    // 控件
    private TextField isbnField, nameField, authorField, translatorField, priceField;
    private ComboBox<String> categoryBox, publisherBox;
    private DatePicker publishDatePicker;
    private Spinner<Integer> orderQtySpinner;
    private ComboBox<String> operatorBox;
    private RadioButton yesRadio;
    private Spinner<Double> discountSpinner;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("新书订购管理");
        dialog.setWidth(740);
        dialog.setHeight(480);
        dialog.setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // 顶部Logo与标题
        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 36));
        logoBox.getChildren().add(UIUtils.createLabel("新书订购管理", 22, true, Color.rgb(26, 35, 126)));
        root.setTop(logoBox);

        // 加载数据
        loadCategoriesAndOperators();
        String[] publishers = loadPublishers();

        // ========== 图书信息区 ==========
        GridPane bookGrid = new GridPane();
        bookGrid.setHgap(24);
        bookGrid.setVgap(15);
        bookGrid.setPadding(new Insets(20, 15, 15, 15));
        bookGrid.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.98), new CornerRadii(8), Insets.EMPTY)));
        bookGrid.setEffect(new DropShadow(7, Color.gray(0, 0.08)));

        isbnField = new TextField();
        isbnField.setPromptText("13位ISBN");
        nameField = new TextField();
        nameField.setPromptText("必填");
        categoryBox = new ComboBox<>(FXCollections.observableArrayList(categoryMap.keySet()));
        if (!categoryBox.getItems().isEmpty()) categoryBox.getSelectionModel().selectFirst();
        authorField = new TextField();
        translatorField = new TextField();
        publisherBox = new ComboBox<>(FXCollections.observableArrayList(publishers));
        if (!publisherBox.getItems().isEmpty()) publisherBox.getSelectionModel().selectFirst();
        publishDatePicker = new DatePicker(LocalDate.now());
        priceField = new TextField();

        bookGrid.add(UIUtils.createLabel("书籍ISBN：", 14, true, Color.BLACK), 0, 0);
        bookGrid.add(isbnField, 1, 0);
        bookGrid.add(UIUtils.createLabel("图书名称：", 14, true, Color.BLACK), 2, 0);
        bookGrid.add(nameField, 3, 0);

        bookGrid.add(UIUtils.createLabel("图书类别：", 14, true, Color.BLACK), 0, 1);
        bookGrid.add(categoryBox, 1, 1);
        bookGrid.add(UIUtils.createLabel("作者：", 14, true, Color.BLACK), 2, 1);
        bookGrid.add(authorField, 3, 1);

        bookGrid.add(UIUtils.createLabel("出版社：", 14, true, Color.BLACK), 0, 2);
        bookGrid.add(publisherBox, 1, 2);
        bookGrid.add(UIUtils.createLabel("译者：", 14, true, Color.BLACK), 2, 2);
        bookGrid.add(translatorField, 3, 2);

        bookGrid.add(UIUtils.createLabel("出版日期：", 14, true, Color.BLACK), 0, 3);
        bookGrid.add(publishDatePicker, 1, 3);
        bookGrid.add(UIUtils.createLabel("图书价格：", 14, true, Color.BLACK), 2, 3);
        bookGrid.add(priceField, 3, 3);

        // ========== 订购信息区 ==========
        GridPane orderGrid = new GridPane();
        orderGrid.setHgap(24);
        orderGrid.setVgap(15);
        orderGrid.setPadding(new Insets(16, 15, 12, 15));
        orderGrid.setBackground(new Background(new BackgroundFill(
                Color.rgb(250, 250, 255, 0.97), new CornerRadii(8), Insets.EMPTY)));

        orderQtySpinner = new Spinner<>(1, 9999, 1, 1);
        orderQtySpinner.setEditable(true);
        operatorBox = new ComboBox<>(FXCollections.observableArrayList(operatorMap.keySet()));
        if (!operatorBox.getItems().isEmpty()) operatorBox.getSelectionModel().selectFirst();
        yesRadio = new RadioButton("是");
        RadioButton noRadio = new RadioButton("否");
        ToggleGroup acceptGroup = new ToggleGroup();
        yesRadio.setToggleGroup(acceptGroup);
        noRadio.setToggleGroup(acceptGroup);
        yesRadio.setSelected(true);
        discountSpinner = new Spinner<>(0.0, 1.0, 1.0, 0.01);
        discountSpinner.setEditable(true);

        orderGrid.add(UIUtils.createLabel("订购数量：", 14, true, Color.BLACK), 0, 0);
        orderGrid.add(orderQtySpinner, 1, 0);

        orderGrid.add(UIUtils.createLabel("操作员：", 14, true, Color.BLACK), 2, 0);
        orderGrid.add(operatorBox, 3, 0);

        orderGrid.add(UIUtils.createLabel("是否验收：", 14, true, Color.BLACK), 0, 1);
        HBox acceptBox = new HBox(14, yesRadio, noRadio);
        orderGrid.add(acceptBox, 1, 1);

        orderGrid.add(UIUtils.createLabel("折扣：", 14, true, Color.BLACK), 2, 1);
        orderGrid.add(discountSpinner, 3, 1);

        // ========== 按钮区 ==========
        HBox btnBox = new HBox(32);
        btnBox.setPadding(new Insets(18, 0, 0, 0));
        btnBox.setAlignment(Pos.CENTER);
        Button addBtn = UIUtils.styledBtn("添加", "#388e3c", "#43a047");
        Button exitBtn = UIUtils.styledBtn("退出", "#78909c", "#b0bec5");
        btnBox.getChildren().addAll(addBtn, exitBtn);

        // ========== 主布局 ==========
        VBox centerBox = new VBox(16, bookGrid, orderGrid, btnBox);
        root.setCenter(centerBox);

        // 添加事件
        addBtn.setOnAction(event -> handleAdd());
        exitBtn.setOnAction(event -> dialog.close());

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    /**
     * 加载类别和操作员数据
     */
    private void loadCategoriesAndOperators() {
        categoryMap.clear();
        operatorMap.clear();
        try {
            for (var type : DBUtils.query(mapper -> mapper.selectAllBookTypes())) {
                categoryMap.put(type.getTypeName(), type.getNumber());
            }
            for (var operator : DBUtils.query(mapper -> mapper.selectAllOperators())) {
                operatorMap.put(operator.getName(), operator.getId());
            }
        } catch (SQLException e) {
            categoryMap.put("计算机", "TP0001");
            categoryMap.put("文学", "I00001");
            operatorMap.put("管理员", 101);
        }
    }
    /**
     * 加载出版社列表
     */
    private String[] loadPublishers() {
        List<String> publishers = new ArrayList<>();
        try {
            publishers.addAll(DBUtils.query(mapper -> mapper.selectDistinctPublishers()));
            if (publishers.isEmpty()) {
                publishers.addAll(Arrays.asList("机械工业出版社", "清华大学出版社", "人民邮电出版社"));
            }
        } catch (SQLException e) {
            publishers.addAll(Arrays.asList("机械工业出版社", "清华大学出版社", "人民邮电出版社"));
        }
        return publishers.toArray(new String[0]);
    }
    /**
     * 处理添加订购逻辑
     * 用 BookInfo 和 Order 实体类封装数据
     */
    private void handleAdd() {
        // 校验
        String isbn = isbnField.getText().trim();
        String bookName = nameField.getText().trim();
        String categoryName = categoryBox.getValue();
        String author = authorField.getText().trim();
        String publisher = publisherBox.getValue();
        String translator = translatorField.getText().trim();
        LocalDate pubDate = publishDatePicker.getValue();
        String priceStr = priceField.getText().trim();
        Integer orderQty = orderQtySpinner.getValue();
        String operator = operatorBox.getValue();
        boolean accepted = yesRadio.isSelected();
        Double discount = discountSpinner.getValue();

        if (isbn.isEmpty()) {
            AlertHelper.showError("输入错误", "书籍ISBN不能为空");
            return;
        }
        if (isbn.length() != 13) {
            AlertHelper.showError("输入错误", "书籍ISBN必须为13位");
            return;
        }
        if (bookName.isEmpty()) {
            AlertHelper.showError("输入错误", "图书名称不能为空");
            return;
        }
        if (orderQty == null || orderQty <= 0) {
            AlertHelper.showError("输入错误", "订购数量必须为正整数");
            return;
        }

        // 处理价格
        double price = 0.0;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException nfe) {
                AlertHelper.showError("输入错误", "价格格式不正确");
                return;
            }
        }

        try {
            String categoryCode = categoryMap.get(categoryName);
            if (categoryCode == null) {
                AlertHelper.showError("错误", "无效的图书类别: " + categoryName);
                return;
            }
            Integer operatorId = operatorMap.get(operator);
            if (operatorId == null) {
                AlertHelper.showError("错误", "无效的操作员: " + operator);
                return;
            }

            BookInfo book = new BookInfo(
                    isbn, categoryCode, bookName,
                    author, publisher, translator,
                    pubDate == null ? null : Date.valueOf(pubDate),
                    price
            );
            Order order = new Order(
                    0,
                    isbn,
                    Timestamp.valueOf(LocalDateTime.now()),
                    orderQty,
                    operatorId,
                    accepted ? 1 : 0,
                    discount == null ? 1.0f : discount.floatValue()
            );

            DBUtils.transaction(mapper -> {
                if (mapper.countBookInfoByIsbn(isbn) == 0) {
                    mapper.insertBookInfo(book);
                    mapper.insertStockpile(isbn, 0);
                }
                mapper.insertOrder(order);
                if (accepted) {
                    int rows = mapper.addStockQuantity(isbn, orderQty);
                    if (rows == 0) {
                        mapper.insertStockpile(isbn, orderQty);
                    }
                }
                return null;
            });
            AlertHelper.showInfo("操作成功", "新书订购成功！");
        } catch (SQLException ex) {
            AlertHelper.showError("数据库错误", ex.getMessage());
        }    }

}
