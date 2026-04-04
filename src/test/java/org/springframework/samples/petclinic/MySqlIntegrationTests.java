package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.school.SchoolRepository;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest(properties = {
	"spring.sql.init.mode=always",
	"spring.sql.init.schema-locations=classpath*:db/mysql/schema.sql",
	"spring.sql.init.data-locations=classpath*:db/mysql/data.sql"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@Testcontainers
@DisabledInNativeImage
@DisabledInAotMode
class MySqlIntegrationTests {

	@ServiceConnection
	@Container
	static MySQLContainer container = new MySQLContainer(DockerImageName.parse("mysql:9.5"));

	@Autowired
	private VetRepository vets;

	@Autowired
	private UserRepository users;

	@Autowired
	private SchoolRepository schools;

	@Autowired
	private GameRepository games;
	@Test
	void testVetsLoadFromDatabase() {
		assertThat(vets.findAll()).isNotEmpty();
	}

	@Test
	void testUsersLoadFromDatabase() {
		assertThat(users.findAll()).isNotEmpty();
	}

	@Test
	void testUserEmailExists() {
		assertThat(users.findAll().stream()
			.anyMatch(u -> "brett.baumgart@kirkwood.edu".equals(u.getEmail())))
			.isTrue();
	}

	@Test
	void testSchoolsLoadFromDatabase() {
		assertThat(schools.findAll()).isNotEmpty();
	}

	@Test
	void testSchoolKirkwoodExists() {
		assertThat(schools.findAll().stream()
			.anyMatch(s -> "Kirkwood Community College".equals(s.getName())))
			.isTrue();
	}

	@Test
	void testGamesLoadFromDatabase() {
		assertThat(games.findAll()).isNotEmpty();
	}

	@Test
	void testGameFindById() {
		Game game = games.findById(1);
		assertThat(game).isNotNull();
		assertThat(game.getName()).isEqualTo("The Legend of Zelda");
	}

	@Test
	void testGameFindByName() {
		Game game = games.findByName("The Legend of Zelda");
		assertThat(game).isNotNull();
		assertThat(game.getGameType()).isEqualTo(Game.GameType.VideoGame);
	}

	@Test
	void testGameCount() {
		assertThat(games.findAll()).hasSizeGreaterThanOrEqualTo(12);
	}
}
