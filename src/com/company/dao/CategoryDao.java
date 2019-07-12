package com.company.dao;

import com.company.vo.Category;

import java.util.List;

interface CategoryDao {
    List<Category> findAllCategories();
}
