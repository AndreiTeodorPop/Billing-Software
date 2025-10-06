package com.marketplace.billingsoftware.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.billingsoftware.entity.CategoryEntityDTO;
import com.marketplace.billingsoftware.io.CategoryRequest;
import com.marketplace.billingsoftware.io.CategoryResponse;
import com.marketplace.billingsoftware.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse addCategory(@RequestPart("category") String categoryString, @RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        CategoryRequest request = null;
        try {
            request = objectMapper.readValue(categoryString, CategoryRequest.class);
            return categoryService.add(request, file);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception occurred while passing the json: " + ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/categories")
    public List<CategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse patchCategory(@PathVariable String id, @RequestBody CategoryEntityDTO dto) {
        return categoryService.patch(id, dto);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
