package tacos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc를 주입

    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/")) //GET을 실행!
                .andExpect(status().isOk()) // HTTP Status 200이 되어야 함
                .andExpect(view().name("home")) //home view가 나와야 함
                .andExpect(content().string(
                        containsString("Welcome to..."))); // content에 'Welcome to...'가 있어야 함
    }

}