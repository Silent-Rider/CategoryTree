package com.example.category_tree.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.category_tree.model.Category;
import com.example.category_tree.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public String addCategory(String name, String parentName) {
        if (parentName == null) {
            if (categoryRepository.findByName(name).isPresent()) {
                return "Категория с именем '" + name + "' уже существует.";
            }
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
            return "Корневая категория '" + name + "' добавлена.";
        } else {
            Optional<Category> parent = categoryRepository.findByName(parentName);
            if (parent.isEmpty()) {
                return "Родительская категория '" + parentName + "' не найдена.";
            } else if (categoryRepository.findByName(name).isPresent()) {
                return "Категория с именем '" + name + "' уже существует.";
            }
            Category child = new Category();
            child.setName(name);
            child.setParent(parent.get());
            categoryRepository.save(child);
            return "Дочерняя категория '" + name + "' добавлена к '" + parentName + "'.";
        }
    }

    public String viewTree() {
        List<Category> roots = categoryRepository.findByParent(null);
        if (roots.isEmpty()) {
            return "Дерево категорий пусто.";
        }
        StringBuilder tree = new StringBuilder("Дерево категорий:\n");
        roots.forEach(root -> buildTree(root, tree, 0));
        return tree.toString();
    }

    public String removeCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isEmpty()) {
            return "Категория '" + name + "' не найдена.";
        }
        deleteCategory(category.get());
        return "Категория '" + name + "' и её дочерние элементы удалены.";
    }

    @Transactional
    private void deleteCategory(Category category) {
        category.getChildren().forEach(this::deleteCategory);
        categoryRepository.delete(category);
    }

    private void buildTree(Category category, StringBuilder tree, int depth) {
        tree.append("  ".repeat(depth)).append("- ").append(category.getName()).append("\n");
        category.getChildren().forEach(child -> buildTree(child, tree, depth + 1));
    }
}
