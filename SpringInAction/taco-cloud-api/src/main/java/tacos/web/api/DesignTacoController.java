package tacos.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacos.Taco;
import tacos.data.TacoRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/design", produces = "application/json")
@CrossOrigin(origins = "*") // 다른 도메인 요청 허용
public class DesignTacoController {
    private TacoRepository tacoRepo;

    @Autowired
    EntityLinks entityLinks;

    public DesignTacoController(TacoRepository tacoRepo) {
        this.tacoRepo = tacoRepo;
    }

    @GetMapping("/recent")
    public Resources<TacoResource> recentTacos() {
//    public Resources<Resource<Taco>> recentTacos() {
//    public Iterable<Taco> recentTacos() {

        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
//        return tacoRepo.findAll(page).getContent();

        List<Taco> tacos = tacoRepo.findAll(page).getContent();
//        Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
//
////        recentResources.add(new Link("http://localhost:8080/design/recent", "recents"));
//        recentResources.add(
//                linkTo(DesignTacoController.class)
//                        .slash("recent")
//                        .withRel("recents"));
//        recentResources.add(
//                linkTo(methodOn(DesignTacoController.class).recentTacos())
//                        .withRel("recents"));

        List<TacoResource> tacoResources = new TacoResourceAssembler().toResources(tacos);
        Resources<TacoResource> recentResources = new Resources<>(tacoResources);
        recentResources.add(
                linkTo(methodOn(DesignTacoController.class).recentTacos())
                        .withRel("recents"));

        return recentResources;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
        Optional<Taco> optTaco = tacoRepo.findById(id);

        return optTaco.isPresent() ?
                new ResponseEntity<>(optTaco.get(), HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepo.save(taco);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            tacoRepo.deleteById(orderId);
        } catch (EmptyResultDataAccessException e) {
        }
    }

}
