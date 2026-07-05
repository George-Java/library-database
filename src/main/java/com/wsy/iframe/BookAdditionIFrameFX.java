package com.wsy.iframe;

import com.wsy.entity.BookInfo;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 图书信息添加页面，已简化，直接用BookInfo实体类封装数据。
 */
public class BookAdditionIFrameFX {

    private final Map<String, String> categoryMap = new HashMap<>();
    private final Map<String, String> publisherMap = new HashMap<>();

    private TextField isbnField;
    private ComboBox<String> categoryComboBox;
    private TextField titleField;
    private TextField authorField;
    private ComboBox<String> publisherComboBox;
    private TextField translatorField;
    private DatePicker publishDatePicker;
    private TextField priceField;
    private Label statusLabel;

    public static void show(Stage owner) {
        new BookAdditionIFrameFX().createAddBookDialog(owner);
    }

    private void createAddBookDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("图书信息添加");
        dialog.setWidth(600);
        dialog.setHeight(550);

        loadCategoriesAndPublishers();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));
        mainLayout.setStyle("-fx-background-color: #f0f5f9;");

        // 顶部图片区域
        ImageView header = UIUtils.createImageView("/res/matching-map.png", getClass(), 560, 120, true);
        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-background-color: #1e3a8a;");
        mainLayout.setTop(headerBox);

        // 中间表单
        GridPane form = createFormGrid();
        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #ffffff; -fx-border-color: #d1d5db;");
        mainLayout.setCenter(scroll);

        // 底部按钮 + 状态
        HBox buttonBox = createButtonBox(dialog);
        mainLayout.setBottom(buttonBox);

        Scene scene = new Scene(mainLayout);
        dialog.setScene(scene);

        // 对话框图标
        UIUtils.addStageIcon(dialog, "/res/book-icon.png", getClass());

        dialog.show();
    }

    private GridPane createFormGrid() {
        GridPane grid = UIUtils.createFormGrid();
        grid.setStyle("-fx-background-color: #ffffff;");

        String labelStyle = "-fx-font-family: 'Arial'; -fx-font-size: 14; -fx-font-weight: bold;";
        String fieldStyle = "-fx-font-size: 14; -fx-pref-height: 35; -fx-pref-width: 250;";

        Label isbnLabel = new Label("图书ISBN：");
        isbnLabel.setStyle(labelStyle);
        isbnField = new TextField();
        isbnField.setStyle(fieldStyle);
        isbnField.setPromptText("13位ISBN号码");
        grid.add(isbnLabel, 0, 0);
        grid.add(isbnField, 1, 0);

        Label catLabel = new Label("类别：");
        catLabel.setStyle(labelStyle);
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setStyle(fieldStyle);
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryMap.keySet()));
        if (!categoryMap.isEmpty())
            categoryComboBox.getSelectionModel().selectFirst();
        grid.add(catLabel, 0, 1);
        grid.add(categoryComboBox, 1, 1);

        Label titleLabel = new Label("书名：");
        titleLabel.setStyle(labelStyle);
        titleField = new TextField();
        titleField.setStyle(fieldStyle);
        grid.add(titleLabel, 0, 2);
        grid.add(titleField, 1, 2);

        Label authorLabel = new Label("作者：");
        authorLabel.setStyle(labelStyle);
        authorField = new TextField();
        authorField.setStyle(fieldStyle);
        grid.add(authorLabel, 0, 3);
        grid.add(authorField, 1, 3);

        Label pubLabel = new Label("出版社：");
        pubLabel.setStyle(labelStyle);
        publisherComboBox = new ComboBox<>();
        publisherComboBox.setEditable(true);
        publisherComboBox.setStyle(fieldStyle);
        publisherComboBox.setItems(FXCollections.observableArrayList(publisherMap.keySet()));
        if (!publisherMap.isEmpty())
            publisherComboBox.getSelectionModel().selectFirst();
        grid.add(pubLabel, 0, 4);
        grid.add(publisherComboBox, 1, 4);

        Label transLabel = new Label("译者：");
        transLabel.setStyle(labelStyle);
        translatorField = new TextField();
        translatorField.setStyle(fieldStyle);
        grid.add(transLabel, 0, 5);
        grid.add(translatorField, 1, 5);

        Label dateLabel = new Label("出版日期：");
        dateLabel.setStyle(labelStyle);
        publishDatePicker = new DatePicker();
        publishDatePicker.setStyle(fieldStyle);
        publishDatePicker.setPromptText("YYYY-MM-DD");
        grid.add(dateLabel, 0, 6);
        grid.add(publishDatePicker, 1, 6);

        Label priceLabel = new Label("单价：");
        priceLabel.setStyle(labelStyle);
        priceField = new TextField();
        priceField.setStyle(fieldStyle);
        priceField.setPromptText("例如: 39.99");
        grid.add(priceLabel, 0, 7);
        grid.add(priceField, 1, 7);

        return grid;
    }

    private HBox createButtonBox(Stage dialog) {
        Button addBtn = UIUtils.styledBtn("添加", "#3b82f6", "#60a5fa");
        addBtn.setOnAction(event -> addBook());

        Button clearBtn = UIUtils.styledBtn("清空", "#94a3b8", "#cbd5e1");
        clearBtn.setOnAction(event -> clearForm());

        Button closeBtn = UIUtils.styledBtn("关闭", "#ef4444", "#f87171");
        closeBtn.setOnAction(event -> dialog.close());

        statusLabel = UIUtils.createLabel("", 12, true, Color.GRAY);
        statusLabel.setPadding(new Insets(0, 0, 0, 10));

        HBox box = new HBox(20, addBtn, clearBtn, closeBtn, statusLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(20, 10, 20, 10));
        box.setStyle("-fx-background-color: #e5e7eb;");
        return box;
    }

    private void loadCategoriesAndPublishers() {
        try {
            for (var type : DBUtils.query(mapper -> mapper.selectAllBookTypes())) {
                categoryMap.put(type.getTypeName(), type.getNumber());
            }
            for (String publisher : DBUtils.query(mapper -> mapper.selectDistinctPublishers())) {
                publisherMap.put(publisher, publisher);
            }
            if (publisherMap.isEmpty()) {
                for (String pub : new String[]{"机械工业出版社", "清华大学出版社", "人民邮电出版社"}) {
                    publisherMap.put(pub, pub);
                }
            }
        } catch (SQLException e) {
            categoryMap.put("计算机", "TP0001");
            publisherMap.put("机械工业出版社", "机械工业出版社");
        }
    }
    private void addBook() {
        String isbn = isbnField.getText().trim();
        String cat = categoryMap.get(categoryComboBox.getValue());
        String name = titleField.getText().trim();
        String author = authorField.getText().trim();
        String pubInput = publisherComboBox.getEditor().getText().trim();
        String pub = publisherMap.getOrDefault(pubInput, pubInput);
        String trans = translatorField.getText().trim();
        LocalDate date = publishDatePicker.getValue();
        String priceTxt = priceField.getText().trim();

        if (isbn.length() != 13) {
            showStatus("请输入13位ISBN", true);
            isbnField.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            showStatus("书名不能为空", true);
            titleField.requestFocus();
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceTxt);
        } catch (Exception ex) {
            showStatus("单价输入不正确", true);
            priceField.requestFocus();
            return;
        }

        // 用实体类BookInfo收集所有字段
        BookInfo book = new BookInfo(
                isbn,
                cat,
                name,
                author.isEmpty() ? "未知" : author,
                pub,
                trans,
                date == null ? null : Date.valueOf(date),
                price
        );

        new Thread(() -> {
            try {
                DBUtils.transaction(mapper -> {
                    mapper.insertBookInfo(book);
                    mapper.insertStockpile(book.getBookISBN(), 0);
                    return null;
                });
                Platform.runLater(() -> {
                    showStatus("添加成功！", false);
                    clearForm();
                });
            } catch (SQLException ex) {
                Platform.runLater(() -> {
                    if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
                        showStatus("ISBN 已存在", true);
                    } else {
                        showStatus("数据库错误：" + ex.getMessage(), true);
                    }
                });
            }
        }).start();
    }

    private void showStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(error ? Color.RED : Color.GREEN);
    }

    private void clearForm() {
        isbnField.clear();
        titleField.clear();
        authorField.clear();
        translatorField.clear();
        publishDatePicker.setValue(null);
        priceField.clear();
        if (!categoryComboBox.getItems().isEmpty())
            categoryComboBox.getSelectionModel().selectFirst();
        if (!publisherComboBox.getItems().isEmpty())
            publisherComboBox.getSelectionModel().selectFirst();
    }
}

