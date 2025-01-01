package com.study.HelloWorld.service;

import com.study.HelloWorld.dto.PageRequestDTO;
import com.study.HelloWorld.dto.PageResponseDTO;
import com.study.HelloWorld.dto.ProductDTO;

import javax.transaction.Transactional;

@Transactional
public interface ProductService {
    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);

    Long register(ProductDTO productDTO);

    ProductDTO get(Long pno);

    void modify(ProductDTO productDTO);

    void remove(Long pno);
}
