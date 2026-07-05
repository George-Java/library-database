package com.wsy;

import com.wsy.iframe.*;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainFX extends Application {
    private Stage primaryStage;
    private StackPane contentArea;

    private static final int INIT_WIDTH = 1000;
    private static final int INIT_HEIGHT = 700;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("图书管理系统");
        primaryStage.setWidth(INIT_WIDTH);
        primaryStage.setHeight(INIT_HEIGHT);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        UIUtils.addStageIcon(primaryStage, "/res/book-icon.png", getClass());

        createLoginScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void createLoginScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a2980, #26d0ce);");
        root.setPadding(new Insets(20));

        VBox loginCard = new VBox(28);
        loginCard.setAlignment(Pos.TOP_CENTER);
        loginCard.setPadding(new Insets(28, 48, 32, 48));
        loginCard.setMinSize(520, 360);
        loginCard.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.94), new CornerRadii(22), Insets.EMPTY)));
        loginCard.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 255, 255, 0.45), BorderStrokeStyle.SOLID,
                new CornerRadii(22), new BorderWidths(1.2))));
        loginCard.setEffect(new javafx.scene.effect.DropShadow(24, Color.rgb(0, 0, 0, 0.18)));
        loginCard.prefWidthProperty().bind(Bindings.max(520, root.widthProperty().subtract(40)));
        loginCard.prefHeightProperty().bind(Bindings.max(360, root.heightProperty().subtract(40)));
        loginCard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox logoBox = new HBox();
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(4, 0, 0, 0));
        UIUtils.addLogoOrTitle(logoBox, loginCard, getClass());

        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(8, 50, 8, 50));
        formBox.setMaxWidth(460);
        Label title = UIUtils.createLabel("用户登录", 30, true, Color.rgb(33, 33, 33));

        GridPane formGrid = UIUtils.createFormGrid();
        TextField username = new TextField();
        username.setPromptText("请输入您的用户名");
        PasswordField password = new PasswordField();
        password.setPromptText("请输入您的密码");
        username.setMaxWidth(Double.MAX_VALUE);
        password.setMaxWidth(Double.MAX_VALUE);
        formGrid.addRow(0, new Label("用户名:"), username);
        formGrid.addRow(1, new Label("密码:"), password);
        formGrid.prefWidthProperty().bind(formBox.widthProperty());
        GridPane.setHgrow(username, Priority.ALWAYS);
        GridPane.setHgrow(password, Priority.ALWAYS);

        Label errorLabel = UIUtils.createLabel("", 12, false, Color.RED);
        errorLabel.setVisible(false);

        Label registerLabel = UIUtils.createInteractiveLabel("注册新用户", 14, "#2196f3", this::showRegisterUserScene);

        formBox.getChildren().addAll(title, formGrid, errorLabel, registerLabel);

        HBox buttons = new HBox(30);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(0, 0, 10, 0));
        Button loginBtn = UIUtils.styledBtn("登录", "#1a2980", "#26d0ce");
        Button resetBtn = UIUtils.styledBtn("重置", "#7b7b7b", "#bdbdbd");
        buttons.getChildren().addAll(loginBtn, resetBtn);

        Runnable auth = () -> {
            errorLabel.setVisible(false);
            String u = username.getText().trim(), p = password.getText().trim();
            if (u.isEmpty() || p.isEmpty()) {
                errorLabel.setText("用户名和密码不能为空！");
                errorLabel.setVisible(true);
            } else if (!DBUtils.userExists(u)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("用户不存在");
                alert.setHeaderText(null);
                alert.setContentText("用户名不存在，是否创建新用户？");
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(type -> {
                    if (type == ButtonType.YES) {
                        showRegisterUserScene();
                    }
                });
            } else if (DBUtils.authenticate(u, p)) {
                showMainScene();
            } else {
                errorLabel.setText("用户名或密码错误！");
                errorLabel.setVisible(true);
            }
        };

        loginBtn.setOnAction(event -> auth.run());
        resetBtn.setOnAction(event -> {
            username.clear();
            password.clear();
            errorLabel.setVisible(false);
        });
        username.setOnAction(event -> auth.run());
        password.setOnAction(event -> auth.run());

        loginCard.getChildren().addAll(logoBox, formBox, buttons);
        VBox.setVgrow(formBox, Priority.ALWAYS);
        root.getChildren().add(loginCard);
        primaryStage.setScene(new Scene(root));
    }

    private void showRegisterUserScene() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("注册新用户");
        dialog.setWidth(600);
        dialog.setHeight(680);
        dialog.setResizable(false);

        BorderPane root = new BorderPane();
        root.setTop(UIUtils.createRightAlignedInteractiveLabel("已有用户？立即登录", 14, "#2196f3", () -> {
            dialog.close();
            createLoginScene();
        }));

        UserAdditionIFrameFX userAdd = new UserAdditionIFrameFX();
        VBox addUserPane = userAdd.getAddUserPane(dialog, () -> {
            dialog.close();
            createLoginScene();
        });
        root.setCenter(addUserPane);

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    /**
     * 现代化主界面（含二级菜单 + 图书添加/修改删除/读者添加/读者修改与删除/图书类别添加/图书类别修改与删除/新书订购/新书验收/图书借阅/图书归还/图书搜索/用户添加/用户修改与删除）
     */
    private void showMainScene() {
        Accordion accordion = new Accordion();
        accordion.setPrefWidth(200);
        accordion.setPadding(new Insets(20));
        accordion.setStyle("-fx-background-color:#fff; -fx-border-color:#e0e0e0; -fx-border-width:0 1 0 0;");

        TitledPane basePane = new TitledPane("基础数据维护", UIUtils.createVBox(
                UIUtils.navItem("读者添加", () -> new ReaderAddIFrameFX().show(primaryStage)),
                UIUtils.navItem("读者修改/删", () -> new ReaderUpdateIFrameFX().show(primaryStage)),
                new Separator(),
                UIUtils.navItem("图书类别添加", () -> new BookCategoryAddIFrameFX().show(primaryStage)),
                UIUtils.navItem("图书类别修改", () -> new BookCategoryUpdateIFrameFX().show(primaryStage)),
                new Separator(),
                UIUtils.navItem("图书添加", () -> BookAdditionIFrameFX.show(primaryStage)),
                UIUtils.navItem("图书修改/删", () -> new BookUpdateIFrameFX().show(primaryStage))
        ));

        TitledPane orderPane = new TitledPane("新书订购管理", UIUtils.createVBox(
                UIUtils.navItem("新书订购", () -> new BookOrderingIFrameFX().show(primaryStage)),
                UIUtils.navItem("新书验收", () -> new BookAcceptanceIFrameFX().show(primaryStage))
        ));

        TitledPane borrowPane = new TitledPane("借阅管理", UIUtils.createVBox(
                UIUtils.navItem("图书借阅", () -> new BookBorrowingIFrameFX().show(primaryStage)),
                UIUtils.navItem("图书归还", () -> new BookReturnIFrameFX().show(primaryStage)),
                UIUtils.navItem("图书搜索", () -> new BookSearchIFrameFX().show(primaryStage))
        ));

        TitledPane sysPane = new TitledPane("系统维护", UIUtils.createVBox(
                UIUtils.navItem("用户添加", () -> new UserAdditionIFrameFX().show(primaryStage)),
                UIUtils.navItem("用户修改/删", () -> new UserUpdateIFrameFX().show(primaryStage)),
                new Separator(),
                UIUtils.navItem("更改密码", () -> new PasswordChangeIFrameFX().show(primaryStage, this::createLoginScene))
        ));

        accordion.getPanes().addAll(basePane, orderPane, borrowPane, sysPane);
        accordion.setExpandedPane(basePane);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color:#2196F3;");
        topBar.getChildren().add(UIUtils.createLabel("图书管理系统", 20, true, Color.WHITE));

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        showDashboard();

        BorderPane root = new BorderPane();
        root.setLeft(accordion);
        root.setTop(topBar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    /**
     * 仪表盘，显示4个统计模块
     */
    private void showDashboard() {
        contentArea.getChildren().clear();
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.TOP_LEFT);

        // 1. 重要字段填写规则
        VBox card1 = UIUtils.createCard(300, 180, 1.0);
        card1.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),8,0,0,4);");
        card1.getChildren().add(UIUtils.createLabel("📑 填写规则", 16, true, Color.web("#1a2980")));
        card1.getChildren().add(new Label("手机号：11位数字，仅支持手机号\n身份证号：18位，前17位数字，最后一位数字或大写X\n用户名：4-20位字符，唯一\n密码：6-10位字符"));

        // 2. 当前库存量前5的图书
        VBox card2 = UIUtils.createCard(300, 180, 1.0);
        card2.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),8,0,0,4);");
        card2.getChildren().add(UIUtils.createLabel("📚 库存量前5的图书", 16, true, Color.web("#1a2980")));
        List<String> topStockBooks = getTopStockBooks();
        if (topStockBooks.isEmpty()) {
            card2.getChildren().add(new Label("暂无数据"));
        } else {
            for (String s : topStockBooks) {
                card2.getChildren().add(new Label(s));
            }
        }

        // 3. 当前借阅量前5的图书
        VBox card3 = UIUtils.createCard(300, 180, 1.0);
        card3.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),8,0,0,4);");
        card3.getChildren().add(UIUtils.createLabel("📖 借阅量前5的图书", 16, true, Color.web("#1a2980")));
        List<String> topBorrowBooks = getTopBorrowedBooks();
        if (topBorrowBooks.isEmpty()) {
            card3.getChildren().add(new Label("暂无数据"));
        } else {
            for (String s : topBorrowBooks) {
                card3.getChildren().add(new Label(s));
            }
        }

        // 4. 当前在职员工
        VBox card4 = UIUtils.createCard(300, 180, 1.0);
        card4.setStyle("-fx-background-color:white; -fx-background-radius:8; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),8,0,0,4);");
        card4.getChildren().add(UIUtils.createLabel("🧑‍💼 当前在职员工", 16, true, Color.web("#1a2980")));
        List<String> operators = getActiveOperators();
        if (operators.isEmpty()) {
            card4.getChildren().add(new Label("暂无数据"));
        } else {
            for (String s : operators) {
                card4.getChildren().add(new Label(s));
            }
        }

        grid.add(card1, 0, 0);
        grid.add(card2, 1, 0);
        grid.add(card3, 0, 1);
        grid.add(card4, 1, 1);

        contentArea.getChildren().add(grid);
    }

    /** 查询库存量前5的图书 */
    private List<String> getTopStockBooks() {
        List<String> result = new ArrayList<>();
        try {
            for (Map<String, Object> row : DBUtils.query(mapper -> mapper.selectTopStockBooks())) {
                result.add(row.get("bookname") + "：" + ((Number) row.get("stockQuantity")).intValue() + "本");
            }
        } catch (Exception ignored) {}
        return result;
    }
    /** 查询借阅量前5的图书 */
    private List<String> getTopBorrowedBooks() {
        List<String> result = new ArrayList<>();
        try {
            for (Map<String, Object> row : DBUtils.query(mapper -> mapper.selectTopBorrowedBooks())) {
                result.add(row.get("bookname") + "：" + ((Number) row.get("borrowCount")).intValue() + "次");
            }
        } catch (Exception ignored) {}
        return result;
    }
    /** 查询在职员工 */
    private List<String> getActiveOperators() {
        List<String> result = new ArrayList<>();
        try {
            for (var operator : DBUtils.query(mapper -> mapper.selectAllOperators())) {
                result.add("编号: " + operator.getId() + "，姓名: " + operator.getName());
            }
        } catch (Exception ignored) {}
        return result;
    }
    /**
     * 占位页面
     */
    private void showPlaceholder(String text) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(UIUtils.createLabel(text, 18, false, Color.GRAY));
    }
}

