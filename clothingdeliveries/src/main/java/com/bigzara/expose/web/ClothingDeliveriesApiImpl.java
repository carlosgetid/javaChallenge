package com.bigzara.expose.web;

import com.bigzara.clothingdeliveries.business.ClothingDeliveriesApiMapper;
import com.bigzara.clothingdeliveries.business.ClothingDeliveriesService;
import com.bigzara.clothingdeliveries.model.api.ProductDetail;
import com.bigzara.clothingdeliveries.util.constants.LogConstants;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ClothingDeliveriesApiImpl implements ProductApiDelegate {

  private ClothingDeliveriesService clothingDeliveriesService;

  private ClothingDeliveriesApiMapper clothingDeliveriesApiMapper;

  @Override
  public Maybe<ResponseEntity<Set<ProductDetail>>> getProductSimilar(String productId) {
    return Maybe.fromSingle(clothingDeliveriesService.showSimilarProducts(productId)
            .map(clothingDeliveriesApiMapper :: mapProduct)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(
                disposable -> log.info(LogConstants.CLOTHING_DELIVERIES_CONTROLLER_START, productId))
            .doOnComplete(
                () -> log.info(LogConstants.CLOTHING_DELIVERIES_CONTROLLER_COMPLETE, productId))
            .doOnError(
                throwable -> log.error(LogConstants.CLOTHING_DELIVERIES_CONTROLLER_ERROR, productId,
                    throwable.getMessage()))
            .toList()
            .map(Set :: copyOf))
        .map(ResponseEntity :: ok);
  }
}
