package com.wsy.iframe;

import com.wsy.entity.BookInfo;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

public class BookSearchIFrameFX {
    // 控件
    private TextField searchField;
    private ComboBox<String> searchOptions;
    private final ObservableList<BookInfo> tableRows = FXCollections.observableArrayList();
    private TextArea detailArea;

    public void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.setTitle("图书搜索");
        dialog.setWidth(950);
        dialog.setHeight(620);
        dialog.setResizable(true);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));

        // 顶部Logo+条件
        HBox logoBox = new HBox(20);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 32));
        logoBox.getChildren().add(UIUtils.createLabel("图书搜索", 22, true, Color.rgb(26, 35, 126)));

        HBox searchBox = new HBox(14);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label searchLab = UIUtils.createLabel("搜索关键词：", 15, true, Color.BLACK);
        searchField = new TextField();
        searchField.setFont(Font.font("楷体", 15));
        searchField.setPromptText("请输入关键词");
        searchField.setPrefWidth(240);
        searchOptions = new ComboBox<>();
        searchOptions.getItems().addAll("按书名搜索", "按ISBN搜索", "按作者搜索");
        searchOptions.getSelectionModel().selectFirst();
        searchBox.getChildren().addAll(searchLab, searchField, searchOptions);

        VBox topBox = new VBox(logoBox, searchBox);
        topBox.setSpacing(10);
        topBox.setPadding(new Insets(0, 0, 8, 0));
        root.setTop(topBox);

        // 中心区：左右分栏
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.68);

        // 表格
        TableView<BookInfo> table = buildTable();
        VBox tableBox = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // 详情
        VBox detailBox = new VBox();
        detailBox.setPadding(new Insets(10));
        detailBox.setSpacing(6);
        detailBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.98), new CornerRadii(8), Insets.EMPTY)));
        detailBox.setEffect(new DropShadow(6, Color.gray(0, 0.09)));
        Label detailLab = UIUtils.createLabel("图书详情", 15, true, Color.web("#1A237E"));
        detailArea = new TextArea();
        detailArea.setEditable(false);
        detailArea.setFont(Font.font("宋体", 15));
        detailArea.setPrefHeight(180);
        detailArea.setWrapText(true);
        detailBox.getChildren().addAll(detailLab, detailArea);

        splitPane.getItems().addAll(tableBox, detailBox);
        root.setCenter(splitPane);

        // 底部按钮
        HBox btnBox = new HBox(24);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.setPadding(new Insets(14, 32, 0, 32));
        Button searchBtn = UIUtils.styledBtn("搜索", "#1565c0", "#42a5f5");
        Button resetBtn = UIUtils.styledBtn("重置", "#757575", "#bdbdbd");
        btnBox.getChildren().addAll(searchBtn, resetBtn);
        root.setBottom(btnBox);

        // 搜索事件
        searchBtn.setOnAction(event -> {
            String searchType = searchOptions.getValue();
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                AlertHelper.showWarn("提示", "请输入搜索关键词");
                return;
            }
            searchBooks(searchType, keyword);
        });

        // 重置事件
        resetBtn.setOnAction(event -> {
            searchField.setText("");
            tableRows.clear();
            detailArea.setText("");
        });

        // 表格选中-详情
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) showBookDetails(newV.getBookISBN());
        });

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    // 构建表格
    private TableView<BookInfo> buildTable() {
        TableView<BookInfo> t = new TableView<>(tableRows);
        t.setPrefHeight(340);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BookInfo, String> colISBN = new TableColumn<>("ISBN");
        colISBN.setCellValueFactory(new PropertyValueFactory<>("bookISBN"));
        colISBN.setMinWidth(120);

        TableColumn<BookInfo, String> colName = new TableColumn<>("书名");
        colName.setCellValueFactory(new PropertyValueFactory<>("bookname"));
        colName.setMinWidth(180);

        TableColumn<BookInfo, String> colWriter = new TableColumn<>("作者");
        colWriter.setCellValueFactory(new PropertyValueFactory<>("writer"));
        colWriter.setMinWidth(110);

        TableColumn<BookInfo, String> colPublisher = new TableColumn<>("出版社");
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colPublisher.setMinWidth(120);

        TableColumn<BookInfo, Date> colDate = new TableColumn<>("出版日期");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setMinWidth(100);

        t.getColumns().addAll(colISBN, colName, colWriter, colPublisher, colDate);
        t.setPlaceholder(new Label("请搜索图书"));
        return t;
    }

    // 搜索图书
    private void searchBooks(String searchType, String keyword) {
        try {
            tableRows.clear();
            if ("按书名搜索".equals(searchType)) {
                tableRows.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.searchBookInfosByName(keyword))));
            } else if ("按ISBN搜索".equals(searchType)) {
                tableRows.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.searchBookInfosByIsbn(keyword))));
            } else if ("按作者搜索".equals(searchType)) {
                tableRows.addAll(new java.util.ArrayList<>(DBUtils.query(mapper -> mapper.searchBookInfosByAuthor(keyword))));
            }
            if (tableRows.isEmpty()) {
                AlertHelper.showInfo("结果", "没有找到匹配的图书");
                detailArea.setText("");
            }
        } catch (SQLException e) {
            AlertHelper.showError("搜索失败", e.getMessage());
        }
    }
    // 显示图书详情
    private void showBookDetails(String isbn) {
        try {
            Map<String, Object> row = DBUtils.query(mapper -> mapper.selectBookDetails(isbn));
            if (row == null) {
                detailArea.setText("");
                return;
            }
            String translator = (String) row.get("translator");
            if (translator == null || translator.trim().isEmpty()) translator = "无";
            Object stockQuantity = row.get("stockQuantity") == null ? "0" : row.get("stockQuantity");
            String details = MessageFormat.format(
                    """
                            ISBN: {0}
                            书名: {1}
                            类别: {2}
                            作者: {3}
                            译者: {4}
                            出版社: {5}
                            出版日期: {6}
                            价格: ￥{7,number,#,##0.00}
                            库存: {8} 本
                            """,
                    row.get("bookISBN"),
                    row.get("bookname"),
                    row.get("categoryName"),
                    row.get("writer"),
                    translator,
                    row.get("publisher"),
                    row.get("date"),
                    row.get("price"),
                    stockQuantity
            );
            detailArea.setText(details);
        } catch (SQLException e) {
            detailArea.setText("获取图书详情失败: " + e.getMessage());
        }
    }}


