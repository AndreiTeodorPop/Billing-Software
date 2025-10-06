package com.marketplace.billingsoftware.service;

import com.marketplace.billingsoftware.entity.CategoryEntityDTO;
import com.marketplace.billingsoftware.io.CategoryRequest;
import com.marketplace.billingsoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    CategoryResponse add(CategoryRequest request, MultipartFile file) throws IOException;

    List<CategoryResponse> read();

    void delete(String categoryId);

    CategoryResponse patch(String categoryId, CategoryEntityDTO dto);
}
