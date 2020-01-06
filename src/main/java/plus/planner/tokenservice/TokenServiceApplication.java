package plus.planner.tokenservice;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import plus.planner.tokenservice.tokengenerator.TokenGenerator;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

import static plus.planner.tokenservice.tokengenerator.PemUtils.readPrivateKeyFromFile;

@Configuration
@EnableEurekaClient
@SpringBootApplication
public class TokenServiceApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TokenGenerator tokenGenerator() throws IOException {
        final Algorithm algorithm = Algorithm.RSA512(null, (RSAPrivateKey) readPrivateKeyFromFile("src/main/resources/PrivateKey.pem", "RSA"));
        return new TokenGenerator(algorithm);
    }

    public static void main(String[] args) {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        SpringApplication.run(TokenServiceApplication.class, args);
    }

}
