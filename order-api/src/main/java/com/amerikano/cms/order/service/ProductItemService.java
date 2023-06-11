package com.amerikano.cms.order.service;

import com.amerikano.cms.order.domain.model.Product;
import com.amerikano.cms.order.domain.model.ProductItem;
import com.amerikano.cms.order.domain.product.AddProductItemForm;
import com.amerikano.cms.order.domain.product.UpdateProductItemForm;
import com.amerikano.cms.order.domain.repository.ProductItemRepository;
import com.amerikano.cms.order.domain.repository.ProductRepository;
import com.amerikano.cms.order.exception.CustomException;
import com.amerikano.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form) {
        Product product = productRepository.findByIdAndSellerId(form.getProductId(), sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        if (product.getProductItems().stream()
                .anyMatch(item -> item.getName().equals(form.getName()))) {
            throw new CustomException(ErrorCode.SAVE_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);
        return product;
    }

    @Transactional
    public ProductItem updateProductItem(Long sellerId, UpdateProductItemForm form) {
        ProductItem productItem = productItemRepository.findById(form.getId())
                .filter(pi -> pi.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));

        productItem.setName(form.getName());
        productItem.setCount(form.getCount());
        productItem.setPrice(form.getPrice());

        return productItem;
    }

    @Transactional
    public void deleteProductItem(Long sellerId, Long productItemId) {
        ProductItem productItem = productItemRepository.findById(productItemId)
                .filter(pi -> pi.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));

        productItemRepository.delete(productItem);
    }
}