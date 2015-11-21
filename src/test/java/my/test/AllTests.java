package my.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FrontendTest.class,
        GameMechanicsTest.class,
        DBServiceTest.class,
        FrontendToGameMechanicsTest.class,
        FrontendToDBServiceTest.class
})
public class AllTests {
}
