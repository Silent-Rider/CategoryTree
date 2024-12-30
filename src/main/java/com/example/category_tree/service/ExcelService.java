package com.example.category_tree.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.category_tree.model.Category;
import com.example.category_tree.repository.CategoryRepository;

@Service
public class ExcelService {

    private final CategoryRepository categoryRepository;

    public ExcelService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ByteArrayInputStream generateExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Categories");

            // Создаем заголовки
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Parent ID");

            // Заполняем данными
            List<Category> categories = categoryRepository.findAll();
            int rowIdx = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getParent() != null ? 
                    category.getParent().getId() : 0);
            }

            // Сохраняем в поток
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public void importExcel(InputStream inputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Считываем данные начиная со второй строки (первая строка — заголовки)
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);

                if (row != null) {
                    String name = row.getCell(1).getStringCellValue();
                    Long parentId = row.getCell(2) != null ? 
                        (long) row.getCell(2).getNumericCellValue() : null;

                    Optional<Category> parent = parentId != null ? 
                        categoryRepository.findById(parentId) : Optional.empty();
                    if (categoryRepository.findByName(name).isEmpty()) {
                        Category category = new Category();
                        category.setName(name);
                        category.setParent(parent.orElse(null));
                        categoryRepository.save(category);
                    }
                }
            }
        }
    }
}
