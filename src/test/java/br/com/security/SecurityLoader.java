package br.com.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.security.infra.entities.AuthorityEntity;
import br.com.security.infra.entities.RoleEntity;
import br.com.security.infra.entities.UserEntity;
import br.com.security.infra.repositories.AuthorityRepository;
import br.com.security.infra.repositories.RoleRepository;
import br.com.security.infra.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityLoader implements CommandLineRunner {

	private final AuthorityRepository authorityRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private UserEntity mainUser;

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		this.mainUser = userRepository.save(
				UserEntity.builder().username("principal_admin").password(passwordEncoder.encode("password")).build());

		cadastrarUsuarioAdmin();
		cadastrarUsuarioPrincipal();
	}

	private void cadastrarUsuarioPrincipal() {

		AuthorityEntity savedAuthority = authorityRepository.save(new AuthorityEntity("auth_adm_user", mainUser));

		RoleEntity savedRole = roleRepository
				.save(RoleEntity.builder().name("ROLE_PRINCIPAL").createdBy(mainUser).build());
		savedRole.addAuthority((savedAuthority));
		roleRepository.save(savedRole);

		userRepository.save(UserEntity.builder().username("principal").password(passwordEncoder.encode("password"))
				.roles(new HashSet<>(Set.of(savedRole))).createdBy(mainUser).build());
	}

	private void cadastrarUsuarioAdmin() {

		AuthorityEntity savedAuthority = authorityRepository.save(new AuthorityEntity("auth_adm_security", mainUser));

		RoleEntity savedRole = roleRepository.save(RoleEntity.builder().name("ROLE_ADMIN").createdBy(mainUser).build());
		savedRole.addAuthority((savedAuthority));
		roleRepository.save(savedRole);

		userRepository.save(UserEntity.builder().username("admin").password(passwordEncoder.encode("password"))
				.roles(new HashSet<>(Set.of(savedRole))).createdBy(mainUser).build());
	}
}
