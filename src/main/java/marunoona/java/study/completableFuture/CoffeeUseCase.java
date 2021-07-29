package marunoona.java.study.completableFuture;

import java.util.concurrent.Future;

public interface CoffeeUseCase {

    int getPrice(String name);                              //Sync(동기)
    Future<Integer> getPriceAsyncV1(String name);             //Async(비동기)
    Future<Integer> getPriceAsyncV2(String name);             //Async(비동기)
    Future<Integer> getDiscountPriceAsync(Integer price);   //Async(비동기)
}
