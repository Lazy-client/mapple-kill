package io.renren;

import io.renren.modules.app.controller.PersonRuleController;
import io.renren.modules.app.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author hxx
 * @date 2022/3/30 18:36
 */
@SpringBootTest
public class PersonRuleControllerTest {
    @Autowired
    PersonRuleController controller;

    @Test
    public void testOnePerson() {
        Person bob = new Person();
        bob.setName("bob");

        controller.fireAllRules4One(bob);
    }

    @Test
    public void testTwoPerson() {
        Person bob = new Person();
        bob.setAge(33);

        Person other = new Person();
        other.setAge(88);

        controller.fireAllRules4List(Arrays.asList(bob, other));
    }
}
