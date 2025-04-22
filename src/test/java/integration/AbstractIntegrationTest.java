package integration;

import com.ifellow.bookstore.configuration.RootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
public abstract class AbstractIntegrationTest {
}
