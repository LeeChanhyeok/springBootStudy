package com.study.HelloWorld.repository;

import com.study.HelloWorld.Repository.ProductRepository;
import com.study.HelloWorld.domain.Product;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import javax.persistence.Column;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTests {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void testInsert(){
       for(int i=0 ; i<10 ; i++){
           Product product = Product.builder()
                   .pname("상품"+i)
                   .price(100*i)
                   .pdesc("상품설명 " + i)
                   .build();
           product.addImageString(UUID.randomUUID().toString() + "_" + "IMAGE1.jpg");
           product.addImageString(UUID.randomUUID().toString() + "_" + "IMAGE2.jpg");

           productRepository.save(product);
           log.info("----------------------------");

       }
    }

    @Test
    public void testRead(){
        Long pno = 1L;

        Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        log.info(product);
        log.info(product.getImageList());
    }

    @Commit
    @Transactional
    @Test
    public void testDel(){
        Long pno= 2L;

        productRepository.updateToDelete(pno, true);
    }

    @Test
    public void testUpdate(){
        Long pno =10L;

        Product product = productRepository.selectOne(pno).get();

        product.changeName("10번 상품");
        product.changeDesc("10번 상품 설명");
        product.changePrice(5000);

        product.clearList();

        product.addImageString(UUID.randomUUID().toString()+"_"+"NEWIMAGE1.jpg");
        product.addImageString(UUID.randomUUID().toString()+"_"+"NEWIMAGE2.jpg");
        product.addImageString(UUID.randomUUID().toString()+"_"+"NEWIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testList(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        result.getContent().forEach(arr->log.info(Arrays.toString(arr)));
    }
}
