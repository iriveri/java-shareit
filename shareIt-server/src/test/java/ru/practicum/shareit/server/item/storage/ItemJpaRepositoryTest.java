package ru.practicum.shareit.server.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ItemJpaRepositoryTest {

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        entityManager.persist(owner);

        Item item1 = new Item();
        item1.setOwnerId(1L);
        item1.setName("Big Dar");
        item1.setAvailable(true);
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setOwnerId(1L);
        item2.setName("Small Dar");
        item2.setAvailable(true);
        entityManager.persist(item2);

        Item item3 = new Item();
        item3.setOwnerId(1L);
        item3.setName("Medium Dar");
        item3.setAvailable(false);
        entityManager.persist(item3);

        Item item4 = new Item();
        item4.setOwnerId(1L);
        item4.setName("Screwdriver");
        item4.setDescription("Poor small driver that screwed himself");
        item4.setAvailable(true);
        entityManager.persist(item4);

        entityManager.flush();
    }

    @Test
    @DisplayName("Test searchForItems search area")
    void searchForItems_ShouldSearchInNameAndDescription() {
        Long ownerId = 1L;
        String searchText = "small";
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Item> items = itemJpaRepository.searchForItems(searchText, pageable);

        assertThat(items).isNotNull();
        assertThat(items.getContent()).hasSize(2);  // Проверка количества элементов
    }

    @Test
    @DisplayName("Test searchForItems only available")
    void searchForItems_ShouldReturnAvailableItems() {
        String searchText = "Dar";
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Item> items = itemJpaRepository.searchForItems(searchText, pageable);

        assertThat(items).isNotNull();
        assertThat(items.getContent()).hasSize(2);  // Проверка количества найденных элементов
    }

    @Test
    @DisplayName("Test searchForItems pangoliation")
    void searchForItems_ShouldPangoliate() {
        String searchText = "Dar";
        Pageable pageable = PageRequest.of(0, 1);
        Slice<Item> items = itemJpaRepository.searchForItems(searchText, pageable);

        assertThat(items).isNotNull();
        assertThat(items.getContent()).hasSize(1);  // Проверка количества найденных элементов
    }
}
