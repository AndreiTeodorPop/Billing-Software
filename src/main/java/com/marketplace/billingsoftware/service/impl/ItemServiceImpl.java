package com.marketplace.billingsoftware.service.impl;

import com.marketplace.billingsoftware.entity.CategoryEntity;
import com.marketplace.billingsoftware.entity.ItemEntity;
import com.marketplace.billingsoftware.io.ItemRequest;
import com.marketplace.billingsoftware.io.ItemResponse;
import com.marketplace.billingsoftware.repository.CategoryRepository;
import com.marketplace.billingsoftware.repository.ItemRepository;
import com.marketplace.billingsoftware.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.marketplace.billingsoftware.util.ImageUtil.deleteImageFile;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Value("${upload.path}")
    private String uploadPath;

    private final CategoryRepository categoryRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) throws IOException {
        ItemEntity newItem = convertToEntity(request);

        String filename = file.getOriginalFilename();
        Path imagePath = Paths.get(uploadPath + File.separator + filename);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());
        String imageUrl = "http://localhost:8080/api/v1.0/images/" + filename;

        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        newItem.setCategory(existingCategory);
        newItem.setImgUrl(imageUrl);
        newItem = itemRepository.save(newItem);

        return convertToResponse(newItem);
    }

    @Override
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(String itemId) {
        ItemEntity existingItem = itemRepository.findByItemId(itemId).orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        boolean isFileDeleted = deleteImageFile(existingItem.getImgUrl(), uploadPath);
        if(isFileDeleted) {
            itemRepository.delete(existingItem);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete the image");
        }
    }

    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImgUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }
}
