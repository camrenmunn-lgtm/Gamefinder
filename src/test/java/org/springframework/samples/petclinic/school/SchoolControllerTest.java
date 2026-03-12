package org.springframework.samples.petclinic.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link SchoolController}
 */
@WebMvcTest(SchoolController.class)
class SchoolControllerTest {

	private static final int TEST_SCHOOL_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SchoolRepository schools;

	private School school;

	@BeforeEach
	void setup() {
		// Create a dummy school to be returned by the mocked repository
		school = new School();
		school.setId(TEST_SCHOOL_ID);
		school.setName("Kirkwood Community College");
		school.setDomain("kirkwood.edu");
		school.setStatus(School.SchoolStatus.ACTIVE);
	}

	@Test
	void testShowSchoolList() throws Exception {
		// 1. Arrange: Create a "Page" of schools to mock the database response
		// matches the 5 items per page logic in your controller
		Pageable pageable = PageRequest.of(0, 5);
		Page<School> schoolPage = new PageImpl<>(List.of(school), pageable, 1);

		// Tell the mock: "When the controller asks for all schools, give them this list"
		given(this.schools.findAll(any(Pageable.class))).willReturn(schoolPage);

		// 2. Act & Assert: Perform the GET request and verify the results
		mockMvc.perform(get("/schools").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listSchools"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(view().name("schools/schoolList"));
	}

}
