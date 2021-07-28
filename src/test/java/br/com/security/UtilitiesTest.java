package br.com.security;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.security.infra.entities.RoleEntity;
import br.com.security.infra.entities.UserEntity;

@DisplayName("Testes para cobertura de código")
class UtilitiesTest {

	@Test
	@DisplayName("Cobertura de código para implementações de @lombok.Builder")
	void buildCoverage() {
		
		assertFalse(RoleEntity.builder().build().toString().isBlank());
		assertFalse(UserEntity.builder().build().toString().isBlank());
	}
}
