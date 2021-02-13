package tacos.web.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.*;
import tacos.Taco;

@Configuration
public class SpringDataRestConfiguration {
    @Bean
    public ResourceProcessor<PagedResources<Resources<Resource<Taco>>>> tacoProcessor(EntityLinks links) {
        return resources -> {
            resources.add(links.linkFor(Taco.class).slash("recent").withRel("recents"));
            return resources;
        };
    }
}
