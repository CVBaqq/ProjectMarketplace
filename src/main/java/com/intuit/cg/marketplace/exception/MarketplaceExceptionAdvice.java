package com.intuit.cg.marketplace.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class MarketplaceExceptionAdvice {

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<String> handle(ResourceNotFoundException e) {
    log.error("An error has occurred.", e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler({UnsatisfactoryBidException.class})
  public ResponseEntity<String> handle(UnsatisfactoryBidException e) {
    log.error("An error has occured.", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }
}
