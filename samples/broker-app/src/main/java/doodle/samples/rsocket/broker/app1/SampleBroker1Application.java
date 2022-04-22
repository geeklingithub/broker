/*
 * Copyright (c) 2022-present Doodle. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package doodle.samples.rsocket.broker.app1;

import doodle.rsocket.broker.client.EnableBrokerClient;
import java.util.concurrent.CountDownLatch;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBrokerClient
@SpringBootApplication
public class SampleBroker1Application {

  public static void main(String[] args) throws InterruptedException {
    // a simple server will throw exception after client disconnected
    // https://github.com/rsocket/rsocket-java/issues/1018
    //    Hooks.onErrorDropped(__ -> {});
    final CountDownLatch countDownLatch = new CountDownLatch(1);
    new SpringApplicationBuilder()
        .sources(SampleBroker1Application.class)
        .properties("spring.config.name=app1")
        .run(args);
    countDownLatch.await();
  }
}
