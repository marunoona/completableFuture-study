package marunoona.java.study.completableFuture;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CoffeeRepository {

    private Map<String, Coffee> coffeeMap = new HashMap<>();

    @PostConstruct
    public void init(){
        coffeeMap.put("latte", Coffee.builder().name("latte").price(2000).build());
        coffeeMap.put("espresso", Coffee.builder().name("espresso").price(1000).build());
        coffeeMap.put("mocha", Coffee.builder().name("mocha").price(2500).build());
    }

    public int getPriceByName(String name){
        try {
            /** 테스트를 위해 1초 지연 설정*/
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return coffeeMap.get(name).getPrice();
    }
}
