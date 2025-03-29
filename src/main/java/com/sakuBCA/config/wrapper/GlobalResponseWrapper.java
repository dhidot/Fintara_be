//package com.sakuBCA.config.wrapper;
//
//import com.sakuBCA.dtos.ApiResponse;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@ControllerAdvice
//public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class converterType) {
//        // Berlaku untuk semua response dari controller
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType,
//                                  org.springframework.http.MediaType selectedContentType,
//                                  Class selectedConverterType,
//                                  org.springframework.http.server.ServerHttpRequest request,
//                                  org.springframework.http.server.ServerHttpResponse response) {
//        // Jika respons sudah dalam format ApiResponse, kembalikan langsung
//        if (body instanceof ApiResponse) {
//            return body;
//        }
//        // Bungkus dalam ApiResponse
//        return new ApiResponse<>(HttpStatus.OK.value(), "Success", body);
//    }
//
//    // Tangani exception global dan bungkus dalam ApiResponse
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
//        return new ResponseEntity<>(
//                new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", e.getMessage()), // Ubah urutan
//                HttpStatus.INTERNAL_SERVER_ERROR
//        );
//    }
//}
