package marunoona.java.study.completableFuture;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;


//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {CoffeeComponent.class, CoffeeRepository.class})
@SpringBootTest
class CoffeeComponentTest {

    Logger log = (Logger) LoggerFactory.getLogger(CoffeeComponentTest.class);

    @Autowired
    private CoffeeComponent coffeeComponent;

    @Test
    void 가격조회_동기_블록킹_호출테스트() {
        int expectedPrice = 2000;

        int resultPrice = coffeeComponent.getPrice("latte");
        log.info("최종 가격 전달 받음");

        assertThat(resultPrice).isEqualTo(expectedPrice);
    }

    /**
     * 메소드를 제공하는 곳에서는 CompletableFuture를 반환하고
     * 호출한 해당 테스트에서는 join을 통하여 데이터를 반환받음
     * Async와 Blocking이 혼합되어있는 상황이다
     */
    @Test
    void 가격조회_비동기_블록킹_호출테스트() {
        int expectedPrice = 2000;

        CompletableFuture<Integer> future = coffeeComponent.getPriceAsyncV1("latte");
        log.info("아직 최종 데이터를 전달 받지는 않았지만. 다른 작업 수행 가능");

        int resultPrice = future.join();//블록킹
        log.info("최종 가격 전달 받음");

        assertThat(resultPrice).isEqualTo(expectedPrice);
    }

    @Test
    void 가격조회_비동기_블록킹_호출테스트ㅍ2() {
        int expectedPrice = 2000;

        CompletableFuture<Integer> future = coffeeComponent.getPriceAsyncV2("latte");
        log.info("아직 최종 데이터를 전달 받지는 않았지만. 다른 작업 수행 가능");

        int resultPrice = future.join();//블록킹
        log.info("최종 가격 전달 받음");

        assertThat(resultPrice).isEqualTo(expectedPrice);
    }

    /**
     * CompletableFuture이 제공하는 콜백함수 thenAccept 사용
     * thenAccept는 CompletableFuture<Void>를 반환
     * -> 즉, 결과를 반환하지 않는다.
     */
    @Test
    void 가격조회_비동기_호출_콜백_반환없는경우_테스트() {
        int expectedPrice = 2000;

        CompletableFuture<Void> future = coffeeComponent.getPriceAsyncV2("latte")
                .thenAccept(p -> {
                    log.info("콜백, 가격은 {}원, 하지만 데이터는 반환하지 않음", p);
                    assertThat(p).isEqualTo(expectedPrice);
                });
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능, 논블럭킹");

        /**
         * 아래 구문이 없으면 main thread가 종료되기 때문에 thenAccept 확인하기 전에 메소드가 끝난다.
         * 때문에 테스트를 위해서 메인스레드가 종료되지 않도록 블록킹으로 대기하기 위한 코드이다.
         * future가 complete가 되면 위에 작성한 thenAccept 코드가 실행된다.
         * 실제 서비스 코드에서는 해당 코드는 필요 없다.
         */
        assertThat(future.join()).isNull();
    }

    /**
     * CompletableFuture이 제공하는 콜백함수 thenApply 사용
     * thenApply는 CompletableFuture<T>를 반환
     * -> 즉, 데이터를 포함하는 Future를 반환한다.
     */
    @Test
    void 가격조회_비동기_호출_콜백_반환있는경우_테스트() {
        int expectedPrice = 2500;

        CompletableFuture<Void> future = coffeeComponent.getPriceAsyncV2("latte")
                .thenApply(p -> {
                    log.info("같은 스레드로 동작");
                    return p + 500;
                })
                .thenAccept(p -> {
                    log.info("콜백, 가격은 {}원, 하지만 데이터는 반환하지 않음", p);
                    assertThat(p).isEqualTo(expectedPrice);
                });
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능, 논블럭킹");

        //마찬가지로 테스트용 블럭킹코드
        assertThat(future.join()).isNull();
    }

    @Test
    void thenCombine_테스트() {
        int expectedPrice = 3000;

        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsyncV2("latte");
        CompletableFuture<Integer> futureB = coffeeComponent.getPriceAsyncV2("espresso");

        /**
         * 스레드풀 사이즈를 1로 바꾸면 다른 스레드에서 돌지 못하여 결국 병렬처리가 되지 못한다.
         */
        //CompletableFuture<Integer> future = futureA.thenCombine(futureB, (a, b) -> a + b);
        Integer resultPrice = futureA.thenCombine(futureB, Integer::sum).join();

        assertThat(resultPrice).isEqualTo(expectedPrice);
    }

    @Test
    void thenCompose_테스트() {
        int expectedPrice = (int) (1000 * 0.9);

        /**
         * 1. 커피의 가격 조회
         * 2. 조회된 가격에 할인율 적용
         */
        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsyncV2("espresso");
        Integer resultPrice = futureA.thenCompose(result ->
                coffeeComponent.getDiscountPriceAsync(result)).join();

        assertThat(resultPrice).isEqualTo(expectedPrice);
    }

    @Test
    void allOf_테스트(){
        int expectedPrice = 2000 + 1000 + 2500;

        //given
        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsyncV2("latte");
        CompletableFuture<Integer> futureB = coffeeComponent.getPriceAsyncV2("espresso");
        CompletableFuture<Integer> futureC = coffeeComponent.getPriceAsyncV2("mocha");

        List<CompletableFuture<Integer>> completableFutureList = Arrays.asList(futureA, futureB, futureC);

        //when
        Integer resultPrice = CompletableFuture.allOf(futureA, futureB, futureC)
                .thenApply(Void -> completableFutureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join()
                .stream()
                .reduce(0, Integer::sum);

        //then
        assertThat(resultPrice).isEqualTo(expectedPrice);
    }
}