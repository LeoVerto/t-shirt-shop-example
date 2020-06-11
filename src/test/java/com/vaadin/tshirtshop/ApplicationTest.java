package com.vaadin.tshirtshop;

import com.github.mvysny.kaributesting.v10.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.tshirtshop.domain.TShirtOrder;
import com.vaadin.tshirtshop.domain.TShirtOrderRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.GridKt.*;
import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@DirtiesContext
public class ApplicationTest {

    private static Routes routes;
    @BeforeAll
    public static void discoverRoutes() {
         routes = new Routes().autoDiscoverViews("com.vaadin.tshirtshop");
    }

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TShirtOrderRepository repo;

    @BeforeEach
    public void setup() throws Exception {
        final SpringServlet servlet = new MockSpringServlet(routes, ctx);
        MockVaadin.setup(UI::new, servlet);
        repo.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void placeOrder() {
        _setValue(_get(TextField.class, spec -> spec.withCaption("Name")), "Foo");
        _setValue(_get(TextField.class, spec -> spec.withCaption("Email")), "foo@bar.baz");
        _setValue(_get(ComboBox.class, spec -> spec.withCaption("T-shirt size")), "Small");
        _click(_get(Button.class, spec -> spec.withCaption("Place order")));

        final List<TShirtOrder> all = repo.findAll();
        assertEquals("orders=" + all, 1, all.size());
        assertEquals("Foo", all.get(0).getName());
    }

    @Test
    public void listOrders() {
        UI.getCurrent().navigate(ListOrdersView.class);
        expectRows(_get(Grid.class), 0);
    }
}
