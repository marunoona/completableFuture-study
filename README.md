# completableFuture-study

해당 Repository는 CompletableFuture를 사용하여 예제와 테스트 코드를 작성한 내용을 포함합니다.

***

1. Sync/Async 
   - 메소드를 제공하는 곳에서의 입장에 대한 것 
     - 호출된 함수의 수행 결과 및 종료를 호출한 함수가(호출된 함수뿐 아니라 호출한 함수도 함께) 신경 쓰면 Synchronous
     - 호출된 함수의 수행 결과 및 종료를 호출된 함수 혼자 직접 신경 쓰고 처리한다면(as a callback fn.) Asynchronous
2. Blocking/Non-Blocking
   - 메소드를 호출하는 곳, 즉 클라이언트에서의 입장에 대한 것 
     - 호출된 함수가 자신이 할 일을 모두 마칠 때까지 제어권을 계속 가지고서 호출한 함수에게 바로 돌려주지 않으면 Block
     - 호출된 함수가 자신이 할 일을 채 마치지 않았더라도 바로 제어권을 건네주어(return) 호출한 함수가 다른 일을 진행할 수 있도록 해주면 Non-block

***
### CompletableFuture 함수 정리
1. 비동기 함수
- supplyAsync()   
  - 반환값이 있는 콜백함수
  - Supplier Functional Interface 를 파라미터로 받고 CompletableFuture<U>를 반환
  - Supplier 는 파라미터는 없지만 반환값이 있는 함수형 인터페이스
  - CompletableFuture 는 Supplier 를 실행해서 비동기적으로 결과를 생성
- runAsync()
  - 반환값이 없는 콜백함수
  - Runnable Functional Interface 를 파라미터로 받는고 CompletableFuture<Void>가 반환형
  - Runnable 은 파라미터와 반환값 모두 없는 함수형 인터페이스
  
2. 블럭킹 함수   
   CompletableFuture 로 실행된 함수의 결과값을 반환받을 때 사용
- join()
- get()

3. 콜백 제공 함수
- thenApply(Function)
  - 리턴값에 대해 처리를 하고 결과값을 반환하는 콜백함수
  - CompletableFuture<T> 를 반환, 즉 데이터를 포함하는 Future 반환
  - 후속 작업에 결과값을 리턴해줄 수 있음
  - 별도의 스레드로 동작하고 싶다면 thenApplyAsync 를 사용
- thenAccept(Consumer)
  - 리턴값에 대해 처리를 하고결과를 반환하지 않는 콜백함수
  - CompletableFuture<Void>를 반환, 즉 데이터를 포함하지 않음
  - 후속 작업에 결과값을 리턴할 수 없음
  - 별도의 스레드로 동작하고 싶다면 thenAcceptAsync 를 사용
- thenRun(Runnable)
  - 리턴값을 받지도 않고 반환하지도 않는 추가적인 행동만을 하는 함수
  
4. 조합 연산 함수
- thenCompose()
  - 여러 작업을 순차적으로 수행, CompletableFuture 를 파이프라인 형식으로 연결해서 실행
  - 첫번째 CompletableFuture 의 결과가 리턴되면 그 결과를 두번째 CompletableFuture 로 전달하여 순차적으로 작업이 처리
- thenCombine()
  - 여러 작업을 동시에 수행, CompletableFuture 2개를 실행해서 결과를 조합할 때 사용
  - 병렬 실행하여 조합하며 순차적으로 실행하지 않음
- allOf()
  - 여러개의 future의 결과를 받아서 처리할 수 있음
  - 동시에 n 개의 요청을 호출하고 모든 호출이 완셩되면 그 다음 작업을 진행해야할 경우 유용
  - allOf는 리턴값이 void 이고 모든 Completable에 Blocking만 걸 뿐이기 때문에 콜백을 사용하여 결과값을 받아
- anyOf()
  - 여러개의 CompletableFuture 중에서 빨리 처리되는 1개의 결과만을 가져옴
  - 동시에 n 개의 요청을 호출하고 하나라도 호출이 완성되면 진행하는 경우에 유용
  
  