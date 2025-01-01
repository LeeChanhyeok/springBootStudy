package com.study.HelloWorld.controller;

import com.study.HelloWorld.domain.Product;
import com.study.HelloWorld.dto.PageRequestDTO;
import com.study.HelloWorld.dto.PageResponseDTO;
import com.study.HelloWorld.dto.ProductDTO;
import com.study.HelloWorld.service.ProductService;
import com.study.HelloWorld.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;

    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO){
        log.info("register: " + productDTO);

        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);

        productDTO.setUploadFileNames(uploadFileNames);
        log.info(uploadFileNames);

        Long pno = productService.register(productDTO);

        return Map.of("result", pno);
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
        return fileUtil.getFile(fileName);
    }

    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("list............................" + pageRequestDTO);

        return productService.getList(pageRequestDTO);

    }

    @GetMapping("/{pno}")
    private ProductDTO read(@PathVariable(name="pno") Long pno){

        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable(name="pno") Long pno, ProductDTO productDTO){

        productDTO.setPno(pno);
        ProductDTO oldProductDTO = productService.get(pno);

        List<String> oldFileNames = oldProductDTO.getUploadFileNames();

        List<MultipartFile> files = productDTO.getFiles();

        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        List<String> uploadedFileNames= productDTO.getUploadFileNames();

        if(currentUploadFileNames != null && currentUploadFileNames.size() >0 ){
            uploadedFileNames.addAll(currentUploadFileNames);
        }
        productService.modify(productDTO);

        if(oldFileNames != null && oldFileNames.size()>0){
            List<String> removeFiles = oldFileNames.stream().filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());

            fileUtil.deleteFiles(removeFiles);
        }
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{pno}")
        public Map<String, String> remove(@PathVariable("pno") Long pno) {

            List<String> oldFileNames = productService.get(pno).getUploadFileNames();

            productService.remove(pno);

            fileUtil.deleteFiles(oldFileNames);

            return Map.of("RESULT", "SUCCESS");
        }


}
