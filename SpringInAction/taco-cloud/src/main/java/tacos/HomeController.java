package tacos;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 컨트롤러
public class HomeController {
//    @GetMapping("/")
    public String home() {
        return "home";
    }
}
