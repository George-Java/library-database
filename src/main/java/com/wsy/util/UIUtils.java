package com.wsy.util;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;

public class UIUtils {
    private static final String BRAND_ICON_PATH = "/res/book-icon.png";

    /**
     * 创建带圆角、阴影的卡片容器
     */
    public static VBox createCard(double maxWidth, double maxHeight, double opacity) {
        VBox card = new VBox(25);
        card.setMaxWidth(maxWidth);
        card.setMaxHeight(maxHeight);
        card.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, opacity), new CornerRadii(15), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(
                Color.rgb(200, 200, 255, 0.3), BorderStrokeStyle.SOLID,
                new CornerRadii(15), BorderWidths.DEFAULT)));
        card.setEffect(new DropShadow(10, Color.gray(0, 0.3)));
        // 绑定宽高
        card.prefWidthProperty().bindBidirectional(card.maxWidthProperty());
        card.prefHeightProperty().bindBidirectional(card.maxHeightProperty());
        return card;
    }

    /**
     * 创建标准字体Label
     */
    public static Label createLabel(String text, int fontSize, boolean bold, Color color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Microsoft YaHei", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
        lbl.setTextFill(color);
        return lbl;
    }

    /**
     * 创建带下划线可点击的Label
     */
    public static Label createInteractiveLabel(String text, int fontSize, String colorHex, Runnable onClick) {
        Label lbl = createLabel(text, fontSize, false, Color.web(colorHex));
        lbl.setUnderline(true);
        lbl.setCursor(javafx.scene.Cursor.HAND);
        lbl.setOnMouseClicked(event -> onClick.run());
        return lbl;
    }

    /**
     * 创建右侧对齐的交互label容器
     */
    public static HBox createRightAlignedInteractiveLabel(String text, int fontSize, String colorHex, Runnable onClick) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(5, 16, 0, 0));
        box.getChildren().add(createInteractiveLabel(text, fontSize, colorHex, onClick));
        return box;
    }

    /**
     * 创建表单网格
     */
    public static GridPane createFormGrid() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(25);
        formGrid.setAlignment(Pos.CENTER);
        return formGrid;
    }

    /**
     * 卡片logo或标题
     */
    public static void addLogoOrTitle(HBox logoBox, Object parent, Class<?> clazz) {
        InputStream logoStream = clazz.getResourceAsStream("/res/matching-map.png");
        if (logoStream != null) {
            Image image = new Image(logoStream);
            ImageView logo = new ImageView(image);
            configureImageView(logo, true);
            if (parent instanceof VBox) {
                VBox card = (VBox) parent;
                double aspectRatio = image.getHeight() <= 0 ? 1.0 : image.getWidth() / image.getHeight();
                logo.fitWidthProperty().bind(Bindings.min(
                        card.widthProperty().multiply(0.72),
                        card.heightProperty().multiply(0.28 * aspectRatio)));
            }
            HBox.setHgrow(logo, Priority.NEVER);
            logoBox.getChildren().add(logo);
        } else {
            logoBox.getChildren().add(createLabel("图书管理系统", 28, true, Color.rgb(26, 35, 126)));
        }
    }

    public static Image loadImage(String resourcePath, Class<?> clazz) {
        try (InputStream stream = clazz.getResourceAsStream(resourcePath)) {
            return stream == null ? null : new Image(stream);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static ImageView createImageView(String resourcePath, Class<?> clazz,
                                            double fitWidth, double fitHeight, boolean preserveRatio) {
        Image image = loadImage(resourcePath, clazz);
        ImageView imageView = image == null ? new ImageView() : new ImageView(image);
        configureImageView(imageView, preserveRatio);
        if (image != null && preserveRatio && fitWidth > 0 && fitHeight > 0) {
            double aspectRatio = image.getHeight() <= 0 ? 1.0 : image.getWidth() / image.getHeight();
            imageView.setFitWidth(Math.min(fitWidth, fitHeight * aspectRatio));
        } else {
            if (fitWidth > 0) {
                imageView.setFitWidth(fitWidth);
            }
            if (fitHeight > 0) {
                imageView.setFitHeight(fitHeight);
            }
        }
        return imageView;
    }

    public static ImageView createBrandIcon(Class<?> clazz, double size) {
        return createImageView(BRAND_ICON_PATH, clazz, size, size, true);
    }

    public static void addStageIcon(Stage stage, String resourcePath, Class<?> clazz) {
        Image icon = loadImage(resourcePath, clazz);
        if (icon != null) {
            stage.getIcons().add(icon);
        }
    }

    private static void configureImageView(ImageView imageView, boolean preserveRatio) {
        imageView.setPreserveRatio(preserveRatio);
        imageView.setSmooth(true);
        imageView.setCache(true);
    }

    /**
     * 快速创建VBox
     */
    public static VBox createVBox(javafx.scene.Node... nodes) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(5));
        box.getChildren().addAll(nodes);
        return box;
    }

    /**
     * 快速创建侧边栏按钮
     */
    public static Button navItem(String text, Runnable action) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setFont(Font.font("Microsoft YaHei", 13));
        b.setStyle("-fx-background-color:transparent; -fx-text-fill:#333; -fx-padding:4 8;");
        b.setOnMouseEntered(event -> b.setStyle("-fx-background-color:#e3f2fd; -fx-text-fill:#2196F3; -fx-padding:4 8;"));
        b.setOnMouseExited(event -> b.setStyle("-fx-background-color:transparent; -fx-text-fill:#333; -fx-padding:4 8;"));
        b.setOnAction(event -> action.run());
        return b;
    }

    /**
     * 快速创建渐变按钮
     */
    public static Button styledBtn(String text, String c1, String c2) {
        Button b = new Button(text);
        b.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        b.setTextFill(Color.WHITE);
        b.setMinSize(140, 40);
        b.setStyle(String.format(
                "-fx-background-radius:20; -fx-background-color:linear-gradient(to right,%s,%s); -fx-cursor:hand;",
                c1, c2));
        b.setOnMouseEntered(event -> b.setOpacity(0.85));
        b.setOnMouseExited(event -> b.setOpacity(1.0));
        b.setOnMousePressed(event -> b.setOpacity(0.7));
        b.setOnMouseReleased(event -> b.setOpacity(0.85));
        return b;
    }
}
