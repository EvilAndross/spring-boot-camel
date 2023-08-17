/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.quickstarts.camel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends RouteBuilder {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure() throws Exception {
        final RouteDefinition from;
        if (Files.exists(keystorePath())) {
            //from = from("netty-http:proxy://0.0.0.0:8443?ssl=true&keyStoreFile=spring/app.jks&passphrase=password&trustStoreFile=spring/app.jks");
            // Path - D:\\\\Code\\\\spring-boot-camel\\\\spring-boot-camel\\\\src\\\\main\\\\resources\\\\spring\\\\app.jks
            from = from("netty-http:proxy://0.0.0.0:8443?ssl=true&keyStoreFile=/tls/keystore.jks&passphrase=password&trustStoreFile=/tls/keystore.jks");
        } else {
            //from = from("netty-http:proxy://0.0.0.0:8443?ssl=true&keyStoreFile=D:\\\\Code\\\\spring-boot-camel\\\\spring-boot-camel\\\\src\\\\main\\\\resources\\\\spring\\\\app.jks&passphrase=password&trustStoreFile=D:\\\\Code\\\\spring-boot-camel\\\\spring-boot-camel\\\\src\\\\main\\\\resources\\\\spring\\\\app.jks");
            from = from("netty-http:proxy://0.0.0.0:9000");
        }

        from
            .process(Application::uppercase)
            .toD("netty-http:"
                + "${headers." + Exchange.HTTP_SCHEME + "}://"
                + "${headers." + Exchange.HTTP_HOST + "}:"
                + "${headers." + Exchange.HTTP_PORT + "}"
                + "${headers." + Exchange.HTTP_PATH + "}")
            .process(Application::uppercase);
    }

    Path keystorePath() {
        //return Path.of("spring", "app.jks");
        return Paths.get("/tls/keystore.jks");
    }

    // @Override
    // public void configure() throws Exception {
    //     from("netty-https:proxy://0.0.0.0:9000")
    //         .process(Application::uppercase)
    //         .toD("netty-http:"
    //             + "${headers." + Exchange.HTTP_SCHEME + "}://"
    //             + "${headers." + Exchange.HTTP_HOST + "}:"
    //             + "${headers." + Exchange.HTTP_PORT + "}"
    //             + "${headers." + Exchange.HTTP_PATH + "}")
    //         .process(Application::uppercase);
    // }

    public static void uppercase(final Exchange exchange) {
        final Message message = exchange.getIn();
        final String body = message.getBody(String.class);
        message.setBody(body.toUpperCase(Locale.US));
    }

    // @Override
    // public void configure() throws Exception {
    //     from("timer://foo?period=5000")
    //         .setBody().constant("Hello World")
    //         .log(">>> ${body}");
    // }   
}
