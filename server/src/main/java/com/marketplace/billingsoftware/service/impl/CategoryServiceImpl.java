package com.marketplace.billingsoftware.service.impl;

import com.marketplace.billingsoftware.entity.CategoryEntity;
import com.marketplace.billingsoftware.entity.CategoryEntityDTO;
import com.marketplace.billingsoftware.io.CategoryRequest;
import com.marketplace.billingsoftware.io.CategoryResponse;
import com.marketplace.billingsoftware.repository.CategoryRepository;
import com.marketplace.billingsoftware.repository.ItemRepository;
import com.marketplace.billingsoftware.service.CategoryService;
import com.marketplace.billingsoftware.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    ImageUtil imageUtil;

    @Value("${upload.path}")
    private String uploadPath;

    private final CategoryRepository categoryRepository;

    private final ItemRepository itemRepository;

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) throws IOException {
        CategoryEntity newCategory = convertToEntity(request);

        String imageUrl = imageUtil.addImageFile(file, uploadPath);

        newCategory.setImgUrl(imageUrl);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path targetDir = projectRoot.resolve("server").resolve(uploadPath).normalize();
        imageUtil.deleteImageFile(existingCategory.getImgUrl(), targetDir.toString());
        categoryRepository.delete(existingCategory);
    }

    @Override
    public CategoryResponse patch(String categoryId, CategoryEntityDTO dto) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
        if (dto.getName() != null) existingCategory.setName(dto.getName());
        if (dto.getDescription() != null) existingCategory.setDescription(dto.getDescription());
        if (dto.getBgColor() != null) existingCategory.setBgColor(dto.getBgColor());
        if (dto.getImgUrl() != null) existingCategory.setImgUrl(dto.getImgUrl());
        CategoryEntity newCategory = categoryRepository.save(existingCategory);
        return convertToResponse(newCategory);
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {

        Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());

        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .imgUrl(newCategory.getImgUrl())
                .bgColor(newCategory.getBgColor())
                .createdAt(newCategory.getUpdatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
