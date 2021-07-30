package marunoona.java.study.completableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * CoffeeUseCase 인터페이스의 구현체
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CoffeeComponent implements CoffeeUseCase {

    private final CoffeeRepository coffeeRepository;

    //Executor executor = Executors.newFixedThreadPool(10);
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public int getPrice(String name) {
        log.info("동기 호출 방식으로 가격 조회 시작");
        return coffeeRepository.getPriceByName(name);
    }

    /**
     * 해당 메소드는 호출하는 쪽에서
     * join이나 get을 호출하여 데이터를 반환 받는다.
     * join이나 get을 수행하는 시점에서는 데이터를 조회할 때까지 블록킹된다.
     * 논블럭킹으로 개선하기 위해서는 콜백함수를 구현해야한다.
     *
     * @param name
     * @return
     */
    @Override
    public CompletableFuture<Integer> getPriceAsyncV1(String name) {
        log.info("비동기 호출 방식으로 가격 조회 시작");

        CompletableFuture<Integer> future = new CompletableFuture<>();

        new Thread(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            Integer price = coffeeRepository.getPriceByName(name);
            future.complete(price);
        }).start();

        return future;
    }

    /**
     * supplyAsync 메소드 사용
     * supplyAsync 는 Supplier Functional Interface를 파라미터로 받는다.
     * Supplier는 파라미터는 없지만 반환값이 있는 함수형 인터페이스다.
     * supplyAsnc로 수행하는 로직은 ForkJoinPool의 commonPool을 사용하는데 이는 바람직하지 않다.
     * supplyAsnc를 실행할 때 Excutor를 파라미터로 추가하면 별도의 스레드 풀에서 동작한다.
     * @param name
     * @return
     */
    @Override
    public CompletableFuture<Integer> getPriceAsyncV2(String name) {
        log.info("비동기 호출 방식으로 가격 조회 시작");
        //return CompletableFuture.supplyAsync(() -> coffeeRepository.getPriceByName(name));
        //로그추가
        return CompletableFuture.supplyAsync(()->{
            log.info("supplyAsync");
            return coffeeRepository.getPriceByName(name);
        }, threadPoolTaskExecutor);

    }

    /**
     * 조회된 커피 가격에서 할인을 해주는 메소드
     * @param price
     * @return
     */
    @Override
    public CompletableFuture<Integer> getDiscountPriceAsync(Integer price) {
        return CompletableFuture.supplyAsync( () -> {
            log.info("getDiscountPriceAsync");
            return (int)(price * 0.9);
        }, threadPoolTaskExecutor);
    }
}
