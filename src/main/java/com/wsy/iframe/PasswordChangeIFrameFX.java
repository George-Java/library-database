package com.wsy.iframe;

import com.wsy.entity.Operator;
import com.wsy.util.AlertHelper;
import com.wsy.util.DBUtils;
import com.wsy.util.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * 密码修改页面，简化：用实体类Operator支撑相关流程。
 */
public class PasswordChangeIFrameFX {

    /**
     * 展示密码修改页面，修改成功后自动退出到登录页面
     *
     * @param owner    主窗口
     * @param onLogout 退出函数（跳转到登录界面）
     */
    public void show(Stage owner, Runnable onLogout) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("更改系统密码");
        dialog.setWidth(480);
        dialog.setHeight(420);
        dialog.setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        // 顶部logo+标题
        HBox logoBox = new HBox(18);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(UIUtils.createBrandIcon(getClass(), 34));
        logoBox.getChildren().add(UIUtils.createLabel("更改系统密码", 22, true, Color.rgb(26, 35, 126)));
        root.setTop(logoBox);

        // 主表单
        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(20);
        form.setPadding(new Insets(25, 35, 25, 35));
        form.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.98), new CornerRadii(8), Insets.EMPTY)));
        form.setEffect(new DropShadow(7, Color.gray(0, 0.08)));

        int row = 0;
        form.add(UIUtils.createLabel("用户名：", 15, true, Color.BLACK), 0, row);
        TextField usernameField = new TextField();
        usernameField.setPromptText("请输入您的用户名");
        form.add(usernameField, 1, row);

        form.add(UIUtils.createLabel("旧密码：", 15, true, Color.BLACK), 0, ++row);
        PasswordField oldPwdField = new PasswordField();
        oldPwdField.setPromptText("请输入旧密码");
        form.add(oldPwdField, 1, row);

        form.add(UIUtils.createLabel("新密码：", 15, true, Color.BLACK), 0, ++row);
        PasswordField newPwdField = new PasswordField();
        newPwdField.setPromptText("请输入新密码");
        form.add(newPwdField, 1, row);

        form.add(UIUtils.createLabel("确认密码：", 15, true, Color.BLACK), 0, ++row);
        PasswordField confirmPwdField = new PasswordField();
        confirmPwdField.setPromptText("再次输入新密码");
        form.add(confirmPwdField, 1, row);

        form.add(UIUtils.createLabel("密码长度应在6-20位，建议包含字母和数字", 12, false, Color.GRAY), 1, ++row);

        root.setCenter(form);

        // 按钮区
        HBox btnBox = new HBox(30);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(20, 0, 0, 0));
        Button changeBtn = UIUtils.styledBtn("确认更改", "#388e3c", "#43a047");
        Button cancelBtn = UIUtils.styledBtn("取消", "#757575", "#bdbdbd");
        btnBox.getChildren().addAll(changeBtn, cancelBtn);

        root.setBottom(btnBox);

        // 事件绑定
        changeBtn.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String oldPwd = oldPwdField.getText();
            String newPwd = newPwdField.getText();
            String confirmPwd = confirmPwdField.getText();

            // 1. 校验输入
            if (username.isEmpty() || oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                AlertHelper.showWarn("输入错误", "所有字段都必须填写！");
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                AlertHelper.showWarn("密码不一致", "新密码与确认密码不一致！");
                newPwdField.clear();
                confirmPwdField.clear();
                newPwdField.requestFocus();
                return;
            }
            if (newPwd.length() < 6 || newPwd.length() > 20) {
                AlertHelper.showWarn("密码长度错误", "新密码长度应在6-20位之间！");
                newPwdField.clear();
                confirmPwdField.clear();
                newPwdField.requestFocus();
                return;
            }
            if (oldPwd.equals(newPwd)) {
                AlertHelper.showWarn("密码无效", "新密码不能与旧密码相同！");
                newPwdField.clear();
                confirmPwdField.clear();
                newPwdField.requestFocus();
                return;
            }

            // 2. 严格验证用户名+旧密码
            Operator operator;
            try {
                operator = DBUtils.query(mapper -> mapper.selectOperatorByUsername(username));
                if (operator == null) {
                    AlertHelper.showError("验证失败", "用户名不存在！");
                    return;
                }
                if (!operator.getPassword().equals(oldPwd)) {
                    AlertHelper.showError("验证失败", "旧密码不正确！");
                    oldPwdField.clear();
                    oldPwdField.requestFocus();
                    return;
                }
            } catch (SQLException ex) {
                AlertHelper.showError("系统错误", "数据库错误: " + ex.getMessage());
                return;
            }
            // 3. 二次确认
            if (AlertHelper.showConfirm("确认修改",
                    "您是否确认更改当前用户密码？如确认，您需要重新登录。\n用户名：" + username)) {
                // 4. 修改密码
                try {
                    int rows = DBUtils.transaction(mapper -> mapper.updateOperatorPassword(username, newPwd));
                    if (rows > 0) {
                        AlertHelper.showInfo("操作成功", "密码更新成功，请重新登录！");
                        dialog.close();
                        if (onLogout != null) onLogout.run();
                    } else {
                        AlertHelper.showError("更新失败", "密码更新失败，请重试！");
                    }
                } catch (SQLException ex) {
                    AlertHelper.showError("系统错误", "数据库错误: " + ex.getMessage());
                }
            }
        });

        cancelBtn.setOnAction(event -> dialog.close());

        dialog.setScene(new Scene(root));
        dialog.show();
    }
}

