package integration;

import com.ifellow.bookstore.AppConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = AppConfig.class)
public abstract class AbstractIntegrationTest {
}
