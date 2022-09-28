package io.github.simpleservice.controller;

import io.github.simpleservice.dto.BuyProductDto;
import io.github.simpleservice.helper.BuyProductHelper;
import java.util.Collections;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyProductController {

  private final BuyProductHelper buyProductHelper;

  public BuyProductController(BuyProductHelper buyProductHelper) {
    this.buyProductHelper = buyProductHelper;
  }

  @PostMapping("/buy")
  public void buyProduct(@RequestBody BuyProductDto dto) {
    buyProductHelper.buyProduct(dto, Collections.emptyList()).orElseThrow();
  }
}
