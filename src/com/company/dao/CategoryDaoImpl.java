package com.company.dao;

import com.company.vo.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    @Override
    public List<Category> findAllCategories() {
        Connection conn = BaseDao.getConnection();
        String sql = "select * from KIND";
        List<Category> categories = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category pet = new Category(
                        rs.getInt("id"),
                        rs.getString("Name"),
                        rs.getInt("ParentID")
                );
                categories.add(pet);
            }
            BaseDao.closeAll(conn, stmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
