package com.amerikano.cms.order.domain.repository;

import com.amerikano.cms.order.domain.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchByName(String name);
}
