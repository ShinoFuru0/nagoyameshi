package com.example.nagoyameshi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	 /**
     * 任意の例外をキャッチする例
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        // エラーの詳細をログに記録する
        logger.error("An error occurred: ", ex);
        
        // カスタマイズしたエラーメッセージを作成する
        String errorMessage = "An unexpected error occurred. Please try again later.";
        
        // エラーレスポンスを返す
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public class ApiError {
        private HttpStatus status;
        private String message;

        public ApiError(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        // ゲッターとセッターを追加
    }
    
}
