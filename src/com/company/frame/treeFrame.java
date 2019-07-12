package com.company.frame;

import com.company.dao.BaseDao;
import com.company.dao.CategoryDaoImpl;
import com.company.vo.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class treeFrame extends JFrame implements ActionListener {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JPanel panel;
    private JScrollPane scrollPane;
    private JPanel btnPanel;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JButton btn5;
    private static StringBuilder sb;
    private static List<Object> sqlObjs;
    private static CategoryDaoImpl dao;

    treeFrame(String title) {
        dao = new CategoryDaoImpl();
        sb = new StringBuilder();
        sqlObjs = new ArrayList<>();
        treeModel = getTreeModel();
        tree = new JTree(treeModel);
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout(0, 0));
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(tree);
        panel.add(scrollPane);
        btnPanel = new JPanel();
        btn1 = new JButton("添加兄弟节点");
        btn2 = new JButton("添加子节点");
        btn3 = new JButton("删除节点");
        btn4 = new JButton("编辑当前节点");
        btn5 = new JButton("保存分类信息");
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btn3.addActionListener(this);
        btn4.addActionListener(this);
        btn5.addActionListener(this);
        btnPanel.add(btn1);
        btnPanel.add(btn2);
        btnPanel.add(btn3);
        btnPanel.add(btn4);
        btnPanel.add(btn5);
        this.setTitle(title);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(btnPanel, BorderLayout.SOUTH);
        this.setLocationRelativeTo(null);
        this.setSize(600, 400);
        this.setVisible(true);
    }

    private DefaultTreeModel getTreeModel() {
        var categories = dao.findAllCategories();
        var root = new DefaultMutableTreeNode("选择题");
        loadTree(categories, 0, root);
        return new DefaultTreeModel(root);
    }

    /**
     * 递归加载树结构
     *
     * @param cats
     * @param parentId
     * @param treeNode
     */
    private void loadTree(List<Category> cats, int parentId, DefaultMutableTreeNode treeNode) {
        if (!cats.isEmpty()) {
            var tops = cats.stream().filter(t -> t.getParentId() == parentId).collect(Collectors.toList());
            for (var top : tops) {
                var node = new DefaultMutableTreeNode();
                node.setUserObject(new Category(top.getId(), top.getName(), top.getParentId()));
                loadTree(cats, top.getId(), node);
                treeNode.add(node);
            }
        }
    }

    private void addSibling(DefaultMutableTreeNode node, String name) {
        var parent = (DefaultMutableTreeNode) node.getParent();
        parent.add(new DefaultMutableTreeNode(name));
        tree.updateUI();
        sb.append("INSERT INTO dbo.KIND ( Name, ParentID ) VALUES  (?, ?)");
        sqlObjs.add(name);
        sqlObjs.add(parent.isRoot() ? 0 : ((Category) node.getUserObject()).getId());
    }

    private void addChild(DefaultMutableTreeNode node, String name) {
        var id = node.isRoot() || node.getUserObject() instanceof String ? 0 : ((Category) node.getUserObject()).getId();
        if (id == 0 && !node.isRoot()) {
            JOptionPane.showMessageDialog(this, "不能一次性添加连续的两个子节点！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        node.add(new DefaultMutableTreeNode(name));
        tree.updateUI();
        sb.append("INSERT INTO dbo.KIND ( Name, ParentID ) VALUES  (?, ?)");
        sqlObjs.add(name);
        sqlObjs.add(id);
    }

    private void removeNode(DefaultMutableTreeNode node) {
        var userObj = node.getUserObject();
        node.removeFromParent();
        tree.updateUI();
        if (userObj instanceof Category) {
            var id = ((Category) node.getUserObject()).getId();
            sb.append("delete from KIND where id=? or parentID=?");
            sqlObjs.add(id);
            sqlObjs.add(id);
        }
    }

    private void editNode(DefaultMutableTreeNode node, String name) {
        var userObj = node.getUserObject();
        if (userObj instanceof Category) {
            var cat = (Category) userObj;
            cat.setName(name);
            tree.updateUI();
            sb.append("update KIND set name=? where id=?");
            sqlObjs.add(name);
            sqlObjs.add(cat.getId());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn1) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                JOptionPane.showMessageDialog(this, "未选中任何节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (node.isRoot()) {
                JOptionPane.showMessageDialog(this, "不能向根节点添加兄弟节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String input = JOptionPane.showInputDialog("请输入节点名称:");
            if (input == null || input.isBlank()) {
                return;
            }
            addSibling(node, input);
        } else if (e.getSource() == btn2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                JOptionPane.showMessageDialog(this, "未选中任何节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String input = JOptionPane.showInputDialog("请输入节点名称:");
            if (input == null || input.isBlank()) {
                return;
            }
            addChild(node, input);
        } else if (e.getSource() == btn3) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                JOptionPane.showMessageDialog(this, "未选中任何节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (node.isRoot()) {
                JOptionPane.showMessageDialog(this, "不能删除根节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            removeNode(node);
        } else if (e.getSource() == btn4) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                JOptionPane.showMessageDialog(this, "未选中任何节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (node.isRoot()) {
                JOptionPane.showMessageDialog(this, "不能编辑根节点！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String input = JOptionPane.showInputDialog("请输入节点名称:");
            if (input == null || input.isBlank()) {
                return;
            }
            editNode(node, input);

        } else if (e.getSource() == btn5) {
            if (sb.length() == 0) {
                JOptionPane.showMessageDialog(this, "没有需要保存的数据！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            var baseDao = new BaseDao();
            try {
                baseDao.executeSql(sb.toString(), sqlObjs.toArray());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "保存失败！", "提示", JOptionPane.WARNING_MESSAGE);
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "保存成功！");
            sb.delete(0, sb.length());
            sqlObjs.clear();
            treeModel = getTreeModel();
            treeModel.reload();
            tree.setModel(treeModel);
            tree.updateUI();
        }
    }
}
