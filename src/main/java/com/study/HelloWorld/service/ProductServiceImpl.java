package com.study.HelloWorld.service;

import com.study.HelloWorld.Repository.ProductRepository;
import com.study.HelloWorld.domain.Product;
import com.study.HelloWorld.domain.ProductImage;
import com.study.HelloWorld.dto.PageRequestDTO;
import com.study.HelloWorld.dto.PageResponseDTO;
import com.study.HelloWorld.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO){
        log.info("getList..........................");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending()
        );
        Page<Object[]> result = productRepository.selectList(pageable);

        List<ProductDTO> dtoList = result.get().map(arr -> {
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage)  arr[1];

            ProductDTO productDTO = ProductDTO.builder().pno(product.getPno()).pname(product.getPname()).pdesc(product.getPdesc()).price(product.getPrice()).build();

            String imageStr = productImage.getFileName();
            productDTO.setUploadFileNames(List.of(imageStr));

            return productDTO;
        }).collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public Long register(ProductDTO productDTO){
        Product product = dtoToEntity(productDTO);

        Product result = productRepository.save(product);

        return result.getPno();
    }

    private Product dtoToEntity(ProductDTO productDTO){
        Product product = Product.builder().
                pno(productDTO.getPno()).
                pname(productDTO.getPname()).
                pdesc(productDTO.getPdesc()).
                price(productDTO.getPrice())
                .build();

        List<String> uploadFileNames = productDTO.getUploadFileNames();

        if(uploadFileNames == null){
            return product;
        }
        uploadFileNames.stream().forEach(uploadName -> {
            product.addImageString(uploadName);
        });
        return product;
    }

    @Override
    public ProductDTO get(Long pno) {
        java.util.Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        ProductDTO productDTO = entityToDTO(product);

        return productDTO;
    }

    private ProductDTO entityToDTO(Product product){
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if(imageList ==null || imageList.size() == 0){
            return productDTO;
        }

        List<String> fileNameList = imageList.stream().map(productImage -> productImage.getFileName()).toList();

        productDTO.setUploadFileNames(fileNameList);

        return productDTO;


    }

    @Override
    public void modify(ProductDTO productDTO){
        Optional<Product> result = productRepository.findById(productDTO.getPno());

        Product product =result.orElseThrow();

        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changePrice(productDTO.getPrice());

        product.clearList();

        List<String> uploadFileNames = productDTO.getUploadFileNames();

        if(uploadFileNames != null && uploadFileNames.size() >0){
            uploadFileNames.stream().forEach(uploadNames -> {
                product.addImageString(uploadNames);
            });
        }
        productRepository.save(product);
    }

    @Override
    public void remove(Long pno){
        productRepository.updateToDelete(pno , true);
    }
}