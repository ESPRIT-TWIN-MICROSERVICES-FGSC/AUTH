package esprit.fgsc.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ExternalServices {
    public static final String GATEWAY_URL = "https://fgsc-gateway.herokuapp.com/";
    private EurekaDiscoveryClient discoveryClient;
    private RestTemplate restTemplate;
    private WebClient reactiveWebClient;
    @Autowired
    public ExternalServices(EurekaDiscoveryClient discoveryClient, RestTemplate restTemplate, WebClient reactiveWebClient){
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
        this.reactiveWebClient = reactiveWebClient;
    }

    public Object TestExternalGet() throws URISyntaxException {
//        RestTemplate t = new RestTemplate();
        return restTemplate.getForObject(new URI(GATEWAY_URL),Object.class);
    }
    public Mono<?> TestReactiveExternalGet() throws URISyntaxException {
        return Mono.just(reactiveWebClient.get().uri(new URI(GATEWAY_URL)).retrieve());
    }
}
