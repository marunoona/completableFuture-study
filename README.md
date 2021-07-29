# completableFuture-study

해당 Repository는 CompletableFuture를 사용하여 예제와 테스트 코드를 작성한 내용을 포함합니다.

1. Sync/Async 
   - 메소드를 제공하는 곳에서의 입장에 대한 것 
     - 호출된 함수의 수행 결과 및 종료를 호출한 함수가(호출된 함수뿐 아니라 호출한 함수도 함께) 신경 쓰면 Synchronous
     - 호출된 함수의 수행 결과 및 종료를 호출된 함수 혼자 직접 신경 쓰고 처리한다면(as a callback fn.) Asynchronous
2. Blocking/Non-Blocking
   - 메소드를 호출하는 곳, 즉 클라이언트에서의 입장에 대한 것 
     - 호출된 함수가 자신이 할 일을 모두 마칠 때까지 제어권을 계속 가지고서 호출한 함수에게 바로 돌려주지 않으면 Block
     - 호출된 함수가 자신이 할 일을 채 마치지 않았더라도 바로 제어권을 건네주어(return) 호출한 함수가 다른 일을 진행할 수 있도록 해주면 Non-block
